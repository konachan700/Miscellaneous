#include "usb.h"

usbd_device *mew_usbd_dev;

uint8_t usbd_control_buffer[128];

const unsigned char keyboard_report_descriptor[MEW_KB_REPORT_SIZE] =
{
	0x05, 0x01, // USAGE_PAGE (Generic Desktop)
	0x09, 0x06, // USAGE (Keyboard)
	0xa1, 0x01, // COLLECTION (Application)
	0x05, 0x07, //     USAGE_PAGE (Keyboard/Keypad)
	0x19, 0xe0, //     USAGE_MINIMUM (Keyboard LeftControl)
	0x29, 0xe7, //     USAGE_MAXIMUM (Keyboard Right GUI)
	0x15, 0x00, //     LOGICAL_MINIMUM (0)
	0x25, 0x01, //     LOGICAL_MAXIMUM (1)
	0x95, 0x08, //     REPORT_COUNT (8)
	0x75, 0x01, //     REPORT_SIZE (1)
	0x81, 0x02, //     INPUT (Data, Var, Abs)
	0x95, 0x01, //     REPORT_COUNT (1)
	0x75, 0x08, //     REPORT_SIZE (8)
	0x81, 0x03, //     INPUT (Cnst, Var, Abs)
	0x95, 0x06, //   REPORT_COUNT (6)
	0x75, 0x08, //   REPORT_SIZE (8)
	0x25, 0xFF, //   LOGICAL_MAXIMUM (255)
	0x19, 0x00, //   USAGE_MINIMUM (Reserved (no event indicated))
	0x29, 0x65, //   USAGE_MAXIMUM (Keyboard Application)
	0x81, 0x00, //     INPUT (Data, Ary, Abs)
	0x25, 0x01, //     LOGICAL_MAXIMUM (1)
	0x95, 0x02, //   REPORT_COUNT (2)
	0x75, 0x01, //   REPORT_SIZE (1)
	0x05, 0x08, //   USAGE_PAGE (LEDs)
	0x19, 0x01, //   USAGE_MINIMUM (Num Lock)
	0x29, 0x02, //   USAGE_MAXIMUM (Caps Lock)
	0x91, 0x02, //   OUTPUT (Data, Var, Abs)
	0x95, 0x01, //   REPORT_COUNT (1)
	0x75, 0x06, //   REPORT_SIZE (6)
	0x91, 0x03, //   OUTPUT (Cnst, Var, Abs)
	0xc0        // END_COLLECTION
};

const struct usb_device_descriptor dev = {
	.bLength 			= USB_DT_DEVICE_SIZE,
	.bDescriptorType 	= USB_DT_DEVICE,
	.bcdUSB 			= 0x0200,
	.bDeviceClass 		= 0,
	.bDeviceSubClass 	= 0,
	.bDeviceProtocol 	= 0,
	.bMaxPacketSize0 	= 64,
	.idVendor 			= 0x1234,
	.idProduct 			= 0x4321,
	.bcdDevice 			= 0x0200,
	.iManufacturer 		= 1,
	.iProduct 			= 2,
	.iSerialNumber 		= 3,
	.bNumConfigurations = 1,
};

const struct usb_endpoint_descriptor hid_endpoints[] = {{
	.bLength 			= USB_DT_ENDPOINT_SIZE,
	.bDescriptorType 	= USB_DT_ENDPOINT,
	.bEndpointAddress 	= 0x81,
	.bmAttributes 		= USB_ENDPOINT_ATTR_INTERRUPT,
	.wMaxPacketSize 	= 8,
	.bInterval 			= 0x04,
}, {
	.bLength 			= USB_DT_ENDPOINT_SIZE,
	.bDescriptorType 	= USB_DT_ENDPOINT,
	.bEndpointAddress 	= 0x01,
	.bmAttributes 		= USB_ENDPOINT_ATTR_INTERRUPT,
	.wMaxPacketSize 	= 8,
	.bInterval 			= 0x04,
}};

static const struct {
	struct usb_hid_descriptor hid_descriptor;
	struct {
		uint8_t bReportDescriptorType;
		uint16_t wDescriptorLength;
	} __attribute__((packed)) hid_report;
} __attribute__((packed)) hid_function = {
	.hid_descriptor = {
		.bLength = sizeof(hid_function),
		.bDescriptorType 		= USB_DT_HID,
		.bcdHID 				= 0x0100,
		.bCountryCode 			= 0,
		.bNumDescriptors 		= 1,
	},
	.hid_report = {
		.bReportDescriptorType 	= USB_DT_REPORT,
		.wDescriptorLength 		= sizeof(keyboard_report_descriptor),
	},
};

const struct usb_interface_descriptor hid_iface = {
	.bLength 				= USB_DT_INTERFACE_SIZE,
	.bDescriptorType 		= USB_DT_INTERFACE,
	.bInterfaceNumber 		= 0,
	.bAlternateSetting 		= 0,
	.bNumEndpoints 			= 2,
	.bInterfaceClass 		= USB_CLASS_HID,
	.bInterfaceSubClass 	= 1,
	.bInterfaceProtocol 	= 1,
	.iInterface 			= 0,
	.endpoint 				= hid_endpoints,
	.extra 					= &hid_function,
	.extralen 				= sizeof(hid_function),
};

