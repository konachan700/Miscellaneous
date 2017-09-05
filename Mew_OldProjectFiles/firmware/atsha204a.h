#ifndef __BOARD_MEW_ATSHA204A__
#define __BOARD_MEW_ATSHA204A__ 

#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/cm3/assert.h>
#include <libopencm3/stm32/flash.h>
#include <libopencm3/stm32/f2/rng.h>
#include <libopencm3/cm3/nvic.h>
#include <libopencm3/stm32/exti.h>
#include <libopencm3/stm32/timer.h>
#include <libopencm3/stm32/usart.h>
#include <libopencm3/stm32/dma.h>
#include <libopencm3/stm32/i2c.h>

#include "board.h" 


extern u32 i2c_atsha204a_read_status(u8* buffer, u16 count);
extern u32 i2c_atsha204a_random(u8* buffer);


#endif
