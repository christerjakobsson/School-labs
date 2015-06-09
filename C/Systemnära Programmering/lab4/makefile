CC=gcc
CFLAGS= -g -std=gnu99 -Wall

all:  mish

mish: mish.o parser.o execute.o sighant.o 
	$(CC) -o mish mish.o parser.o sighant.o execute.o $(CFLAGS)

mish.o: mish.c mish.h
	$(CC) -c mish.c $(CFLAGS)

parser.o: parser.c parser.h
	$(CC) -c parser.c $(CFLAGS)

sighant.o: sighant.c sighant.h
	$(CC) -c sighant.c $(CFLAGS)

execute.o: execute.h execute.c
	$(CC) -c execute.c $(CFLAGS)
clean: 
	rm -rf *.o
