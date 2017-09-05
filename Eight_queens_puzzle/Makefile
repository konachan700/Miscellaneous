CC	= gcc
LD	= gcc
OPT 	= -O2 -s 
CFLAGS 	= $(OPT) -c
LDFLAGS	= -DPTW32_STATIC_LIB $(OPT)
LIBS	= -lrt

all:
	$(CC) $(CFLAGS) test.c -o test.o
	$(LD) $(LDFLAGS) test.o $(LIBS) -o test

clean: 
	rm -f *.o test
