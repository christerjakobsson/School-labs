#include <unistd.h>
#include <errno.h>
#include <stdio.h>
#include "execute.h"

/* Duplicate a pipe to a standard I/O file descriptor, and close both pipe ends
 * Arguments:	pip	the pipe
 *		end	tells which end of the pipe shold be dup'ed; it can be
 *			one of READ_END or WRITE_END
 *		destfd	the standard I/O file descriptor to be replaced
 * Returns:	-1 on error, else destfd
 */
int dupPipe(int pip[2], int end, int destfd) {
	if (pip[end] == destfd) { // No need to duplicate
		fprintf(stderr, "No need to dup");
		return destfd;
	}

	if (dup2(pip[end], destfd) < 0) {
		perror("Could not dup pipe");
		return -1;
	}

	if (close(pip[READ_END]) < 0) {
		perror("mish could not close file descriptor");
	}
	if (close(pip[WRITE_END]) < 0) {
		perror("mish could not close file descriptor");
	}
	return destfd;
}

/* Redirect a standard I/O file descriptor to a file
 * Arguments:	filename	the file to/from which the standard I/O file
 * 				descriptor should be redirected
 * 		flags	indicates whether the file should be opened for reading
 * 			or writing
 * 		destfd	the standard I/O file descriptor which shall be
 *			redirected
 * Returns:	-1 on error, else destfd
 */
int redirect(char *filename, int flags, int destfd) {

	char c[1];
	c[0] = (char) flags;
	int fileExists = access(filename, F_OK);

	/* Check file existence. */
	FILE *fp;
	if (fileExists != 0 && c[0] == 'w') {
		if ((fp = fopen(filename, c)) == NULL) {
			perror("mish");
			return -1;
		}
	} else if (fileExists == 0 && c[0] == 'r'
			&& (access(filename, R_OK) == 0)) {

		if ((fp = fopen(filename, c)) == NULL) {
			perror("mish");
			return -1;
		}
	} else if(fileExists == 0 && c[0] == 'w') {
		errno = EEXIST;
		perror("mish");
		return -1;
	} else {
		fprintf(stderr, "Error when opening file: %s\n", filename);
	}
	if (fp == NULL) {
		return -1;
	} else {
		dup2(fileno(fp), destfd);
		fclose(fp);
		return destfd;
	}
}