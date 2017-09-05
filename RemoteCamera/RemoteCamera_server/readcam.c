#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <getopt.h>
#include <fcntl.h> 
#include <unistd.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <sys/mman.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <linux/videodev2.h>

#include "./readcam.h" 
#include "./imgp.h" 

int            	fd = -1;
struct buffer 	*buffers;
unsigned int   	n_buffers;

int img_height = 480, img_width = 640;

void get_pixelformat(int px, char *outbuf) {
  outbuf[0] = (unsigned char)((px << 24) >> 24);
  outbuf[1] = (unsigned char)((px << 16) >> 24);
  outbuf[2] = (unsigned char)((px << 8) >> 24);
  outbuf[3] = (unsigned char)(px >> 24);
  outbuf[4] = '\0';
}

int xioctl(int fh, int request, void *arg)
{
  int r;
  do {
    r = ioctl(fh, request, arg);
  } while (-1 == r && EINTR == errno);

  return r;
}

int open_camera() {
  struct stat st;

  if (-1 == stat(CAMERA_DEV, &st)) {
    #ifdef DEBUG
    printf("DEBUG: Device not exist\n");
    #endif
    return 3;
  }

  if (!S_ISCHR(st.st_mode)) {
    #ifdef DEBUG
    printf("DEBUG: Incorrect device type\n");
    #endif
    return 2;
  }

  fd = open(CAMERA_DEV, O_RDWR | O_NONBLOCK, 0);

  if (-1 == fd) {
    #ifdef DEBUG
    printf("DEBUG: Cannot open device\n");
    #endif
    return 1;
  }
  return 0;
}

