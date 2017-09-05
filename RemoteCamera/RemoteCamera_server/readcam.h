
#define CLEAR(x) memset(&(x), 0, sizeof(x))

// Все настраивается тут, никаких конфигов, поскольку не предназначено для использования на пека.
// ************************************************************************************************************

// Устройство камеры, ищем через `ls -l /dev/video*`
#define CAMERA_DEV 		"/dev/video0"

// Отладочные сообщения, при впиливании в робот лучше отключить, ибо в них нет особого смысла
#define DEBUG 			1

// Хак для камер. Большинство китайских вебок имеют автонастройку, для чего им надо некоторое время поработать.
// Вот этот параметр и заставляет их прокрутить некое кол-во кадров вхолостую, прежде чем взять кадр для отображения на пульте робота.
// Если закомментировать эту строку - функция отключится вообще.
//#define MAX_FRAMES 		5
// ************************************************************************************************************

#define FLAG_RF_CREATE_IMAGE 		1
#define FLAG_RF_DONT_CREATE_IMAGE 	2

#define IMAGE_TYPE_NONE 		0
#define IMAGE_TYPE_YUYV			1
#define IMAGE_TYPE_BMP			2
#define IMAGE_TYPE_JPG			3

struct buffer {
  void   *start;
  size_t  length;
};

extern int 	open_camera();
extern int 	init_camera(int h, int w);
extern void 	read_camera(int image_type, int socket, int iscolor) ;
extern int 	close_camera();
