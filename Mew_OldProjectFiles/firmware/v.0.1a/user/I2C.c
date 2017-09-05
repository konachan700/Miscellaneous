/****************************************************************************
* �ļ�����I2C.c
* ���ݼ���: 
*			      ģ��I2C
*			   
*			                          
*�ļ���ʷ��
*			�汾��	  ��������		����
*			 v0.1	 2012/11/03	   TianHei
*��ϵ��ʽ��Qq:763146170  Email��763146170@qq.com
* ˵    ����
****************************************************************************/

#include "include.h"
#include "stm32f10x_gpio.h"
#include "I2C.h"



/*
 *���� I2C��������
 *���� ��
 *���� ��
*/
void I2C_GPIOInit(void)
{
  GPIO_InitTypeDef GPIO_InitStructure;
	
		/*ʹ�ܶ˿�B��ʱ��*/
  RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOB
							,ENABLE);
	
	/* Configure I2C1 pins: SCL and SDA */
  GPIO_InitStructure.GPIO_Pin =  GPIO_Pin_10 | GPIO_Pin_11;
  GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	
	/*��©���ģʽ����Ҫ��������*/
  GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_OD;
	
  GPIO_Init(GPIOB, &GPIO_InitStructure);
	
  SDA_H;						//SDA�ø�
  SCL_H;						//SCL�ø�
}

/*
 *���� I2C��ʱ����
 *���� ��
 *���� ��
*/
void I2C_delay(void)
{	
   u8 i=30; 
   while(i) 
   { 
     i--; 
   } 
}

/*
 *���� ����һ��I2Cͨ��
 *���� ��
 *���� �ɹ�����1��ʧ�ܷ���0
*/
bool I2C_Start(void)
{
	SDA_H;						//SDA�ø�
	SCL_H;						//SCL�ø�
	I2C_delay();
	if(!SDA_read)return FALSE;	//SDA��Ϊ�͵�ƽ������æ,�˳�
	SDA_L;
	I2C_delay();
	if(SDA_read) return FALSE;	//SDA��Ϊ�ߵ�ƽ�����߳���,�˳�
	SDA_L;						//SDA�õ�
	I2C_delay();
	return TRUE;
}

/*
 *���� ֹͣI2C
 *���� ��
 *���� ��
*/
void I2C_Stop(void)
{
	SCL_L;				  
	I2C_delay();
	SDA_L;
	I2C_delay();
	SCL_H;
	I2C_delay();
	SDA_H;
	I2C_delay();
}

/*
 *���� I2C ACKӦ��
 *���� ��
 *���� ��
*/
void I2C_Ack(void)
{	
	SCL_L;
	I2C_delay();
	SDA_L;
	I2C_delay();
	SCL_H;
	I2C_delay();
	SCL_L;
	I2C_delay();
}

/*
 *���� I2C ��Ӧ��
 *���� ��
 *���� ��
*/
void I2C_NoAck(void)
{	
	SCL_L;
	I2C_delay();
	SDA_H;
	I2C_delay();
	SCL_H;
	I2C_delay();
	SCL_L;
	I2C_delay();
}

/*
 *���� I2C �ȴ�Ӧ��
 *���� ��
 *���� ����Ϊ:=1��ACK,=0��ACK
*/
bool I2C_WaitAck(void)
{
	SCL_L;
	I2C_delay();
	SDA_H;			
	I2C_delay();
	SCL_H;
	I2C_delay();
	if(SDA_read)
	{
     SCL_L;
		 I2C_delay();
     return FALSE;
	}
	SCL_L;
	return TRUE;
}

/*
 *���� I2C ����һ�ֽ� ���ݴӸ�λ����λ
 *���� SendByte��Ҫ���ͷ��ֽ�
 *���� ��
*/
void I2C_SendByte(u8 SendByte)
{
  u8 i = 8;
	
  while(i--)
  {
    SCL_L;
    I2C_delay();
    if(SendByte&0x80)
      SDA_H;  
    else 
      SDA_L;   
    SendByte<<=1;
    I2C_delay();
		SCL_H;
    I2C_delay();
  }
	
  SCL_L;
}

