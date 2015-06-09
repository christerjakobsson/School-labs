#include "mpi.h"
#include <stdio.h>
 
int main(int argc, char *argv[])
{
    int myid, numprocs, left, right;
    int buffer[1], buffer2[1];
    MPI_Request request;
    MPI_Status status;

    MPI_Init(&argc,&argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numprocs);
    MPI_Comm_rank(MPI_COMM_WORLD, &myid);
	int i = 0;
	for(i = 0; i < 1; i++) {
		buffer[i] = myid;	
} 
    right = (myid + 1) % numprocs;
    left = myid - 1;
    if (left < 0)
        left = numprocs - 1;
 
    MPI_Sendrecv(buffer, 1, MPI_INT, left, 123, buffer2, 1, MPI_INT, right, 123, MPI_COMM_WORLD, &status);
	printf("Rank %d send %d got %d\n", myid, buffer[0], buffer2[0]); 
    MPI_Finalize();
    return 0;
}

