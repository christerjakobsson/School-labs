/*
 * Systemnära programmering
 * HT14
 * Uppgift 3

 * Fil: mfind.c
 * Author: Christer Jakobsson
 * Username: dv12cjn
 * Datum: 28 September 2014
 *
 * Description: Searches the file system for a file given as argument.
 * Optionally takes a option for which type of file to search for.
 * Takes filepaths which specified where to search, and searches the
 * underlying file structure on each filepath.
 * Prints the filepaths where the file was found.
 *
 */

#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <limits.h>
#include <ctype.h>
#include <unistd.h>
#include "linkedlist.h"

bool searchForFile(char* name, char *find, int t, struct Node* list);
bool compareName(char *firstVal, char *secVal);
void removeFileName(char *buf, char *fileToFind);
bool checkDir(int t, struct stat f_info, char* d_name, char* name);
bool checkReg(int t, struct stat f_info, char* d_name, char* name);
bool checkLink(int t, struct stat f_info, char* d_name, char* name);
bool isDot(char* name);
void print_list(struct Node* list);
void addDirToList(char* name, struct Node* paths);

/**
 * Main, uses getopt to check for -t arguments, puts all filepath arguments in a list
 * and starts to search through each filepath for the file until the list is empty.
 * Then prints the list with all hits and frees all allocated memory.
 */
int main(int argc, char *argv[]) {

	int c;
	int fileType = 0;
	int firstArg = 1;

	while ((c = getopt(argc, argv, "t:")) != -1) {
		char* str = optarg;

		switch (c) {
		case 't':
			firstArg = optind;
			if (*str == 'd') {
				fileType = 1;
			} else if (*str == 'f') {
				fileType = 2;
			} else if (*str == 'l') {
				fileType = 3;
			}
			break;
		case '?':
			if (optopt == 'c')
				fprintf(stderr, "Option -%c requires an argument.\n", optopt);
			else if (isprint(optopt))
				fprintf(stderr, "Unknown option `-%c'.\n", optopt);
			else
				fprintf(stderr, "Unknown option character `\\x%x'.\n", optopt);
			return 1;
		default:
			abort();
		}
	}

	// Create linked lists to store paths.
	linkedList dirList = empty();
	linkedList dirsWithFile = empty();

	// Add path arguments to dirList
	char *searchDir;
	while (firstArg < argc - 1) {
		searchDir = calloc(1, sizeof(char[strlen(argv[firstArg]) + 1]));
		strcpy(searchDir, argv[firstArg]);
		insert(dirList, searchDir);
		firstArg++;
	}

	// Search for file in each path.
	linkedList node = dirList;
	while (node->next != NULL) {
		node = node->next;
		if (searchForFile((char*) node->data, argv[argc - 1], fileType,
				dirList)) {
			addDirToList((char*) node->data, dirsWithFile);
		}
	}

	// Print paths where file was found
	print_list(dirsWithFile);

	freeLinkedList(dirList);
	freeLinkedList(dirsWithFile);

	return 0;
}

/**
 * Removes the file name from the path string.
 */
void removeFileName(char *buf, char *fileToFind) {
	for (int i = strlen(buf) - strlen(fileToFind); i < strlen(buf); i++) {
		buf[i] = '\0';
	}
}

/**
 * Opens the directory "name" and reads the first entry in it, changes the working directory to the same directory.
 * prints error message if failing and return false, meaning skip reading this directory.
 */
bool openDir(DIR** dir, char* name, struct dirent** ent) {
	bool keepOn = true;
	if (!(*dir = opendir(name))) {
		fprintf(stderr, "cannot read dir ");
		perror(name);
		keepOn = false;
	}
	if (keepOn && (!(*ent = readdir(*dir)))) {
		fprintf(stderr, "cannot read file in dir ");
		perror(name);
		keepOn = false;
	}
	if (keepOn && chdir(name) != 0) {
		fprintf(stderr, "cant change working directory to %s", name);
		perror("");
		keepOn = false;
	}
	return keepOn;
}

/**
 * Add a string representing the full path of the directory where the file was found.
 */
void addDirToList(char* name, struct Node* paths) {
	char *string = calloc(1, sizeof(char[strlen(name)]) + 1);
	strcpy(string, name);
	insert(paths, string);
}

/**
 * Checks if a directory and that the user have read rights.
 */
int checkDirAndRights(struct stat f_info) {
	return S_ISDIR(f_info.st_mode) && (f_info.st_mode & S_IRUSR);
}

/**
 * Searches in the "name" directory for the "find" file,
 * if file is found and the user wanted to find that filetype
 * the path is added to a list that will be printed at the end.
 * Also puts all directories found in a list for it to be
 * searched.
 */
bool searchForFile(char* name, char *find, int t, struct Node* list) {
	DIR *dir;

	struct dirent *ent;
	struct stat f_info;
	bool foundFile = false;

	if (openDir(&dir, name, &ent)) {
		do {
			if ((lstat(ent->d_name, &f_info)) < 0) {
				fprintf(stderr, "lstat error: ");
				perror(ent->d_name);
			} else {
				if (t == 0 && compareName(ent->d_name, find)) {
					foundFile = true;
				} else if (checkDir(t, f_info, ent->d_name, find)) {
					foundFile = true;
				} else if (checkReg(t, f_info, ent->d_name, find)) {
					foundFile = true;
				} else if (checkLink(t, f_info, ent->d_name, find)) {
					foundFile = true;
				}
				if (checkDirAndRights(f_info) && !isDot(ent->d_name)) {
					int l = strlen(name) + strlen(ent->d_name);
					char *str = calloc(1, sizeof(char[l + 2]));

					strcpy(str, name);
					strcat(str, "/");
					strcat(str, ent->d_name);
					insert(list, str);
				}
			}
		} while ((ent = readdir(dir)));

	}
	closedir(dir);

	return foundFile;
}

/**
 * Checks if file is a directory and that the user searches for this filetype
 * and if the name for the file is the same as the name file searched for.
 */
bool checkDir(int t, struct stat f_info, char* d_name, char* name) {
	return (t == 1 && S_ISDIR(f_info.st_mode) && compareName(d_name, name));
}

/**
 * Checks if file is a regular file and that the user searches for this filetype
 * and if the name for the file is the same as the name file searched for.
 */
bool checkReg(int t, struct stat f_info, char* d_name, char* name) {
	return (t == 2 && S_ISREG(f_info.st_mode) && compareName(d_name, name));
}

/**
 * Checks if file is a link and that the user searches for this filetype
 * and if the name for the file is the same as the name file searched for.
 */
bool checkLink(int t, struct stat f_info, char* d_name, char* name) {
	return (t == 3 && S_ISLNK(f_info.st_mode) && compareName(d_name, name));
}

/**
 * Checks if the filename is "." or ".." returns true if it is
 */
bool isDot(char* name) {
	return compareName(name, ".") || compareName(name, "..");
}

/**
 * Compares two strings and returns true if they are equal.
 */
bool compareName(char *firstVal, char *secVal) {
	return (strcmp(firstVal, secVal) == 0);
}

/* Prints the list */
void print_list(struct Node* list) {
	struct Node* node = list;
	if (!isEmpty(node)) {
		while ((node = getNext(node)) != NULL) {
			printf("%s\n", (char *) node->data);
		}
	}
}
