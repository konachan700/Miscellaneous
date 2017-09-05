#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <linux/i2c-dev.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <getopt.h>
#include <stdint.h>

#include "pca9685tool.h"

static const char short_opt[] = "d:b:v:nf";
static const struct option long_opt[] = {
	{ "device", required_argument, NULL, 'd' }, 
	{ "block",  required_argument, NULL, 'b' },
	{ "value",  required_argument, NULL, 'v' },
	{ "no-init",  no_argument, NULL, 'n' },
	{ "fast",  no_argument, NULL, 'f' },
	{ 0, 0, 0, 0 }};

int _pca9685_write_fresh(int fd, uint8_t addr, int value) {
	uint8_t buf[5];
	buf[0] = addr;
	buf[1] = 1; // LEDx_ON_L
	buf[2] = 0; // LEDx_ON_H
	buf[3] = value & 0xff; // LEDx_OFF_L
	buf[4] = (value >> 8) & 0xff; // LEDx_OFF_H
	if (write(fd, buf, 5) != 5) {
		printf("_pca9685_write(); i2c i/o error, cannot write registers\n");
		return 1;
	} else {
		return 0;
	} 
}

int _pca9685_write_nonfresh(int fd, uint8_t addr, int value) {
	uint8_t buf[3];
	buf[0] = addr;
	buf[1] = value & 0xff; // LEDx_OFF_L
	buf[2] = (value >> 8) & 0xff; // LEDx_OFF_H
	if (write(fd, buf, 3) != 3) {
		printf("_pca9685_write(); i2c i/o error, cannot write registers\n");
		return 1;
	} else {
		return 0;
	}
}

int _pca9685_init(int fd) {
	uint8_t buf[2];
	buf[0] = PCA9685_PRESCALE;
	buf[1] = PCA9685_50HZ_CLOCK;
	if (write(fd, buf, 2) != 2) {
		printf("_pca9685_init(); i2c i/o error, cannot write registers\n");
		return 1;
	}
	
	buf[0] = PCA9685_MODE;
	buf[1] = (1 | PCA9685_MODE_AI);
	if (write(fd, buf, 2) != 2) {
		printf("_pca9685_init(); i2c i/o error, cannot write registers\n");
		return 1;
	} else {
		return 0;
	}
}

int main(int argc, char *argv[]) {
	int c = 0, opt_index = 0, device = 0, block = 0, value = 0, noinit = 0, fresh = 0;
	for (;;) {
		c = getopt_long(argc, argv, short_opt, long_opt, &opt_index);
		if (c < 0) break;

		switch (c) {
		case 'd':
			if (optarg != NULL) device = atoi(optarg);
			break;
		case 'b':
			if (optarg != NULL) block = atoi(optarg);
			break;
		case 'v':
			if (optarg != NULL) value = atoi(optarg);
			break;
		case 'n':
			noinit = 1;
			break;
		case 'f':
			fresh = 1;
			break;
		}
	}

	if ((device <= 0) || (device > 127)) {
		printf("error: device number is out of range (1-127)!\n");
		return 2;
	}

	if ((block < 0) || (block > 15)) {
		printf("error: block number is out of range (0-15)!\n");
        return 2;
	}

	if ((value < 0) || (value > 1023)) {
		printf("error: value is out of range (0-1023)!\n");
        return 2;
	}

	int i2c_file = open("/dev/i2c-0", O_RDWR); // захардкожено, ибо у меня i2c пока один в системе =)
	if (i2c_file < 0) {
		printf("Cannot open i2c device!\n");
		return 1;
	}

	if (ioctl(i2c_file, I2C_SLAVE, device) < 0) {
		printf("cannot set i2c slave address\n");
		return 3;
	}

	if (noinit == 0) {	
		if (_pca9685_init(i2c_file) == 1) {
			printf("cannot init i2c device\n");
			return 4;
		}
	}
	
	if (fresh == 0) {
		if (_pca9685_write_fresh(i2c_file, (6 + (4*block)), value) == 1) {
			printf("cannot write to i2c device\n");
			return 4;
		} 	
	} else {
		if (_pca9685_write_nonfresh(i2c_file, (8 + (4*block)), value) == 1) {
			printf("cannot write to i2c device\n");
			return 4;
		} 
	}

	close(i2c_file);
	printf("OK\n");
	return 0;
}
