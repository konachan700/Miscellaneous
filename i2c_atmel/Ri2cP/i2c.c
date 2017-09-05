#include <avr/io.h>
#include "i2c.h"
#include "usart.h"

u8 i2c_err		= I2C_ERR_NO;
u8 i2c_err_code	= 0;

void _i2c_stop(u8 errcode, u8 errno) {
	i2c_err = errno;
	i2c_err_code = errcode;
	TWCR = (1 << TWINT) | (1 << TWSTO) | (1 << TWEN);
}

void _i2c_start(void) {
	TWCR = (1 << TWINT) | (1 << TWSTA) | (1 << TWEN) ;
	while ((TWCR & (1 << TWINT)) == 0);
	if (((TWSR & 0xF8) != I2C_START) && ((TWSR & 0xF8) != I2C_R_START)) 
		_i2c_stop((TWSR & 0xF8), I2C_ERR_START);
}

void _i2c_write_da(u8 dev_addr) {
	TWDR = dev_addr;
	TWCR = (1 << TWINT) | (1 << TWEN);
	while (!(TWCR & (1 << TWINT)));
	if ( ((TWSR & 0xF8) != I2C_SLA_ACK) && ((TWSR & 0xF8) != I2C_MR_DATA_ACK) && ((TWSR & 0xF8) != I2C_MR_BR_ACK) )
		_i2c_stop((TWSR & 0xF8), I2C_ERR_WADDR);
}

void _i2c_write(u8 data, u8 ack) {
	TWDR = data;
	TWCR = (1 << TWINT) | (1 << TWEN) | ((ack != I2C_ACK) ? 0 : (1 << TWEA));
	while (!(TWCR & (1 << TWINT)));
	if ((TWSR & 0xF8) != I2C_DATA_ACK)
		_i2c_stop((TWSR & 0xF8), I2C_ERR_WRITE);
}

u8 _i2c_read(u8 ack) {
	TWCR = (1 << TWINT) | (1 << TWEN) | ((ack != I2C_ACK) ? 0 : (1 << TWEA));
	while (!(TWCR & (1 << TWINT)));
	if ((TWSR & 0xF8) == I2C_MR_BR_ACK) {
		return TWDR;
	} else if ((TWSR & 0xF8) == I2C_MR_BR_NACK) 
		return TWDR;
	else 
		_i2c_stop((TWSR & 0xF8), I2C_ERR_READ);
		
	return 0;
}

void i2c_init(void) {
	TWBR = (I2C_CPU_SPEED / 100000UL - 16) / 2;
	TWSR = 0;
}

void ds1307_writeb(u8 addr, u8 data) {
	i2c_err = 0;
	i2c_err_code = 0;
	if (addr > DS1307_RAM_END) return;
	
	_i2c_start();
	if (i2c_err != I2C_ERR_NO) return;
	
	_i2c_write_da(DS1307_ADDR);
	if (i2c_err != I2C_ERR_NO) return;
	
	_i2c_write(addr, I2C_NACK);
	if (i2c_err != I2C_ERR_NO) return;
	
	_i2c_write(data, I2C_NACK);
	if (i2c_err != I2C_ERR_NO) return;
	
	_i2c_stop(I2C_ERR_NO, I2C_ERR_NO);
}

u8 ds1307_readb(u8 addr) {
	i2c_err = 0;
	i2c_err_code = 0;
	if (addr > DS1307_RAM_END) return 0;
	
	_i2c_start();
	if (i2c_err != I2C_ERR_NO) return 0;
	
	_i2c_write_da(DS1307_ADDR);
	if (i2c_err != I2C_ERR_NO) return 0;
	
	_i2c_write(addr, I2C_NACK);
	if (i2c_err != I2C_ERR_NO) return 0;
	
	_i2c_start();
	if (i2c_err != I2C_ERR_NO) return 0;
	
	_i2c_write_da(DS1307_ADDR | I2C_READ);
	if (i2c_err != I2C_ERR_NO) return 0;
	
	u8 retval = _i2c_read(I2C_NACK);
	if (i2c_err != I2C_ERR_NO) return 0;
	
	_i2c_stop(I2C_ERR_NO, I2C_ERR_NO);
	
	return retval;
}

u8 ds1307_readt(u8 addr) {
	u8 ret8 = ds1307_readb(addr);
	u8 byteH = ((ret8 >> 4) * 10), byteL = (ret8 & 0x0f);
	return (byteH + byteL);
}

void ds1307_writet(u8 addr, u8 data) {
	u8 byteH = ((data >> 4) * 10), byteL = (data & 0x0f);
	ds1307_writeb(addr, byteH + byteL);
}

void ds1307_init(u8 conf) {
	u8 ch = ds1307_readb(0x00);
	ds1307_writeb(0x00, ch & (~DS1307_CH));
	ds1307_writeb(0x07, conf);
}






















//
//
//
//
//
//void __x_i2c_init(void) {
	//TWBR = (I2C_CPU_SPEED / 100000UL -16) / 2;
	//TWSR = 0;
//}
//
//void __i_i2c_stop(void) {
	//TWCR = (1 << TWINT) | (1 << TWSTO) | (1 << TWEN);
	//#ifdef USART_DEBUG_STOP
		//__x_usart_putchar('#');
		//__x_usart_putint8h(i2c_err);
		//__x_usart_putchar(':');
		//__x_usart_putint8h(i2c_err_code);
		//__x_usart_putchar(';');
	//#endif
//}
//
//void __i_i2c_start(void) {
	//TWCR = (1 << TWINT) | (1 << TWSTA) | (1 << TWEN) ;
	//while ((TWCR & (1 << TWINT)) == 0);
	//if ( ((TWSR & 0xF8) != I2C_START) && ((TWSR & 0xF8) != I2C_R_START) ) {
		//i2c_err = 1;
		//i2c_err_code = (TWSR & 0xF8);
		//__i_i2c_stop();
	//}
