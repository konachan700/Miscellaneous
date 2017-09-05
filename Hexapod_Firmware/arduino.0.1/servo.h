#ifndef SERVO_H_
#define SERVO_H_

#define F_CPU 16000000

#include <avr/io.h>
#include <math.h>
#include "avr/interrupt.h"
#include "util/delay.h"

#define SERVO_CORR_01		40 
#define SERVO_CORR_02		41
#define SERVO_CORR_03		40
#define SERVO_CORR_04		63 
#define SERVO_CORR_05		32
#define SERVO_CORR_06		51
#define SERVO_CORR_07		60
#define SERVO_CORR_08		48
#define SERVO_CORR_09		35
#define SERVO_CORR_10		60
#define SERVO_CORR_11		56
#define SERVO_CORR_12		50 

#define LEG_UP_DOWN_HEIGHT	14
#define LEG_DOWN_SPEED		1 // [0-20]
#define STEP_SIZE			12 // DO NOT CHANGE !!!
#define STEP_ALL			(STEP_SIZE / 3)
#define LEG_UP_WAIT			12
#define LEG_MOVE_WAIT		70
#define LEGS_WAIT			5

#define IR_1				(1 << PINH6)
#define IR_2				(1 << PINK7) 
#define IR_1_PORT			PINH
#define IR_2_PORT			PINK
#define IR_MAX_ARRAY		20

extern void servo_init(void);
extern void servo_set(unsigned long a, unsigned long b);

extern void go_next(void);
extern void go_back(void);
extern void rotate_right(void);
extern void rotate_left(void);

extern void ir_init(void);
extern bool ir_read_right(void);
extern bool ir_read_left(void);

#endif /* SERVO_H_ */
