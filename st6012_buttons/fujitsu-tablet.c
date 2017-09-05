/* Kernel driver for FSC Tablet PC buttons
 *
 * Copyright (C) 2006-2011 Robert Gerlach <khnz@gmx.de>
 * Copyright (C) 2005-2006 Jan Rychter <jan@rychter.com>
 *
 * You can redistribute and/or modify this program under the terms of the
 * GNU General Public License version 2 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place Suite 330, Boston, MA 02111-1307, USA.
 */

#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/init.h>
#include <linux/bitops.h>
#include <linux/io.h>
#include <linux/ioport.h>
#include <linux/acpi.h>
#include <linux/device.h>
#include <linux/interrupt.h>
#include <linux/input.h>
#include <linux/delay.h>
#include <linux/dmi.h>

#define MODULENAME "fujitsu-tablet"

static const struct acpi_device_id fujitsu_ids[] = {
	{ .id = "FUJ02BD" },
	{ .id = "FUJ02BF" },
	{ .id = "" }
};

struct fujitsu_config {
	bool ignore_tablet_mode_if_undocked;
	bool invert_tablet_mode_bit;
	unsigned short keymap[16];
};

static struct fujitsu_config config_Lifebook_Tseries __initconst = {
	.ignore_tablet_mode_if_undocked = false,
	.invert_tablet_mode_bit = true,
	.keymap = {
		KEY_F8,
		KEY_F9,
		KEY_F10,
		KEY_F11,
		KEY_F13,
		KEY_F14,
		KEY_F15,
		KEY_F24,
		KEY_F23,
		KEY_F22,
		KEY_F21,
		KEY_F20,
		KEY_F19,
		KEY_F18,
		KEY_F17,
		KEY_F16
	}
};

static struct fujitsu_config config_Lifebook_U810 __initconst = {
	.ignore_tablet_mode_if_undocked = false,
	.invert_tablet_mode_bit = true,
	.keymap = {
		KEY_F8,
		KEY_F9,
		KEY_F10,
		KEY_F11,
		KEY_F13,
		KEY_F14,
		KEY_F15,
		KEY_F24,
		KEY_F23,
		KEY_F22,
		KEY_F21,
		KEY_F20,
		KEY_F19,
		KEY_F18,
		KEY_F17,
		KEY_F16
	}
};

static struct fujitsu_config config_Stylistic_Tseries __initconst = {
	.ignore_tablet_mode_if_undocked = true,
	.invert_tablet_mode_bit = false,
	.keymap = {
		KEY_F8,
		KEY_F9,
		KEY_F10,
		KEY_F11,
		KEY_F13,
		KEY_F14,
		KEY_F15,
		KEY_F24,
		KEY_F23,
		KEY_F22,
		KEY_F21,
		KEY_F20,
		KEY_F19,
		KEY_F18,
		KEY_F17,
		KEY_F16
	}
};

static struct fujitsu_config config_Stylistic_ST5xxx __initconst = {
	.ignore_tablet_mode_if_undocked = true,
	.invert_tablet_mode_bit = false,
	.keymap = {
		KEY_F8,
		KEY_F9,
		KEY_F10,
		KEY_F11,
		KEY_F13,
		KEY_F14,
		KEY_F15,
		KEY_F24,
		KEY_F23,
		KEY_F22,
		KEY_F21,
		KEY_F20,
		KEY_F19,
		KEY_F18,
		KEY_F17,
		KEY_F16
	}
};

static struct {						/* fujitsu_t */
	struct input_dev *idev;
	struct fujitsu_config config;
	unsigned long prev_keymask;

	int irq;
	int io_base;
	int io_length;
} fujitsu;

static inline u8 fujitsu_ack(void)
{
	return inb(fujitsu.io_base + 2);
}

static inline u8 fujitsu_status(void)
{
	return inb(fujitsu.io_base + 6);
}

static inline u8 fujitsu_read_register(const u8 addr)
{
	outb(addr, fujitsu.io_base);
	return inb(fujitsu.io_base + 4);
}

static void fujitsu_send_state(void)
{
	int state;
	int dock;

	state = fujitsu_read_register(0xdd);

	dock = !!(state & 0x02);
	input_report_switch(fujitsu.idev, SW_DOCK, dock);

	if (!fujitsu.config.ignore_tablet_mode_if_undocked || dock) {
		int tablet_mode = state & 0x01;

		if (fujitsu.config.invert_tablet_mode_bit)
			tablet_mode = !tablet_mode;

		input_report_switch(fujitsu.idev, SW_TABLET_MODE, tablet_mode);
	}

	input_sync(fujitsu.idev);
}

