#define _GNU_SOURCE

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "./sockets.h" 

int mainSocket 		= -1;
int currentSocket 	= -1;

char r_buffer[SOCKET_BUFFER];

int open_socket(char *baddr, int port) {
  struct in_addr bind_addr;
  int res1 = inet_aton(baddr, &bind_addr);
  if (res1 == 0) {
      return -2;
  }

  mainSocket = socket(AF_INET, SOCK_STREAM, 0);
  if (mainSocket < 0) {
    return -1;
  }
  
  struct sockaddr_in addr;
  addr.sin_family = AF_INET;
  addr.sin_port = htons(port);
  addr.sin_addr = bind_addr;
  
  if(bind(mainSocket, (struct sockaddr *)&addr, sizeof(addr)) < 0) {
    return -3;
  }
  
  listen(mainSocket, 1);
  
  return 0; 
}

int wait_for_connect() {
  while (1) {
    currentSocket = accept(mainSocket, NULL, NULL);
    if (currentSocket > 0) {
      return 0;
    }
  }
}

int read_current_socket() {
  int buf_len = recvfrom(currentSocket, r_buffer, SOCKET_BUFFER, 0, NULL, NULL);
  if (buf_len == -1) {
    if ((errno && ECONNREFUSED) || (errno && EBADF) || (errno && ENOTCONN)) {
      return 2;
    }
    return 1;
  }
  r_buffer[buf_len] = '\0';
  return 0;
}

int get_current_socket() {
  return currentSocket;
}

void close_main_socket() {
  close(mainSocket);
}

void close_current_socket() {
  close(currentSocket);
}

int check_get_params(char *param) {
  char* a = strcasestr(r_buffer, "GET ");
  if (!a) {
    return -1;
  }
  
  char *b = strcasestr(a, "HTTP");
  if (!b) {
    return -1;
  }

  char *to = (char*) malloc(b - a);
  memset(to, 0, b - a);
  strncpy(to, a + 4, b - (a + 4));
  
  #ifdef DEBUG
  printf("DEBUG: GET param: %s; CHECK: %s;\n", to, param);
  #endif
  
  if (strcasestr(to, param)) {
    free(to);
    //memset(r_buffer, 0, SOCKET_BUFFER);
    return 1;
  } else { 
    free(to);
    return 0;
  }
}


