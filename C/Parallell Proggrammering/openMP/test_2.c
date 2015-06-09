#include <stdio.h>
#include <omp.h>

int main() {
    int n = 9, a[n], l[n];
    for(int i = 0; i < n; i++) {
        printf("%d\n", a[i]);
    }
    #pragma omp parallel
    {
        #pragma omp for schedule(static, 2)
        for (int k = 0; k < n; ++k) {
            printf("Threadnr: %d\ta[%d] = %d\n", omp_get_thread_num(), k, a[k]);
            a[k] += 1;

        }
    }
    for(int i = 0; i < n; ++i) {
        printf("a[%d]\t%d\n",i, a[i]);
    }
    return 0;
}