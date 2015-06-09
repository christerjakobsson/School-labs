CC = gcc -std=c99
CFLAGS = -Wall -g
LDLIBS = -lpthread -lm
SRC = main.c
OBJ = $(SRC:.c=.o)
TARGETS = test0 test1 test2

.PHONY : all
all : $(TARGETS)

test0 : main.c
	$(CC) $(CFLAGS) -O0 -o $@ $< $(LDLIBS)

test1 : main.c
	$(CC) $(CFLAGS) -O1 -o $@ $< $(LDLIBS)

test2 : main.c
	$(CC) $(CFLAGS) -O2 -o $@ $< $(LDLIBS)

.PHONY : clean
clean :
	rm -fv $(TARGETS) $(OBJ)
