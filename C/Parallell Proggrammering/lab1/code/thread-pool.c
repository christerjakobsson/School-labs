#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "thread-pool.h"
#include "queue.h"

static void *doTasks(void *args);
static void freeTask(void* data);

static pthread_t *threads;
static pthread_mutex_t mLock;
static pthread_cond_t cvIsStopping;
static queue taskQueue;
static int nrOfThreads;
static bool isStopped;
static bool quitAction;

/**
* Initialises global variables, initiates mutex and condition variables.
* Create num_threads threads and let them start work.
*/
void tp_start(int num_threads) {

    // Setup shared variables.
    if (pthread_mutex_init(&mLock, NULL) != 0) {
        perror("Error when initiating mutex");
        fprintf(stderr, "Exiting\n");
        exit(1);
    }

    if(pthread_cond_init(&cvIsStopping, NULL) != 0) {
        perror("Error when initiating contidion variable");
        fprintf(stderr, "Exiting\n");
        exit(1);
    }

    taskQueue = empty();
    isStopped = false;
    quitAction = false;
    nrOfThreads = num_threads;

    // Create numThreads worker threads.
    threads = (pthread_t*) calloc((size_t) nrOfThreads, sizeof(pthread_t));
    if(threads == NULL) {
        perror("Error when creating threads");
        fprintf(stderr, "Exiting\n");
        exit(1);
    }

    for (int i = 0; i < nrOfThreads; i++) {
        if (pthread_create(&threads[i], NULL, &doTasks, NULL)) {
            perror("Unable to start threads");
            fprintf(stderr, "Exiting\n");
            exit(1);
        }
    }
}

/**
* Thread worker function, each thread in the thread-pool runs this function.
* the function consists of a while loop keep going until tp_stop has been called
* and have set isStopped to true and the queue is empty of tasks.
* Uses a condition variable that tells the threads that there is tasks in the queue.
*/
void *doTasks(void *args) {

    // Keep working until tp_stop is called.
    pthread_mutex_lock(&mLock);
    while(!quitAction) {

        // cond_wait if quitAction = false
        if (!isStopped) {
            pthread_cond_wait(&cvIsStopping, &mLock);
        }

        // If queue is not empty call task with args
        if (!isEmpty(taskQueue)) {
            tp_task_t *task;

            task = (tp_task_t*) dequeue(taskQueue);
            pthread_mutex_unlock(&mLock);

            task->func(task->arg);
            freeTask(task);
            pthread_mutex_lock(&mLock);
        } else if(isStopped) {
            quitAction = true;
        }

        if(!isEmpty(taskQueue) && nrOfThreads > 1) {
            pthread_cond_signal(&cvIsStopping);
        }
    }
    pthread_mutex_unlock(&mLock);

    return NULL;
}

/**
* Stops the thread-pool, sets a global variable isStopped to true
* and then joins on the threads.
* When the join is completed all allocated memory is free'd and the
* mutex and condition variables are destroyed.
*/
void tp_stop(void)
{
    // Signal threads to initiate stop.
    pthread_mutex_lock(&mLock);
    isStopped = true;
    pthread_mutex_unlock(&mLock);

    // Broadcast to threads, resuminig from cond_wait.
    if(pthread_cond_broadcast(&cvIsStopping) != 0) {
        perror("Error when broadcasting to threads");
    }

    for (int i = 0; i < nrOfThreads; i++) {
        if(pthread_join(threads[i], NULL) != 0) {
            perror("error when joining threads");
        }
    }

    // Free allocated
    freeQueue(taskQueue);
    free(threads);

    // Destroy mutex and condition
    pthread_cond_destroy(&cvIsStopping);
    pthread_mutex_destroy(&mLock);
}

/**
* Free function used by the linked queue to free a arbitrary datatype.
*/
void freeTask(void* data) {
    tp_task_t *temp = (tp_task_t*)data;
    free(temp);
}

/**
* Submits a task to the queue.
* signals the condition and a thread in the thread-pool will
* take the task and execute it.
*/
void tp_submit_task(tp_task_t task) {

    // Allocate a pointer to a task.
    tp_task_t *tempTask = malloc(sizeof(tp_task_t));
    if(tempTask == NULL) {
        perror("Error allocating memory for task");
        fprintf(stderr, "Exiting\n");
        tp_stop();
    }
    memcpy(tempTask, &task, sizeof(task));

    // Insert task into queue.
    pthread_mutex_lock(&mLock);
    enqueue(taskQueue, tempTask);
    pthread_mutex_unlock(&mLock);

    // Signal condition
    if(pthread_cond_signal(&cvIsStopping) != 0) {
        perror("Error signaling condition");
    }
}