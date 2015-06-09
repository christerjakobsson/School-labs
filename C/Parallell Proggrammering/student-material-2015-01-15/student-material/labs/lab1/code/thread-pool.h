#ifndef _THREADPOOL_H_
#define _THREADPOOL_H_


typedef void (*tp_task_function_t)(void*);


typedef struct tp_task
{
    tp_task_function_t func;
    void *arg;
} tp_task_t;


void tp_start(int num_threads);

void tp_stop(void);

void tp_submit_task(tp_task_t task);


#endif
