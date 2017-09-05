#ifndef ENGINE_H_
#define ENGINE_H_

#define F_CPU 16000000

#include <avr/io.h>
#include <math.h>
#include "util/delay.h"

#define USS_ERROR_MAX_RANGE 10000
#define USS_ERROR_MIN_RANGE 100
#define USS_AND_IR_RANGE	1170

#define BTN_1				(1 << PINC6)
#define BTN_2				(1 << PINC4) 
#define BTN_3				(1 << PINC2)
#define BTN_PORT			PINC

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "uart.h"
#include "servo.h"

extern void ultrasonic_init(void);
extern int get_distance(void);

extern void buttons_init(void);
extern bool button_get_state(unsigned char n);

extern void move_proc(void);
extern void move_enable(bool n);

#endif /* ENGINE_H_ */