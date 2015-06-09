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
#include <semaphore.h>
#include <pthread.h>
#include <errno.h>
#include "linkedlist.h"

bool compareName(char *firstVal, char *secVal);
bool checkDir(int t, struct stat f_info, char* d_name, char* name);
bool checkReg(int t, struct stat f_info, char* d_name, char* name);
bool checkLink(int t, struct stat f_info, char* d_name, char* name);
bool isDot(char* name);
void addToList(linkedList dirList, char* name);
char* getPath(linkedList dirList);
char* addPath(char *name, char *path);
void *searchDir(void *data);
static linkedList dirList;

void removeFileName(char *buf, char *fileToFind);
//void print_list(struct List* list);
void addDirToList(char* name, struct List* paths);

int checkDirAndRights(struct stat f_info);
static int nrOfThreads;
static int threadsComplete;
static char* fileToSearch;
static pthread_mutex_t lock;
static int fileType;
static sem_t semafor;
static sem_t initSem;
static int* threadSearchCount;
static bool allThreadsComplete;
/**
 * Main, uses getopt to check for -t arguments, puts all filepath arguments in a list
 * and starts to search through each filepath for the file until the list is empty.
 * Then prints the list with all hits and frees all allocated memory.
 */
int main(int argc, char *argv[]) {

	if (argc < 3) {
		fprintf(stderr, "Too few arguments, exiting...\n");
		exit(EXIT_FAILURE);
	}
	int c;
	nrOfThreads = 0;
	fileType = 0;
	threadsComplete = 0;
	if (pthread_mutex_init(&lock, NULL) != 0) {
		printf("\n mutex init failed\n");
		exit(1);
	}

	while ((c = getopt(argc, argv, "t:p:")) != -1) {
		char *str = optarg;
		switch (c) {
			case 't':
				if (*str == 'd') {
					fileType = 1;
				} else if (*str == 'f') {
					fileType = 2;
				} else if (*str == 'l') {
					fileType = 3;
				} else {
					fprintf(stderr, "Wrong argument to -t, exiting...");
					exit(EXIT_FAILURE);
				}
				break;
			case 'p': {
				char *pt;
				errno = 0;
				nrOfThreads = strtol(optarg, &pt, 10) - 1;
				if (pt == optarg) {
					fprintf(stderr, "Wrong argument to -p, exiting...\n");
					exit(EXIT_FAILURE);
				}
				if ((nrOfThreads == LONG_MAX || nrOfThreads == LONG_MIN || nrOfThreads)
						&& errno == ERANGE) {
					fprintf(stderr, "Value out of range, exiting\n");
					exit(EXIT_FAILURE);
				} else if (nrOfThreads+1 > 10000) {
					fprintf(stderr, "Error: to many threads, Max is 10000\n");
					exit(1);
				}

				break;
			}
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



	threadSearchCount = calloc(nrOfThreads + 1, sizeof(int));

	sem_init(&semafor, 0, 1);
	sem_init(&initSem, 0, 0);
	dirList = empty();
	getFirst(dirList);
	allThreadsComplete = false;

	fileToSearch = argv[argc - 1];

	// Add path arguments to dirList
	char *dirToSearch;
	while (optind < argc - 1) {
		dirToSearch = calloc(1, sizeof(char[strlen(argv[optind]) + 1]));
		strcpy(dirToSearch, argv[optind]);
		insert(dirList, dirToSearch);
		optind++;
	}

	int mainNr = 0;

	if (nrOfThreads >= 1) {
		pthread_t threads[nrOfThreads];

		int rc[nrOfThreads];
		for (int i = 0; i < nrOfThreads; i++) {
			rc[i] = i + 1;
			if (pthread_create(&threads[i], NULL, &searchDir, &rc[i])) {
				fprintf(stderr, "Error creating thread\n");
				exit(1);
			}
		}

		for(int i = 0; i <= nrOfThreads+1; i++) {
			sem_post(&initSem);
		}

		searchDir(&mainNr);

		for (int i = 0; i < nrOfThreads; i++) {
			pthread_join(threads[i], NULL);
		}

	} else {
		searchDir(&mainNr);
	}

	for (int i = 0; i < nrOfThreads + 1; i++) {
		fprintf(stderr,"Thread searched %d directories\n", threadSearchCount[i]);
	}

	freeLinkedList(dirList);
	free(threadSearchCount);
	pthread_mutex_destroy(&lock);
	return 0;
}

/**
 * Locks the mutex and inserts a path into the list.
 * Then unlocks the mutex
 */
void addToList(linkedList dirList, char* path) {
	pthread_mutex_lock(&lock);
	insert(dirList, path);
	pthread_mutex_unlock(&lock);

}

/**
 * Locks the mutex then gets the heads data and stores it in a
 * char pointer, unlocks the mutex and returns the pointer.
 * If the lists head is null: NULL is returned.
 */
char* getPath(linkedList dirList) {
	char *d = NULL;
	pthread_mutex_lock(&lock);
	if (dirList->head != NULL) {
		int len = strlen((char*) dirList->head->data);
		d = malloc(sizeof(char[len + 1]));
		strcpy(d, (char*) dirList->head->data);
		remove_node(dirList->head, dirList);
	}
	pthread_mutex_unlock(&lock);
	return d;
}

/**
 * Concatenates the path and the filename into a string.
 */
char* addPath(char *name, char* path) {
	int nameLen = strlen(name);
	int pathLen = strlen(path);
	char* fullPath = calloc((size_t) (nameLen + pathLen + 2), sizeof(char));
	strcat(fullPath, path);
	strcat(fullPath, "/");
	strcat(fullPath, name);
	return fullPath;
}

/**
 * Reads all the files in a directory and checks if the file is the one to be
 * found and is the same filetype, if so printfs the path.
 * If the file read is a directory it is added to the list.
 */
char* readFilesInDir(int fileType, linkedList dirList,
		struct dirent* ent, char* name, struct stat* f_info, DIR* dir) {
	do {
		char* fullPath = addPath(ent->d_name, name);
		if ((lstat(fullPath, f_info)) < 0) {
			fprintf(stderr, "lstat error: ");
			perror(ent->d_name);
		} else {
			if (fileType == 0 && compareName(ent->d_name, fileToSearch)) {
				printf("%s/%s\n", name,fileToSearch);
			} else if (checkDir(fileType, *f_info, ent->d_name, fileToSearch)) {
				printf("%s/%s\n", name,fileToSearch);
			} else if (checkReg(fileType, *f_info, ent->d_name, fileToSearch)) {
				printf("%s/%s\n", name, fileToSearch);
			} else if (checkLink(fileType, *f_info, ent->d_name,
					fileToSearch)) {
				printf("%s/%s\n", name,fileToSearch);
			}
			if (checkDirAndRights(*f_info) && !isDot(ent->d_name)) {
				sem_post(&semafor);
				addToList(dirList, fullPath);
			} else {
				free(fullPath);
			}
		}
	} while ((ent = readdir(dir)));
	return name;
}

/**
 * Used by each thread to go through the list, picks a path from the list
 * and searches in that directory, if a thread finds that the list is empty the
 * threads calls sem_wait and waits until another thread have added a
 * directory to the list.
 * If all threads finds that the list is empty the threads have traversed
 * the whole file structure, prints how many files they made opendir on.
 */
void* searchDir(void *data) {
	int dirs = 0;
	char* name = "";
	int threadNr = *((int*) data);

	sem_wait(&initSem);
	pthread_mutex_lock(&lock);

	while (!allThreadsComplete) {
		threadsComplete++;
		pthread_mutex_unlock(&lock);
		sem_wait(&semafor);
		pthread_mutex_lock(&lock);
		threadsComplete--;

		pthread_mutex_unlock(&lock);
	
		/*	
		 * Go through the list and search in each directory,
		 * add new directories to the list if found
		 */
		while (!isEmpty(dirList)) {
			name = getPath(dirList);
			dirs++;

			if (name != NULL) {
				DIR *dir;

				struct dirent *ent;
				struct stat f_info;
				bool keepOn = true;

				if (!(dir = opendir(name))) {
					fprintf(stderr, "cannot read dir ");
					perror(name);
					keepOn = false;
				}
				if (keepOn && (!(ent = readdir(dir)))) {
					fprintf(stderr, "cannot read file in dir ");
					perror(name);
					keepOn = false;
				}

				if (keepOn) {
				
					name = readFilesInDir(fileType, dirList, ent,
							name, &f_info, dir);
					if (closedir(dir) == -1) {
						perror("Could not close file");
					}
				}

				free(name);
			}
		}
		
		pthread_mutex_lock(&lock);
		int semVal;
		sem_getvalue(&semafor, &semVal);
		if(semVal == 0 && nrOfThreads == threadsComplete && isEmpty(dirList)) {
			allThreadsComplete = true;
			for(int i = 0; i <= nrOfThreads; i++) {
				sem_post(&semafor);
			}
		}
	}
	threadSearchCount[threadNr] = dirs;
	pthread_mutex_unlock(&lock);
	return NULL;
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
 * Add a string representing the full path of the directory where the file was found.
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

/**
 * Searches in the "name" directory for the "find" file,
 * if file is found and the user wanted to find that filetype
 * the path is added to a list that will be printed at the end.
 * Also puts all directories found in a list for it to be
 * searched.
 */

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
