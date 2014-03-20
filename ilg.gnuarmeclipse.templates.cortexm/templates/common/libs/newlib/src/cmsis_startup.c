//
// This file is part of the GNU ARM Eclipse Plug-ins project.
// Copyright (c) 2014 Liviu Ionescu
//

// This module contains the startup code for a portable Cortex-M
// C/C++ application, built with CMSIS and newlib.
//
// Control reaches here from Reset_Handler via jump or call.
//
// The actual steps performed by _start are:
// - clear the BSS area
// - copy the initialised data section
// - initialise the system via the CMSIS SystemInit()
// - run the preinit/init array (for the C++ static constructors)
// - initialise the arc/argv
// - branch to main()
// - run the fini array (for the C++ static destructors)
// - call _exit(), directly or via exit()
//
// After Reset the Cortex-M processor is in Thread mode,
// priority is Privileged, and the Stack is set to Main.
//
// The normal configuration is standalone, with all support
// functions implemented locally.
//
// For this to be called, the project linker must be configured without
// the startup sequence (-nostartfiles).

#include <sys/types.h>

#include "cmsis_device.h"

// ----------------------------------------------------------------------------

// The CMSIS system initialisation routine.
extern void
SystemInit (void);

// Begin address for the initialisation values of the .data section.
// defined in linker script
extern unsigned int _sidata;
// Begin address for the .data section; defined in linker script
extern unsigned int _sdata;
// End address for the .data section; defined in linker script
extern unsigned int _edata;

// Begin address for the .bss section; defined in linker script
extern unsigned int __bss_start__;
// End address for the .bss section; defined in linker script
extern unsigned int __bss_end__;

extern void
__initialise_args (int*, char***);

// The entry point for the application.
// main() is the entry point for Newlib based applications
extern int
main (int argc, char* argv[]);

// The implementation for the exit routine, where for embedded
// applications, a system reset is performed.
extern void
__attribute__((noreturn))
_exit (int);

// ----------------------------------------------------------------------------

inline void
__attribute__((always_inline))
__initialise_data (unsigned int* from, unsigned int* section_begin,
		   unsigned int* section_end)
{
  // Iterate and copy word by word.
  // It is assumed that the pointers are word aligned.
  unsigned int *p = section_begin;
  while (p < section_end)
    *p++ = *from++;
}

inline void
__attribute__((always_inline))
__initialise_bss (unsigned int* section_begin, unsigned int* section_end)
{
  // Iterate and clear word by word.
  // It is assumed that the pointers are word aligned.
  unsigned int *p = section_begin;
  while (p < section_end)
    *p++ = 0;
}

// These magic symbols are provided by the linker.
extern void
(*__preinit_array_start[]) (void) __attribute__((weak));
extern void
(*__preinit_array_end[]) (void) __attribute__((weak));
extern void
(*__init_array_start[]) (void) __attribute__((weak));
extern void
(*__init_array_end[]) (void) __attribute__((weak));
extern void
(*__fini_array_start[]) (void) __attribute__((weak));
extern void
(*__fini_array_end[]) (void) __attribute__((weak));

// Iterate over all the preinit/init routines.
inline void
__attribute__((always_inline))
__run_init_array (void)
{
  size_t count;
  size_t i;

  count = __preinit_array_end - __preinit_array_start;
  for (i = 0; i < count; i++)
    __preinit_array_start[i] ();

  // If you need to run the code in the .init section, please use
  // the startup files, since this requires the code in crti.o and crtn.o
  // to add the function prologue/epilogue.
  //_init();

  count = __init_array_end - __init_array_start;
  for (i = 0; i < count; i++)
    __init_array_start[i] ();
}

// Run all the cleanup routines.
inline void
__attribute__((always_inline))
__run_fini_array (void)
{
  size_t count;
  size_t i;

  count = __fini_array_end - __fini_array_start;
  for (i = count; i > 0; i--)
    __fini_array_start[i - 1] ();

  // If you need to run the code in the .fini section, please use
  // the startup files, since this requires the code in crti.o and crtn.o
  // to add the function prologue/epilogue.
  //_fini();
}

// This is the place where Cortex-M core will go immediately after reset,
// via a call or jump from the Reset_Handler
void __attribute__ ((section(".after_vectors"),noreturn))
_start (void)
{

  // Use Old Style Data and BSS section initialisation,
  // That will initialise a single BSS sections.

  // Zero fill the bss segment
  __initialise_bss (&__bss_start__, &__bss_end__);

  // Copy the data segment from Flash to RAM.
  // When using startup files, this code is executed via the preinit array.
  __initialise_data (&_sidata, &_sdata, &_edata);

  // Call the CSMSIS system initialisation routine
  SystemInit ();

#if defined (__VFP_FP__) && !defined (__SOFTFP__)

  // Enable the Cortex-M4 FPU only when -mfloat-abi=hard.
  // Code taken from Section 7.1, Cortex-M4 TRM (DDI0439C)

  // Set bits 20-23 to enable CP10 and CP11 coprocessor
  SCB->CPACR |= (0xF << 20);

#endif // (__VFP_FP__) && !(__SOFTFP__)

  // Eventually get the args (useful in semihosting configurations).
  int argc;
  char** argv;
  __initialise_args (&argc, &argv);

  // Call the standard library initialisation (mandatory for C++ to
  // execute the constructors for the static objects).
  __run_init_array ();

  // Call the main entry point, and save the exit code.
  int code = main (argc, argv);

  // Run the C++ static destructors.
  __run_fini_array ();

  // On embedded platforms, usually reset the processor (or hang in
  // a loop in DEBUG configurations).
  // On test platforms, like semihosting, inform the host on the test result.
  _exit (code);
}
