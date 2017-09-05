#include "servo.h"

volatile unsigned char servo[13] = {47,
									SERVO_CORR_01, SERVO_CORR_02, SERVO_CORR_03, SERVO_CORR_04, 
									SERVO_CORR_05, SERVO_CORR_06, SERVO_CORR_07, SERVO_CORR_08, 
									SERVO_CORR_09, SERVO_CORR_10, SERVO_CORR_11, SERVO_CORR_12};
volatile unsigned char servo_cnt8_1  = 0;
volatile unsigned char servo_cnt8_2  = 0;
volatile unsigned char servo_counter = 0;

volatile unsigned char ir1[IR_MAX_ARRAY] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
volatile unsigned char ir2[IR_MAX_ARRAY] = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
volatile unsigned char ir_counter = 0;

void __macro_0001_move(unsigned char a1, unsigned char a2, unsigned char b1, unsigned char b2, bool up_down = true) {
	unsigned char i = 0;
	
	servo[a1] += LEG_UP_DOWN_HEIGHT;
	servo[a2] -= LEG_UP_DOWN_HEIGHT;
	servo_counter = LEG_UP_WAIT;
	while (servo_counter > 0) asm("NOP");
	
	if (up_down) {
		servo[b1] -= STEP_SIZE;
		servo[b2] += STEP_SIZE;
	} else {
		servo[b1] += STEP_SIZE;
		servo[b2] -= STEP_SIZE;
	}
	servo_counter = LEG_MOVE_WAIT;
	while (servo_counter > 0) asm("NOP");
	
	for (i=0; i<LEG_UP_DOWN_HEIGHT; i++) {
		servo[a1]--;
		servo[a2]++;
		servo_counter = (i*LEG_DOWN_SPEED);
		while (servo_counter > 0) asm("NOP");
	}
	
	if (up_down) {
		servo[7]+=STEP_ALL; servo[8]+=STEP_ALL; servo[9]+=STEP_ALL; 
		servo[4]-=STEP_ALL; servo[5]-=STEP_ALL; servo[6]-=STEP_ALL;
	} else {
		servo[7]-=STEP_ALL; servo[8]-=STEP_ALL; servo[9]-=STEP_ALL; 
		servo[4]+=STEP_ALL; servo[5]+=STEP_ALL; servo[6]+=STEP_ALL;
	}	
	servo_counter = LEGS_WAIT;
	while (servo_counter > 0) asm("NOP");
}

void __macro_0002_move(bool up_down = true) {
	if (up_down) {
		servo[7]+=STEP_SIZE; servo[8]+=STEP_SIZE; servo[9]+=STEP_SIZE; 
		servo[4]+=STEP_SIZE; servo[5]+=STEP_SIZE; servo[6]+=STEP_SIZE;
	} else {
		servo[7]-=STEP_SIZE; servo[8]-=STEP_SIZE; servo[9]-=STEP_SIZE; 
		servo[4]-=STEP_SIZE; servo[5]-=STEP_SIZE; servo[6]-=STEP_SIZE;
	}		
}

void __macro_0003_move(unsigned char a1, unsigned char a2, unsigned char b1, unsigned char b2, bool up_down = true) {
	unsigned char i = 0;
	servo[a1] += LEG_UP_DOWN_HEIGHT;
	servo[a2] -= LEG_UP_DOWN_HEIGHT;
	servo_counter = LEG_UP_WAIT;
	while (servo_counter > 0) asm("NOP");
	
	if (up_down) {
		servo[b1] -= STEP_SIZE;
		servo[b2] -= STEP_SIZE;
	} else {
		servo[b1] += STEP_SIZE;
		servo[b2] += STEP_SIZE;
	}
	servo_counter = LEG_MOVE_WAIT;
	while (servo_counter > 0) asm("NOP");

	for (i=0; i<LEG_UP_DOWN_HEIGHT; i++) {
		servo[a1]--;
		servo[a2]++;
		servo_counter = (i*LEG_DOWN_SPEED);
		while (servo_counter > 0) asm("NOP");
	}
}	

