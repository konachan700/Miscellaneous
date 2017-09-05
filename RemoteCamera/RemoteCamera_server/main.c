#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <getopt.h> 

#include "./readcam.h" 
#include "./sockets.h" 
#include "./imgp.h" 
#include "./main.h" 

char *http_headers = 	"HTTP/1.1 200 OK\r\n"
			"Date: Tue, 15 Oct 2013 01:01:01 GMT\r\n"
			"Expires:  Tue, 15 Oct 2013 02:02:02 GMT"
			"Server: Remote Camera beta (Linux)\r\n"
			"X-Powered-By: NekoKoNeko\r\n"
			"Connection: close\r\n";

char *header_ct_jpeg =  "Content-Type: image/jpeg\r\n\r\n";
char *header_ct_bmp  =  "Content-Type: image/bmp\r\n\r\n";

static const char short_options[] = "h:w:p:";

static const struct option long_options[] = {
        { "height", required_argument, NULL, 'h' },
        { "width",  required_argument, NULL, 'w' },
	{ "port",   required_argument, NULL, 'p' },
        { 0, 0, 0, 0 }
};

int main(int argc, char **argv) {
  int c = -1;
  int option_index;
  int img_height = DEFAULT_HEIGHT; 
  int img_width  = DEFAULT_WIDTH;
  int listen_port = LISTEN_PORT;
  
  for (;;) {
    c = getopt_long(argc, argv, short_options, long_options, &option_index);
    if (c == -1) {
      break;
    }
    
    switch(c) {
      case 'h': {
	if (optarg != NULL) {
	  img_height = atoi(optarg);
	}
	break;
      };
      case 'w': {
	if (optarg != NULL) {
	  img_width = atoi(optarg);
	}
	break;
      };
      case 'p': {
	if (optarg != NULL) {
	  listen_port = atoi(optarg);
	}
	break;
      };
    };
  }
  
  if (open_socket(LISTEN_ADDR, listen_port) != 0) {
    printf("ERROR: cannot open socket\n");
    return 1;
  }
  
  if (open_camera() != 0) {
    printf("Camera file error, exiting...\n");
    return 1;
  }
  
  if (init_camera(img_height, img_width) != 0) {
    printf("Camera error, exiting...\n");
    return 1;
  } else {
    printf("INFO: image size is %dx%d;\n\n", img_width, img_height);
  }

  while (1) {
    while (1) {
      if (wait_for_connect() == 0) { break; }
    }
    
    while (1) {
      if (read_current_socket() == 0) {
	if (check_get_params("exit") == 1) {
	  goto APP_EXIT;
	} else if (check_get_params("bmp") == 1) {
	  if (check_get_params(GETPARAM_SEND_HTTP_HEADERS)) {
	    send(get_current_socket(), http_headers, strlen(http_headers), 0);
	    send(get_current_socket(), header_ct_bmp, strlen(header_ct_bmp), 0);
	  }
	  read_camera(IMAGE_TYPE_BMP, get_current_socket(), check_get_params(GETPARAM_COLOR_IMAGE));
	  close_current_socket();
	  break;
	} else if (check_get_params("jpg") == 1) { 
	  //print_time(0);
	  if (check_get_params(GETPARAM_SEND_HTTP_HEADERS)) {
	    send(get_current_socket(), http_headers, strlen(http_headers), 0);
	    send(get_current_socket(), header_ct_jpeg, strlen(header_ct_jpeg), 0);
	  }
	  read_camera(IMAGE_TYPE_JPG, get_current_socket(), check_get_params(GETPARAM_COLOR_IMAGE));
	  close_current_socket();
	  //print_time(1);
	  break;
	} else if (check_get_params("yuyv") == 1) { 		// GET:yuyv
	  read_camera(IMAGE_TYPE_YUYV, get_current_socket(), 0);
	  close_current_socket();
	  break;
	}
      }
    }
  }
  
  APP_EXIT:
  printf("Exiting...\n");
  close_camera();
  close_current_socket();
  close_main_socket();
  
  return 0;
}