CC = gcc -std=c99
CFLAGS = -Wall -O0 -g
LDLIBS = -lpthread -lm
SRC = thread-pool.c test.c queue.c
OBJ = $(SRC:.c=.o)
TARGET = test

.PHONY : all
all : $(TARGET)

$(TARGET) : $(OBJ)
	$(CC) $(CFLAGS) -o $(TARGET) $(OBJ) $(LDLIBS)

test.o : test.c thread-pool.h
	$(CC) $(CFLAGS) -c -o $@ $<

thread-pool.o : thread-pool.c thread-pool.h
	$(CC) $(CFLAGS) -c -o $@ $<

.PHONY : clean
clean :
	rm -fv $(TARGET) $(OBJ)
