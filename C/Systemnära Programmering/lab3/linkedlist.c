#include<stdio.h>
#include<stdlib.h>
#include<stdbool.h>
#include "linkedlist.h"

/* Frees allocated memory from the list. */
void freeLinkedList(struct List* list) {
	struct Node* curr = list->head;
	struct Node* temp;

	while (curr != NULL) {
		temp = curr;
		curr = curr->next;

		free(temp->data);
		free(temp);
	}
	free(list);
}

/* Sets up the linked list if it hasnt been created.
 */
struct List* empty() {
	struct List* list = calloc(1, sizeof(struct List));
	list->head = NULL;
	list->curr = NULL;
	return list;
}

/* Adds the parameter n into the linked list,
 * return true if it succeded.
 */
bool insert(struct List* list, void *data) {
	struct Node* n = calloc(1, sizeof(struct Node));
	n->next = NULL;
	n->data = data;

	if (list->head != NULL) {
		struct Node* curr = list->head;

		while (curr->next != NULL) {
			curr = curr->next;
		}
		curr->next = n;
	} else {
		list->head = n;
	}

	return true;
}

/**
 * Checks if current node is the last node in the list.
 */
bool isEnd(struct List* list) {
	return (list->curr->next == NULL);
}

/**
 * Remove a node from the list.
 */
void remove_node(struct Node* removeNode, struct List* lista) {
	lista->curr = lista->head;

	if (lista->head == removeNode) {
		if (lista->curr != NULL) {
			struct Node* temp = lista->head;
			lista->head = lista->head->next;
			free(temp->data);
			free(temp);
		} else {
			free(lista->head->data);
		}

	} else {
		while (lista->curr->next != NULL) {

			if (lista->curr->next == removeNode) {
				struct Node* temp = lista->curr->next;
				lista->curr->next = lista->curr->next->next;
				free(temp->data);
				free(temp);
			} else
			lista->curr = lista->curr->next;
		}
	}
}

/**
 * Returns the next node in the list.
 */
void next(struct List* list) {
	list->curr = list->curr->next;
}

/**
 * Checks if the list is empty.
 */
bool isEmpty(struct List* list) {
	return list->head == NULL;
}

void getFirst(struct List* list) {
	list->curr = list->head;
}

/**
 * Returns the size of the list.
 */
int listSize(struct Node* node) {
	int i = 0;
	while (node->next != NULL) {
		i++;
		node = node->next;
	}
	return i - 1;
}
