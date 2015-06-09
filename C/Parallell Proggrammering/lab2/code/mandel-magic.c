#include "common.h"

int main(int argc, char *argv[])
{

    parse_options(argc, argv);

    if(options.magic_box_min_size <= 0) {
        fprintf(stderr, "Error: Minimum magic box size must be bigger then zero\n");
        exit(1);
    }

    create_blank_image();

    mandelbrot_magic_image();

    save_image("image.ppm");
    
    return 0;
}
