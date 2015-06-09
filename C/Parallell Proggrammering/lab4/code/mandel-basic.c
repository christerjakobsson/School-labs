#include "common.h"


int main(int argc, char *argv[])
{
    parse_options(argc, argv);

    create_blank_image();

    generate_mandelbrot_image();

    //save_image("image.ppm");
    
    return 0;
}
