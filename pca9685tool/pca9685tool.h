#ifndef __PCA9685_TOOL__
#define __PCA9685_TOOL__
 
#define DEVICES_COUNT 3
#define BLOCKS_COUNT 16

#define PCA9685_PRESCALE	0xFE
#define PCA9685_MODE		0x00 

#define PCA9685_MODE_AI		(1 << 5)
#define PCA9685_50HZ_CLOCK	0x79

extern int _pca9685_init(int fd);
extern int _pca9685_write_fresh(int fd, uint8_t addr, int value);
extern int _pca9685_write_nonfresh(int fd, uint8_t addr, int value);

#endif