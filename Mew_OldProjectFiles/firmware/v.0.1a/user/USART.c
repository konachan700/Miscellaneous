/****************************************************************************
* �ļ�����USART.c
* ���ݼ���: 
*			      ���ڳ�ʼ������غ�������
*			      ��ʼ������������ԭ��
*			                           1����ʹ�����ʱ�ӣ�2���������� 3��Ҫ���ж��������ж���������
*�ļ���ʷ��
*			�汾��	  ��������		����
*			 v0.1	 2012/11/02	   TianHei
*��ϵ��ʽ��Qq:763146170  Email��763146170@qq.com
* ˵    ����
****************************************************************************/

#include "stm32f10x.h"
#include "stm32f10x_usart.h"
#include "include.h"
#include <stdio.h>

//uint8_t rec_f = 0;  		      //���ձ�־λ
//uint8_t RxCounter = 0;	      //�����ַ�����
//uint8_t RxBuff[512] = {0};		//���ջ���

/*
 *���� ���ʱ��ʹ��
 *���� ��
 *���� ��
*/
__inline void Uart_ClockInit(void)
{
	/*ʹ�ܶ˿�A��ʱ��*/
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA
							,ENABLE);
	
	/*ʹ�ܴ���1ʱ��*/
	RCC_APB2PeriphClockCmd(RCC_APB2Periph_USART1, ENABLE);
}

/*
 *���� ����1���ų�ʼ������
 *���� ��
 *���� ��
*/
__inline void Uart_GPIOInit(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9;				//USART1 TX
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;			//�����������
	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;		
	GPIO_Init(GPIOA, &GPIO_InitStructure);					//A�˿�

	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;				//USART1 RX
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;	//���ÿ�©����
//	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;
	GPIO_Init(GPIOA, &GPIO_InitStructure);					//A�˿�
}

/*
 *���� ���ڳ�ʼ������
 *���� USARTx��Ҫ��ʼ���Ĵ��ں�
 *���� ��
*/
void USART_InitConfig(void)
{
	USART_InitTypeDef USART_InitStructure;
  
	/*ʱ�ӳ�ʼ��*/
	Uart_ClockInit();
	
	/*���ų�ʼ��*/
	Uart_GPIOInit();
	
	USART_InitStructure.USART_BaudRate = 115200;					           //����115200bps
	USART_InitStructure.USART_WordLength = USART_WordLength_8b;		 //����λ8λ
	USART_InitStructure.USART_StopBits = USART_StopBits_1;			   //ֹͣλ1λ
	USART_InitStructure.USART_Parity = USART_Parity_No;				     //��У��λ
	USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;		 //��Ӳ������
	USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;						 //�շ�ģʽ

	USART_Init(USART1, &USART_InitStructure);							//���ô��ڲ�������

//	USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);                    //ʹ�ܽ����ж�
//	USART_ITConfig(USARTx, USART_IT_TXE, ENABLE);						        //ʹ�ܷ��ͻ�����ж�

	USART_Cmd(USART1, ENABLE);							//ʹ�ܴ���
}

/*
 * ���� ���ڷ���һ�ֽ�����
 * ���� USARTx�����ں�
 * ���� m_char��Ҫ���͵�����
 * ���� �� 
 */
void USARTx_PrintfChar(USART_TypeDef* USARTx, uint8_t m_char)
{
	USART_SendData(USARTx, m_char);
  while(USART_GetFlagStatus(USARTx, USART_FLAG_TXE) == RESET);
}

/*
 * ���� ���ڷ���һ�ֽ�����
 * ���� USARTx�����ں�
 * ���� pm_string��ָ��Ҫ�����ַ�����ָ��
 * ���� �� 
 */
void USARTx_PrintfString(USART_TypeDef* USARTx, uint8_t* pm_string)
{
	while((*pm_string) != '\0')
	{
		USARTx_PrintfChar(USARTx, *pm_string);
		pm_string++;
	}
}

/*
 * ���� ��ʮ���Ʒ���32λ��������
 * ���� USARTx�����ں�
 * ���� m_uint32��Ҫ���͵���
 * ���� �� 
 */
void USARTx_PrintfUint32(USART_TypeDef* USARTx, uint32_t m_uint32)
{
	uint8_t i;
	uint8_t buff[10];
	for(i=9;i!=0;i--)
	{
	 	buff[i] = m_uint32%10 + '0';  //ת�ַ���
		m_uint32 /=	10;
	}
	for(i=0;i<10;i++)
	{
		USARTx_PrintfChar(USARTx, buff[i]);
	}
}

/*
 * ���� �ض���c�⺯��printf��USART3
 * ����
 * ����
 */
#if 1
int fputc(int ch, FILE *f)
{
  /*��Printf���ݷ�������*/
  USART_SendData(USART1, (unsigned char) ch);
  while (!(USART1->SR & USART_FLAG_TXE));
  return (ch);
}
#endif

