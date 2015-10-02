//
// This file is part of the µOS++ III distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef CORTEXM_EXCEPTION_HANDLERS_H_
#define CORTEXM_EXCEPTION_HANDLERS_H_

// ----------------------------------------------------------------------------



// External references to cortexm_handlers.c

extern void
Reset_Handler(void);
extern void
NMI_Handler(void);
extern void
HardFault_Handler(void);


extern void
MemManage_Handler(void);
extern void
BusFault_Handler(void);
extern void
UsageFault_Handler(void);


extern void
SVC_Handler(void);


extern void
DebugMon_Handler(void);


extern void
PendSV_Handler(void);
extern void
SysTick_Handler(void);


// ----------------------------------------------------------------------------

#endif // CORTEXM_EXCEPTION_HANDLERS_H_
