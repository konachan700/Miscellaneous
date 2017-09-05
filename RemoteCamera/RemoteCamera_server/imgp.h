/*****************************************************************************************************/

// Отладочные сообщения, при впиливании в робот лучше отключить, ибо в них нет особого смысла
//#define DEBUG 				1
//#define DEBUG1 				1

/*****************************************************************************************************/

#define RGB_SIZE 			3
#define YUYV_SIZE 			4
#define IMAGE_QUALITY			80
#define BMP32_PALETTE_SIZE 		32

#define FLAG_CONV_NOCOLOR		2
#define FLAG_CONV_RGB			1
#define FLAG_CONV_BGR			0

#pragma pack(2)
#pragma push(pack(2))
struct RGB2 {
  unsigned char r1;
  unsigned char g1;
  unsigned char b1;
  unsigned char r2;
  unsigned char g2;
  unsigned char b2;
};

struct BGR2 {
  unsigned char b1;
  unsigned char g1;
  unsigned char r1;
  unsigned char b2;
  unsigned char g2;
  unsigned char r2;
};

struct YUYV {
  unsigned char y0;
  unsigned char u0;
  unsigned char y1;
  unsigned char v0;
};

typedef struct tagBITMAPFILEHEADER
{
   unsigned short int bfType;
   unsigned int   bfSize;
   unsigned short int    bfReserved1;
   unsigned short int    bfReserved2;
   unsigned int   bfOffBits;
}
BITMAPFILEHEADER, *PBITMAPFILEHEADER;

typedef struct tagBITMAPINFOHEADER
 {
   unsigned int  biSize;
   signed int   biWidth;
   signed int   biHeight;
   unsigned short int   biPlanes;
   unsigned short int   biBitCount;
   unsigned int  biCompression;
   unsigned int  biSizeImage;
   signed int   biXPelsPerMeter;
   signed int   biYPelsPerMeter;
   unsigned int  biClrUsed;
   unsigned int  biClrImportant;
} 
BITMAPINFOHEADER, *PBITMAPINFOHEADER;
#pragma pop

extern int yuyv_to_jpeg(const void *yuyv_array, int size, int img_h, int img_w, int socket, int iscolor) ;
extern int yuyv_to_bmp32(const void *yuyv_array, int size, int img_h, int img_w, int socket, int iscolor);


