/**
 * Systemnära Programmering *
 * HT14
 * Uppgift 4
 *
 * File: sighant.h
 * Author: Christer Jakobsson
 * Username: dv12cjn
 * Date: 2014-10-9
 *
 * Description: Signal handler, used my mish.c to make processes
 * handle SIGINT signal.
 */
#ifndef _SIGHANT_
#define _SIGHANT_

#include "parser.h"

/* Signal handler
 * Contains two global variables that represents each childs pid and a iterator
 * for how many children the parent has */
void sighant(int signum);

int processIdArray[MAXCOMMANDS];
int nrChild;
#endif

