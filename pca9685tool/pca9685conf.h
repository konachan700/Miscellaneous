#ifndef __PCA9685_CONF__
#define __PCA9685_CONF__
 
#define _GNU_SOURCE
 
#define CONFIG_FILE "/etc/pca9685_conf.bin"

#define PCA9685_COUNT 		3
#define PCA9685_OUTS 		16

struct PCA9685_settings {
	int min; 
	int max;
	int inital;
	int da;
	int channel;
};

 #endif