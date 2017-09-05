KERNELRELEASE	:= $(shell uname -r)
KERNEL_SOURCE	:= /lib/modules/$(KERNELRELEASE)/build
PWD		:= $(shell pwd)

EXTRA_CFLAGS	+= -Wall -Werror

ifdef DEFS
EXTRA_CFLAGS	+= $(DEFS)
endif


obj-m		+= fujitsu-tablet.o

all: fujitsu-tablet.ko
modules: fujitsu-tablet.ko
modules_install: install

fujitsu-tablet.ko: fujitsu-tablet.c
	$(MAKE) -C $(KERNEL_SOURCE) M=$(PWD) modules

install: fujitsu-tablet.ko
	$(MAKE) -C $(KERNEL_SOURCE) M=$(PWD) modules_install

uninstall:
	rm -f /lib/modules/$(KERNELRELEASE)/extra/fujitsu-tablet.ko

clean:
	$(MAKE) -C $(KERNEL_SOURCE) M=$(PWD) clean

distclean: clean
	rm -f Module.symvers

distdir: Makefile fujitsu-tablet.c
	cp -p $^ $(distdir)

%:
	@echo; \
	 echo " *** unknown target $@ *** "; \
	 echo

.PHONY: all install modules modules_install clean
