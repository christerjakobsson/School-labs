CC=gcc
CFLAGS= -std=gnu99 -Wall

all: mfind clean

mfind: mfind.c linkedlist.c
	$(CC) -o mfind mfind.c linkedlist.c $(CFLAGS)

clean: 
	rm -rf *.o
