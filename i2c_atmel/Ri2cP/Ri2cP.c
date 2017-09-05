#include <avr/io.h>
#include <avr/eeprom.h>

#include "usart.h"
#include "i2c.h"



void __leds_init(void) {
	DDRB  |= (1<<PINB0);
	DDRD  |= (1<<PIND7);
	PORTB |= (1<<PINB0);
	PORTD |= (1<<PIND7);
}

int main(void)
{
	WDTCSR = 0;
	i2c_init();
	ds1307_init(DS1307_SQWE);
	
	
	
	
	__x_usart_init();
	__leds_init();
	
	asm("SEI");
	unsigned short int i = 0;

	
    while(1)
    {

		__x_usart_putchar(' ');
		__x_usart_putint8h(ds1307_readb(0x02));
		__x_usart_putchar(':');
		__x_usart_putint8h(ds1307_readb(0x01));
		__x_usart_putchar(':');
		__x_usart_putint8h(ds1307_readb(0x00));
		__x_usart_putchar('\r');
		
		for (i=0; i<65000; i++) asm("NOP");
		for (i=0; i<65000; i++) asm("NOP");
		for (i=0; i<65000; i++) asm("NOP");
    }
}





