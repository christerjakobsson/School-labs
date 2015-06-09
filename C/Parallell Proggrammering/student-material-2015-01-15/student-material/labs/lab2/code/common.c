#include "common.h"


struct options options;


struct image
{
    int height;
    int width;
    unsigned char *pixels;
};


static struct image image = { 0, 0, NULL };


void parse_options(int argc, char *argv[])
{
    switch (argc)
    {
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


int iterate_point(double complex c)
{
    int iterations = 0;
    double complex z = 0.0;
    
    while (cabs(z) < 2 && iterations < options.max_iterations)
    {
        z = z * z + c;
        iterations += 1;
    }
    
    return iterations;
}


void generate_mandelbrot_subimage(int row1, int row2, int column1, int column2)
{
    double y1 = cimag(options.center) + (options.pixel_size * (options.image_height - 1)) / 2.0;
    double x1 = creal(options.center) - (options.pixel_size * (options.image_width - 1)) / 2.0;

    // Loop over image rows (numbered from top to bottom).
    for (int image_row = row1; image_row <= row2; ++image_row)
    {
        double y = y1 - image_row * options.pixel_size;
        
        // Loop over image columns (numbered from left to right).
        for (int image_column = column1; image_column <= column2; ++image_column)
        {
            double x = x1 + image_column * options.pixel_size;
            double complex c = x + y * I;
            int iterations = iterate_point(c);
            set_pixel_from_iteration_count(image_row, image_column, iterations);
        }
    }
}


void generate_mandelbrot_image(void)
{
    generate_mandelbrot_subimage(0, options.image_height - 1, 0, options.image_width - 1);
}


void set_pixel_from_iteration_count(int image_row, int image_column, int iterations)
{
    unsigned char *pixel = image.pixels + 3 * image_row * image.width + 3 * image_column;
    pixel[0] = (13 * iterations) % 256;
    pixel[1] = (5 * iterations) % 256;
    pixel[2] = (11 * iterations) % 256;
}


void create_blank_image(void)
{
    image.width = options.image_width;
    image.height = options.image_height;
    image.pixels = malloc(3 * image.width * image.height);
    unsigned char *pixel = image.pixels;
    for (int i = 0; i < image.width * image.height; ++i)
    {
        pixel[0] = pixel[1] = pixel[2] = 0;
        pixel += 3;
    }
}


void save_image(const char *file)
{
    FILE *fp = fopen(file, "w");
    if (fp == NULL)
    {
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
