/*
 * Systemnära programmering
 * HT14
 * Uppgift 1

 * Fil: mpasswdsort.c
 * Author: Christer Jakobsson
 * Username: dv12cjn
 * Datum: 8 September 2014
 *
 * Description: Takes a filename either as argument or
 * from stdin and takes the uuid and the username.
 * Puts them in a datatype representing a linked list,
 * sorts the list by uuid and prints the sorted list.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "linkedlist.h"

void print_list(struct Node* node);
void addLinesToList(FILE *f, struct Node* list);
void bubbleSort(linkedList start);
void swap(linkedList a, linkedList b);
FILE* openFile(int argc, char *argv[], FILE *fp);

/* Main that runs executes the program sequence */
int main(int argc, char *argv[]) {
	FILE *fp = NULL;

	// Open file
	fp = openFile(argc, argv, fp);

	//Create list
	linkedList passwdList = empty();

	// Add each line to the list
	addLinesToList(fp, passwdList);

	// Close the file
	fclose(fp);

	// Sort the list by uuid
	bubbleSort(passwdList);

	// Print the list
	print_list(passwdList);

	// Free the allocated memory
	freeLinkedList(passwdList);

	return 0;
}

/* Opens the file, either from argument or from stdin
 * Exits the program if the file failed to open.
 */
FILE* openFile(int argc, char *argv[], FILE *fp) {

	if (argc == 1) {
		fp = stdin;
	} else if (argc == 2) {
		fp = fopen(argv[1], "r");
	} else {
		fprintf(stderr, "Wrong amount of arguments");
		exit(1);
	}

	if (!fp) {
		perror("Error");
		exit(1);
	}
	return fp;
}

/* Adds the uuid and the username from the each row
 * if the row is in wrong format the data is thrown away.
 */
void addLinesToList(FILE *f, struct Node* list) {
	char string[1024];
	int line = 0;
	while (fgets(string, sizeof(string), f) != NULL) {
		line++;
		int i = 0;
		char *tok = string;

		linkedList p = malloc(sizeof(struct Node));
		p->uuid = 0;
		p->next = NULL;
		p->uname = NULL;
		int num = -1;

		while ((tok = strtok(tok, ":")) != NULL) {

			if (i == 2) {
				num = atoi(tok);
				if ((num == 0 && tok[0] != '0')
						|| (num == 0 && (strlen(tok) > 1))) {
					num = -1;
				} else {
					p->uuid = num;
				}
			}
			if (i == 0) {
				int l = strlen(tok);
				if (!strchr("!#¤%&/()=?;:*<>\"", *tok) && l != 0) {
					p->uname = malloc(sizeof(char[l]) + 1);
					strcpy(p->uname, tok);
				}
			}
			tok = NULL;
			i++;
		}
		if (p == NULL || p->uname == NULL || num < 0 || i > 7) {
			fprintf(stderr, "ERROR: Line %d have invalid format, ignoring...\n",
					line);
			if (p->uname != NULL) {
				free(p->uname);
			}
			free(p);
		} else {
			if (!insert(list, p)) {
				fprintf(stderr, "Could not add to list");
			}
		}
	}
}

/* Sorts the linked list */
void bubbleSort(linkedList start) {
	bool swapped;

	linkedList node_one;
	linkedList node_two = NULL;

	/* Checking for empty list */

	if (start == NULL) {
		return;
	}

	do {
		swapped = false;
		node_one = start;

		while (node_one->next != node_two) {
			if (node_one->uuid > node_one->next->uuid) {
				swap(node_one, node_one->next);
				swapped = true;
			}
			node_one = node_one->next;
		}
		node_two = node_one;
	} while (swapped);
}

/* function to swap data of two nodes a and b */
void swap(linkedList a, linkedList b) {
	int temp = a->uuid;
	char *tempS = a->uname;

	a->uname = b->uname;
	b->uname = tempS;

	a->uuid = b->uuid;
	b->uuid = temp;
}

/* Prints the list */
void print_list(struct Node* node) {
	node = node->next;
	if (node != NULL) {
		while (node->next != NULL) {
			printf("%d:%s\n", node->uuid, node->uname);
			node = node->next;
		}
		printf("%d:%s\n", node->uuid, node->uname);
	}
}
