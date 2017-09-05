
#ifndef __ADXL345_H__
#define __ADXL345_H__

/*����������ַ*/

//#define IIC_ACC_ADDR 0x1D	  	//��ע��˴� SDO�ѽ��������� SDOĬ�ϸߵ�ƽ IIC��ַΪ0X1D  ���ӵ���IIC��ַΪ0x53  
							
#define ADXL345_Addr 0x3A		//��д��ַҲҪ����SDO�ĵ�ƽ�޸�


/*�Ĵ�����ַ����*/

#define THRESH_TAP 0x1D    //�û��¼���ֵ

#define OFSX 0x1E   	     //ƫ�ƼĴ���X

#define OFSY 0x1F 		     //ƫ�ƼĴ���Y

#define OFSZ 0x20 		     //ƫ�ƼĴ���Z

#define DUR	0x21					 //�趨�����û��¼������ʱ��Ϊ0����õ�����˫������

#define Latent 0x22  			 //ʱ����أ�Ϊ0����˫������

#define Window 0x23 			 //ʱ����أ�Ϊ0����˫������

#define THRESH_ACT 0x24		 //���ֵ

#define THRESH_INACT 0x25  //���澲ֹ��ֵ

#define TIME_INACT 0x26    //ʱ�����

#define ACT_INACT_CTL 0x27 //�ж����ʹ�ܽ�ֹ

#define THRESH_FF 0x28     //���ڼ���Ƿ������������¼�

#define TIME_FF 0x29       //�����������ʱ�����

#define TAP_AXES 0x2A      //�û������أ�Ϊ1ʹ�ܸ�������û���⣬Ϊ0���û����������ų�ѡ����

#define ACT_TAP_STATUS 0x2B //�����������¼����������ã�Ϊ0�����߲�����

#define BW_RATE 0x2C       //���ĺ�����ѡ��Ϊ1�ǵ͹�����������

#define POWER_CTL 0x2D 		 //����״̬���

#define INT_ENABLE 0x2E 	 //����ж�ʹ��λ����

#define INT_MAP 0x2F 			 //�жϷ�������ӳ������

#define INT_SOURCE 0x30    //ֻ�����жϱ�־λ

#define DATA_FORMAT 0x31   //���ݸ�ʽ�������

#define DATAAX0 0x32       //X������0	Ϊ����Ч�ֽ�

#define DATAAX1 0x33       //X������1	Ϊ����Ч�ֽ�

#define DATAAY0 0x34       //Y������0

#define DATAAY1 0x35       //Y������1

#define DATAAZ0 0x36       //Z������0

#define DATAAZ1 0x37       //Z������1

#define FIFO_CTL 0x38      //����FIF0��ģʽ

#define	FIFO_STATUS 0x39   //FIFO���


/*������������*/

typedef struct
{
  int16_t ax;
  int16_t ay;
  int16_t az;   
}ADXL345_TYPE;


/*��������*/

void ADXL345_Init(void);

void ADXL345_Calibrate(void);

void ADXL345_Read(ADXL345_TYPE * ptResult);

void ADXL345_MultRead(ADXL345_TYPE * ptResult);

void ADXL345_Printf(ADXL345_TYPE * ptResult);

//void ADXL345_ReadData(s16 * pwGyroX, s16 * pwGyroY, s16 * pwGyroZ);

#endif