//}
//
//void __i_i2c_write_device_addr(unsigned char dev_addr) {
	//TWDR = dev_addr;
	//TWCR = (1 << TWINT) | (1 << TWEN);
	//while (!(TWCR & (1 << TWINT)));
	//if ((TWSR & 0xF8) != I2C_SLA_ACK) {
		//i2c_err = 2;
		//i2c_err_code = (TWSR & 0xF8);
		//__i_i2c_stop();
	//}
//}
//
//
//
//
//void __i_i2c_write_data(unsigned char data) {
	//TWDR = data;
	//TWCR = (1 << TWINT) | (1 << TWEN);
	//while (!(TWCR & (1 << TWINT)));
	//if ((TWSR & 0xF8) != I2C_DATA_ACK) {
		//i2c_err = 3;
		//i2c_err_code = (TWSR & 0xF8);
		//__i_i2c_stop();
	//}
//}
//
//unsigned char __i_i2c_read_data(unsigned char dev_addr, unsigned char ack) {
	//TWDR = dev_addr | I2C_READ;
	//if (ack == 0) 
		//TWCR = (1 << TWINT) | (1 << TWEN);
	//else
		//TWCR = (1 << TWINT) | (1 << TWEN) | (1 << TWEA);
	//
	//while (!(TWCR & (1 << TWINT)));
	//if ( ((TWSR & 0xF8) != I2C_MR_DATA_ACK) && ((TWSR & 0xF8) != I2C_MR_BR_ACK) ) {
		//i2c_err = 4;
		//i2c_err_code = (TWSR & 0xF8);
		//__i_i2c_stop();
		//return 0;
	//}
	//return TWDR;
//}
//
//void __i_i2c_write_byte_to(unsigned char slave_adddr, unsigned char byte_addrL, unsigned char xbyte) {
	//i2c_err = I2C_ERR_NO;
	//
	//__i_i2c_start();
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_write_device_addr(slave_adddr);
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_write_data(byte_addrL);
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_write_data(xbyte);
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_stop();
//}
//
//unsigned char __x_24c16_read_byte(unsigned char slave_addr, unsigned short int data_addr) {
	//i2c_err = I2C_ERR_NO;
	//
	//unsigned char addrL = (data_addr & AT24C16A_8LBITS);
	//unsigned char addrH = ((data_addr & AT24C16A_3HBITS) >> 7) | AT24C16A_ADDR;
	//
	//__i_i2c_start();
	//__i_i2c_write_device_addr(addrH);
	//__i_i2c_write_data(addrL);
	//__i_i2c_start();
	//unsigned char retVal = __i_i2c_read_data(addrH, 0);
	//__i_i2c_stop();
//
	//__x_usart_putint8h(retVal);
	//__x_usart_putchar(' ');
	//
	//i2c_err = I2C_ERR_OK;
	//return retVal;
//}
//
//void __x_ds1307_read_all(void) {
	//i2c_err = I2C_ERR_NO;
//
	//__i_i2c_start();
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_write_device_addr(DS1307_ADDR);
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_write_data(0x00);
	//if (i2c_err != I2C_ERR_NO) return;
	//
	//__i_i2c_start();
	//if (i2c_err != I2C_ERR_NO) return;
//
	////i2c_ds1307_nvram[0] = __i_i2c_read_data(DS1307_ADDR, 1);
	////__i_i2c_write_device_addr(DS1307_ADDR | I2C_READ);
	//__i_i2c_read_data(DS1307_ADDR, 1);
	//if (i2c_err != I2C_ERR_NO) return;
	//#ifdef USART_DEBUG
		//__x_usart_putchar('S');
		////__x_usart_putint8h(i2c_ds1307_nvram[0]);
	//#endif
	//
	//int i;
	//for (i=0; i<DS1307_NVRAM_SIZE; i++) {
		//TWCR = (1 << TWINT) | (1 << TWEN) | ((i == (DS1307_NVRAM_SIZE-1)) ? 0 : (1 << TWEA));
		//while (!(TWCR & (1 << TWINT)));
		//if ((TWSR & 0xF8) == I2C_MR_BR_ACK) {
			//i2c_ds1307_nvram[i] = TWDR;
			//#ifdef USART_DEBUG
				//__x_usart_putint8h(i2c_ds1307_nvram[i]);
			//#endif
		//} else if ((TWSR & 0xF8) == I2C_MR_BR_NACK) {
			//i2c_ds1307_nvram[i] = TWDR;
			//#ifdef USART_DEBUG
				//__x_usart_putint8h(i2c_ds1307_nvram[i]);
			//#endif
			//break;
		//} else {
			//i2c_err = 5;
			//i2c_err_code = (TWSR & 0xF8);
			//break;
		//}
	//}
	//
	//__i_i2c_stop();
	//
	//#ifdef USART_DEBUG
		//__x_usart_putchar('\r');
	//#endif
//}
//
//void __x_ds1307_init(unsigned char conf_reg) {
	//__i_i2c_write_byte_to(DS1307_ADDR, 0x00, 0);
	//__i_i2c_write_byte_to(DS1307_ADDR, 0x07, conf_reg);
	//#ifdef USART_DEBUG
		//if (i2c_err != I2C_ERR_NO) {
			//__x_usart_putchar('#');
			//__x_usart_putint8h(i2c_err);
			//__x_usart_putchar(':');
			//__x_usart_putint8h(i2c_err_code);
			//__x_usart_putchar(';');
			//__x_usart_putchar('\r');
			//__x_usart_putchar('\n');
		//}
	//#endif
//}
