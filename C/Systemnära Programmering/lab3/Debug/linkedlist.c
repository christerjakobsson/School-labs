#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include "linkedlist.h"

/* Frees allocated memory from the list. */
void freeLinkedList(struct node* node) {
	linkedList temp;

	while (node != NULL) {
		temp = node;
		node = node->next;

		free(temp->data);
		free(temp);
	}
}

/* Sets up the linked list if it hasnt been created. */
struct node* create_list() {
	struct node* head = calloc(1, sizeof(struct node));
	return head;
}

/* Adds the parameter n into the linked list,
 * return true if it succeded. */
bool addToList(struct node* list, void *data) {
	struct node* n = calloc(1, sizeof(struct node));
	n->next = NULL;
	n->data = data;

	while (list->next != NULL) {
		list = list->next;
	}

	list->next = n;
	return true;
}

/**
 * Remove a node from the list.
 */
void removeFromList(struct node* removeNode, struct node* list) {
	list = list->next;

	while (list != NULL) {

		if (list->next == removeNode) {
			list->next = list->next->next;
			free(removeNode->data);
			free(removeNode);
		}
		list = list->next;
	}
}

/**
 * Checks if the list is empty.
 */
bool isEmpty(struct node* node) {
	return node->next == NULL;
}

/**
 * Returns the next node in the list.
 */
struct node* getNext(struct node* node) {
	return node->next;
}

/**
 * Returns the size of the list.
 */
int listSize(struct node* node) {
	int i = 0;
	while(node->next != NULL) {
		i++;
		node = node->next;
	}
	return i-1;
}
