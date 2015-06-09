#include <stdio.h>

#include "thread-pool.h"


static void my_task_function(void *arg)
{
    char *msg = arg;
    printf("Message: %s\n", msg);
}


int main(void)
{
    // Start a thread pool with 4 worker threads.
    tp_start(4);

    // Create some tasks.
    tp_task_t t1 = { my_task_function, "T1" };
    tp_task_t t2 = { my_task_function, "T2" };
    tp_task_t t3 = { my_task_function, "T3" };

    // Submit the tasks to the thread pool.
    tp_submit_task(t1);
    tp_submit_task(t2);
    tp_submit_task(t3);

    // Stop the thread pool (wait until all tasks have been executed).
    tp_stop();

    return 0;
}
