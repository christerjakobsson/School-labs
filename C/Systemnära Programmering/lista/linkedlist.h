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
void remove_node(struct Node *node, struct List* list);
struct List* empty();
bool insert(struct List* list, void *data);
void freeLinkedList(struct List* list);
bool isEmpty(struct List* node);
void next(struct List* node);

#endif // LINKEDLIST_H_INCLUDED
