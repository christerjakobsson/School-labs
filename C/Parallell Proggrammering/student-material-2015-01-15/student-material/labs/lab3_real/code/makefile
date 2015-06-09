CC = mpicc -std=c99
CFLAGS = -Wall -O0 -g
LDLIBS = -lm
SRC = gol.c test.c
OBJ = $(SRC:.c=.o)
TARGET = gol

.PHONY : all
all : $(TARGET)

gol : $(OBJ)
	$(CC) -o $@ $(OBJ) $(LDLIBS)

test.o : test.c gol.h
	$(CC) -o $@ -c $<

gol.o : gol.c gol.h
	$(CC) -o $@ -c $<

.PHONY : clean
clean :
	rm -fv $(TARGET) $(OBJ)
