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
#include <signal.h>
#include <pthread.h>
#include <errno.h>
#include <sys/mman.h>

#include "pca9685conf.h"
#include "pca9685servo.h"

int i2c_file;
static pthread_t worker_t;

volatile int devs[PCA9685_COUNT];
struct PCA9685_settings sett[PCA9685_COUNT * PCA9685_OUTS];
 
int _pca9685_write_fresh(int fd, uint8_t addr, int value) {
	uint8_t buf[5];
	buf[0] = addr;
	buf[1] = 1; // LEDx_ON_L
	buf[2] = 0; // LEDx_ON_H
	buf[3] = value & 0xff; // LEDx_OFF_L
	buf[4] = (value >> 8) & 0xff; // LEDx_OFF_H
	if (write(fd, buf, 5) != 5) {
		fprintf(stdout, "_pca9685_write(); i2c i/o error, cannot write registers\n");
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
		fprintf(stdout, "_pca9685_write(); i2c i/o error, cannot write registers\n");
		return 1;
	} else {
		return 0;
	}
}

int _pca9685_write_nonfresh_c8(char dev_index_c, char value_p) {
	if ((value_p < 0) || (value_p > 100)) return -1;
	
	int dev_index = (int) dev_index_c;
	if (sett[dev_index].da <= 0) return -1;
	
	uint8_t addr = ((sett[dev_index].channel & 0xFF) * 4) + 8;
	
	if (ioctl(i2c_file, I2C_SLAVE, sett[dev_index].da) < 0) {
		fprintf(stdout, "_pca9685_write_nonfresh_c8(): cannot set i2c slave address\n");
		return -1;
	}
	
	float value_f = ((float) value_p) * (((float) (sett[dev_index].max - sett[dev_index].min)) / 100.0f);
	int value = (int) (value_f + 0.5);
	
	return _pca9685_write_nonfresh(i2c_file, addr, value);
}

int _pca9685_init(int fd) {
	uint8_t buf[2];
	buf[0] = PCA9685_PRESCALE;
	buf[1] = PCA9685_50HZ_CLOCK;
	if (write(fd, buf, 2) != 2) {
		fprintf(stdout, "_pca9685_init(); i2c i/o error, cannot write registers\n");
		return 1;
	}
	
	buf[0] = PCA9685_MODE;
	buf[1] = (1 | PCA9685_MODE_AI);
	if (write(fd, buf, 2) != 2) {
		fprintf(stdout, "_pca9685_init(); i2c i/o error, cannot write registers\n");
		return 1;
	} else {
		return 0;
	}
}
 
void makePID(int pid) {
    FILE* f = fopen(PCA9685_PIDFILE, "w+");
    if (f) {
        fprintf(f, "%u", pid);
        fclose(f);
    }
}

