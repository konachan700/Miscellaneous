#ifndef __USB_MEW__
#define __USB_MEW__

#include <stdlib.h>
#include <libopencm3/stm32/rcc.h>
#include <libopencm3/stm32/gpio.h>
#include <libopencm3/cm3/systick.h>
#include <libopencm3/usb/usbd.h>
#include <libopencm3/usb/hid.h>

#define USB_DEVICE_DESCRIPTOR_TYPE              0x01
#define USB_CONFIGURATION_DESCRIPTOR_TYPE       0x02
#define USB_STRING_DESCRIPTOR_TYPE              0x03
#define USB_INTERFACE_DESCRIPTOR_TYPE           0x04
#define USB_ENDPOINT_DESCRIPTOR_TYPE            0x05 

#define HID_DESCRIPTOR_TYPE                     0x21

#define MEW_KB_REPORT_SIZE 						61
#define MEW_CUSTOM_HID_REPORT_SIZE 				61

#define MEW_UPPER_CASE							0x02
#define MEW_LOWER_CASE 							0x00

char __is_shift(char in);
char __to_hex(char in);
void __byte_to_hid_hex(char in, char *out);

extern void mew_usb_init(void);
extern void mew_usb_poll(void);
extern void mew_hid_send(char* buf, int len);
extern void mew_send_char(char ch, char char_case);
extern void mew_send_debug_hex(uint8_t *buf, uint16_t len);

#endif
