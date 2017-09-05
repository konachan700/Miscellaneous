MAKEFLAGS += --no-print-directory

CC     	?= "gcc"
LD     	?= "ld"
CFLAGS 	?= -O2 -g 
LIBS	+= -lrt

prefix = /usr

all:
	$(CC) $(CPPFLAGS) $(CFLAGS) $(LIBS) ndisp.c -c -o ndisp.o 
	$(CC) $(LDFLAGS) ndisp.o $(LIBS) -o ndisp

clean: 
	rm -f *.o ndisp

install:
	cp ./ndisp $(DESTDIR)$(prefix)/bin/display-daemon
	chmod +x $(DESTDIR)$(prefix)/bin/display-daemon
