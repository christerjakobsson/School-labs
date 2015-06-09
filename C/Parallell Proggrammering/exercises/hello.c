#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>
#include <string.h>

/************************************************************
This is a simple send/receive program in MPI
************************************************************/

int main(argc,argv)
int argc;
char *argv[];
{
    int myid, numprocs;
    int tag,source,destination,count;
    int buffer;
    MPI_Status status;
 
    MPI_Init(&argc,&argv);
    MPI_Comm_size(MPI_COMM_WORLD,&numprocs);
    MPI_Comm_rank(MPI_COMM_WORLD,&myid);
    tag=1234;
    
    destination=0;
    buffer=myid;
    count=1;

   

    if(myid > 0) {
    	source=myid;
    }

    if(myid == source && myid > 0){
    	MPI_Send(&buffer,count,MPI_INT,destination,tag,MPI_COMM_WORLD);
      	printf("processor %d  sent %d\n",myid,buffer);
    }
 	
    if(myid == destination){
    	int received = 0;
    	source = 1;
    	while(received < 3) {
        MPI_Recv(&buffer,count,MPI_INT,MPI_ANY_SOURCE,tag,MPI_COMM_WORLD,&status);
        printf("processor %d  got %d\n",myid,buffer);
    	received++;
    	source++;
    	}
    }
    MPI_Finalize();
}