#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>

#include "thread-pool.h"


// TODO Note that you will have to add additional file-scope (static)
// functions to this file to implement the API.


void tp_start(int num_threads)
{
    printf("tp_start() not implemented!\n");

    // TODO Setup shared variables.

    // TODO Create numThreads worker threads.
}


void tp_stop(void)
{
    printf("tp_stop() not implemented!\n");

    // TODO Wait for all tasks to finish.

    // TODO Signal all worker threads to quit.

    // TODO Join each worker thread.
}


void tp_submit_task(tp_task_t task)
{
    printf("tp_submit_task() not implemented!\n");

    // TODO Share the task with the worker threads.

    // TODO Signal the worker threads that a new task is available.
}
