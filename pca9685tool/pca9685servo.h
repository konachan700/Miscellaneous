#ifndef __PCA9685_SERVO__
#define __PCA9685_SERVO__

#define DEVICES_COUNT 					3
#define BLOCKS_COUNT 					16
#define FIFO_BUF_SIZE 					250

#define PCA9685_PRESCALE				0xFE
#define PCA9685_MODE					0x00 

#define PCA9685_MODE_AI					(1 << 5)
#define PCA9685_50HZ_CLOCK				0x79

#define I2C_DEV 						"/dev/i2c-0"
#define PCA9685_PIDFILE 				"/tmp/pca9685servo.pid"
 
#define SHMEM_COMMANDLINE 				"pca9685_cmd"
#define SHMEM_COMMANDLINE_BUFSIZE 		4096
#define CMD_OFFSET						3

/* 
	u8		char; 
	u16		char[2]{Hb,Lb}; 
	%		(0-100); 
*/
#define MEMCMD_SERVO_ONE_SET			0x01 // {u8 COMMAND, u8 DEVICE_ID, % VALUE}
#define MEMCMD_SERVO_ALL_SET			0x02 // {u8 COMMAND, % VALUES[n]}
#define MEMCMD_GET_RAW_SETTINGS			0x03 // {u8 COMMAND}
#define MEMCMD_GET_TXTSETT_BY_INDEX		0x04 // {u8 COMMAND, u8 DEVICE_ID}
#define MEMCMD_GET_TXTSETT_ALL			0x05 // {u8 COMMAND}

#endif 