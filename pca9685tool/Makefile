default: all

all:
	$(CC) pca9685tool.c -o ./pca9685tool
	$(CC) pca9685conf.c -o ./pca9685conf
	$(CC) pca9685servo.c -D_REENTERANT -lpthread -lrt -o ./pca9685servo
	 