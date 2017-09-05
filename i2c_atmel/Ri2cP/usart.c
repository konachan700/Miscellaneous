#include <avr/io.h>
#include "usart.h"

unsigned char usart_rx_buf[USART_BUF_SIZE];
unsigned char usart_rx_counter = 0;

void __x_usart_init(void) {
	DDRD	&= ~(1 << PIND0); // RX
	DDRD	|=  (1 << PIND1); // TX
	//PORTD	|= ((1 << PIND0) | (1 << PIND1));
	UBRR0H = 0;
	UBRR0L = 3;
	UCSR0A = 0;
	UCSR0B = ((1 << TXEN0) | (1 << RXEN0) | (1 << RXCIE0));
}

void __x_usart_putchar(unsigned char c) {
	while (!(UCSR0A & (1 << UDRE0)));
	UDR0 = c;
}

unsigned char __x_usart_hex(unsigned char c) {
	if (c > 0x0f) return '0';
	if (c > 0x09) return ('A' + (c - 0x0A));
	return c + '0';
}

void __x_usart_putint8h(unsigned char c) {
	__x_usart_putchar(__x_usart_hex(c >> 4));
	__x_usart_putchar(__x_usart_hex(c & 0x0f));
}

void __x_usart_putcharsn(unsigned char *c, unsigned char len) {
	unsigned char i;
	for (i=0; i<len; i++) {
		__x_usart_putchar(*c);
		c++;
	}
}

void __x_usart_putchars(unsigned char *c) {
	while (*c != '\0') {
		__x_usart_putchar(*c);
		c++;
	}
}

void __x_usart_clear_buf(void) {
	int i;
	for (i=0; i<USART_BUF_SIZE; i++) {
		usart_rx_buf[i] = 0;
	}
}

unsigned char* __x_usart_read_buf(void) {
	return usart_rx_buf;
}

ISR(USART_RX_vect) {
	unsigned char rb = UDR0;
	if (usart_rx_counter < USART_BUF_SIZE) {
		usart_rx_buf[usart_rx_counter] = rb;
		usart_rx_counter++;
	}
	return 0;
}