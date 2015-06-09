#ifndef LINKEDLIST_H_INCLUDED
#define LINKEDLIST_H_INCLUDED

struct Node {
    unsigned int uuid;
    char *uname;
    struct Node *next;
};

typedef struct Node* linkedList;

struct Node* empty();
bool insert(struct Node* list, struct Node* n);
void freeLinkedList(struct Node* node);

#endif // LINKEDLIST_H_INCLUDED
