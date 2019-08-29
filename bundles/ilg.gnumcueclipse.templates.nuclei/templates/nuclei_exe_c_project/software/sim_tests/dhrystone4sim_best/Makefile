TARGET := dhrystone4sim_best

C_SRCS := dhry_stubs.c dhry_1.c dhry_2.c 
HEADERS := dhry.h

CFLAGS := -O3 -fno-common -funroll-loops -finline-functions  -falign-functions=4 -falign-jumps=4 -falign-loops=4 -funswitch-loops -fpeel-loops -fgcse-sm -fgcse-las


BSP_BASE = ../../bsp
include $(BSP_BASE)/$(BOARD)/n200/env/common.mk

