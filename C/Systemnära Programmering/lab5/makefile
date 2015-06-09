CC=gcc
CFLAGS= -std=gnu99 -Wall

all: mfind_p

mfind_p: linkedlist.o mfind_p.o
	$(CC)  linkedlist.o mfind_p.o -o mfind_p $(CFLAGS) -pthread

linkedlist.o: linkedlist.c linkedlist.h
	$(CC) linkedlist.c  -c $(CFLAGS) -pthread

mfind_p.o: linkedlist.o mfind_p.c mfind_p.h
	$(CC) linkedlist.c mfind_p.c -c $(CFLAGS) -pthread

clean:
	rm -rf *.o mfind_p