static void fujitsu_reset(void)
{
	int timeout = 50;

	fujitsu_ack();

	while ((fujitsu_status() & 0x02) && (--timeout))
		msleep(20);

	printk(KERN_DEBUG MODULENAME ": fujitsu_reset: time left: %d * 20ms", timeout);

	fujitsu_send_state();
}

static int input_fujitsu_setup(struct device *dev)
{
	struct input_dev *idev;
	int error;
	int x;

	idev = input_allocate_device();
	if (!idev)
		return -ENOMEM;

	idev->dev.parent = dev;
	idev->phys = KBUILD_MODNAME "/input16";
	idev->name = "Fujitsu tablet buttons";
	idev->id.bustype = BUS_HOST;
	idev->id.vendor  = 0x1734;	/* Fujitsu Siemens Computer GmbH */
	idev->id.product = 0x0001;
	idev->id.version = 0x0101;

	idev->keycode = fujitsu.config.keymap;
	idev->keycodesize = sizeof(fujitsu.config.keymap[0]);
	idev->keycodemax = ARRAY_SIZE(fujitsu.config.keymap);

	__set_bit(EV_REP, idev->evbit);

	for (x = 0; x < ARRAY_SIZE(fujitsu.config.keymap); x++)
		if (fujitsu.config.keymap[x])
			input_set_capability(idev, EV_KEY, fujitsu.config.keymap[x]);

	input_set_capability(idev, EV_MSC, MSC_SCAN);

	input_set_capability(idev, EV_SW, SW_DOCK);
	input_set_capability(idev, EV_SW, SW_TABLET_MODE);

	error = input_register_device(idev);
	if (error) {
		input_free_device(idev);
		return error;
	}

	fujitsu.idev = idev;
	return 0;
}

static void input_fujitsu_remove(void)
{
	if (fujitsu.idev)
		input_unregister_device(fujitsu.idev);
}

static irqreturn_t fujitsu_interrupt(int irq, void *dev_id)
{
	unsigned long keymask;
	unsigned long changed;

	if (unlikely(!(fujitsu_status() & 0x01)))
		return IRQ_NONE;

	fujitsu_send_state();

	keymask  = fujitsu_read_register(0xde);
	keymask |= fujitsu_read_register(0xdf) << 8;
	keymask ^= 0xffff;

	printk(KERN_DEBUG MODULENAME ": state=0x%02x keymask=0x%04lx\n",
			fujitsu_read_register(0xdd), keymask);

	changed = keymask ^ fujitsu.prev_keymask;
	if (changed) {
		unsigned int keycode;
		int pressed;
		int x = 0;

		fujitsu.prev_keymask = keymask;

		x = find_first_bit(&changed, 16);
		keycode = fujitsu.config.keymap[x];
		pressed = !!(keymask & changed);

		if (pressed)
			input_event(fujitsu.idev, EV_MSC, MSC_SCAN, x);

		input_report_key(fujitsu.idev, keycode, pressed);
		input_sync(fujitsu.idev);
	}

	fujitsu_ack();
	return IRQ_HANDLED;
}

static int fujitsu_dmi_matched(const struct dmi_system_id *dmi)
{
	printk(KERN_INFO MODULENAME ": %s\n", dmi->ident);
	memcpy(&fujitsu.config, dmi->driver_data,
			sizeof(struct fujitsu_config));
	return 1;
}