/*
 *���� I2C ����һ�ֽ� ���ݴӸ�λ����
 *���� ��
 *���� �յ�������
*/
u8 I2C_ReceiveByte(void)
{ 
  u8 i = 8;
  u8 ReceiveByte = 0;

  SDA_H;				
  while(i--)
  {
    ReceiveByte<<=1;      
    SCL_L;
    I2C_delay();
	  SCL_H;
    I2C_delay();	
    if(SDA_read)
    {
      ReceiveByte|=0x01;
    }
  }
  SCL_L;
	
  return ReceiveByte;
}

/*
 *���� I2C д���ֽں���
 *���� dev_addr��������ַ
 *���� reg_addr��д�����ݵ���ʼ��ַ
 *���� data����Ҫд�������
 *���� �ɹ���ʧ��
*/
bool Single_Write(uint8_t dev_addr,uint8_t reg_Addr,uint8_t data)
{
    if(!I2C_Start())return FALSE;
    I2C_SendByte(dev_addr);   //�����豸��ַ+д�ź�//I2C_SendByte(((REG_Address & 0x0700) >>7) | SlaveAddress & 0xFFFE);//���ø���ʼ��ַ+������ַ 
    if(!I2C_WaitAck()){I2C_Stop(); return FALSE;}
    I2C_SendByte(reg_Addr);   //���õ���ʼ��ַ      
    I2C_WaitAck();  
    I2C_SendByte(data);
    I2C_WaitAck();   
    I2C_Stop(); 
    //delay5ms();
    return TRUE;
}

/*
 *���� I2C ���ֽڶ�����
 *���� dev_addr��������ַ
 *���� reg_addr����ȡ���ݵĵ�ַ
 *���� ��ȡ������
*/
uint8_t Single_Read(uint8_t dev_addr,uint8_t reg_Addr)
{   
    uint8_t REG_data;       
    if(!I2C_Start())return FALSE;
    I2C_SendByte(dev_addr); //I2C_SendByte(((REG_Address & 0x0700) >>7) | REG_Address & 0xFFFE);//���ø���ʼ��ַ+������ַ 
    if(!I2C_WaitAck()){I2C_Stop();return FALSE;}
    I2C_SendByte((u8) reg_Addr);   //���õ���ʼ��ַ      
    I2C_WaitAck();
    I2C_Start();
    I2C_SendByte(dev_addr+1);
    I2C_WaitAck();

    REG_data= I2C_ReceiveByte();
    I2C_NoAck();
    I2C_Stop();
    //return true;
    return REG_data;
}

/*
 *���� I2C д���ݵ�������ָ����ʼ��ַ
 *���� dev_addr��������ַ
 *���� reg_addr��д�����ݵ���ʼ��ַ
 *���� pdata��ָ����Ҫд������ݵ���ʼ��ַ
 *���� count����Ҫд�����ݵĸ���
 *���� ��
*/
void I2C_WriteData(u8 dev_addr, u8 reg_addr, u8 *pdata, u8 count)
{
	u8 i = 0;
	
	I2C_Start();
	I2C_SendByte(dev_addr);				//д��������ַ
	I2C_WaitAck();
	
	I2C_SendByte(reg_addr);				//д���ݵ�ַ
	I2C_WaitAck();
	I2C_Start();                  //������������
	
	for(i=count; i!=0; --i)
	{
		I2C_SendByte(*pdata++);			 //д����
		I2C_WaitAck();
//		pdata++;
	}
	
	I2C_Stop();
}

/*
 *���� I2C ��ȡ����ָ����ʼ��ַ������
 *���� dev_addr��������ַ
 *���� reg_addr����ȡ���ݵ���ʼ��ַ
 *���� pdata��ָ�������ݵĵ�ַ
 *���� count����Ҫ��ȡ���ݵĸ���
 *���� ��
*/
void I2C_ReadData(u8 dev_addr, u8 reg_addr, u8 *pdata, u8 count)
{
	u8 i = 0;
	
	I2C_Start();
	I2C_SendByte(dev_addr);				//д��������ַ
	I2C_WaitAck();
	
	I2C_SendByte(reg_addr);				//д���ݵ�ַ
	I2C_WaitAck();

	I2C_Start();                  //������������
	I2C_SendByte(dev_addr + 1);   //������ַ��һ��ʾҪ��
  I2C_WaitAck();
	
	for(i=1; i<count; ++i)
	{
		*pdata++ = I2C_ReceiveByte();			 //������
		I2C_Ack();
//		pdata++;
	}

	*pdata++ = I2C_ReceiveByte();			 //������
	I2C_NoAck();
	I2C_Stop();
}





