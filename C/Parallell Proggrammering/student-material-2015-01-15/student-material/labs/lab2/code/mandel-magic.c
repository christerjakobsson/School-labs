#include "common.h"


int main(int argc, char *argv[])
{
    parse_options(argc, argv);

    create_blank_image();

    // TODO Generate image in parallel using the magic box algorithm.

    save_image("image.ppm");
    
    return 0;
}
