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
#include <linux/spi/spidev.h>
#include <sys/sysinfo.h>
#include "ndisp.h" 
#include "dispfont.h" 

static uint32_t speed = 100000;
static uint8_t  mode = SPI_MODE_0;
static uint8_t  bits = 8;
long double cpu_load_last[4];
int shmem_text = -1;

struct pcd8544_disp_struct* disp_s;

int main() {
    int fd, ret, n, i;
    char buf[255]; 
    struct sysinfo si;
    
    ret =   init_gpio_out(NDISP_GPIO_RESET) | 
            init_gpio_out(NDISP_GPIO_CS)    |
            init_gpio_out(NDISP_GPIO_DC)    ;
    if (ret < 0) {
        printf("Can't init gpio #%d\n", ret);
		exit(1);
    }
    
    gpio_out(NDISP_GPIO_CS, 0);
    gpio_out(NDISP_GPIO_RESET, 0);
    usleep(1000 * 20);
    gpio_out(NDISP_GPIO_RESET, 1);
    
    fd = spi_open();
    if (fd < 0) exit(1);
	
	if ((pcd8544_ext_command(fd, PCD8544_SETBIAS | 4)  < 0) |
        (pcd8544_ext_command(fd, PCD8544_SETVOP  | 63) < 0) |
        (pcd8544_command(fd, PCD8544_SETXADDR)) | 
        (pcd8544_command(fd, PCD8544_SETYADDR))
    ) {
        printf("Can't sent command for device %s\n", NDISP_SPI_DEV); 
    }   

    gpio_out(NDISP_GPIO_DC, 1);
    memset(pcd8544_buffer, 0, PCD8544_BUF_SIZE);

    if ((shmem_text = shm_open(SHARED_MEMORY_OBJECT_NAME, O_CREAT | O_RDWR, 0777)) == -1) {
        printf("Can't create shared memory object %s\n", SHARED_MEMORY_OBJECT_NAME);
        close(fd);
        exit(1);
    }
    
    if (ftruncate(shmem_text, sizeof(disp_s)+1) == -1) {
        printf("Can't truncate shared memory object %s\n", SHARED_MEMORY_OBJECT_NAME);
        exit(1);
    }
    
    disp_s = (struct pcd8544_disp_struct *)(mmap(NULL, sizeof(disp_s)+1, PROT_WRITE|PROT_READ, MAP_SHARED, shmem_text, 0));
    if (disp_s == MAP_FAILED) {
        printf("Can't mmap for shmem object %s\n", SHARED_MEMORY_OBJECT_NAME);
        exit(1);
    }
    
    disp_s->magic = 0x4343;
    disp_s->line_0_clear = 0;
    for (i=0; i<6; i++) memset(disp_s->text[i], ' ', 14);
	
    while (1) {
        usleep(1000 * 1000);
        
        memset(pcd8544_buffer, 0, PCD8544_BUF_SIZE);
        
        if (disp_s->magic == 0x4343) {
            if (disp_s->line_0_clear != 1) {
                get_cpu_load_precentage(buf);
                draw_chars(0, LCD_CHARS_IN_ROW-strlen(buf), buf, strlen(buf));
                
                get_memory_usage_mb(buf);
                draw_chars(0, 0, buf, strlen(buf));
            }
            
            for (i=((disp_s->line_0_clear == 1) ? 0 : 1); i<6; i++) {
                draw_chars(i, 0, disp_s->text[i], 14);
            }
        } else {
            sprintf(buf, "Shmem corrupted");
            draw_chars(0, 0, buf, strlen(buf));
        }
      
        pcd8544_write_buf(fd);
    }

    munmap(disp_s, sizeof(disp_s)+1);
    close(shmem_text);
    close(fd);
    return 0;
}

int get_memory_usage_mb(char* buf) {
    long ram_cached, ram_total, ram_buf, ram_free;
    FILE *fp;
    char* line = NULL;
    size_t read, len, i=0;
    
    fp = fopen("/proc/meminfo","r");
    while ((read = getline(&line, &len, fp)) != -1) {
        switch(i) {
            case 0:
                sscanf(line, "MemTotal: %lu kB", &ram_total);
                break;
            case 1:
                sscanf(line, "MemFree: %lu kB", &ram_free);
                break;
            case 2:
                sscanf(line, "Buffers: %lu kB", &ram_buf);
                break;
            case 3:
                sscanf(line, "Cached: %lu kB", &ram_cached);
                break;
        }
        
        i++;
        if (i > 3) break;
    }

    fclose(fp);
    
    sprintf(buf, "%d/%dMB", (ram_total - (ram_cached + ram_buf + ram_free)) / 1024, ram_total / 1024);
    if (line) free(line);
    
    return 0;
}

/*
MemTotal:          59288 kB
MemFree:            6672 kB
Buffers:            1968 kB
Cached:            34116 kB
*/

int get_cpu_load_precentage(char* buf) {
    long double a[4], loadavg;
    FILE *fp;
    unsigned char i;
    
    fp = fopen("/proc/stat","r");
    fscanf(fp,"%*s %Lf %Lf %Lf %Lf", &a[0], &a[1], &a[2], &a[3]);
    fclose(fp);
    
    loadavg = ((a[0]+a[1]+a[2]) - (cpu_load_last[0]+cpu_load_last[1]+cpu_load_last[2])) / ((a[0]+a[1]+a[2]+a[3]) - (cpu_load_last[0]+cpu_load_last[1]+cpu_load_last[2]+cpu_load_last[3]));
    
    for (i=0; i<4; i++) cpu_load_last[i] = a[i];
    
    sprintf(buf, "%d%%", (int)(loadavg * 100));
    
    return 0;
}

