//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef KLXX_CMSIS_DEVICE_H_
#define KLXX_CMSIS_DEVICE_H_

#if defined(MKL25Z4)
#include "MKL25Z4.h"
#elif defined(MKL46Z4)
#include "MKL46Z4.h"
#else
#error "No CMSIS header"
#endif

#endif // KLXX_CMSIS_DEVICE_H_
