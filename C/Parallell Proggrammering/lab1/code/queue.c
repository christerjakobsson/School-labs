#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include "queue.h"


/* Frees allocated memory from the queue. */
void freeQueue(struct queue *q) {
	struct Node* curr = q->head;
	struct Node* temp;

	while (curr != NULL) {
		temp = curr;
		curr = curr->next;

		free(temp);
	}

	free(q);
}

/* Sets up the queue if it has not been created. */
struct queue* empty() {
	struct queue *q = calloc(1, sizeof(struct queue));
	q->head = NULL;
    return q;
}

/* Add a new item to the end of the queue. */
void enqueue(struct queue *q, void *data) {
	struct Node* n = calloc(1, sizeof(struct Node));
	n->next = NULL;
	n->data = data;


    if(q->head == NULL) {
        q->head = n;
    } else {
        struct Node* curr = q->head;

        while(curr->next != NULL) {
            curr = curr->next;
        }
        curr->next = n;
    }
}

/**
* Remove the head of the queue and make
* its next be head. Return heads data and free head node.
*/
void* dequeue(struct queue *q) {
    void* data =  q->head->data;
    struct Node* node = q->head;

    q->head = q->head->next;
    free(node);
    return data;
}

/* Checks if the queue is empty. */
bool isEmpty(struct queue *q) {
	return q->head == NULL;
}