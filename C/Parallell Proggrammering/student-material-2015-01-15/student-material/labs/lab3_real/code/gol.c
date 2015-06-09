#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>

#include "gol.h"


#define EMPTY 0
#define ALIVE 1


// A grid of cells. Each cell holds a value that is either EMPTY (0)
// or ALIVE (1). The cells member holds the 2D array of cells in
// column-major order. The grid has height rows and width
// columns. Around the entire grid is one layer of ghost cells. These
// greatly simplify the code by eliminating special cases at the
// boundary of the grid.
//
// The cell at (row, col) is stored at
//
//   cells[(1 + row) + (1 + col) * (height + 2)].
//
// The quantity (height + 2) is referred to as the stride and is
// computed by the grid_stride function.
//
// Row 0 is the top row. Column 0 is the left column.
struct grid
{
    int height;
    int width;
    char *cells;
};


// Pointers to two grids. The simulation will alternate between them:
// reading from one and to writing the other.
static struct grid *grids[2];

// The index (0 or 1) of the current input grid in grids[]. 
static int cur_grid = 0;


// Compute the column stride of the cells array in the grid.
static int grid_stride(struct grid *grid)
{
    return grid->height + 2;
}


// Return the state of a specified cell by its coordinates. 
static int grid_get(struct grid *grid, int row, int col)
{
    int stride = grid_stride(grid);
    return grid->cells[(1 + row) + (1 + col) * stride];
}


// Set the state of a specified cell.
static void grid_set(struct grid *grid, int row, int col, int state)
{
    int stride = grid_stride(grid);
    grid->cells[(1 + row) + (1 + col) * stride] = state;
}


// Create a new grid with empty cells.
static struct grid *new_grid(int height, int width)
{
    struct grid *grid = malloc(sizeof(struct grid));
    grid->height = height;
    grid->width = width;
    grid->cells = calloc((height + 2) * (width + 2), sizeof(char));
    return grid;
}


// Randomize the state of all cells.
static void randomize_grid(struct grid *grid)
{
    int stride = grid_stride(grid);
    for (int col = 0; col < grid->width; ++col)
    {
        for (int row = 0; row < grid->height; ++row)
        {
            grid_set(grid, row, col, rand() % 2);
        }
    }
}


// Initialize the two grids.
void gol_init(int height, int width)
{
    grids[0] = new_grid(height, width);
    grids[1] = new_grid(height, width);
    cur_grid = 0;
}


// Randomize the current grid in a perfectly parallel fashion.
void gol_randomize(void)
{
    randomize_grid(grids[cur_grid]);
}


void gol_finalize(void)
{
    // Deallocate memory.
    for (int i = 0; i < 2; ++i)
    {
        free(grids[i]->cells);
        free(grids[i]);
    }
}


// Advance a specified cell using the rules of Game of Life.
static void advance_cell(struct grid *in, struct grid *out, int row, int col)
{
    // Count alive neighbors.
    int count = 0;
    if (ALIVE == grid_get(in, row - 1, col - 1)) count += 1;
    if (ALIVE == grid_get(in, row - 1, col    )) count += 1;
    if (ALIVE == grid_get(in, row - 1, col + 1)) count += 1;
    if (ALIVE == grid_get(in, row    , col - 1)) count += 1;
    if (ALIVE == grid_get(in, row    , col + 1)) count += 1;
    if (ALIVE == grid_get(in, row + 1, col - 1)) count += 1;
    if (ALIVE == grid_get(in, row + 1, col    )) count += 1;
    if (ALIVE == grid_get(in, row + 1, col + 1)) count += 1;

    // Apply the rules (rule source: Wikipedia).

    // Copy cell from input to output.
    grid_set(out, row, col, grid_get(in, row, col));

    // Rule 1: Any live cell with fewer than two live neighbours dies, as if caused by under-population.
    if (ALIVE == grid_get(in, row, col) && count < 2)
    {
        grid_set(out, row, col, EMPTY);
    }
            
    // Rule 2: Any live cell with two or three live neighbours lives on to the next generation.
    if (ALIVE == grid_get(in, row, col) && 2 <= count && count <= 3)
    {
        grid_set(out, row, col, ALIVE);
    }
            
    // Rule 3: Any live cell with more than three live neighbours dies, as if by overcrowding.
    if (ALIVE == grid_get(in, row, col) && count > 3)
    {
        grid_set(out, row, col, EMPTY);
    }
            
    // Rule 4: Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
    if (EMPTY == grid_get(in, row, col) && count == 3)
    {
        grid_set(out, row, col, ALIVE);
    }
}


// Advance the global grid one step.
void gol_advance(void)
{
    // Input grid.
    struct grid *in = grids[cur_grid];

    // Output grid.
    struct grid *out = grids[1 - cur_grid];

    // TODO Communicate boundary elements with neighboring processes (left and/or right).

    // Loop over all interior cells and advance them.
    for (int col = 0; col < in->width; ++col)
    {
        for (int row = 0; row < in->height; ++row)
        {
            advance_cell(in, out, row, col);
        }
    }
    
    // Swap input/output grids.
    cur_grid = 1 - cur_grid;
}


// Count the number of live cells and return the count on rank 0.
int gol_count_alive(void)
{
    struct grid *grid = grids[cur_grid];

    // Count the number of live cells in the local grid.
    int count = 0;
    for (int col = 0; col < grid->width; ++col)
    {
        for (int row = 0; row < grid->height; ++row)
        {
            if (ALIVE == grid_get(grid, row, col))
            {
                count += 1;
            }
        }
    }

    // TODO Reduce the local counts to a global count on rank 0.
    
    return count;
}


// Print the global grid to stdout from rank 0.
void gol_print(void)
{
    // TODO This function only works sequentially (one process).  Need
    // to communicate remote cells to rank 0.
    
    struct grid *grid = grids[cur_grid];
    struct grid *old = grids[1 - cur_grid];
    for (int row = 0; row < grid->height; ++row)
    {
        for (int col = 0; col < grid->width; ++col)
        {
            if (ALIVE == grid_get(grid, row, col))
            {
                // Live cell.
                printf("o ");
            }
            else if (ALIVE == grid_get(old, row, col))
            {
                // Dying cell (empty now but alive in the previous generation).
                printf("+ ");
            }
            else
            {
                // Dead cell.
                printf(". ");
            }
        }
        printf("\n");
    }
}
