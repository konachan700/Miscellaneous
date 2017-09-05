CC	= gcc
LD	= gcc
OPT 	= -O1 -s 
CFLAGS 	= $(OPT) -c
LDFLAGS	= -DPTW32_STATIC_LIB $(OPT)
LIBS	= -lrt

all:
	$(CC) $(CFLAGS) hello.c -o hello.o
	$(LD) $(LDFLAGS) hello.o $(LIBS) -o hello

clean: 
	rm -f *.o hello
