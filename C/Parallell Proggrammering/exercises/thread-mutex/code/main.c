#include <pthread.h>
#include <stdio.h>

static int counter = 0;
static pthread_mutex_t lock;



static void *thread_function(void *arg)
{
    for (int rep = 0; rep < 1000000; ++rep)
    {
        pthread_mutex_lock(&lock);
        counter += 1;
        pthread_mutex_unlock(&lock);
    }

    return NULL;
}

int main(void)
{
    counter = 0;

    if (pthread_mutex_init(&lock, NULL) != 0) {
        printf("\n mutex init failed\n");
        return 1;
    }

    pthread_t thr;
    pthread_create(&thr, NULL, thread_function, NULL);
    thread_function(NULL);
    pthread_join(thr, NULL);

    printf("counter: %d\n", counter);

    pthread_mutex_destroy(&lock);
    return 0;
}
