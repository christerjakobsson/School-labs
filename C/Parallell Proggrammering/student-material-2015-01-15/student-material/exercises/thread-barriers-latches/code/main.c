#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>


// === BARRIER ===

typedef struct barrier
{
    pthread_mutex_t lock;
    // TODO Add additional members
} barrier_t;


void barrier_wait(barrier_t *barrier)
{
    // TODO Implement
}


// === LATCH ===

typedef struct latch
{
    pthread_mutex_t lock;
    // TODO Add additional members
} latch_t;


void latch_signal(latch_t *latch)
{
    // TODO Implement
}


void latch_wait(latch_t *latch)
{
    // TODO Implement
}


// === TEST ===


barrier_t barrier =
{
    // TODO Initialize members
};

latch_t latch =
{
    // TODO Initialize members
};


void master(void)
{
    printf("master sleeping for two seconds...\n");
    sleep(2);
    printf("master waiting at barrier...\n");
    barrier_wait(&barrier);
    printf("master after barrier\n");

    printf("master sleeping for two seconds...\n");
    sleep(2);
    printf("master signaling latch...\n");
    latch_signal(&latch);
    printf("master after latch signal\n");
}


void worker(void)
{
    printf("worker waiting at barrier...\n");
    barrier_wait(&barrier);
    printf("worker after barrier\n");

    printf("worker waiting at latch...\n");
    latch_wait(&latch);
    printf("worker after latch wait\n");
}


void *thread_function(void *arg)
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
