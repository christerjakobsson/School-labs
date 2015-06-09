#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include "linkedlist.h"

/* Frees the allocated memory from the list. */
void freeLinkedList(struct Node* node) {
	linkedList temp;

	while (node != NULL) {
		temp = node;
		node = node->next;
		free(temp->uname);
		free(temp);
	}
}

/* Sets up the linked list if it hasnt been created. */
struct Node* empty() {
	struct Node* head = calloc(1, sizeof(struct Node));
	return head;
}

/* Adds the parameter n into the linked list,
 * return true if it succeed. */
bool insert(struct Node* list, struct Node* n) {

	while (list->next != NULL) {
		list = list->next;
	}

	list->next = n;

	return true;
}
