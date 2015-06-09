#ifndef QUEUE_H_INCLUDED
#define QUEUE_H_INCLUDED

#include <stdbool.h>

struct Node {
    void *data;
    struct Node *next;
};

typedef struct queue {
	struct Node *head;
} *queue;

/* Sets up the queue if it has not been created. */
struct queue* empty();

/* Add a new item to the end of the queue. */
void enqueue(struct queue *q, void *data);

/* Frees allocated memory from the queue. */
void freeQueue(struct queue *q);

/* Checks if the queue is empty. */
bool isEmpty(struct queue *q);

/**
* Remove the head of the queue and make
* its next be head. Return heads data and free head node.
*/
void* dequeue(struct queue *q);

#endif // QUEUE_H_INCLUDED