int draw_pixel(int x, int y, int color) {
    x = LCDWIDTH  - x - 1;
    y = LCDHEIGHT - y - 1;

    if ((x < 0) || (x >= LCDWIDTH) || (y < 0) || (y >= LCDHEIGHT))
        return;

    if (color) 
        pcd8544_buffer[x+ (y/8)*LCDWIDTH] |= _BV(y%8);  
    else
        pcd8544_buffer[x+ (y/8)*LCDWIDTH] &= ~_BV(y%8); 
    
    return 0;
}

int draw_chars(int line, int col, unsigned char* buf, int len) {
    int i;
    for (i=col; i<(LCDWIDTH / 6); i++) {
        if ((i - col) >= len)
            break;
        draw_char_raw(i * 6, line * 8, buf[i-col]);
    }

    return 0;
}

int draw_char_raw(int x, int y, unsigned char c) {
    unsigned char line, i, j;
    
    for(i=0; i<6; i++) {
      if(i < 5) 
          line = pcd8544_font[(c * 5) + i];
      else      
          line = 0x0;
      
      for(j=0; j<8; j++, line >>= 1) {
        if(line & 0x1) 
            draw_pixel(x+i, y+j, 1); 
        else
            draw_pixel(x+i, y+j, 0); 
      }
    }
    return 0;
}

int spi_open(void) {
    int fd, ret;
    
    fd = open(NDISP_SPI_DEV, O_RDWR | O_SYNC);
	if (fd <= 0) { 
		printf("spi_open: Device %s not found\n", NDISP_SPI_DEV);
		return -1;
	}

	ret = ioctl(fd, SPI_IOC_WR_MAX_SPEED_HZ, &speed);
	if (ret == -1) { 
		printf("spi_open: Can't set speed for device %s\n", NDISP_SPI_DEV);
		return -2;
	}
	
	ret = ioctl(fd, SPI_IOC_WR_BITS_PER_WORD, &bits);
	if (ret == -1) { 
		printf("spi_open: Can't set bits for device %s\n", NDISP_SPI_DEV);
		return -3;
	}
	
    ret = ioctl(fd, SPI_IOC_WR_MODE, &mode);
	if (ret == -1) { 
		printf("spi_open: Can't set mode for device %s\n", NDISP_SPI_DEV); 
		return -4;
	}
	
	return fd;
}

int pcd8544_write_buf(int fd) {
    if ((pcd8544_command(fd, PCD8544_SETXADDR)) | 
        (pcd8544_command(fd, PCD8544_SETYADDR))
    ) {
        printf("pcd8544_write_buf: Can't sent command for device %s\n", NDISP_SPI_DEV); 
    } 
    
    if (gpio_out(NDISP_GPIO_DC, 1) < 0)
        return -1;
    if (write(fd, pcd8544_buffer, PCD8544_BUF_SIZE) <= 0)
        return -2;
    return 0;
}

int pcd8544_command(int fd, char cmd) {    
    if (gpio_out(NDISP_GPIO_DC, 0) < 0)
        return -1;
    write(fd, &cmd, 1);
    return 0;
}

int pcd8544_ext_command(int fd, char cmd) {
    if (pcd8544_command(fd, PCD8544_FUNCTIONSET | PCD8544_EXTENDEDINSTRUCTION) < 0) return -1;
    if (pcd8544_command(fd, cmd) < 0) return -1;
    if (pcd8544_command(fd, PCD8544_FUNCTIONSET) < 0) return -1;
    if (pcd8544_command(fd, PCD8544_DISPLAYCONTROL | PCD8544_DISPLAYNORMAL) < 0) return -1;
    return 0;
}

int init_gpio_out(int gpio_num) {
    int fd, ret;
    char buf[255]; 
    
    sprintf(buf, "/sys/class/gpio/gpio%d", gpio_num);
    if (access(buf, W_OK) == -1) {
        fd = open("/sys/class/gpio/export", O_WRONLY | O_SYNC);
        if (fd <= 0)
            return -1;
        sprintf(buf, "%d", gpio_num);
        ret = write(fd, buf, strlen(buf));        
        close(fd);
    } else {
        printf("Gpio %d already exported.\n", gpio_num);
    }
    
    sprintf(buf, "/sys/class/gpio/gpio%d/direction", gpio_num);
    if (access(buf, W_OK) != -1) {
        fd = open(buf, O_WRONLY);
        if (fd <= 0)
            return -4;
        sprintf(buf, "out");
        ret = write(fd, buf, strlen(buf));
        close(fd);
    } else
        return -3;

    return 0;
}

int gpio_out(int gpio_num, int level) {
    int fd, ret;
    char buf[255]; 
    
    sprintf(buf, "/sys/class/gpio/gpio%d/value", gpio_num);
    if (access(buf, W_OK) != -1) {
        fd = open(buf, O_WRONLY);
        if (fd <= 0)
            return -3;
        sprintf(buf, "%d", level);
        ret = write(fd, buf, strlen(buf));
        close(fd);
    } else 
        return -1;
    
    return 0;
}
