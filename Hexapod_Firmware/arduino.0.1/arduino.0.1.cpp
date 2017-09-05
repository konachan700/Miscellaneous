#define F_CPU 16000000

#include <avr/io.h>
#include <math.h>
#include "util/delay.h"

#include "engine.h"

int main(void)
{
	_delay_ms(250);

	servo_init();
	ir_init();
	bluetooth_init();
	ultrasonic_init();
	buttons_init();
	sei();
	
    while(1) {
		comm_exec();
		move_proc();
		
		if (button_get_state(1)) move_enable(false);
		if (button_get_state(2)) move_enable(true);
    }
}

