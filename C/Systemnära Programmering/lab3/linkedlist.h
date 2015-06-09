#ifndef LINKEDLIST_H_INCLUDED
#define LINKEDLIST_H_INCLUDED

#include <stdbool.h>

struct Node {
    void *data;
    struct Node *next;
};

struct List {
	struct Node *head;
	struct Node *curr;
};

typedef struct List* linkedList;

int listSize(struct Node* node);


/* Remove a node from the list. */
void remove_node(struct Node* node, struct List* list);

/* Sets up the linked list if it hasnt been created. */
struct List* empty();

/* Adds the parameter n into the linked list,
 * return true if it succeded. */
bool insert(struct List* list, void *data);

/* Frees allocated memory from the list. */
void freeLinkedList(struct List* list);

/* Checks if the list is empty. */
bool isEmpty(struct List* node);


/* Returns the next node in the list. */
void next(struct List* node);

/** Makes curr me the head, used to start iterating through the list. */
void getFirst(struct List* list);

/* Checks if current node is the last node in the list. */
bool isEnd(struct List* list);

#endif // LINKEDLIST_H_INCLUDED
