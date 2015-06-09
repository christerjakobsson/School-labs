#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "gol.h"


int main(int argc, char *argv[])
{
    // TODO Initialize MPI.
    MPI_Init(&argc, &argv);

    // TODO Get process rank.
    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    // TODO Get number of processes.
    int num_procs;
    MPI_Comm_size(MPI_COMM_WORLD, &num_procs);


    // Initialize random number generator.
    srand(1 + rank);

    // Parse command line on rank 0 and broadcast to all.
    int width = 0, height = 0, generations = 0;
    if (rank == 0)
    {
        // Parse command line.
        if (argc == 4)
        {
            height = atoi(argv[1]);
            width = atoi(argv[2]);
            generations = atoi(argv[3]);
        }
        else
        {
            printf("Usage: %s  HEIGHT  WIDTH  GENERATIONS\n", argv[0]);
            int error = 0;
            // TODO Abort MPI.
            MPI_Abort(MPI_COMM_WORLD, error);
        }
    }
    
    // TODO Broadcast height.
    MPI_Bcast(&height, 1, MPI_INT, 0, MPI_COMM_WORLD);
    
    // TODO Broadcast width.
    MPI_Bcast(&width, 1, MPI_INT, 0, MPI_COMM_WORLD);
    
    // TODO Broadcast generations.
    MPI_Bcast(&generations, 1, MPI_INT, 0, MPI_COMM_WORLD);
    
    // Compute the width of my local grid.
    int my_width = width / num_procs;
    if (rank == num_procs - 1)
    {
        my_width = width - (num_procs - 1) * my_width;
    }
 
    // Initialize my local grid.
    gol_init(height, my_width);


    // Initialize my local grid.
    gol_init(height, my_width);

    // Randomize the global grid.
    gol_randomize();

    // Print the global grid.
    gol_print(height, width);

    // Count and print the global number of live cells.
    int num_alive = gol_count_alive();
    if (rank == 0)
    {
        printf("Number of live cells: %d\n\n", num_alive);
    }

    // Simulate the given number of generations.
    for (int gen = 0; gen < generations; ++gen)
    {
        // Sleep for a while to get an animation effect.
        sleep(1);

        // Advance the global grid.
        gol_advance();

        // Print the global grid.
        gol_print(height, width);

        // Count and print the global number of live cells.
        int num_alive = gol_count_alive();
        if (rank == 0)
        {
            printf("Number of live cells: %d\n\n", num_alive);
        }
    }

    // Clean up.
    gol_finalize();

    // TODO Finalize MPI.
    MPI_Finalize();
    return 0;
}
