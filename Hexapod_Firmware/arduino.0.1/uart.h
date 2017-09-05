#ifndef _UART_3
#define _UART_3

#define F_CPU 16000000

#include <avr/io.h>
#include <math.h>
#include "avr/interrupt.h"
#include "util/delay.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "servo.h"

/* Bluetooth (RX3/TX3) section */
#define BLUETOOTH_IN_BUFFER_LEN  32
#define BLUETOOTH_RX_PIN  PINJ0
#define BLUETOOTH_TX_PIN  PINJ1
#define BLUETOOTH_RX_DDR  DDRJ
#define BLUETOOTH_TX_DDR  DDRJ
#define BLUETOOTH_RX_PORT PORTJ
#define BLUETOOTH_TX_PORT PORTJ

extern void comm_exec(void);
extern void bluetooth_init(void);

#endif		