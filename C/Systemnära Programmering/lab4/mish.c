/**
 * Systemnära Programmering *
 * HT14
 * Uppgift 4
 *
 * File: mish.c
 * Author: Christer Jakobsson
 * Username: dv12cjn
 * Date: 2014-10-9
 *
 * Description: The handler for a program that acts as a mini-shell and can run
 * external commands such as run commands and pipe the data between them and
 * also to use files as input or output for a command. Also implements some
 * basic internal commands such as cd and echo.
 */

#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>

#include "mish.h"
#include "execute.h"

bool startSignalHandler();
void shellPrompt(char* currentDirectory);
void getTextLine(char userInput, char* comLine);
void processCommands(command* comLine, int size);
void runCdCommand(command com);
void runEchoCommand(command com);
void executeCommand(command com);

/**
 * Registers a signal handler for the program.
 * Handles the input from the user, parses commands sends them to
 * processCommands
 */
int main(void) {

	if (!startSignalHandler()) {
		exit(EXIT_FAILURE);
	}

	command com[MAXCOMMANDS];
	char currentDirectory[1024];
	char string[MAXLINELEN];

	shellPrompt(currentDirectory);
	while (fgets(string, sizeof(string), stdin) != NULL) {
		int commandSize = parse(string, com);
		if (commandSize == 0) {
			fprintf(stderr, "Error: incorrect line %s\n", string);
		} else if (commandSize == 1) {
			if (strcmp(com[0].argv[0], "cd") == 0) {
				com[0].internal = 1;
			} else if (strcmp(com[0].argv[0], "echo") == 0) {
				com[0].internal = 2;
			} else {
				com[0].internal = 0;
			}

			processCommands(com, commandSize);
		} else {
			processCommands(com, commandSize);
		}
		shellPrompt(currentDirectory);
	}

	printf("\n");
	return 0;
}

bool startSignalHandler() {
	if (signal(SIGINT, sighant) == SIG_ERR) {
		fprintf(stderr, "Error, couldn't register SIGINT\n");
		perror("mish");
		return false;
	} else
		return true;
}

/**
 * Runs a internal command that works like echo.
 */
void runEchoCommand(command com) {
	for (int i = 1; i < com.argc - 1; i++) {
		printf("%s ", com.argv[i]);
	}
	printf("%s", com.argv[com.argc - 1]);
	printf("\n");
}

/**
 * Runs a internal commands that works like cd
 */
void runCdCommand(command com) {
	if (com.argc == 1) {
		chdir(getenv("HOME"));
	} else {
		if (chdir(com.argv[1]) == -1) {
			perror("mish");
		}
	}
}

/**
 * Processes commands and forks once for each command. redirects the stdout
 * and stdin according to how the whole commandline looked like.
 */
void processCommands(command* comLine, int size) {
	int fdPrevChild = fileno(stdin);
	nrChild = 0;

	for (int i = 0; i < size; i++) {
		command com = comLine[i];

		if (com.internal == 1) {
			runCdCommand(com);
		} else if (com.internal == 2) {
			runEchoCommand(com);
		} else {

			int fd[2];
			pipe(fd);

			switch (processIdArray[i] = fork()) {
			case -1:
				fprintf(stderr, "Could not fork");
				perror("mish");
				exit(EXIT_FAILURE);
				break;
			case 0:
				if (dup2(fdPrevChild, 0) < 0) {
					perror("Could not dup pipe");
					exit(EXIT_FAILURE);
				}

				if (nrChild + 1 != size) {
					if (dupPipe(fd, WRITE_END, fileno(stdout)) < 0) {
						exit(EXIT_FAILURE);
					}
				}

				if (com.infile != NULL) {
					if (redirect(com.infile, 'r', fileno(stdin)) == -1) {
						exit(EXIT_FAILURE);
					}
				}
				if (com.outfile != NULL) {
					if (redirect(com.outfile, 'w', fileno(stdout)) == -1) {
						exit(EXIT_FAILURE);
					}
				}

				executeCommand(com);
				break;
			default:
				nrChild++;
				if (close(fd[1]) < 0) {
					perror("mish could not close file descriptor");
				}
				fdPrevChild = fd[0];
				break;
			}
		}
	}

	int status;
	for (int i = 0; i < nrChild; i++) {
		if (waitpid(processIdArray[i], &status, 0) < 0) {
			fprintf(stderr, "Error waiting for process %d: 	",
					processIdArray[i]);
			perror("");
		}
	}
}

/**
 * Executes a system call with its arguments.
 */
void executeCommand(command com) {
	execvp(com.argv[0], com.argv);
	perror("mish failed to execute external command");
	exit(EXIT_FAILURE);
}

/**
 * Gets the whole command line the user has entered and store it in a Char[]
 * Used getchar to get a character at a time and put it in the comLine array
 *
 */
void getTextLine(char userInput, char* comLine) {
	int bufferChars = 0;

	while ((userInput != '\n') && (bufferChars < MAXLINELEN)) {
		comLine[bufferChars++] = userInput;
		userInput = getchar();
	}
	comLine[bufferChars] = '\0';
}

/**
 * Prints a prompt with current working directory.
 */
void shellPrompt(char* currentDirectory) {
	fprintf(stderr, "%s:mish%% ", getcwd(currentDirectory, 1024));
	fflush(stderr);
}
