/*
 * i2c.h
 *
 * Created: 4/11/2015 12:37:10 PM
 *  Author: Konata
 */ 


#ifndef I2C_H_
#define I2C_H_

#define I2C_CPU_SPEED			7372800

#define AT24C16A_ADDR		0xA0		// 0b1010xxxx
#define AT24C16A_3HBITS		0x0700
#define AT24C16A_8LBITS		0x00ff

#define DS1307_ADDR			0xD0
#define DS1307_OUT			(1 << 7)
#define DS1307_SQWE			(1 << 4)
#define DS1307_RS1			(1 << 1)
#define DS1307_RS0			(1)
#define DS1307_CH			(1 << 7)

#define DS1307_SECOND		0x00
#define DS1307_MINUTE		0x01
#define DS1307_HOUR			0x02
#define DS1307_DAY_OF_WEEK	0x03
#define DS1307_DAY			0x04
#define DS1307_MONTH		0x05
#define DS1307_YEAR			0x06
#define DS1307_CONTROL		0x07
#define DS1307_RAM_BEGIN	0x08
#define DS1307_RAM_END		0x3f

#define I2C_READ			0x01
#define I2C_START			0x08
#define I2C_R_START			0x10
#define I2C_SLA_ACK			0x18
#define I2C_DATA_ACK		0x28
#define I2C_MR_DATA_ACK		0x40
#define I2C_MR_BR_ACK		0x50
#define I2C_MR_BR_NACK		0x58

#define I2C_ERR_NO			0x00
#define I2C_ERR_E 			0x01
#define I2C_ERR_OK 			0x02

#define I2C_ERR_START		0x01
#define I2C_ERR_WRITE		0x02
#define I2C_ERR_READ		0x03
#define I2C_ERR_WADDR		0x04

#define I2C_NACK			0x00
#define I2C_ACK				0x01

#define USART_DEBUG 1

typedef unsigned char u8;
typedef unsigned short int u16;
	
extern void		i2c_init(void);

extern void		ds1307_init		(u8 conf);
extern void		ds1307_writeb	(u8 addr, u8 data);
extern void		ds1307_writet	(u8 addr, u8 data);
extern u8		ds1307_readb	(u8 addr);
extern u8		ds1307_readt	(u8 addr)

#endif /* I2C_H_ */