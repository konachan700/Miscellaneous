#include <avr/io.h>
#include <math.h>
#include "util/delay.h"

#ifndef MT10T7_H_
#define MT10T7_H_

#define MT10T7_DEBUG_CONN 1
//#define MT10T7_DEBUG_LED  1

#ifdef MT10T7_DEBUG_LED
  #define MT10T7_DDR_LED    DDRB
  #define MT10T7_PORT_LED   PORTB
  #define MT10T7_BIT_LED    7 //13
#endif

#ifdef MT10T7_DEBUG_CONN
  #define MT10T7_DDR_E      DDRC
  #define MT10T7_PORT_E     PORTC
  #define MT10T7_BIT_E	    6 //31
  
  #define MT10T7_DDR_V0     DDRC
  #define MT10T7_PORT_V0    PORTC
  #define MT10T7_BIT_V0     4 //33
  
  #define MT10T7_DDR_GND    DDRC
  #define MT10T7_PORT_GND   PORTC 
  #define MT10T7_BIT_GND    2 //35
#endif

#define MT10T7_DDR_DB0      DDRC
#define MT10T7_PORT_DB0     PORTC
#define MT10T7_BIT_DB0      0 //37

#define MT10T7_DDR_DB1      DDRG
#define MT10T7_PORT_DB1     PORTG
#define MT10T7_BIT_DB1      2 //39

#define MT10T7_DDR_DB2      DDRG
#define MT10T7_PORT_DB2     PORTG
#define MT10T7_BIT_DB2      0 //41

#define MT10T7_DDR_DB3      DDRL
#define MT10T7_PORT_DB3     PORTL
#define MT10T7_BIT_DB3      6 //43

#define MT10T7_DDR_WR1      DDRL
#define MT10T7_PORT_WR1     PORTL
#define MT10T7_BIT_WR1      4 //45

#define MT10T7_DDR_WR2      DDRL
#define MT10T7_PORT_WR2     PORTL
#define MT10T7_BIT_WR2      2 //47

#define MT10T7_DDR_A0       DDRL
#define MT10T7_PORT_A0      PORTL
#define MT10T7_BIT_A0       0 //49

#define MT10T7_NUM_0_L 0b00001110
#define MT10T7_NUM_0_H 0b00001110
#define MT10T7_NUM_1_L 0b00000000
#define MT10T7_NUM_1_H 0b00000110
#define MT10T7_NUM_2_L 0b00001111 
#define MT10T7_NUM_2_H 0b00000010
#define MT10T7_NUM_3_L 0b00001101 
#define MT10T7_NUM_3_H 0b00000110
#define MT10T7_NUM_4_L 0b00000001
#define MT10T7_NUM_4_H 0b00001110
#define MT10T7_NUM_5_L 0b00001101
#define MT10T7_NUM_5_H 0b00001100
#define MT10T7_NUM_6_L 0b00001111 
#define MT10T7_NUM_6_H 0b00001100
#define MT10T7_NUM_7_L 0b00001000
#define MT10T7_NUM_7_H 0b00000110
#define MT10T7_NUM_8_L 0b00001111
#define MT10T7_NUM_8_H 0b00001110
#define MT10T7_NUM_9_L 0b00001101
#define MT10T7_NUM_9_H 0b00001110

extern void mt10t7_clear(void);
extern void mt10t7_display_init(void);
extern void mt10t7_numWrite(char number, char pos, char is_dec);
extern void mt10t7_display_number(long val);

#endif /* MT10T7_H_ */