/*
 * usart.h
 *
 * Created: 4/11/2015 10:47:50 AM
 *  Author: Konata
 */ 

#ifndef USART_H_
#define USART_H_

#define USART_BUF_SIZE 16

extern void __x_usart_init(void);
extern void __x_usart_putchar(unsigned char c);
extern void __x_usart_putcharsn(unsigned char *c, unsigned char len);
extern void __x_usart_putchars(unsigned char *c);
extern void __x_usart_clear_buf(void);
extern void __x_usart_putint8h(unsigned char c);

extern unsigned char* __x_usart_read_buf(void);

#endif /* USART_H_ */