const struct usb_interface ifaces[] = {{
	.num_altsetting 		= 1,
	.altsetting 			= &hid_iface,
}};

const struct usb_config_descriptor config = {
	.bLength 				= USB_DT_CONFIGURATION_SIZE,
	.bDescriptorType 		= USB_DT_CONFIGURATION,
	.wTotalLength 			= 0,
	.bNumInterfaces 		= 1,
	.bConfigurationValue 	= 1,
	.iConfiguration 		= 0,
	.bmAttributes 			= 0xC0,
	.bMaxPower 				= 0x32,
	.interface 				= ifaces,
};

static const char *usb_strings[] = {
	"JNeko Lab",
	"MeW HPM",
	"JNEKO",
};

static int hid_control_request(usbd_device *usbd_dev, struct usb_setup_data *req, uint8_t **buf, uint16_t *len,
			void (**complete)(usbd_device *usbd_dev, struct usb_setup_data *req))
{
	(void)complete;
	(void)usbd_dev;

	if ((req->bmRequestType != 0x81) || (req->bRequest != USB_REQ_GET_DESCRIPTOR) || (req->wValue != 0x2200))
		return 0;

	/* Handle the HID report descriptor. */
	*buf = (uint8_t *)keyboard_report_descriptor;
	*len = sizeof(keyboard_report_descriptor);

	return 1;
}

static void hid_set_config(usbd_device *usbd_dev, uint16_t wValue) {
	(void)wValue;
	(void)usbd_dev;

	usbd_ep_setup(usbd_dev, 0x01, USB_ENDPOINT_ATTR_INTERRUPT, 8, NULL);
	usbd_ep_setup(usbd_dev, 0x81, USB_ENDPOINT_ATTR_INTERRUPT, 8, NULL);

	usbd_register_control_callback(
				usbd_dev,
				USB_REQ_TYPE_STANDARD | USB_REQ_TYPE_INTERFACE,
				USB_REQ_TYPE_TYPE | USB_REQ_TYPE_RECIPIENT,
				hid_control_request);
/*#ifdef INCLUDE_DFU_INTERFACE
	usbd_register_control_callback(
				usbd_dev,
				USB_REQ_TYPE_CLASS | USB_REQ_TYPE_INTERFACE,
				USB_REQ_TYPE_TYPE | USB_REQ_TYPE_RECIPIENT,
				dfu_control_request);
#endif*/

	//systick_set_clocksource(STK_CSR_CLKSOURCE_AHB_DIV8);
	/* SysTick interrupt every N clock pulses: set reload to N-1 */
	//systick_set_reload(99999);
	//systick_interrupt_enable();
	//systick_counter_enable();
}



void mew_usb_init(void) {
	mew_usbd_dev = usbd_init(&st_usbfs_v1_usb_driver, &dev, &config, usb_strings, 3, usbd_control_buffer, sizeof(usbd_control_buffer));
	usbd_register_set_config_callback(mew_usbd_dev, hid_set_config);
}

void mew_usb_poll(void) {
	usbd_poll(mew_usbd_dev);
}

void mew_hid_send(char* buf, int len) {
	if (len != 8) return;
	usbd_ep_write_packet(mew_usbd_dev, 0x81, buf, len);
}

void mew_send_char(char ch, char char_case) {
	unsigned const char null_buf[8] = { 0, 0, 0, 0, 0, 0, 0, 0 };
	unsigned const char buf[8] = { char_case, 0, ch, 0, 0, 0, 0, 0 };
	if ((ch <= 0x03) || (ch >= 0x29)) return;
	while (usbd_ep_write_packet(mew_usbd_dev, 0x81, buf, 8) == 0);
	while (usbd_ep_write_packet(mew_usbd_dev, 0x81, null_buf, 8) == 0);
}

void mew_send_debug_hex(uint8_t *buf, uint16_t len) {
	char hex[2];
	uint16_t i;

	for(i=0; i<len; i++) {
		__byte_to_hid_hex(buf[i], hex);
		mew_send_char(hex[1], __is_shift(hex[1]));
		mew_send_char(hex[0], __is_shift(hex[0]));
	}
}

void __byte_to_hid_hex(char in, char *out) {
	char buf_l = (in & 0x0F), buf_h = ((in >> 4) & 0x0F);
	out[0] = __to_hex(buf_l);
	out[1] = __to_hex(buf_h);
}

char __is_shift(char in) {
	if ((in >= 0x04) && (in <= 0x0F))
		return MEW_UPPER_CASE;
	else
		return MEW_LOWER_CASE;
}

char __to_hex(char in) {
	char buf = in & 0x0F;
	switch (buf) {
	case 0:
		return 0x27;
	case 1:
		return 0x1E;
	case 2:
		return 0x1F;
	case 3:
		return 0x20;
	case 4:
		return 0x21;
	case 5:
		return 0x22;
	case 6:
		return 0x23;
	case 7:
		return 0x24;
	case 8:
		return 0x25;
	case 9:
		return 0x26;
	case 0x0A:
		return 0x04;
	case 0x0B:
		return 0x05;
	case 0x0C:
		return 0x06;
	case 0x0D:
		return 0x07;
	case 0x0E:
		return 0x08;
	case 0x0F:
		return 0x09;
	}
	return 0x0F;
}













