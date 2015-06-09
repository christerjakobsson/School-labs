/**
 * Systemnära Programmering *
 * HT14
 * Uppgift 4
 *
 * File: sighant.c
 * Author: Christer Jakobsson
 * Username: dv12cjn
 * Date: 2014-10-9
 *
 * Description: Signal handler, used my mish.c to make processes
 * handle SIGINT signal.
 */
#include <stdio.h>
#include <signal.h>
#include "sighant.h"
#include <unistd.h>
#include <sys/wait.h>

extern int processIdArray[MAXCOMMANDS];
extern int nrChild;

/* Signal handler
 * Contains two global variables that represents each childs pid and a iterator
 * for how many children the parent has */
void sighant(int signum) {

	if (signum == SIGINT) {
		int status;
		for (int i = 0; i < nrChild; i++) {
			kill(processIdArray[i], SIGINT);
			if (waitpid(processIdArray[i], &status, 0) < 0) {
				fprintf(stderr, "Error waiting for process %d: ",
						processIdArray[i]);
				perror("");
			}

			nrChild = 0;
		}
	}
}
