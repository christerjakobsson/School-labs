/*
 * Systemnära programmering
 * HT14
 * Uppgift 3

 * File: mfind.c
 * Author: Christer Jakobsson
 * Username: dv12cjn
 * Date: 28 September 2014
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
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/stat.h>
#include <ctype.h>
#include "linkedlist.h"

void searchForFile(char* name, char *find, int t, struct List* list);
bool compareName(char *firstVal, char *secVal);
void removeFileName(char *buf, char *fileToFind);
bool checkDir(int t, struct stat f_info, char* d_name, char* name);
bool checkReg(int t, struct stat f_info, char* d_name, char* name);
bool checkLink(int t, struct stat f_info, char* d_name, char* name);
bool isDot(char* name);
void print_list(struct List* list);
void addDirToList(char* name, struct List* paths);

/**
 * Main, uses getopt to check for -t arguments, puts all filepath arguments in a list
 * and starts to search through each filepath for the file until the list is empty.
 * Then prints the list with all hits and frees all allocated memory.
 */
int main(int argc, char *argv[]) {

	if(argc < 3) {
		fprintf(stderr,"Wrong amount of arguments (Min 3)\n");
		exit(EXIT_FAILURE);
	}

	int c;
	int fileType = 0;

	while ((c = getopt(argc, argv, "t:")) != -1) {
		char* str = optarg;

		switch (c) {
		case 't':
			if (*str == 'd') {
				fileType = 1;
			} else if (*str == 'f') {
				fileType = 2;
			} else if (*str == 'l') {
				fileType = 3;
			} else {
				fprintf(stderr,
						"Wrong argument to -t, doing a default search\n");
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
			exit(EXIT_FAILURE);
		}
	}

	// Create linked lists to store paths.
	linkedList dirList = empty();

	// Add path arguments to dirList
	char *searchDir;
	for (int i = optind; i < argc - 1; i++) {
		searchDir = calloc(strlen(argv[i]) + 1, sizeof(char));
		strcpy(searchDir, argv[i]);
		insert(dirList, searchDir);
	}

	/*
	 * Go through the list and search in each directory,
	 * add new directories to the list if found
	 */
	while (!isEmpty(dirList)) {
		(searchForFile((char*) dirList->head->data, argv[argc - 1], fileType,
				dirList));
		remove_node(dirList->head, dirList);
	}

	freeLinkedList(dirList);
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
 * Opens the directory "name" and reads the first entry in it, changes the
 * working directory to the same directory.
 * prints error message if failing and return false,
 * meaning skip reading this directory.
 */
bool openDir(DIR** dir, char* name, struct dirent** ent) {
	if (!(*dir = opendir(name))) {
		fprintf(stderr, "cannot read dir ");
		perror(name);
		return false;
	} else {
		if (!(*ent = readdir(*dir))) {
			fprintf(stderr, "cannot read file in dir ");
			perror(name);
			closedir(*dir);
			return false;
		}
	}
	return true;
}

/**
 * Add a string representing the full path of the directory
 * 	where the file was found.
 */
void addDirToList(char* name, struct List* paths) {
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

char* addPath(char *name, char* path) {
	char* fullPath = calloc(strlen(name) + strlen(path) + 2, sizeof(char));
	strcat(fullPath, path);
	strcat(fullPath, "/");
	strcat(fullPath, name);
	return fullPath;
}

/**
 * Searches in the "name" directory for the "find" file,
 * if file is found and the user wanted to find that filetype
 * the path is added to a list that will be printed at the end.
 * Also puts all directories found in a list for it to be
 * searched.
 */
void searchForFile(char* name, char *find, int t, struct List* list) {
	DIR *dir;

	struct dirent *ent;
	struct stat f_info;

	if (openDir(&dir, name, &ent)) {
		do {
			char* fullpath = addPath(ent->d_name, name);
			if ((lstat(fullpath, &f_info)) < 0) {
				fprintf(stderr, "lstat error: ");
				perror(ent->d_name);
			} else {
				if (t == 0 && compareName(ent->d_name, find)) {
					printf("%s\n", name);
				} else if (checkDir(t, f_info, ent->d_name, find)) {
					printf("%s\n", name);
				} else if (checkReg(t, f_info, ent->d_name, find)) {
					printf("%s\n", name);
				} else if (checkLink(t, f_info, ent->d_name, find)) {
					printf("%s\n", name);
				}
				if (checkDirAndRights(f_info) && !isDot(ent->d_name)) {
					insert(list, fullpath);
				} else {
					free(fullpath);
				}
			}
		} while ((ent = readdir(dir)));

		if (closedir(dir) == -1) {
			perror("Could not close file");
		}
	}
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
void print_list(struct List* list) {
	getFirst(list);
	if (!isEmpty(list)) {
		while (!isEnd(list)) {
			printf("%s\n", (char *) list->curr->data);
			next(list);
		}
		printf("%s\n", (char *) list->curr->data);
	}
}