ISR(TIMER0_COMPA_vect) {
	if (servo_cnt8_2 == 0) {
		if (servo_cnt8_1 < servo[0])  asm("SBI 0x0E,3"); else asm("CBI 0x0E,3");
		if (servo_cnt8_1 < servo[1])  asm("SBI 0x05,7"); else asm("CBI 0x05,7");
		if (servo_cnt8_1 < servo[2])  asm("SBI 0x05,6"); else asm("CBI 0x05,6");
		if (servo_cnt8_1 < servo[3])  asm("SBI 0x05,5"); else asm("CBI 0x05,5");
		if (servo_cnt8_1 < servo[4])  asm("SBI 0x05,4"); else asm("CBI 0x05,4");
		if (servo_cnt8_1 < servo[5])  asm("SBI 0x11,0"); else asm("CBI 0x11,0");
		if (servo_cnt8_1 < servo[6])  asm("SBI 0x11,1"); else asm("CBI 0x11,1");
		if (servo_cnt8_1 < servo[7])  asm("SBI 0x11,2"); else asm("CBI 0x11,2");
		if (servo_cnt8_1 < servo[8])  asm("SBI 0x11,3"); else asm("CBI 0x11,3");
		if (servo_cnt8_1 < servo[9])  asm("SBI 0x11,4"); else asm("CBI 0x11,4");
		if (servo_cnt8_1 < servo[10]) asm("SBI 0x11,5"); else asm("CBI 0x11,5");
		if (servo_cnt8_1 < servo[11]) asm("SBI 0x11,6"); else asm("CBI 0x11,6");
		if (servo_cnt8_1 < servo[12]) asm("SBI 0x11,7"); else asm("CBI 0x11,7");
	}
	
	if ((servo_cnt8_1 == 0) && (servo_counter > 0)) servo_counter--;
	
	servo_cnt8_1++;
	if (servo_cnt8_1 >= 95) {
		servo_cnt8_1 = 0;
		servo_cnt8_2++;
		if (servo_cnt8_2 >= 10) {
			servo_cnt8_2 = 0;
			ir1[ir_counter] = (IR_1_PORT & IR_1) ? 1 : 0;
			ir2[ir_counter] = (IR_2_PORT & IR_2) ? 1 : 0;
			ir_counter++;
			if (ir_counter > IR_MAX_ARRAY) ir_counter = 0;
		}
	}		
	TCNT0 = 0;
}

void servo_init(void) {
	TCCR0A = 0;
	TCCR0B = (1 << CS01) | (1 << CS00);
	TIMSK0 = (1 << OCIE0A);
	OCR0A  = 5;
	DDRE |= (1 << PINE3);
	DDRB |= (1 << PINB7) | (1 << PINB6) | (1 << PINB5) | (1 << PINB4);
	DDRF |= 0xFF; 
}

void go_next(void) {
	__macro_0001_move(12, 1, 9, 5, true);
	__macro_0001_move(10, 3, 7, 4, true);
	__macro_0001_move(11, 2, 8, 6, true);
	return;
}

void go_back(void) {
	__macro_0001_move(12, 1, 9, 5, false);
	__macro_0001_move(10, 3, 7, 4, false);
	__macro_0001_move(11, 2, 8, 6, false);
	return;
}

void rotate_right(void) {
	__macro_0002_move(true);
	__macro_0003_move(12, 1, 9, 5, true);
	__macro_0003_move(10, 3, 7, 4, true);
	__macro_0003_move(11, 2, 8, 6, true);
	return;
}

void rotate_left(void) {
	__macro_0002_move(false);
	__macro_0003_move(12, 1, 9, 5, false);
	__macro_0003_move(10, 3, 7, 4, false);
	__macro_0003_move(11, 2, 8, 6, false);
	return;
}

void servo_set(unsigned long a, unsigned long b) {
	servo[a] = (unsigned char)b;
	return;
}

void ir_init(void) {
	DDRH &= ~(1 << 6);
	DDRK &= ~(1 << 7);
	PORTH |= (1 << 6);
	PORTK |= (1 << 7);
}

bool ir_read_right(void) {
	unsigned char i = 0;
	unsigned char irx_counter = 0;
	for (i=0; i<IR_MAX_ARRAY; i++) {
		if (ir1[i] == 0) irx_counter++;
	}
	if (irx_counter > (IR_MAX_ARRAY - 3)) return true; else return false;
}

bool ir_read_left(void) {
	unsigned char i = 0;
	unsigned char irx_counter = 0;
	for (i=0; i<IR_MAX_ARRAY; i++) {
		if (ir2[i] == 0) irx_counter++;
	}
	if (irx_counter > (IR_MAX_ARRAY - 3)) return true; else return false;
}