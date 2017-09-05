#include "uart.h"

/* Bluetooth (RX3/TX3) section */
bool			bluetooth_init_status						= false;
bool			bluetooth_nchr_status						= false;
bool			bluetooth_in_buffer_ovf						= false;
unsigned char	bluetooth_in_buffer_counter					= 0;
char			bluetooth_in_buffer[BLUETOOTH_IN_BUFFER_LEN];

unsigned long	servo_current								= 0;

/* Bluetooth (RX3/TX3) section */
void __bluetooth_rs232_init(void) {
	unsigned char i;
	for (i=0; i<BLUETOOTH_IN_BUFFER_LEN; i++) bluetooth_in_buffer[i] = 0;
	BLUETOOTH_RX_DDR  &= ~(1 << BLUETOOTH_RX_PIN); 
	BLUETOOTH_RX_PORT |=  (1 << BLUETOOTH_RX_PIN);
	BLUETOOTH_TX_DDR  |=  (1 << BLUETOOTH_TX_PIN); 
	BLUETOOTH_TX_PORT |=  (1 << BLUETOOTH_TX_PIN);
	UBRR3 = 103;
	UCSR3B = (1 << RXEN3) | (1 << TXEN3) | (1 << RXCIE3);
    bluetooth_init_status = true;
}

int __bluetooth_rs232_putchar(char c, FILE *stream) {
	if (bluetooth_init_status == false) return 0;
	if (c == 13) __bluetooth_rs232_putchar(10, stream);
	while (!( UCSR3A & (1 << UDRE3) ));
	UDR3 = c;
	return 0;
}

void __bluetooth_rs232_clear_in_buffer(void) {
	unsigned char i;
	for (i=0; i<BLUETOOTH_IN_BUFFER_LEN; i++) bluetooth_in_buffer[i] = 0;
	bluetooth_in_buffer_counter = 0;
	bluetooth_in_buffer_ovf = false;
}

char* __bluetooth_rs232_get_buffer(void) {
	return bluetooth_in_buffer;
}

bool __bluetooth_rs232_ibo(void) {
	return bluetooth_in_buffer_ovf;
}

bool __bluetooth_rs232_nc(void) {
	bool a = bluetooth_nchr_status;
	bluetooth_nchr_status = false;
	return a;
}

ISR(USART3_RX_vect) {
	if (bluetooth_in_buffer_counter >= BLUETOOTH_IN_BUFFER_LEN) {
		bluetooth_in_buffer_ovf = true;
		char i = UDR3;
		return;
	}
	bluetooth_in_buffer[bluetooth_in_buffer_counter] = UDR3;
	bluetooth_nchr_status = true;
	bluetooth_in_buffer_counter++;
}


void bluetooth_init(void) {
	__bluetooth_rs232_init();
	static FILE stdout_dbg = FDEV_SETUP_STREAM(__bluetooth_rs232_putchar, NULL, _FDEV_SETUP_WRITE);
	stdout = &stdout_dbg;
	printf("start ok\r\n");
}

void comm_exec(void) {
	char* dc = __bluetooth_rs232_get_buffer();
	int str_len = strlen(dc);
	unsigned long zval = 0;
	int i = 0;
	
	if ((str_len > 0) && (__bluetooth_rs232_nc())) __bluetooth_rs232_putchar(dc[str_len-1], NULL);
	
	if ((dc[str_len-1] == 13) || (dc[str_len-1] == 10)) {
		zval = strtol(dc + 2, NULL, 10);
		if (strstr(dc, "AA") != NULL) {
			servo_current = zval;
			printf("OK\r\n");
		} else if (strstr(dc, "AS") != NULL) {
			servo_set(servo_current, zval);
			printf("OK\r\n");
		} 
		
		else if (strstr(dc, "ZZ") != NULL) {
			rotate_right();
			printf("OK\r\n");
		} else if (strstr(dc, "XX") != NULL) {
			rotate_left();
			printf("OK\r\n");
		} else if (strstr(dc, "CC") != NULL) {
			go_next();
			printf("OK\r\n");
		} else if (strstr(dc, "VV") != NULL) {
			go_back();
			printf("OK\r\n");
		}
		
		
		__bluetooth_rs232_clear_in_buffer();
	}
	if (__bluetooth_rs232_ibo()) __bluetooth_rs232_clear_in_buffer();
}

