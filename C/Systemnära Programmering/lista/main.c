#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <string.h>
#include "linkedlist.h"

void print_list(struct List* list);

int main() {

	linkedList list = empty();

	char *test;
	int i;
	char *str[] = {"ETT", "TVÅ","TRE", "FYRA", "FEM" };
	for (i = 0; i < 5; i++) {
		test = calloc(1, sizeof(char[strlen(str[i])]));
		test = strcpy(test, str[i]);
		if (!insert(list, test) == true) {
			printf("FAIL");
		}
	}

	print_list(list);
	//removeFromList(list->head->next, list);
	print_list(list);
	freeLinkedList(list);
	return 0;
}

/* Prints the list */
void print_list(struct List* list) {

	if (!isEmpty(list)) {
		list->curr = list->head;
		printf("%s\n", (char *) list->curr->data);
		while (hasNext(list) == true) {
			next(list);
			printf("%s\n", (char *) list->curr->data);
		}
	}
}
