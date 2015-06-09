CC = gcc -std=c99 -fopenmp
CFLAGS = -Wall -O0 -g -O3 -march=native
LDLIBS = -lm
SRC = mandel-basic.c mandel-magic.c common.c
OBJ = $(SRC:.c=.o)
TARGETS = mandel-basic mandel-magic

.PHONY : all
all : $(TARGETS)

mandel-basic : mandel-basic.o common.o
	$(CC) $(CFLAGS) -o $@ $^ $(LDLIBS)

mandel-magic : mandel-magic.o common.o
	$(CC) $(CFLAGS) -o $@ $^ $(LDLIBS)

mandel-basic.o : mandel-basic.c common.h
	$(CC) $(CFLAGS) -o $@ -c $<

mandel-magic.o : mandel-magic.c common.h
	$(CC) $(CFLAGS) -o $@ -c $<

common.o : common.c common.h
	$(CC) $(CFLAGS) -o $@ -c $<

.PHONY : clean
clean :
	rm -fv $(TARGETS) $(OBJ) image.ppm
