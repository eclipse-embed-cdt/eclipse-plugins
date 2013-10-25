/*
 * Copyright (c) 2013 Liviu Ionescu
 */

#if defined(STM32F10X_LD)
#include "../startup/startup_stm32f10x_ld.s"
#elif defined(STM32F10X_LD_VL)
#include "../startup/startup_stm32f10x_ld_vl.s"
#elif defined(STM32F10X_MD)
#include "../startup/startup_stm32f10x_md.s"
#elif defined(STM32F10X_MD_VL)
#include "../startup/startup_stm32f10x_md_vl.s"
#elif defined(STM32F10X_HD)
#include "../startup/startup_stm32f10x_hd.s"
#elif defined(STM32F10X_HD_VL)
#include "../startup/startup_stm32f10x_hd_vl.s"
#elif defined(STM32F10X_XL)
#include "../startup/startup_stm32f10x_xl.s"
#elif defined(STM32F10X_CL)
#include "../startup/startup_stm32f10x_cl.s"
#else
#error "no startup"
#endif

