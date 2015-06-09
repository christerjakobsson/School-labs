#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>


static int state = 0;
static pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t cond = PTHREAD_COND_INITIALIZER;

static void master(void)
{
    printf("master sleeping for one second...\n");
    sleep(1);
    printf("master changing the state...\n");
    pthread_mutex_lock(&lock);

    state = 1;
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&lock);
    printf("master exiting...\n");
}


static void worker(void)
{
    printf("worker waiting...\n");
    pthread_mutex_lock(&lock);
    pthread_cond_wait(&cond, &lock);
    int st = state;
    pthread_mutex_unlock(&lock);

    printf("worker detected state change to %d.\n", st);
    printf("worker exiting...\n");
}


static void *thread_function(void *arg)
{
    worker();
    return NULL;
}


int main(void)
{
    pthread_t thr;
    pthread_create(&thr, NULL, thread_function, NULL);
    master();
    pthread_join(thr, NULL);
    return 0;
}
