BINARY		= mew
BOOTPORT	?= /dev/ttyUSB0
BOOTSPEED	?= 115200
# change this linking script depending on particular MCU model,
# for example, if you have STM32F103VBT6, you should write:
LDSCRIPT	= mew.ld
LIBNAME		= opencm3_stm32f1
DEFS		= -DSTM32F1
# -DEBUG

OBJDIR = mk
INDEPENDENT_HEADERS=

FP_FLAGS	?= -msoft-float
ARCH_FLAGS	= -mthumb -mcpu=cortex-m3 $(FP_FLAGS) -mfix-cortex-m3-ldrd

###############################################################################
# Executables
PREFIX		?= arm-none-eabi

RM			:= rm -f
RMDIR		:= rmdir
CC			:= $(PREFIX)-gcc 
LD			:= $(PREFIX)-gcc --specs=nosys.specs 
AR			:= $(PREFIX)-ar
AS			:= $(PREFIX)-as
OBJCOPY		:= $(PREFIX)-objcopy
OBJDUMP		:= $(PREFIX)-objdump
GDB			:= $(PREFIX)-gdb
STFLASH		= $(shell which st-flash)
STBOOT		= $(shell which stm32flash)

###############################################################################
# Source files
LDSCRIPT	?= $(BINARY).ld
SRC			=  $(wildcard *.c)  
SRC			+= $(wildcard libatcrypto/*.c) 
SRC			+= $(wildcard libatcrypto/*/*.c) 

OBJS		= $(addprefix $(OBJDIR)/, $(SRC:%.c=%.o))

ifeq ($(strip $(OPENCM3_DIR)),)
OPENCM3_DIR := /home/misaki/devel/stm32/libopencm3
$(info Using $(OPENCM3_DIR) path to library)
endif

INCLUDE_DIR	= $(OPENCM3_DIR)/include
LIB_DIR		= $(OPENCM3_DIR)/lib
SCRIPT_DIR	= $(OPENCM3_DIR)/scripts

INCLUDE_LIBATCRYPTO=/home/misaki/devel/Mew/firmware/v.0.3/libatcrypto
INCLUDE_ME=/home/misaki/devel/Mew/firmware/v.0.3/

###############################################################################
# C flags
CFLAGS		+= -Os -g
CFLAGS		+= -Wall -Wimplicit-function-declaration #-Wextra -Wshadow  -Wno-unused
CFLAGS		+= -Wredundant-decls
# -Wmissing-prototypes -Wstrict-prototypes
CFLAGS		+= -fno-common -ffunction-sections -fdata-sections

###############################################################################
# C & C++ preprocessor common flags
CPPFLAGS	+= -MD
CPPFLAGS	+= -Wall #-Werror -Wno-unused
CPPFLAGS	+= -I$(INCLUDE_DIR) -I$(INCLUDE_LIBATCRYPTO) -I$(INCLUDE_ME) $(DEFS)

###############################################################################
# Linker flags
LDFLAGS		+= --static -nostartfiles
LDFLAGS		+= -L$(LIB_DIR)
LDFLAGS		+= -T$(LDSCRIPT)
LDFLAGS		+= -Wl,-Map=$(*).map
LDFLAGS		+= -Wl,--gc-sections

###############################################################################
# Used libraries
LDLIBS		+= -l$(LIBNAME)
LDLIBS		+= -Wl,--start-group -lc -lgcc -Wl,--end-group

.SUFFIXES: .elf .bin .hex .srec .list .map .images
.SECONDEXPANSION:
.SECONDARY:

ELF		:= $(OBJDIR)/$(BINARY).elf
LIST	:= $(OBJDIR)/$(BINARY).list
BIN		:= $(BINARY).bin
HEX		:= $(BINARY).hex

all: bin

elf: $(ELF)
bin: $(BIN)
hex: $(HEX)
list: $(LIST)

$(OBJDIR):
	mkdir $(OBJDIR)

$(OBJDIR)/%.o: %.c
	@printf "  CC      $<\n"
	$(CC) $(CFLAGS) $(CPPFLAGS) $(ARCH_FLAGS) -o $@ -c $<

$(SRC) : %.c : %.h $(INDEPENDENT_HEADERS)
	@touch $@

%.h: ;

$(BIN): $(ELF)
	@printf "  OBJCOPY $(BIN)\n"
	$(OBJCOPY) -Obinary $(ELF) $(BIN)

$(HEX): $(ELF)
	@printf "  OBJCOPY $(HEX)\n"
	$(OBJCOPY) -Oihex $(ELF) $(HEX)

$(LIST): $(ELF)
	@printf "  OBJDUMP $(LIST)\n"
	$(OBJDUMP) -S $(ELF) > $(LIST)

$(ELF): $(OBJDIR) $(OBJS) $(LDSCRIPT) $(LIB_DIR)/lib$(LIBNAME).a
	@printf "  LD      $(ELF)\n"
	$(LD) $(LDFLAGS) $(ARCH_FLAGS) $(OBJS) $(LDLIBS) -o $(ELF)

clean:
	@printf "  CLEAN\n"
	$(RM) -rf $(OBJDIR)
	@mkdir $(OBJDIR)
	@mkdir $(OBJDIR)/libatcrypto
	#@mkdir $(OBJDIR)/libatcrypto/atcacert
	@mkdir $(OBJDIR)/libatcrypto/basic
	#@mkdir $(OBJDIR)/libatcrypto/crypto
	@mkdir $(OBJDIR)/libatcrypto/hal
	#@mkdir $(OBJDIR)/libatcrypto/host
	#@mkdir $(OBJDIR)/libatcrypto/tls
	#$(RM) $(OBJS) $(OBJDIR)/*.d $(ELF) $(HEX) $(LIST) $(OBJDIR)/*.map
	#$(RMDIR) $(OBJDIR)

flash: $(BIN)
	@printf "  FLASH  $(BIN)\n"
	$(STFLASH) write $(BIN) 0x8000000

boot: $(BIN)
	@printf "  LOAD $(BIN) through bootloader\n"
	$(STBOOT) -b$(BOOTSPEED) $(BOOTPORT) -w $(BIN)

.PHONY: clean elf hex list flash boot

#-include $(OBJS:.o=.d)
