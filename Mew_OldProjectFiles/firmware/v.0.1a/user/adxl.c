/****************************************************************************
* �ļ�����adxl.c
* ���ݼ���: 
*			      ���ٶȴ�����ADXL345��غ���
*			   
*			                          
*�ļ���ʷ��
*			�汾��	  ��������		����
*			 v0.1	 2012/11/03	   TianHei
*��ϵ��ʽ��Qq:763146170  Email��763146170@qq.com
* ˵    �������ֳ�����������
****************************************************************************/

#include "include.h"
#include "adxl.h"
#include "I2C.h"
#include <stdio.h>
#include <math.h>


/*
 *���� ADCL345��ʼ������
 *���� ��
 *���� ��
*/
void ADXL345_Init(void)
{
	 /*д0��ֹ��˫������*/
	 Single_Write(ADXL345_Addr,DUR,0);

	 /*ֱ����Ϸ�ʽ�������ֹ����*/
	 Single_Write(ADXL345_Addr,ACT_INACT_CTL,0);

	 /*�������û����*/
	 Single_Write(ADXL345_Addr,TAP_AXES,0);

	 /*��������ģʽ��ת������Ϊ100hz����������ʣ�Ĭ��Ϊ0x0a*/
	 Single_Write(ADXL345_Addr,BW_RATE,0x0A);

	 /*����ģʽ��������*/
	 Single_Write(ADXL345_Addr,POWER_CTL,0x08);

	 /*��ֹ�����ж�*/
	 Single_Write(ADXL345_Addr,INT_ENABLE,0);

	 /*�����ж�����ӳ�䣬Ϊ0�����ж�0������*/
	 Single_Write(ADXL345_Addr,INT_MAP,0);

	 /*��ֹ�Բ�����4��SPIģʽ���жϸߵ�ƽ��Ч��ȫ�ֱ���ģʽ�������Ҷ��룬+-16g��Χ*/
	 Single_Write(ADXL345_Addr,DATA_FORMAT,0x0B);

	 /*��·FIFO*/
	 Single_Write(ADXL345_Addr,FIFO_CTL,0);
}

/*
 *���� ADCL345У׼
 *���� ��
 *���� ��
*/
void ADXL345_Calibrate(void)
{
	 uint8_t i;
   ADXL345_TYPE tmp_adxl345;
   int32_t ax,ay,az;
    
//   Delayms(2);
//   Single_Write(ADXL345_Addr,0x31,0x0B);   //������Χ,����16g��13λģʽ
//   Single_Write(ADXL345_Addr,0x2C,0x0e);   //�����趨Ϊ100hz �ο�pdf13ҳ
//   Single_Write(ADXL345_Addr,0x2D,0x08);   //ѡ���Դģʽ   �ο�pdf24ҳ
//   Single_Write(ADXL345_Addr,0x2E,0x80);   //ʹ�� DATA_READY �ж�
//   Single_Write(ADXL345_Addr,0x1E,0);      //�Ƚ�������������,Ȼ����У��
//   Single_Write(ADXL345_Addr,0x1F,0);
//   Single_Write(ADXL345_Addr,0x20,0);
    
   ax = ay = az = 0;
   for(i=0; i<100; i++)
   {
//      Delayms(12);
      ADXL345_Read(&tmp_adxl345);
      ax += tmp_adxl345.ax;
      ay += tmp_adxl345.ay;
      az += tmp_adxl345.az;
   }
   ax = -(ax/400);
   ay = -(ay/400);
   az = -(az/100 -256)/4;
   //if(abs(ax) > 255)ax=0;
   //if(abs(ay) > 255)ay=0;
   //if(abs(az) > 255)az=0;
   //����ƫ��ֵ
   Single_Write(ADXL345_Addr,0x1E,(uint8_t)ax);
   Single_Write(ADXL345_Addr,0x1F,(uint8_t)ay);
   Single_Write(ADXL345_Addr,0x20,(uint8_t)az);
}

/*
 *���� ��ȡADCL345����
 *���� ptResult��ָ�������ݵ�ָ��
 *���� ��
*/
void ADXL345_Read(ADXL345_TYPE* ptResult)
{
    uint8_t tmp[6];
    
    tmp[0]=Single_Read(ADXL345_Addr,0x32);//OUT_X_L_A
    tmp[1]=Single_Read(ADXL345_Addr,0x33);//OUT_X_H_A
    
    tmp[2]=Single_Read(ADXL345_Addr,0x34);//OUT_Y_L_A
    tmp[3]=Single_Read(ADXL345_Addr,0x35);//OUT_Y_H_A
    
    tmp[4]=Single_Read(ADXL345_Addr,0x36);//OUT_Z_L_A
    tmp[5]=Single_Read(ADXL345_Addr,0x37);//OUT_Z_H_A
    
    ptResult->ax    = (int16_t)((tmp[1]<<8)+tmp[0]);  //�ϳ�����
    ptResult->ay    = (int16_t)((tmp[3]<<8)+tmp[2]);
    ptResult->az    = (int16_t)((tmp[5]<<8)+tmp[4]);
}