void *worker_thread(void *args) { 
	printf("WORKER STARTED;\n");
	
	fclose(stdout);
	stdout = fopen("/tmp/PCA9685.log","w+");
		
	int config = open(CONFIG_FILE, O_RDONLY);
	if (config == -1) {
		fprintf(stdout, "Cannot open config file!\n");
		return NULL;
	}
	
	if (read(config, sett, sizeof(sett)) != sizeof(sett)) {
		fprintf(stdout, "Invalid data in config file\n");
		return NULL;
	}
	
	close(config);
	memset((void*)devs, 0, sizeof(devs));
	
	int i, j, counter1 = 0, fail;
	for (i=0; i<(PCA9685_COUNT * PCA9685_OUTS); i++) {
		fail = 0;
		if (sett[i].da > 0)
			for (j=0; j<PCA9685_COUNT; j++) {
				if (sett[i].da == devs[j]) fail = 1;
			}
		if ((fail == 0) && (counter1 < PCA9685_COUNT)) {
			devs[counter1] = sett[i].da;
			counter1++;
		}
	}

	i2c_file = open(I2C_DEV, O_RDWR);
	if (i2c_file < 0) {
		fprintf(stdout, "Cannot open i2c device!\n");
		return NULL;
	}
	
	for (j=0; j<PCA9685_COUNT; j++) {
		if (ioctl(i2c_file, I2C_SLAVE, devs[j]) < 0) {
			fprintf(stdout, "cannot set i2c slave address\n");
		} else {
			_pca9685_init(i2c_file);
		}
	}
	
	for (i=0; i<(PCA9685_COUNT * PCA9685_OUTS); i++) {
		if (sett[i].da > 0) {
			fprintf(stdout, "DeviceID: %d;\t device address: 0x%X; \tchannel: %d; \tmin: %d; \tmax: %d; \tinital: %d\n", i, sett[i].da, sett[i].channel, sett[i].min, sett[i].max, sett[i].inital);
			if (ioctl(i2c_file, I2C_SLAVE, sett[i].da) < 0) {
				fprintf(stdout, "cannot set i2c slave address\n");
			} else {
				_pca9685_write_fresh(i2c_file, 6 + (sett[i].channel * 4), sett[i].inital);
			}
		}
	}
	
	int sh_mem_1 = shm_open(SHMEM_COMMANDLINE, O_RDWR, 0777); 
	if (sh_mem_1 == -1) {
		sh_mem_1 = shm_open(SHMEM_COMMANDLINE, O_CREAT | O_RDWR, 0777);
		if (sh_mem_1 == -1) {
			fprintf(stdout, "cannot open shared memory block\n");
			return NULL;
		}
		
		if (ftruncate(sh_mem_1, SHMEM_COMMANDLINE_BUFSIZE+1) == -1) {
			fprintf(stdout, "cannot allocate memory block\n");
			return NULL;
		}
	}
	
	void *shmem_addr = mmap(0, SHMEM_COMMANDLINE_BUFSIZE+1, PROT_WRITE | PROT_READ, MAP_SHARED, sh_mem_1, 0);
	if (shmem_addr == (char*)-1) {
		fprintf(stdout, "cannot do mmap() for memory block\n");
		return NULL;
	}
	
	memset(shmem_addr, 0, SHMEM_COMMANDLINE_BUFSIZE);
	
	char cmd_magic[3] = {'J','N','K'}, 
		 ok_magic[2]  = {'O','K'}, 
		 cmd_buf[SHMEM_COMMANDLINE_BUFSIZE];
	memset(cmd_buf, 0, SHMEM_COMMANDLINE_BUFSIZE);
	
	for (;;) {
		if (memcmp(cmd_magic, shmem_addr, 3) == 0) {
			memcpy(cmd_buf, (shmem_addr + 3), SHMEM_COMMANDLINE_BUFSIZE - 3);
			
			switch ((int) cmd_buf[0]) {
				case MEMCMD_SERVO_ONE_SET:
					_pca9685_write_nonfresh_c8(cmd_buf[1], cmd_buf[2]);
					memset(shmem_addr, 0, SHMEM_COMMANDLINE_BUFSIZE);
					memcpy(shmem_addr, ok_magic, 2);
					break;
				case MEMCMD_SERVO_ALL_SET:
					for (i=1; i<(PCA9685_COUNT * PCA9685_OUTS); i++) {
						if (i < (SHMEM_COMMANDLINE_BUFSIZE-4)) {
							_pca9685_write_nonfresh_c8(i-1, cmd_buf[i]);
						}
					}
					memset(shmem_addr, 0, SHMEM_COMMANDLINE_BUFSIZE);
					memcpy(shmem_addr, ok_magic, 2);
					break;
				case MEMCMD_GET_RAW_SETTINGS:
					memset(shmem_addr, 0, SHMEM_COMMANDLINE_BUFSIZE);
					memcpy(shmem_addr, ok_magic, 2);
					memcpy(shmem_addr+2, sett, sizeof(sett));
					break;
				case MEMCMD_GET_TXTSETT_BY_INDEX:
					i = (int) cmd_buf[1];
					memset(shmem_addr, 0, SHMEM_COMMANDLINE_BUFSIZE);
					snprintf((char *)(shmem_addr), SHMEM_COMMANDLINE_BUFSIZE, "DID:%d;DA:%d;CH:%d;MIN:%d;MAX:%d;INIT:%d;\n", i, sett[i].da, sett[i].channel, sett[i].min, sett[i].max, sett[i].inital);
					break;
				case MEMCMD_GET_TXTSETT_ALL:
					memset(shmem_addr, 0, SHMEM_COMMANDLINE_BUFSIZE);
					for (i=0; i<(PCA9685_COUNT * PCA9685_OUTS); i++) {
						snprintf((char *)(shmem_addr + strlen((char *)shmem_addr)), SHMEM_COMMANDLINE_BUFSIZE - strlen((char *)shmem_addr), "DID:%d;DA:%d;CH:%d;MIN:%d;MAX:%d;INIT:%d;\n", i, sett[i].da, sett[i].channel, sett[i].min, sett[i].max, sett[i].inital);
					}
					break;
			}
		}

		pthread_testcancel();
	}

	close(i2c_file);
	return NULL;
}
 
int main(int argc, char *argv[]) {
	int f = fork();
	if (f == 0) {
		umask(0);
		setsid();
		chdir("/");
		
		sigset_t 	sigset;
		siginfo_t 	siginfo;
		
		sigemptyset(&sigset);
		sigaddset(&sigset, SIGQUIT);
		sigaddset(&sigset, SIGINT);
		sigaddset(&sigset, SIGTERM);
		sigaddset(&sigset, SIGCHLD);
		sigaddset(&sigset, SIGUSR1);
		sigprocmask(SIG_BLOCK, &sigset, NULL);

		int s1 = pthread_create(&worker_t, NULL, worker_thread, NULL);
		if (s1 != 0) {
			printf("pthread_create error %d;\n", s1);
			return 5;
		} else {
			for (;;) {
				sigwaitinfo(&sigset, &siginfo);
				if (siginfo.si_signo == SIGUSR1) {
					printf("SIGUSR1 detected;\n");
				} else {
					pthread_cancel(worker_t);
					pthread_join(worker_t, NULL);
					return 0;
				}
			}
		}
		return 0;
		
	} else if (f == -1) {
		printf("fork(1) error;\n");
		return 1;
	} else {
		printf("OK; PID M: %d;\n", f);
		makePID(f);
		return 0;
	}
	return 0;
}
