#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include "jpeglib.h"
#include "./imgp.h"

char clip(short int x){
  if (x > 255) return 255;
  if (x < 0)   return 0;
  return x;
}

void yuv_to_rgb(struct YUYV yuyv_color, struct RGB2 *rgb2_color, int rgb_flags) {
  signed int v128 = (yuyv_color.v0 - 128);
  signed int u128 = (yuyv_color.u0 - 128);

  if (rgb_flags == FLAG_CONV_RGB) {
    rgb2_color->r1 = clip(yuyv_color.y0 + 1.13983 * v128);
    rgb2_color->r2 = clip(yuyv_color.y1 + 1.13983 * v128);
    rgb2_color->g1 = clip(yuyv_color.y0 - (0.39465 * u128) - (0.58060 * v128));
    rgb2_color->g2 = clip(yuyv_color.y1 - (0.39465 * u128) - (0.58060 * v128));
    rgb2_color->b1 = clip(yuyv_color.y0 + 2.03211 * u128);
    rgb2_color->b2 = clip(yuyv_color.y1 + 2.03211 * u128);    
  } else if (rgb_flags == FLAG_CONV_BGR) {
    rgb2_color->r1 = clip(yuyv_color.y0 + 2.03211 * u128);
    rgb2_color->r2 = clip(yuyv_color.y1 + 2.03211 * u128); 
    rgb2_color->g1 = clip(yuyv_color.y0 - (0.39465 * u128) - (0.58060 * v128));
    rgb2_color->g2 = clip(yuyv_color.y1 - (0.39465 * u128) - (0.58060 * v128));
    rgb2_color->b1 = clip(yuyv_color.y0 + 1.13983 * v128);
    rgb2_color->b2 = clip(yuyv_color.y1 + 1.13983 * v128);
  } else if (rgb_flags == FLAG_CONV_NOCOLOR) {
    rgb2_color->r1 = yuyv_color.y0;
    rgb2_color->g1 = yuyv_color.y0;
    rgb2_color->b1 = yuyv_color.y0;
    rgb2_color->r2 = yuyv_color.y1;
    rgb2_color->g2 = yuyv_color.y1;
    rgb2_color->b2 = yuyv_color.y1;
  }
}

int yuyv_to_rgb(const void *yuyv_array, int yuyv_size, void *rgb_out, int img_w, int img_h, int rgb_flags) {
  struct YUYV 	yuyv_color;
  struct RGB2 	rgb_color;
  memset(&rgb_color, 255, (RGB_SIZE*2));
  
  int rgb_size = (img_w * img_h * RGB_SIZE);
  memset(rgb_out, 255, rgb_size);
  
  int i=0, counter = 0;
  for (i=0; i<yuyv_size; i=i+YUYV_SIZE) {
    if ((counter + (RGB_SIZE * 2)) > rgb_size) {
      #ifdef DEBUG
      printf("DEBUG: RGB buffer overflow [yuyv_to_rgb]\n");
      #endif
      return -1;
    }    
    
    memcpy(&yuyv_color, yuyv_array + i, YUYV_SIZE);
    yuv_to_rgb(yuyv_color, &rgb_color, rgb_flags);
    memcpy(rgb_out + counter, &rgb_color, (RGB_SIZE * 2));
    
    counter = counter + (RGB_SIZE * 2);
  }

  return 0;
}

