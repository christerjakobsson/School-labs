#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>


static double gettime(void)
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec + 1e-6 * tv.tv_usec;
}


#define SIZE 32
#define BIGNUMBER 100000000

static int array[SIZE];


static void *thread_function(void *arg)
{
    int *var = arg;
    for (int rep = 0; rep < BIGNUMBER; ++rep)
    {
        *var += 1;
    }
    return NULL;
}


int main(void)
{
    pthread_t thr;
    for (int offset = 1; offset < SIZE; ++offset)
    {
        for (int k = 0; k < SIZE; ++k)
        {
            array[k] = 0;
        }

        double tm = gettime();
        pthread_create(&thr, NULL, thread_function, array + offset);
        thread_function(array);
        pthread_join(thr, NULL);
        tm = gettime() - tm;
        printf("%d\t%lf\n", offset, tm);
    }
    return 0;
}

