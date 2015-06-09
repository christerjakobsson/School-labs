#ifndef LINKEDLIST_H_INCLUDED
#define LINKEDLIST_H_INCLUDED

struct Node {
    void *data;
    struct Node *next;
};

typedef struct Node* linkedList;

int listSize(struct Node* node);
void remove_node(struct Node *node, struct Node* list);
struct Node* empty();
bool insert(struct Node* list, void *data);
void freeLinkedList(struct Node* node);
struct Node* getFirstNode(struct Node* node);
bool isEmpty(struct Node* node);
struct Node* getNext(struct Node* node);

#endif // LINKEDLIST_H_INCLUDED
