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
#include <ctype.h>

#include "pca9685conf.h" 

static const char short_opt[] = "d:s:e:i:a:c:v";
static const struct option long_opt[] = {
	{ "device", required_argument, NULL, 'd' }, 
	{ "start-val",  required_argument, NULL, 's' },
	{ "end-val",  required_argument, NULL, 'e' },
	{ "inital-val",  required_argument, NULL, 'i' },
	{ "dev-addr",  required_argument, NULL, 'a' },
	{ "channel",  required_argument, NULL, 'c' },
	{ "debug",  no_argument, NULL, 'v' },
	{ 0, 0, 0, 0 }};
	
struct PCA9685_settings sett[PCA9685_COUNT * PCA9685_OUTS];

int main(int argc, char *argv[]) {
	int c = 0, device = -1, min = 0, max = 0, init = 0, da = 0, debug = 0, opt_index = 0, channel = -1;
	for (;;) {
		c = getopt_long(argc, argv, short_opt, long_opt, &opt_index);
		if (c < 0) break;
	
		switch (c) {
		case 'd':
			if (optarg != NULL) device = atoi(optarg);
			break;
		case 's':
			if (optarg != NULL) min = atoi(optarg);
			break;
		case 'e':
			if (optarg != NULL) max = atoi(optarg);
			break;
		case 'i':
			if (optarg != NULL) init = atoi(optarg);
			break;
		case 'a':
			if (optarg != NULL) da = atoi(optarg);
			break;
		case 'c':
			if (optarg != NULL) channel = atoi(optarg);
			break;
		case 'v':
			debug = 1;
			break;
		}
	}
	
	if ((device < 0) || (device >= (PCA9685_COUNT * PCA9685_OUTS))) {
		printf("error: device ID is out of range!\n");
		return 2;
	}
	
	if ((da <= 0) || (da > 127)) {
		printf("error: device i2c address is out of range (1-127)!\n");
		return 2;
	}
	
	if ((min <= 0) || (min > 1023)) {
		printf("error: init value is out of range (1-1023)!\n");
        return 2;
	}
	
	if ((max <= 0) || (max > 1023)) {
		printf("error: max value is out of range (1-1023)!\n");
        return 2;
	}
	
	if ((init <= 0) || (init > 1023)) {
		printf("error: init value is out of range (1-1023)!\n");
        return 2;
	}
	
	if ((channel < 0) || (channel > 15)) {
		printf("error: channel value is out of range (0-15)!\n");
        return 2;
	}
	
	int config;
	if (access(CONFIG_FILE, R_OK | W_OK) != 0) {
		config = open(CONFIG_FILE, O_RDWR | O_CREAT, S_IRUSR | S_IRGRP | S_IROTH | S_IWUSR | S_IWGRP);
	} else {
		config = open(CONFIG_FILE, O_RDWR);
	}
	
	if (config == -1) {
		printf("Cannot open config file!\n");
		return 1;
	}
	
	int struct_size = sizeof(sett);
	if (read(config, sett, struct_size) != struct_size) {
		memset(sett, 0, struct_size); // если в структуре мусор, то обнуляем ее
	}
	
	sett[device].min 		= min;
	sett[device].max 		= max;
	sett[device].inital 	= init;
	sett[device].da 		= da;
	sett[device].channel  	= channel;
	
	lseek(config, 0, SEEK_SET);
	// Я знаю, что так конфиги не делают, но так проще. То килобайт в памяти, а то едрить какой затратный по ресурсам парсинг текстового конфига...
	if (write(config, sett, struct_size) != struct_size) {
		printf("Cannot write config file, wft?\n");
	}
	
	close(config);
	
	if (debug == 1) {
		int i;
		for (i=0; i<(PCA9685_COUNT * PCA9685_OUTS); i++) {
			if (sett[i].da > 0)
				printf("DeviceID: %d;\t device address: 0x%X; \tchannel:0x%X; \tmin: %d; \tmax: %d; \tinital: %d\n", i, sett[i].da, sett[i].channel, sett[i].min, sett[i].max, sett[i].inital);
		}
		printf("Total struct size: %d;\n", struct_size);
		printf("Total elements count: %d;\n", struct_size / sizeof(sett[0]));
	} else {
		printf("OK\n");
	}
	
	return 0;
}
