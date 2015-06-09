#include <stdio.h>
#include <omp.h>

int main() {
    int n = 9, a[n], b[n];
    for(int i = 0; i < n; i++) {
        b[i] = i;
    }
#pragma omp parallel
    {
#pragma omp for schedule(static, 2)
        for (int k = 0; k < n; ++k)
        {
            a[k] = b[k];
        }
    }
    for(int i = 0; i < n; ++i) {
        printf("a[%d]\t%d\n",i, a[i]);
    }
    return 0;
}