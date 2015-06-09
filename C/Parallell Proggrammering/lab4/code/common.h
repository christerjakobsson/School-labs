#ifndef _COMMON_H_
#define _COMMON_H_


#include <stdlib.h>
#include <stdio.h>
#include <omp.h>
#include <complex.h>
#include <stdbool.h>

struct options
{
    int image_height;
    int image_width;
    double pixel_size;
    double complex center;
    int max_iterations;
    int magic_box_min_size;
};

extern struct options options;

void parse_options(int argc, char *argv[]);

void create_blank_image(void);

void save_image(const char *filename);

void set_pixel_from_iteration_count(int image_row, int image_column, int iterations);

int iterate_point(double complex c);

void generate_mandelbrot_image(void);

void generate_mandelbrot_subimage(int row1, int row2, int column1, int column2);

void mandelbrot_magic_image();

void generate_mandelbrot_magic_image(int row1, int row2, int column1, int column2);

int calcPoint(double y1, double x1, int row, int col);

#endif