/*
 *���� ��ȡADCL345���ݣ�����������ȡ�ķ�ʽ
 *���� ptResult��ָ�������ݵ�ָ��
 *���� ��
*/
void ADXL345_MultRead(ADXL345_TYPE* ptResult)
{
    uint8_t tmp[6];
    
		I2C_ReadData(ADXL345_Addr, 0x32, tmp, 6);
//    if(true == Mult_Read(ADXL345_Addr,0x32,tmp,6))
//    {   //ptResult->ax    = (int16_t)((tmp[1]<<8)+tmp[0]);  //�ϳ�����
        //ptResult->ay    = (int16_t)((tmp[3]<<8)+tmp[2]);
        //ptResult->az    = (int16_t)((tmp[5]<<8)+tmp[4]);
        ptResult->ax      = *( (int16_t *)(&tmp[0]) );      //�Ż�Ч������
        ptResult->ay      = *( (int16_t *)(&tmp[2]) );
        ptResult->az      = *( (int16_t *)(&tmp[4]) );
//    }
}

/*
 *���� ��ӡ������������
 *���� ptResult��ָ�������ݵ�ָ��
 *���� ��
*/
void ADXL345_Printf(ADXL345_TYPE* ptResult)
{
    float tempX,tempY,tempZ;
		float roll,pitch,yaw;
    
    //temp=(float)dis_data*3.9;  //�������ݺ���ʾ,�鿼ADXL345�������ŵ�4ҳ
    tempX = (float)ptResult->ax * 0.0039;
    tempY = (float)ptResult->ay * 0.0039;
    tempZ = (float)ptResult->az * 0.0039;
//    printf("ADXL345:\tax: %.3fg,\tay: %.3fg,\tsz: %.3fg\n\r",tempX,tempY,tempZ);
	
		roll =  (float)(((atan2(tempZ,tempX)*180)/3.1416)-90); //x��Ƕ�
		pitch = (float)(((atan2(tempZ,tempY)*180)/3.1416)-90); //y��Ƕ�
		yaw =   (float)((atan2(tempX,tempY)*180)/3.1416);      //Z��Ƕ�
		printf("ADXL345:\tax: %.3fg,\tay: %.3fg,\tsz: %.3fg\n\r",roll,pitch,yaw);
}




#if 0
/*
 *���� ADCL345��ʼ������
 *���� ��
 *���� ��
*/
void ADXL345_Init(void)
{
	u8 reg_addr = 0;
	u8 byData[13] = {0};
	
  //����ͨ�Ÿ�ʽ;����Ϊ�Լ칦�ܽ���,4����SPI�ӿ�,�͵�ƽ�ж����,13λȫ�ֱ���,��������Ҷ���,16g����
  reg_addr = 0x31;
  byData[0] = 0x2B;
	
  I2C_WriteData(IIC_ACC_WR, reg_addr, byData, 1);			//0x31�Ĵ�����ʼ����	 DATA_FORMAT

  reg_addr = 0x1E;

  byData[0] = 0x00;  //X������; (15.6mg/LSB)
  byData[1] = 0x00;  //Y������; (15.6mg/LSB)
  byData[2] = 0x00;  //Z������; (15.6mg/LSB)
  byData[3] = 0x00;  //�û���ʱ0:����; (1.25ms/LSB)
  byData[4] = 0x00;  //����һ���û������ʱ0:����; (1.25ms/LSB)
  byData[5] = 0x00;  //�û�����0:����; (1.25ms/LSB)
  byData[6] = 0x01;  //��������ֵ; (62.5mg/LSB)
  byData[7] = 0x01;  //�����⾲ֹ��ֵ; (62.5mg/LSB)
  byData[8] = 0x2B;  //���ʱ�䷧ֵ; (1s/LSB)
  byData[9] = 0x00;
  byData[10] = 0x09;   //�����������Ƽ���ֵ; (62.5mg/LSB)
  byData[11] =  0xFF;  //����������ʱ�䷧ֵ,����Ϊ���ʱ��; (5ms/LSB)
  byData[12] =  0x80;
  I2C_WriteData(IIC_ACC_WR, reg_addr, byData, 13);
														//0x1E
  reg_addr = 0x2C;

  byData[0] = 0x0A;
  byData[1] = 0x28;  //����Link,��������;�ر��Զ�����,����,���ѹ���
  byData[2] = 0x00;  //���о��ر�
  byData[3] = 0x00;  //�жϹ����趨,��ʹ���ж�
  I2C_WriteData(IIC_ACC_WR, reg_addr, byData, 4);
    
  //FIFOģʽ�趨,Streamģʽ����������INT1,31����������
  reg_addr = 0x38;
  byData[0] = 0x9F;
  I2C_WriteData(IIC_ACC_WR, reg_addr, byData, 1);
}

/*
 *���� ��ADCL345
 *���� pwGyroX�����X�����ݵ�ַ
 *���� pwGyroY�����Y�����ݵ�ַ
 *���� pwGyroZ�����Y�����ݵ�ַ
 *���� ��
*/
void ADXL345_ReadData(s16 * pwGyroX, s16 * pwGyroY, s16 * pwGyroZ)
{
	u8 reg_addr;
  u8 byData[6];
  s16 wTemp;

  reg_addr = 0x32; //���ݴ����ʼ��ַ
    
  I2C_ReadData(IIC_ACC_WR, reg_addr, byData, 6); //������ַ�����ݼĴ�����ַ��������ţ�����

  wTemp  = 0;
  wTemp = byData[1] << 8;		//�����ȵͺ�����
  wTemp |= byData[0];
  *pwGyroX = wTemp;

  wTemp  = 0;
  wTemp = byData[3] << 8;
  wTemp |= byData[2];
  *pwGyroY = wTemp;
    
  wTemp  = 0;
  wTemp = byData[5] << 8;
  wTemp |= byData[4];
  *pwGyroZ = wTemp;
}

#endif
