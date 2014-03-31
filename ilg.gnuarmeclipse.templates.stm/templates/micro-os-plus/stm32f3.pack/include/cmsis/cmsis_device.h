//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef STM32F3_CMSIS_DEVICE_H_
#define STM32F3_CMSIS_DEVICE_H_

#if defined(STM32F30X)
#include "stm32f30x.h"
#elif defined(STM32F37X)
#include "stm32f37x.h"
#else
#error "No CMSIS header file"
#endif

#endif // STM32F3_CMSIS_DEVICE_H_