int init_camera(int h, int w) {
  struct v4l2_capability cap;
  //struct v4l2_cropcap cropcap;
  //struct v4l2_crop crop;
  struct v4l2_format fmt;
  unsigned int min;
  
  if (-1 == xioctl(fd, VIDIOC_QUERYCAP, &cap)) {
    #ifdef DEBUG
    printf("DEBUG: Not supported\n");
    #endif
    return 1; // Not supported
  }
  
  if (!(cap.capabilities & V4L2_CAP_VIDEO_CAPTURE)) {
    #ifdef DEBUG
    printf("DEBUG: It isn't camera\n");
    #endif
    return 2; // It isn't camera
  }
  
  
  if (!(cap.capabilities & V4L2_CAP_STREAMING)) {
      #ifdef DEBUG
      printf("DEBUG: Not support streaming\n");
      #endif
    return 3; // Not support streaming
  }
  
  printf("FORMAT INFO:\n");
  struct v4l2_fmtdesc fmtdesc;
  CLEAR(fmtdesc);
  fmtdesc.index = 0;
  fmtdesc.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  while(ioctl(fd, VIDIOC_ENUM_FMT, &fmtdesc) == 0) {
    char obfr1[5];
    get_pixelformat(fmtdesc.pixelformat, obfr1);
    printf("\tID=%d, format=%s; pxformat=%s;\n", fmtdesc.index, fmtdesc.description, obfr1);
    
    fmtdesc.index++;
    
    struct v4l2_frmsizeenum frmsizeenum;
    CLEAR(frmsizeenum);
    frmsizeenum.index = 0;
    frmsizeenum.pixel_format = fmtdesc.pixelformat;
    printf("\tsupported formats: ");
    while(ioctl(fd, VIDIOC_ENUM_FRAMESIZES, &frmsizeenum) == 0) {
      if (frmsizeenum.type == V4L2_FRMSIZE_TYPE_DISCRETE) {
	printf("%dx%d; ", frmsizeenum.discrete.width, frmsizeenum.discrete.height);
      }
      frmsizeenum.index++;
    }
    printf("\n\n");
  }
  
  CLEAR(fmt);
  fmt.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  if (-1 == xioctl(fd, VIDIOC_G_FMT, &fmt)) {
    #ifdef DEBUG
    printf("DEBUG: Unknown video format\n");
    #endif
    return 4; // Unknown video format
  }
  
  fmt.fmt.pix.width = w;
  fmt.fmt.pix.height = h;
  
  if (-1 == xioctl(fd, VIDIOC_S_FMT, &fmt)) {
    #ifdef DEBUG
    printf("DEBUG: Cannot set format\n");
    #endif
  }
  
  char obfr2[5];
  get_pixelformat(fmt.fmt.pix.pixelformat, obfr2);
  fprintf(stderr, "CURRENT FORMAT:\n"
		  "\theight=%d; width=%d; sizeimage=%d; pixelformat=%s;\n"
		  "\n", fmt.fmt.pix.height, fmt.fmt.pix.width, fmt.fmt.pix.sizeimage, obfr2);
  
  img_height = fmt.fmt.pix.height;
  img_width  = fmt.fmt.pix.width;
  
  min = fmt.fmt.pix.width * 2;
  if (fmt.fmt.pix.bytesperline < min) {
    fmt.fmt.pix.bytesperline = min;
  }
  min = fmt.fmt.pix.bytesperline * fmt.fmt.pix.height;
  if (fmt.fmt.pix.sizeimage < min) { 
    fmt.fmt.pix.sizeimage = min;
  }
  
  struct v4l2_requestbuffers req;
  CLEAR(req);
  req.count = 4;
  req.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  req.memory = V4L2_MEMORY_MMAP;
  
  if (-1 == xioctl(fd, VIDIOC_REQBUFS, &req)) {
    #ifdef DEBUG
    printf("DEBUG: Device not support memory mapping\n");
    #endif
    return 5;
  }
  
  if (req.count < 2) { 
    #ifdef DEBUG
    printf("DEBUG: Insufficient buffer memory on device\n");
    #endif
    return 6;
  }
  
  buffers = calloc(req.count, sizeof(*buffers));
  if (!buffers) {
    #ifdef DEBUG
    printf("DEBUG: Out of memory\n");
    #endif
    return 7;
  }
  
  for (n_buffers = 0; n_buffers < req.count; ++n_buffers) {
    struct v4l2_buffer buf;
    CLEAR(buf);
    buf.type   = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory = V4L2_MEMORY_MMAP;
    buf.index  = n_buffers;
    
    if (-1 == xioctl(fd, VIDIOC_QUERYBUF, &buf)) {
      #ifdef DEBUG
      printf("DEBUG: ioctl VIDIOC_QUERYBUF error\n");
      #endif
      return 8;
    }
    
    buffers[n_buffers].length = buf.length;
    buffers[n_buffers].start = mmap(NULL, buf.length, PROT_READ | PROT_WRITE, MAP_SHARED, fd, buf.m.offset);
    if (MAP_FAILED == buffers[n_buffers].start) {
      #ifdef DEBUG
      printf("DEBUG: mmap error\n");
      #endif
      return 9;
    }
  }
  
  unsigned int i;
  enum v4l2_buf_type type;
  
  for (i = 0; i < n_buffers; ++i) {
    struct v4l2_buffer buf;
    CLEAR(buf);
    buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
    buf.memory = V4L2_MEMORY_MMAP;
    buf.index = i;
    
    if (-1 == xioctl(fd, VIDIOC_QBUF, &buf)) {
      #ifdef DEBUG
      printf("DEBUG: ioctl VIDIOC_QBUF error\n");
      #endif
      return 10;
    }
  }
  
  type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  if (-1 == xioctl(fd, VIDIOC_STREAMON, &type)) {
    #ifdef DEBUG
    printf("DEBUG: ioctl VIDIOC_STREAMON error\n");
    #endif
    return 11;
  }
  return 0;
}

