#include "common.h"

struct options options;

struct image {
    int height;
    int width;
    unsigned char *pixels;
};


static struct image image = {0, 0, NULL};

void parse_options(int argc, char *argv[]) {
    switch (argc) {
        case 8:
            options.magic_box_min_size = atoi(argv[7]);

        case 7:
            options.image_height = atoi(argv[1]);
            options.image_width = atoi(argv[2]);
            options.pixel_size = atof(argv[3]);
            options.center = atof(argv[4]) + atof(argv[5]) * I;
            options.max_iterations = atoi(argv[6]);
            break;

        default:
            printf("Wrong number of arguments!\n");
            exit(1);
            break;
    }
}


int iterate_point(double complex c) {
    int iterations = 0;
    double complex
    z = 0.0;

    while (cabs(z) < 2 && iterations < options.max_iterations) {
        z = z * z + c;
        iterations += 1;
    }

    return iterations;
}


void generate_mandelbrot_subimage(int row1, int row2, int column1, int column2) {
    double y1 = cimag(options.center) + (options.pixel_size * (options.image_height - 1)) / 2.0;
    double x1 = creal(options.center) - (options.pixel_size * (options.image_width - 1)) / 2.0;
    
    int expSize = 10;
    int threads = omp_get_max_threads();
    printf("Max threads %d\n",omp_get_max_threads());
    for(int i = 1; i <= threads; i++) {
        omp_set_num_threads(i);    
        
        for(int j = 0; j < expSize; j++) {
        
            // Loop over image rows (numbered from top to bottom).
            #pragma omp parallel
            {   
                double before = omp_get_wtime();        
                #pragma omp for collapse(2) schedule(dynamic)
                for (int image_row = row1; image_row <= row2; ++image_row) {
                    for (int image_column = column1; image_column <= column2; ++image_column) {
                        double y = y1 - image_row * options.pixel_size;
                        double x = x1 + image_column * options.pixel_size;
                        double complex
                        c = x + y * I;
                        int iterations = iterate_point(c);
                        set_pixel_from_iteration_count(image_row, image_column, iterations);
                    }
                }
                #pragma omp barrier
                #pragma omp master
                printf("%d\t%d\t%f\n", i, j+1, omp_get_wtime()-before);
            }
            
        }
        
    }
}

void generate_mandelbrot_image(void) {
    generate_mandelbrot_subimage(0, options.image_height - 1, 0, options.image_width - 1);
}

/**
* Splits the image into four pieces, and then runs a recursive  function on each piece that will use
* the magic box trick to calculate all points in the piece.
*/
void mandelbrot_magic_image() {


    int row2 = options.image_height - 1;
    int col2 = options.image_width - 1;

    #pragma omp parallel
    {
        #pragma omp single
        {
            // upper left
            #pragma omp task
            generate_mandelbrot_magic_image(0, row2 / 2, 0, col2 / 2);

            //upper right
            #pragma omp task
            generate_mandelbrot_magic_image(0, row2 / 2, col2 / 2, col2);

            //lower left
            #pragma omp task
            generate_mandelbrot_magic_image(row2 / 2, row2, 0, col2 / 2);

            //lower right
            #pragma omp task
            generate_mandelbrot_magic_image(row2 / 2, row2, col2 / 2, col2);
        }
    }
}

/**
* Calculates all the points in the border of the box with the given parameters,
* if all these points are the same color all points inside the box is filled with
* the same color, else points are calculated normally.
* Recurses until row * column of the box is smaller than the parameter
* magic_box_min_size.
*/
void generate_mandelbrot_magic_image(int row1, int row2, int column1, int column2) {
    double y1 = cimag(options.center) + (options.pixel_size * (options.image_height - 1)) / 2.0;
    double x1 = creal(options.center) - (options.pixel_size * (options.image_width - 1)) / 2.0;

    int r = row2 - row1;
    int c = column2 - column1;

    if (r * c > options.magic_box_min_size) {

        int color = calcPoint(y1, x1, row1, column1);
        bool sameColor = true;

        for (int row = row1 + 1; row <= row2; row++) {
            if (calcPoint(y1, x1, row, column1) != color || calcPoint(y1, x1, row, column2) != color) {
                sameColor = false;
            }
        }

        for (int col = column1; col <= column2; col++) {
            if (calcPoint(y1, x1, row1, col) != color || calcPoint(y1, x1, row2, col) != color) {
                sameColor = false;
            }
        }

        if (sameColor) {
            for (int row = row1; row < row2; row++) {
                for (int col = column1; col < column2; col++) {
                    set_pixel_from_iteration_count(row, col, color);
                }
            }

            int inverseColor = abs(color - options.max_iterations);
            for (int row = row1 + 1; row < row2 - 1; row++) {
                set_pixel_from_iteration_count(row, column1 + 1, inverseColor);
                set_pixel_from_iteration_count(row, column2 - 1, inverseColor);
            }

            for (int col = column1 + 1; col < column2 - 1; col++) {
                set_pixel_from_iteration_count(row1 + 1, col, inverseColor);
                set_pixel_from_iteration_count(row2 - 1, col, inverseColor);
            }
        } else {
            r = row2 - (r / 2);
            c = column2 - (c / 2);

            //upper left
            #pragma omp task
            generate_mandelbrot_magic_image(row1, r, column1, c);

            //upper right
            #pragma omp task
            generate_mandelbrot_magic_image(row1, r, c, column2);

            //lower left
            #pragma omp task
            generate_mandelbrot_magic_image(r, row2, column1, c);

            //lower right
            #pragma omp task
            generate_mandelbrot_magic_image(r, row2, c, column2);
        }
    } else {
        for (int image_row = row1; image_row <= row2; ++image_row) {
            for (int image_column = column1; image_column <= column2; ++image_column) {
                double y = y1 - image_row * options.pixel_size;
                double x = x1 + image_column * options.pixel_size;
                double complex
                c = x + y * I;
                int iterations = iterate_point(c);
                set_pixel_from_iteration_count(image_row, image_column, iterations);
            }
        }
    }
}

int calcPoint(double y1, double x1, int row, int col) {

    double y = y1 - row * options.pixel_size;
    double x = x1 + col * options.pixel_size;
    double complex
    c = x + y * I;
    int iterations = iterate_point(c);
    return iterations;
}

void set_pixel_from_iteration_count(int image_row, int image_column, int iterations) {
    unsigned char *pixel = image.pixels + 3 * image_row * image.width + 3 * image_column;
    pixel[0] = (13 * iterations) % 256;
    pixel[1] = (5 * iterations) % 256;
    pixel[2] = (11 * iterations) % 256;
}

void create_blank_image(void) {
    image.width = options.image_width;
    image.height = options.image_height;
    image.pixels = malloc(3 * image.width * image.height);
    unsigned char *pixel = image.pixels;
    for (int i = 0; i < image.width * image.height; ++i) {
        pixel[0] = pixel[1] = pixel[2] = 0;
        pixel += 3;
    }
}

void save_image(const char *file) {
    FILE *fp = fopen(file, "w");
    if (fp == NULL) {
        printf("Failed to open file %s\n", file);
        return;
    }
    fprintf(fp, "P6\n");
    fprintf(fp, "%d %d\n", options.image_width, options.image_height);
    fprintf(fp, "255\n");
    fwrite(image.pixels, 3 * image.width * image.height, 1, fp);
    fclose(fp);
    free(image.pixels);
    image.pixels = NULL;
}