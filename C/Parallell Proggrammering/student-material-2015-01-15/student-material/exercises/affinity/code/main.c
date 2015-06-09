#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>


#define BIGSIZE 100000000
#define BIGNUMBER 10


static double gettime(void)
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec + 1e-6 * tv.tv_usec;
}


int main(void)
{
    char *p1 = calloc(BIGSIZE, 1);
    char *p2 = calloc(BIGSIZE, 1);
    double t = gettime();
    for (int rep = 0; rep < BIGNUMBER; ++rep)
    {
        memcpy(p2, p1, BIGSIZE);
        memcpy(p1, p2, BIGSIZE);
    }
    printf("%lf\n", gettime() - t);
    free(p1);
    free(p2);
    return 0;
}