static struct dmi_system_id dmi_ids[] __initdata = {
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Fujitsu Siemens P/T Series",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, "FUJITSU"),
			DMI_MATCH(DMI_PRODUCT_NAME, "LIFEBOOK")
		},
		.driver_data = &config_Lifebook_Tseries
	},
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Fujitsu Lifebook T Series",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, "FUJITSU"),
			DMI_MATCH(DMI_PRODUCT_NAME, "LifeBook T")
		},
		.driver_data = &config_Lifebook_Tseries
	},
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Fujitsu Siemens Stylistic T Series",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, "FUJITSU"),
			DMI_MATCH(DMI_PRODUCT_NAME, "Stylistic T")
		},
		.driver_data = &config_Stylistic_Tseries
	},
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Fujitsu LifeBook U810",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, "FUJITSU"),
			DMI_MATCH(DMI_PRODUCT_NAME, "LifeBook U810")
		},
		.driver_data = &config_Lifebook_U810
	},
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Fujitsu Siemens Stylistic ST5xxx Series",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, "FUJITSU"),
			DMI_MATCH(DMI_PRODUCT_NAME, "STYLISTIC ST5")
		},
		.driver_data = &config_Stylistic_ST5xxx
	},
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Fujitsu Siemens Stylistic ST5xxx Series",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, "FUJITSU"),
			DMI_MATCH(DMI_PRODUCT_NAME, "Stylistic ST5")
		},
		.driver_data = &config_Stylistic_ST5xxx
	},
	{
		.callback = fujitsu_dmi_matched,
		.ident = "Unknown (using defaults)",
		.matches = {
			DMI_MATCH(DMI_SYS_VENDOR, ""),
			DMI_MATCH(DMI_PRODUCT_NAME, "")
		},
		.driver_data = &config_Lifebook_Tseries
	},
	{ NULL }
};

static acpi_status fujitsu_walk_resources(struct acpi_resource *res, void *data)
{
	switch(res->type) {
		case ACPI_RESOURCE_TYPE_IRQ:
			fujitsu.irq = res->data.irq.interrupts[0];
			return AE_OK;

		case ACPI_RESOURCE_TYPE_IO:
			fujitsu.io_base = res->data.io.minimum;
			fujitsu.io_length = res->data.io.address_length;
			return AE_OK;

		case ACPI_RESOURCE_TYPE_END_TAG:
			if (fujitsu.irq && fujitsu.io_base)
				return AE_OK;
			else
				return AE_NOT_FOUND;

		default:
			return AE_ERROR;
	}
}

static int acpi_fujitsu_add(struct acpi_device *adev)
{
	acpi_status status;
	int error;

	if (!adev)
		return -EINVAL;

	status = acpi_walk_resources(adev->handle, METHOD_NAME__CRS,
			fujitsu_walk_resources, NULL);
	if (ACPI_FAILURE(status) || !fujitsu.irq || !fujitsu.io_base)
		return -ENODEV;

	error = input_fujitsu_setup(&adev->dev);
	if (error)
		return error;

	if (!request_region(fujitsu.io_base, fujitsu.io_length, MODULENAME)) {
		dev_err(&adev->dev, "region 0x%04x-0x%04x busy\n", fujitsu.io_base, fujitsu.io_base+fujitsu.io_length);
		input_fujitsu_remove();
		return -EBUSY;
	}

	fujitsu_reset();

	error = request_irq(fujitsu.irq, fujitsu_interrupt,
			IRQF_SHARED, MODULENAME, fujitsu_interrupt);
	if (error) {
		dev_err(&adev->dev, "unable to get irq %d\n", fujitsu.irq);
		release_region(fujitsu.io_base, fujitsu.io_length);
		input_fujitsu_remove();
		return error;
	}


	return 0;
}

static int acpi_fujitsu_remove(struct acpi_device *adev)
{
	free_irq(fujitsu.irq, fujitsu_interrupt);
	release_region(fujitsu.io_base, fujitsu.io_length);
	input_fujitsu_remove();
	return 0;
}
/*
static int acpi_fujitsu_resume(struct acpi_device *adev)
{
	fujitsu_reset();
	return 0;
}*/

static struct acpi_driver acpi_fujitsu_driver = {
	.name  = MODULENAME,
	.class = "hotkey",
	.ids   = fujitsu_ids,
	.ops   = {
		.add    = acpi_fujitsu_add,
		.remove	= acpi_fujitsu_remove,
	//	.resume = acpi_fujitsu_resume,
	}
};

static int fujitsu_module_init(void)
{
	int error;

	dmi_check_system(dmi_ids);

	error = acpi_bus_register_driver(&acpi_fujitsu_driver);
	if (ACPI_FAILURE(error))
		return error;

	return 0;
}

static void fujitsu_module_exit(void)
{
	acpi_bus_unregister_driver(&acpi_fujitsu_driver);
}

module_init(fujitsu_module_init);
module_exit(fujitsu_module_exit);

MODULE_AUTHOR("Robert Gerlach <khnz@gmx.de>");
MODULE_DESCRIPTION("Fujitsu Siemens tablet button driver");
MODULE_LICENSE("GPL");
MODULE_VERSION("2.3.2");

MODULE_DEVICE_TABLE(acpi, fujitsu_ids);
