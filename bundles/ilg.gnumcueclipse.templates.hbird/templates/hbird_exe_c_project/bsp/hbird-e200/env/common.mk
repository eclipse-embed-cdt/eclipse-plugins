# See LICENSE for license details.

ifndef _HBIRD_MK_COMMON
_HBIRD_MK_COMMON := # defined

.PHONY: all
all: $(TARGET)

FLASHXIP    := flashxip
FLASH       := flash
ITCM        := itcm
DOWNLOAD    := flash


HEAD_DIR = $(BSP_BASE)/$(BOARD)/include
DRIVER_DIR = $(BSP_BASE)/$(BOARD)/drivers
ENV_DIR = $(BSP_BASE)/$(BOARD)/env
STUB_DIR = $(BSP_BASE)/$(BOARD)/stubs

ASM_SRCS += $(ENV_DIR)/start.S
ASM_SRCS += $(ENV_DIR)/entry.S
C_SRCS += $(ENV_DIR)/init.c
C_SRCS += $(STUB_DIR)/close.c
C_SRCS += $(STUB_DIR)/_exit.c
C_SRCS += $(STUB_DIR)/write_hex.c
C_SRCS += $(STUB_DIR)/fstat.c
C_SRCS += $(STUB_DIR)/isatty.c
C_SRCS += $(STUB_DIR)/lseek.c
C_SRCS += $(STUB_DIR)/read.c
C_SRCS += $(STUB_DIR)/sbrk.c
C_SRCS += $(STUB_DIR)/write.c
ifeq ($(REPLACE_PRINTF),1) 
C_SRCS += $(STUB_DIR)/malloc.c
C_SRCS += $(STUB_DIR)/printf.c
endif

ifeq ($(DOWNLOAD),${FLASH}) 
LINKER_SCRIPT := $(ENV_DIR)/link_flash.lds
endif

ifeq ($(DOWNLOAD),${ITCM}) 
LINKER_SCRIPT := $(ENV_DIR)/link_itcm.lds
endif

ifeq ($(DOWNLOAD),${FLASHXIP}) 
LINKER_SCRIPT := $(ENV_DIR)/link_flashxip.lds
endif

INCLUDES += -I$(STUB_DIR)
INCLUDES += -I$(DRIVER_DIR)
INCLUDES += -I$(ENV_DIR)
INCLUDES += -I$(HEAD_DIR)

LDFLAGS += -T $(LINKER_SCRIPT)  -nostartfiles -Wl,--gc-sections  -Wl,--check-sections
ifeq ($(USE_NANO),1) 
LDFLAGS += --specs=nano.specs 
endif

ifeq ($(NANO_PFLOAT),1) 
LDFLAGS += -u _printf_float 
endif

ifeq ($(REPLACE_PRINTF),1) 
LDFLAGS += -Wl,--wrap=malloc -Wl,--wrap=printf 
endif

LDFLAGS += -L$(ENV_DIR)

ASM_OBJS := $(ASM_SRCS:.S=.o)
C_OBJS := $(C_SRCS:.c=.o)
DUMP_OBJS := $(C_SRCS:.c=.dump)
VERILOG_OBJS := $(C_SRCS:.c=.verilog)

LINK_OBJS += $(ASM_OBJS) $(C_OBJS)
LINK_DEPS += $(LINKER_SCRIPT)

CLEAN_OBJS += $(TARGET) $(LINK_OBJS) $(DUMP_OBJS) $(VERILOG_OBJS)

CFLAGS += -g
CFLAGS += -march=$(RISCV_ARCH)
CFLAGS += -mabi=$(RISCV_ABI)
CFLAGS +=   -ffunction-sections -fdata-sections -fno-common
ifeq ($(REPLACE_PRINTF),1) 
CFLAGS +=   -fno-builtin-printf -fno-builtin-malloc 
endif


$(TARGET): $(LINK_OBJS) $(LINK_DEPS)
	$(CC) $(CFLAGS) $(INCLUDES) $(LINK_OBJS) -o $@ $(LDFLAGS)
	$(SIZE) $@

$(ASM_OBJS): %.o: %.S $(HEADERS)
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

$(C_OBJS): %.o: %.c $(HEADERS)
	$(CC) $(CFLAGS) $(INCLUDES) -include sys/cdefs.h -c -o $@ $<

.PHONY: clean
clean:
	rm -f $(CLEAN_OBJS)

endif # _HBIRD_MK_COMMON