void create_image(const void *p, int size, int image_type, int socket, int iscolor) {
  if (socket < 0) {
    return;
  }
  
  if (image_type == IMAGE_TYPE_NONE) {
    return;
  }
  
  if (image_type == IMAGE_TYPE_BMP) {
    if (yuyv_to_bmp32(p, size, img_height, img_width, socket, iscolor) == 0) {
      #ifdef DEBUG
      printf("DEBUG: BMP32 image created.\n");
      #endif
    } else {
      #ifdef DEBUG
      printf("DEBUG: BMP32 image not created.\n");
      #endif
    } 
  }
  
  if (image_type == IMAGE_TYPE_YUYV) {
    send(socket, p, size, 0);
  }

  if (image_type == IMAGE_TYPE_JPG) {
    if (yuyv_to_jpeg(p, size, img_height, img_width, socket, iscolor) == 0) {
      #ifdef DEBUG
      printf("DEBUG: JPEG image created.\n");
      #endif
    } else {
      #ifdef DEBUG
      printf("DEBUG: JPEG image not created.\n");
      #endif
    } 
  }
  
  return;
}

void read_camera(int image_type, int socket, int iscolor) {
  int retval = 0; 
#ifdef MAX_FRAMES
  int i = 0;
  for (i=0; i<MAX_FRAMES; i++) {
#endif
    for (;;) {
      fd_set fds;
      struct timeval tv;
      
      FD_ZERO(&fds);
      FD_SET(fd, &fds);
      
      tv.tv_sec = 1;
      tv.tv_usec = 0;
      retval = select(fd + 1, &fds, NULL, NULL, &tv);
      if (-1 == retval) {
	if (EINTR == errno) {
	  continue;
	}
	#ifdef DEBUG
	printf("DEBUG: select error\n");
	#endif
	return;
      }
      
      if (0 == retval) {
	#ifdef DEBUG
	printf("DEBUG: select timeout\n");
	#endif
	return;
      }
      
      struct v4l2_buffer buf;
      CLEAR(buf);
      buf.type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
      buf.memory = V4L2_MEMORY_MMAP;
      
      if (-1 == xioctl(fd, VIDIOC_DQBUF, &buf)) {
	switch (errno) {
	case EAGAIN:
	  return;
	case EIO:
	  #ifdef DEBUG
	  printf("DEBUG: Could ignore EIO, see spec.\n");
	  #endif
	default:
	  #ifdef DEBUG
	  printf("DEBUG: ioctl VIDIOC_DQBUF error\n");
	  #endif
	  return;
	}
      }
      
      assert(buf.index < n_buffers);
#ifdef MAX_FRAMES
      if (i == (MAX_FRAMES-1)) {
	create_image(buffers[buf.index].start, buf.bytesused, image_type, socket, iscolor);
      }
#else
      create_image(buffers[buf.index].start, buf.bytesused, image_type, socket, iscolor);
#endif
      if (-1 == xioctl(fd, VIDIOC_QBUF, &buf)) {
	#ifdef DEBUG
	printf("DEBUG: ioctl VIDIOC_DQBUF error\n");
	#endif
	return;
      }
      break;
    }
#ifdef MAX_FRAMES
  }
#endif
  return;
}

int close_camera() {
  enum v4l2_buf_type type;
  type = V4L2_BUF_TYPE_VIDEO_CAPTURE;
  if (-1 == xioctl(fd, VIDIOC_STREAMOFF, &type)) {
    #ifdef DEBUG
    printf("DEBUG: ioctl VIDIOC_STREAMOFF error\n");
    #endif
    return -1;
  }
  
  int i = 0;
  for (i = 0; i < n_buffers; ++i) {
    if (-1 == munmap(buffers[i].start, buffers[i].length)) {
      #ifdef DEBUG
      printf("DEBUG: munmap error\n");
      #endif
      return -2;
    }
  }

  free(buffers);
  if (-1 == close(fd)) {
    #ifdef DEBUG
    printf("DEBUG: device close error\n");
    #endif
    return -3;
  }
  fd = -1;
  return 0;
}
