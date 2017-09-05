APP_NAME=remcam
BIN_PATH=./bin/$(APP_NAME)
SRC_FILES=`ls *.c`

CC=gcc
CFLAGS+=-ljpeg

all:
	$(CC) $(CFLAGS) $(SRC_FILES) -o $(BIN_PATH)

clean:
	$(RM) ./bin/*

.PHONY: all clean