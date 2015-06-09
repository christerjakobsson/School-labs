#include <stdio.h>
#include <stdlib.h>

/** A recursive function that changes values on increases and decreases two
 * two variables.
*
* A is a recursive function that recurses forever or until program is stopped.
* The m and n values gets manipulated at each call and the values are increased
* or decreased.
* if m == 0 at the first call the function returns n+1 else it will keep
* recursing until stopped.
*
* @param m A long that gets calculated on.
* @param n A long that gets calculated on.
* @return long depending on values of m and n.
*/
long A(long m, long n)
{
	if (m == 0) return n + 1;.
	if (n == 0) return A(m - 1, 1);
	return A(m - 1, A(m, n - 1));
}

/** Main checks for correct arguments and calls function A.
 *
 * Main function that gets called at program execution, checks that the
 * arguments given to the program is as the program wants it. Needs
 * more then 2 arguments to be able to run. If two or more arguments were given
 * will print the return value from A with the two first arguments made
 * to long int.
 *
 * @param argc Number of arguments given to program.
 * @param argv An array of strings for each argument to the program.
 *
 * return 0 on success.
 */
int main(int argc, char *argv[])
{
	if (argc > 2) {
		printf("%ld\n", A(atol(argv[1]), atol(argv[2])));
	} else {
		fprintf(stderr, "Usage: %s number number\n", argv[0]);
	}
	return 0;
}
