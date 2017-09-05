
#include "mt10t7.h"

char __mt10t7_get_last(long val) {
  long i = 0;
  i = val / 10;
  return (val - (i*10));
}

void __mt10t7_write(char lbyte, char hbyte, bool addr) {
  lbyte &= 0b00001111;
  hbyte &= 0b00001111;
  
  #ifdef MT10T7_DEBUG_LED
  MT10T7_PORT_LED ^= (1 << MT10T7_BIT_LED);
  #endif
  
  MT10T7_PORT_WR1 &= ~(1 << MT10T7_BIT_WR1);
  if (addr == true) MT10T7_PORT_A0 &= ~(1 << MT10T7_BIT_A0); else MT10T7_PORT_A0 |= (1 << MT10T7_BIT_A0);
  
  if (lbyte & 0b00000001) MT10T7_PORT_DB0 |= (1 << MT10T7_BIT_DB0); else MT10T7_PORT_DB0 &= ~(1 << MT10T7_BIT_DB0);
  if (lbyte & 0b00000010) MT10T7_PORT_DB1 |= (1 << MT10T7_BIT_DB1); else MT10T7_PORT_DB1 &= ~(1 << MT10T7_BIT_DB1);
  if (lbyte & 0b00000100) MT10T7_PORT_DB2 |= (1 << MT10T7_BIT_DB2); else MT10T7_PORT_DB2 &= ~(1 << MT10T7_BIT_DB2);
  if (lbyte & 0b00001000) MT10T7_PORT_DB3 |= (1 << MT10T7_BIT_DB3); else MT10T7_PORT_DB3 &= ~(1 << MT10T7_BIT_DB3);
	  
  if (addr == false) { 
	  MT10T7_PORT_WR1 |= (1 << MT10T7_BIT_WR1);
	  _delay_ms(2);
	  MT10T7_PORT_WR1 &= (~(1 << MT10T7_BIT_WR1));
	  
	  if (hbyte & 0b00000001) MT10T7_PORT_DB0 |= (1 << MT10T7_BIT_DB0); else MT10T7_PORT_DB0 &= ~(1 << MT10T7_BIT_DB0);
	  if (hbyte & 0b00000010) MT10T7_PORT_DB1 |= (1 << MT10T7_BIT_DB1); else MT10T7_PORT_DB1 &= ~(1 << MT10T7_BIT_DB1);
	  if (hbyte & 0b00000100) MT10T7_PORT_DB2 |= (1 << MT10T7_BIT_DB2); else MT10T7_PORT_DB2 &= ~(1 << MT10T7_BIT_DB2);
	  if (hbyte & 0b00001000) MT10T7_PORT_DB3 |= (1 << MT10T7_BIT_DB3); else MT10T7_PORT_DB3 &= ~(1 << MT10T7_BIT_DB3);
  }
  MT10T7_PORT_WR1 |= (1 << MT10T7_BIT_WR1);
  _delay_ms(2);
  return;
}

void mt10t7_numWrite(char number, char pos, char is_dec) {
  __mt10t7_write(pos, 0, true);
  if (is_dec > 1) is_dec = 1;
  if (number == 0) {
    __mt10t7_write(MT10T7_NUM_0_L, MT10T7_NUM_0_H | is_dec, false);
  } else if (number == 1) {
    __mt10t7_write(MT10T7_NUM_1_L, MT10T7_NUM_1_H | is_dec, false);
  } else if (number == 2) {
    __mt10t7_write(MT10T7_NUM_2_L, MT10T7_NUM_2_H | is_dec, false);
  } else if (number == 3) {
    __mt10t7_write(MT10T7_NUM_3_L, MT10T7_NUM_3_H | is_dec, false);
  } else if (number == 4) {
    __mt10t7_write(MT10T7_NUM_4_L, MT10T7_NUM_4_H | is_dec, false);
  } else if (number == 5) {
    __mt10t7_write(MT10T7_NUM_5_L, MT10T7_NUM_5_H | is_dec, false);
  } else if (number == 6) {
    __mt10t7_write(MT10T7_NUM_6_L, MT10T7_NUM_6_H | is_dec, false);
  } else if (number == 7) {
    __mt10t7_write(MT10T7_NUM_7_L, MT10T7_NUM_7_H | is_dec, false);
  } else if (number == 8) {
    __mt10t7_write(MT10T7_NUM_8_L, MT10T7_NUM_8_H | is_dec, false);
  } else if (number == 9) {
    __mt10t7_write(MT10T7_NUM_9_L, MT10T7_NUM_9_H | is_dec, false);
  } else {
    __mt10t7_write(0, 0, false);
  }
}

void mt10t7_display_number(long val) {
  char i = 0;
  char buf = 0;
  for (i=9; i>=0; i--) {
    buf = __mt10t7_get_last(val);
    mt10t7_numWrite(buf, i, 0);
    val = val / 10;
    if (val == 0) return;
  }
}

#ifdef MT10T7_DEBUG_CONN
void __mt10t7_power_init(void) {
	MT10T7_DDR_E  |= (1 << MT10T7_BIT_E);
	MT10T7_PORT_E |= (1 << MT10T7_BIT_E);
	
	MT10T7_DDR_V0  |= (1 << MT10T7_BIT_V0);
	MT10T7_PORT_V0 &= ~(1 << MT10T7_BIT_V0);
	
	MT10T7_DDR_GND  |= (1 << MT10T7_BIT_GND);
	MT10T7_PORT_GND &= ~(1 << MT10T7_BIT_GND);
}
#endif

#ifdef MT10T7_DEBUG_LED
void __mt10t7_led_init(void) {
    MT10T7_DDR_LED  |= (1 << MT10T7_BIT_LED);
	MT10T7_PORT_LED &= ~(1 << MT10T7_BIT_LED);
}
#endif

void mt10t7_display_init(void) {
	#ifdef MT10T7_DEBUG_CONN
	__mt10t7_power_init();
	#endif
	#ifdef MT10T7_DEBUG_LED
	__mt10t7_led_init();
	#endif
	MT10T7_DDR_DB0  |= (1 << MT10T7_BIT_DB0);
	MT10T7_PORT_DB0 &= ~(1 << MT10T7_BIT_DB0);
	
	MT10T7_DDR_DB1  |= (1 << MT10T7_BIT_DB1);
	MT10T7_PORT_DB1 &= ~(1 << MT10T7_BIT_DB1);
	
	MT10T7_DDR_DB2  |= (1 << MT10T7_BIT_DB2);
	MT10T7_PORT_DB2 &= ~(1 << MT10T7_BIT_DB2);
	
	MT10T7_DDR_DB3  |= (1 << MT10T7_BIT_DB3);
	MT10T7_PORT_DB3 &= ~(1 << MT10T7_BIT_DB3);
	
    MT10T7_DDR_WR1  |= (1 << MT10T7_BIT_WR1);
	MT10T7_PORT_WR1 &= ~(1 << MT10T7_BIT_WR1);
	
	MT10T7_DDR_WR2  |= (1 << MT10T7_BIT_WR2);
	MT10T7_PORT_WR2 &= ~(1 << MT10T7_BIT_WR2);
	
	MT10T7_DDR_A0   |= (1 << MT10T7_BIT_A0);
	MT10T7_PORT_A0  &= ~(1 << MT10T7_BIT_A0);
	
	__mt10t7_write(0, 0, true);
	for (int i=0; i<10; i++) __mt10t7_write(0, 0, false);
	
	return;
}

void mt10t7_clear(void) {
	__mt10t7_write(0, 0, true);
	for (int i=0; i<10; i++) __mt10t7_write(0, 0, false);
	
	return;
}