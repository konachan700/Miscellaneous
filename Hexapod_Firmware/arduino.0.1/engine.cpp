#include "engine.h"


bool move_enable_disable = false;

void __ultrasonic_write_pulse(void) {
	PORTE |=  (1 << 4);
	TCNT1 = 0;
	while (TCNT1 < 22) {};
	PORTE &= ~(1 << 4);
}

int __ultrasonic_read_pulse(void) {
	unsigned int tmr = 0;
	__ultrasonic_write_pulse();
	
	unsigned char c = 0;
	while (c == 0) {
		c = PINE & (1 << PINE5);
	}
	
	TCNT1 = 0;
	while (c > 0) {
		tmr = TCNT1;
		if (tmr > 60000) goto TMR_ERR;
		c = PINE & (1 << PINE5);
	};
	return tmr;
	TMR_ERR:
		return -1;
}

void ultrasonic_init(void) {
	// Ultrasonic HR-SC04 sensor
	DDRE  |=  (1 << 4); //trig
	DDRE  &= ~(1 << 5); //echo
	PORTE &= ~(1 << 4);
	PORTE |=  (1 << 5);
	// Timer for delay & sensors
	TCCR1A = 0;
	TCCR1B |= (1 << CS11);
	TCCR3C = 0;
	TCNT1 = 0xFFFF;
	TIMSK1 = 0;
}

//Distance = ((Duration of high level)*(Sonic :340m/s))/2
int get_distance(void) {
	unsigned int tmr = __ultrasonic_read_pulse();
	if (tmr < USS_ERROR_MIN_RANGE) return -1;
	if (tmr > USS_ERROR_MAX_RANGE) return -2;
	return (tmr / 58);
}

void buttons_init(void) {
	DDRC  &= ~((1 << 6) | (1 << 4) | (1 << 2));
	PORTC |=  ((1 << 6) | (1 << 4) | (1 << 2));
}

bool button_get_state(unsigned char n) {
	if (n == 1) {
		return (BTN_PORT & BTN_1) ? false : true;
	} else if (n == 2) {
		return (BTN_PORT & BTN_2) ? false : true;
	} else if (n == 3) {
		return (BTN_PORT & BTN_3) ? false : true;
	} else {
		return false;
	}
}

void move_proc(void) {
	if (move_enable_disable) return;
	
	if ((ir_read_right()) && (ir_read_left())) {
		go_back();
		return;
	}
	
	if ((ir_read_right()) && (ir_read_left() == false)) {
		rotate_left();
		rotate_left();
		rotate_left();
		go_back();
		return;
	}
	
	if ((ir_read_right() == false) && (ir_read_left())) {
		rotate_right();
		rotate_right();
		rotate_right();
		go_back();
		return;
	}
	
	go_next();
}

void move_enable(bool n) {
	move_enable_disable = n;
}

		//if (button_get_state(2)) printf("b2\r\n");
		//if (button_get_state(3)) printf("b3\r\n");
		
		//if (ir_read_right()) printf("RR\r\n");
		//if (ir_read_left()) printf("RL\r\n");