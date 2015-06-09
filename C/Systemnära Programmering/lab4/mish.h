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

#ifndef MISH_H_
#define MISH_H_

#include "execute.h"
#include "sighant.h"

int processIdArray[MAXCOMMANDS];
int nrChild;

#endif /* MISH_H_ */
