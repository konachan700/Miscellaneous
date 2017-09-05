#include <stdint.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <errno.h>
#include <linux/types.h>
#include "hello.h" 

int shmem_text = -1;

struct pcd8544_disp_struct* disp_s;

int main() {
    if ((shmem_text = shm_open(SHARED_MEMORY_OBJECT_NAME, O_RDWR, 0777)) == -1) {
        printf("Can't create shared memory object %s\n", SHARED_MEMORY_OBJECT_NAME);
        exit(1);
    }

    disp_s = (struct pcd8544_disp_struct *)(mmap(NULL, sizeof(disp_s)+1, PROT_WRITE|PROT_READ, MAP_SHARED, shmem_text, 0));
    if (disp_s == MAP_FAILED) {
        printf("Can't mmap for shmem object %s\n", SHARED_MEMORY_OBJECT_NAME);
        exit(1);
    }
    
    disp_s->magic = 0x4343;
    disp_s->line_0_clear = 0;

	sprintf(disp_s->text[1], "Hello, world!");
    sprintf(disp_s->text[2], "   It's test.");

    close(shmem_text);
    return 0;
}
