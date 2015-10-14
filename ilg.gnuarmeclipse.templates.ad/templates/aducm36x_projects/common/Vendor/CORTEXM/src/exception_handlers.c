//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

// ----------------------------------------------------------------------------

#include "cortexm/ExceptionHandlers.h"
#include "cmsis_device.h"


// ----------------------------------------------------------------------------

extern void
__attribute__((noreturn,weak))
_start (void);

// ----------------------------------------------------------------------------
// Default exception handlers. Override the ones here by defining your own
// handler routines in your application code.
// ----------------------------------------------------------------------------


// The DEBUG version is not naked, to allow breakpoints at Reset_Handler
void __attribute__ ((section(".after_vectors"),weak))
Reset_Handler (void)
  {
	_start();
  }



void __attribute__ ((section(".after_vectors"),weak))
NMI_Handler(void)
{
  while (1)
    {
    }
}

void __attribute__ ((section(".after_vectors"),weak))
HardFault_Handler(void)
{
  while (1)
    {
    }
}


void __attribute__ ((section(".after_vectors"),weak))
MemManage_Handler(void)
  {
    while (1)
      {
      }
  }

void __attribute__ ((section(".after_vectors"),weak))
BusFault_Handler(void)
  {
    while (1)
      {
      }
  }

void __attribute__ ((section(".after_vectors"),weak))
UsageFault_Handler(void)
  {
    while (1)
      {
      }
  }



void __attribute__ ((section(".after_vectors"),weak))
SVC_Handler(void)
{
  while (1)
    {
    }
}



void __attribute__ ((section(".after_vectors"),weak))
DebugMon_Handler(void)
{
  while (1)
    {
    }
}



void __attribute__ ((section(".after_vectors"),weak))
PendSV_Handler(void)
{
  while (1)
    {
    }
}

void __attribute__ ((section(".after_vectors"),weak))
SysTick_Handler(void)
{
  while (1)
    {
    }
}