int yuyv_to_jpeg(const void *yuyv_array, int size, int img_h, int img_w, int socket, int iscolor) {
  struct jpeg_compress_struct 	cinfo;
  struct jpeg_error_mgr 	jerr;
  
  JSAMPROW row_pointer[1];
  int row_stride;
  
  FILE *tmp = fopen("/tmp/camjpeg.tmp", "w+");
  if (tmp == NULL) {
    #ifdef DEBUG
    printf("DEBUG: Cannot open temporary file for write jpeg\n");
    #endif
    return -1;
  }
  
  char rgb_buffer[img_w * img_h * RGB_SIZE];
  int retval = yuyv_to_rgb(yuyv_array, size, (void *)rgb_buffer, img_w, img_h, ((iscolor == 0) ? FLAG_CONV_NOCOLOR : FLAG_CONV_RGB));
  if (retval < 0) {
    return -2;
  }
  
  cinfo.err 			= jpeg_std_error(&jerr);
  jpeg_create_compress(&cinfo);
  jpeg_stdio_dest(&cinfo, tmp);
  
  cinfo.image_width 		= img_w;
  cinfo.image_height 		= img_h;
  cinfo.input_components 	= RGB_SIZE;
  cinfo.in_color_space 		= JCS_RGB;
  
  jpeg_set_defaults(&cinfo);
  jpeg_set_quality(&cinfo, IMAGE_QUALITY, TRUE);
  
  jpeg_start_compress(&cinfo, TRUE);
  
  row_stride = img_w * RGB_SIZE;
  while (cinfo.next_scanline < cinfo.image_height) {
    row_pointer[0] = &rgb_buffer[cinfo.next_scanline * row_stride];
    jpeg_write_scanlines(&cinfo, row_pointer, 1);
  }
  
  jpeg_finish_compress(&cinfo);
  jpeg_destroy_compress(&cinfo);
  
  fflush(tmp);
  fseek(tmp, 0, SEEK_END);
  int sizeOfJPEG = ftell(tmp);
  fseek(tmp, 0, SEEK_SET);
  
  if (sizeOfJPEG <= 0) {
    #ifdef DEBUG
    printf("DEBUG: Null temporary file\n");
    #endif
    return -3;
  }
  
  char *out_buffer = malloc(sizeOfJPEG);
  fread(out_buffer, 1, sizeOfJPEG, tmp);
  
  int retv = send(socket, out_buffer, sizeOfJPEG, 0);
  #ifdef DEBUG1
  printf("DEBUG: yuyv_to_jpeg().send(xxx)=%d;\n", retv);
  #endif
  
  fclose(tmp);
  free(out_buffer);
  return 0;
}

int yuyv_to_bmp32(const void *yuyv_array, int size, int img_h, int img_w, int socket, int iscolor) {
  BITMAPFILEHEADER bfh;
  BITMAPINFOHEADER bih;
  char Palette [BMP32_PALETTE_SIZE];

  int obs = sizeof(bfh) + sizeof(bih) + BMP32_PALETTE_SIZE + (img_h * img_w * RGB_SIZE);
  char *buf_image = malloc(obs);

  memset(buf_image, 255, obs);
  
  #ifdef DEBUG1
  printf("DEBUG: yuyv_to_bmp32().obs=%d;\n", obs);
  #endif
  
  memset(&bfh, 0, sizeof(bfh));
  bfh.bfType = 0x4D42; 
  bfh.bfSize = 0x00;
  bfh.bfReserved1 = 0;
  bfh.bfReserved2 = 0;
  bfh.bfOffBits = sizeof(bfh) + sizeof(bih) + BMP32_PALETTE_SIZE;
  
  memset(&bih, 0, sizeof(bih));
  bih.biSize = sizeof(bih);
  bih.biWidth = img_w;
  bih.biHeight = img_h;
  bih.biPlanes = 1;
  bih.biBitCount = 24;
  bih.biCompression = 0;
  bih.biSizeImage = 0;
  bih.biXPelsPerMeter = 3780;
  bih.biYPelsPerMeter = 3780;
  bih.biClrUsed = 0;
  bih.biClrImportant = 0;

  memset(Palette, 0, BMP32_PALETTE_SIZE);

  memcpy(buf_image, &bfh, sizeof(bfh));
  memcpy(buf_image + sizeof(bfh), &bih, sizeof(bih));
  memcpy(buf_image + sizeof(bfh) + sizeof(bih), Palette, BMP32_PALETTE_SIZE);
  
  int retval = yuyv_to_rgb(yuyv_array, size, (buf_image + sizeof(bfh) + sizeof(bih) + BMP32_PALETTE_SIZE), img_w, img_h, ((iscolor == 0) ? FLAG_CONV_NOCOLOR : FLAG_CONV_BGR));
  if (retval < 0) {
    return -2;
  }

  int retv = send(socket, buf_image, obs, 0);
  #ifdef DEBUG1
  printf("DEBUG: yuyv_to_bmp32().send(xxx)=%d;\n", retv);
  #endif
  free(buf_image);
  
  return 0;
}
