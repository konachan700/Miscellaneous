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

#define SIZE_X 22
#define SIZE_Y 22

struct one_entry {
    unsigned int locked : 1;
    unsigned int figure : 1;
    unsigned int bad    : 1;
};

void add(int x, int y);

struct one_entry entries[SIZE_Y][SIZE_X];

int main() {
    double start, end;
    start = clock();
    
    memset(entries, 0, sizeof(struct one_entry) * SIZE_Y * SIZE_X);
    
    int x=0, y=0, counter, z_counter=0;
    
    while (1) {
        if ((entries[y][x].figure == 0) && (entries[y][x].locked == 0)) add(x, y);
        x += 1; // сумма х и y - простые числа
        y += 2;
        if ((x >= SIZE_X) || (y >= SIZE_Y)) break;
    }
    
    y = 1; x += 2;
    
    while (1) {
        if ((entries[y][x].figure == 0) && (entries[y][x].locked == 0)) add(x, y);
        x += 1; // сумма х и y - простые числа
        y += 2;
        if ((x >= SIZE_X) || (y >= SIZE_Y)) break;
    }
    
    while (1) {
        for (x=0; x<SIZE_X; x++) {
            for (y=0; y<SIZE_Y; y++) {
                if ((entries[y][x].figure == 0) && (entries[y][x].locked == 0)) {
                    add(x, y);
                    counter++;
                }
            }
        }
        
        if (counter == 0) break;
        counter = 0;
    }
    
    for (x=0; x<SIZE_X; x++) {
        for (y=0; y<SIZE_Y; y++) {
            if (entries[y][x].figure == 1) {
                printf(" X ");
                z_counter++;
            } else if (entries[y][x].locked == 1) {
                
                printf(" | ");
            } else if (entries[y][x].bad == 1) {
                
                printf(" B ");
            } else {
                printf(" - "); // невозможная ситуация. Сигнализирует об ошибке.
            }
        }
        printf("\n");
    }
    
    end = clock();
    printf("Time: %.2lf\n", (end-start)/CLOCKS_PER_SEC);
    printf("Count: %d\n", z_counter);
                             
    return 0;
}

void add(int x, int y) {
    int i, ix, iy;
    
    for (i=0; i<SIZE_X; i++) entries[y][i].locked = 1;
    for (i=0; i<SIZE_Y; i++) entries[i][x].locked = 1;
    
    for (ix=x, iy=y; (iy<SIZE_Y) && (ix<SIZE_X); ix++, iy++) if (entries[iy][ix].figure == 0) entries[iy][ix].locked = 1; else entries[iy][ix].bad = 1;
    for (ix=x, iy=y; (iy>=0)     && (ix>=0);     ix--, iy--) if (entries[iy][ix].figure == 0) entries[iy][ix].locked = 1; else entries[iy][ix].bad = 1;
    
    for (ix=x, iy=y; (iy<SIZE_Y) && (ix>=0); ix--, iy++) if (entries[iy][ix].figure == 0) entries[iy][ix].locked = 1; else entries[iy][ix].bad = 1;
    for (ix=x, iy=y; (iy>=0) && (ix<SIZE_X); ix++, iy--) if (entries[iy][ix].figure == 0) entries[iy][ix].locked = 1; else entries[iy][ix].bad = 1;
    
    entries[y][x].figure = 1;
}
