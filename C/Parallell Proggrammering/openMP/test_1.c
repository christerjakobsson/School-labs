#include <stdio.h>
#include <omp.h>


int main() {

    #pragma omp parallel
    {
    #pragma omp for
        for (int i = 0; i < 12; i++) {
            printf("Threadnr: %d\ti: %d\n", omp_get_thread_num(), i);
        }
    }
    return 0;
}