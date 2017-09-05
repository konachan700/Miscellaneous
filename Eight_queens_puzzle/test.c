#define _GNU_SOURCE

#include <stdint.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <sys/sysinfo.h>
#include <math.h>
#include <linux/random.h>
#include <sys/syscall.h>
#include <time.h>

//#define SIZE_X 33
//#define SIZE_Y SIZE_X

#define NUL_BLOCK 0x00
#define BAD_BLOCK 0x01
#define F01_BLOCK 0x02
#define L0K_BLOCK 0x03
#define F02_BLOCK 0x04

//unsigned char entries[SIZE_Y][SIZE_X];

unsigned long board_size = 15;
unsigned char* entries;
unsigned char data_error = 0;

inline unsigned char array_get(int x, int y) {
    if ((x >= board_size) || (y >= board_size)) {
        printf("array_get overflow %d x %d\n", x, y);
        return 0xFF;
    }
    return entries[board_size * y + x];
}

inline void array_set(int x, int y, unsigned char val) {
    if ((x >= board_size) || (y >= board_size)) return;
    entries[board_size * y + x] = val;
}

long get_nano_time(void) {
    struct timeval time;
    gettimeofday(&time, NULL);
    return ((unsigned long long)time.tv_sec * 1000000) + time.tv_usec;
}

void lock_block(int x, int y) {
    if ((x >= board_size) || (y >= board_size)) return;
    if (array_get(x, y) == L0K_BLOCK) return;
    
    if (array_get(x, y) == NUL_BLOCK) {
        array_set(x, y, L0K_BLOCK);  
    } else { 
        //array_set(x, y, BAD_BLOCK);
        printf("\nBad operation detected.\n");
        exit(1);
    }
}

void add_figure(int x, int y, unsigned char fig) {
    int i, ix, iy;
    
    if (array_get(x, y) != NUL_BLOCK) return;
    
    for (i=0; i<board_size; i++) { 
        lock_block(i, y); 
        lock_block(x, i); 
    }
    
    for (ix=x, iy=y; (iy<board_size) && (ix<board_size); ix++, iy++) lock_block(ix, iy);
    for (ix=x, iy=y; (iy>=0)     && (ix>=0);     ix--, iy--) lock_block(ix, iy);
    
    for (ix=x, iy=y; (iy<board_size) && (ix>=0); ix--, iy++) lock_block(ix, iy);
    for (ix=x, iy=y; (iy>=0) && (ix<board_size); ix++, iy--) lock_block(ix, iy);
    
    array_set(x, y, fig);
}

void fill_algo_1(int xoff, int yoff) {
    int x=0, y=0, flag_x_ovf=0, yn_counter=0;
    
    while (1) {
        if (array_get(x, y) == NUL_BLOCK) {
            add_figure(x, y, F01_BLOCK);
        }

        x += xoff; 
        y += yoff;
        
        if ((x >= board_size) && (y >= board_size)) break;
        if ((x >= board_size) && (flag_x_ovf == 1)) break;
        
        if ((x >= board_size) && (xoff > yoff)) {
            flag_x_ovf = 1;
            if ((board_size % 6) == 3) {
                x = 3;
            } else if ((board_size % 6) == 4) {
                x = 3;
            } else {
                x = 1;
            }
        }
    }
    
    if ((board_size % 6) == 4) add_figure(1, board_size - 1, F01_BLOCK);
}

int fill_all(void) {
    int x=0, y=0, counter=0, fig_counter=0;
    while (1) {
        for (y=0; y<board_size; y++) {
            for (x=0; x<board_size; x++) {
                if (array_get(x, y) == NUL_BLOCK) {
                    add_figure(x, y, F02_BLOCK);
                    counter++;
                    fig_counter++;
                }
            }
        }
        
        if (counter == 0) break;
        counter = 0;
    }
    return fig_counter;
}

int check_number(int number, int display_result) {
    int res, x, y, fig_counter=0;
    board_size = number;
    
    entries = calloc(board_size * board_size, sizeof(unsigned char));
    
    fill_algo_1(2, 1);
    res = fill_all();
    
    if (display_result == 1) {
        for (y=0; y<board_size; y++) {
            for (x=0; x<board_size; x++) {
                switch (array_get(x, y)) {
                    case NUL_BLOCK:
                        printf("  ");
                        break;
                    case L0K_BLOCK:
                        printf(" \e[90m#\e[39m");
                        break;
                    case BAD_BLOCK:
                        printf(" b");
                        break;
                    case F01_BLOCK:
                        printf(" \e[1m\e[32mX\e[39m\e[0m");
                        fig_counter++;
                        break;
                    case F02_BLOCK:
                        printf(" \e[32mA\e[39m");
                        fig_counter++;
                        break;
                    default:
                        printf(" e");
                        break;
                }
            }
            printf("\n");
        }
        
        printf("Count: %d, auto: %d\n", fig_counter, res);
        printf("Board size: %d x %d\n", board_size, board_size);
    }
    
    free(entries);
    return (res > 0) ? 0 : 1;
}

int main(int argc, char **argv) {
    unsigned long long start_time = get_nano_time();
    
    if (argc == 2) {
        int size = atoi(argv[1]);
        if ((size >= 20) && (size <= 500)) check_number(size, 1);
    }
    
    if (argc == 3) {
        int size_from = atoi(argv[1]);
        int size_to = atoi(argv[2]);
        
        int i;
        for (i=size_from; i<size_to; i++) {
            if (check_number(i, 0) == 0) printf("%d, ", i);
        }
    }
    
    printf("\nTime: %d ns\n", (get_nano_time() - start_time));
    
    return 0;
}
