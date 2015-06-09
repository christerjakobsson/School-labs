CC = gcc -std=c99
CFLAGS = -O0 -Wall -g
LDLIBS = -lpthread -lm
SRC = main.c
OBJ = $(SRC:.c=.o)
TARGET = test

.PHONY : all
all : $(TARGET)

$(TARGET) : $(OBJ)
	$(CC) $(CFLAGS) -o $@ $(OBJ) $(LDLIBS)

%.o : %.c
	$(CC) $(CFLAGS) -c -o $@ $<

.PHONY : clean
clean :
	rm -fv $(TARGET) $(OBJ)
