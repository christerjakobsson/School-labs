#include <stdio.h>
#include <omp.h>

int main() {
    int n = 9, a[n];
#pragma omp parallel
    {
#pragma omp for schedule(static, 2)
        //Need controlling predicate!
        for (int k = 0; ; ++k)
        {
            if (k >= n)
            {
                //Cant use break in openMp for??
                break;
            }
            a[k] += 1;
        }
    }

    for(int i = 0; i < n; ++i) {
        printf("a[%d]\t%d\n",i, a[i]);
    }
    return 0;
}