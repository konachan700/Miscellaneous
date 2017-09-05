 
#define SOCKET_BUFFER 1024
#define DEBUG 1
 
 
extern int open_socket(char *baddr, int port);
 
extern int wait_for_connect();
extern int check_get_params(char *param);
extern int get_current_socket();
 
extern void close_main_socket();
extern void close_current_socket();

 