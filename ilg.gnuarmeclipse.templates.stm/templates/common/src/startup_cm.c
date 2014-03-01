//
// This file is part of the GNU ARM Eclipse project
// Copyright (c) 2014 Liviu Ionescu
//
// This module contains the startup code for a portable Cortex-M
// C/C++ application, built with CMSIS and newlib.
//
// The actual steps performed are:
// - clear the BSS area
// - run the preinit array, where, as the first entry, a separate routine
// is used to copy the initialised data section and to configure the
// system call via the CMSIS SystemInit()
// - run the init array (for the C++ static constructors)
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
// If required, the standard startup files can be called to
// perform all initialisations (define USE_STARTUP_FILES).

#include <sys/types.h>

#if defined(USE_STARTUP_FILES)
extern void
_start(void);

int
__register_exitproc(int type, void (*fn) (void), void *arg, void *d);

#else

void
__attribute__((noreturn))
_start(void);

#endif

// The CMSIS system initialisation routine.
extern void
SystemInit(void);

// Begin address for the initialisation values of the .data section.
// defined in linker script
extern unsigned int _sidata;
// Begin address for the .data section; defined in linker script
extern unsigned int _sdata;
// End address for the .data section; defined in linker script
extern unsigned int _edata;

#if !defined(USE_STARTUP_FILES)

// Begin address for the .bss section; defined in linker script
extern unsigned int __bss_start__;
// End address for the .bss section; defined in linker script
extern unsigned int __bss_end__;

// The entry point for the application.
// main() is the entry point for Newlib based applications
extern int
main(int argc, char* argv[]);

// The implementation for the exit routine, where for embedded
// applications, a system reset is performed.
extern void
__attribute__((noreturn))
_exit(int);

#endif

// ----------------------------------------------------------------------------

inline void
__attribute__((always_inline))
__initialise_data(unsigned int* from, unsigned int* section_begin,
    unsigned int* section_end)
{
  // Iterate and copy word by word.
  // It is assumed that the pointers are word aligned.
  unsigned int *p = section_begin;
  while (p < section_end)
    *p++ = *from++;
}

#if !defined(USE_STARTUP_FILES)

inline void
__attribute__((always_inline))
__initialise_bss(unsigned int* section_begin, unsigned int* section_end)
{
  // Iterate and clear word by word.
  // It is assumed that the pointers are word aligned.
  unsigned int *p = section_begin;
  while (p < section_end)
    *p++ = 0;
}

// These magic symbols are provided by the linker.
extern void
(*__preinit_array_start[])(void) __attribute__((weak));
extern void
(*__preinit_array_end[])(void) __attribute__((weak));
extern void
(*__init_array_start[])(void) __attribute__((weak));
extern void
(*__init_array_end[])(void) __attribute__((weak));
extern void
(*__fini_array_start[])(void) __attribute__((weak));
extern void
(*__fini_array_end[])(void) __attribute__((weak));

// Iterate over all the preinit/init routines.
inline void
__attribute__((always_inline))
__run_init_array(void)
{
  size_t count;
  size_t i;

  count = __preinit_array_end - __preinit_array_start;
  for (i = 0; i < count; i++)
    __preinit_array_start[i]();

  // If you need to run the code in the .init section, please use
  // the startup files, since this requires the code in crti.o and crtn.o
  // to add the function prologue/epilogue.
  //_init();

  count = __init_array_end - __init_array_start;
  for (i = 0; i < count; i++)
    __init_array_start[i]();
}

// Run all the cleanup routines.
inline void
__attribute__((always_inline))
__run_fini_array(void)
{
  size_t count;
  size_t i;

  count = __fini_array_end - __fini_array_start;
  for (i = count; i > 0; i--)
    __fini_array_start[i - 1]();

  // If you need to run the code in the .fini section, please use
  // the startup files, since this requires the code in crti.o and crtn.o
  // to add the function prologue/epilogue.
  //_fini();
}

// This is the place where Cortex-M core will go immediately after reset,
// via a call or jump from the Reset_Handler
void __attribute__ ((section(".after_vectors")))
_start(void)
{

  // Use Old Style Data and BSS section initialisation,
  // That will initialise a single BSS sections.

  // Zero fill the bss segment
  __initialise_bss(&__bss_start__, &__bss_end__);

#if !defined(USE_STARTUP_FILES)
  // Copy the data segment from Flash to RAM.
  // When using startup files, this code is executed via the preinit array.
  __initialise_data(&_sidata, &_sdata, &_edata);
#endif

  // Call the standard library initialisation (mandatory, SystemInit()
  // and C++ static constructors are called from here).
  __run_init_array();

  char* argv[2] = { "", 0 };
  // Call the main entry point, and save the exit code.
  int code = main(1, &argv[0]);

  // Run the static destructors.
  __run_fini_array();

  // On test platforms, like semihosting, this can be used to inform
  // the host on the test result.
  // On embedded platforms, usually reset the processor.
  _exit(code);

}

#endif

// System initialisation, executed before constructors.
static void
__attribute__((section(".after_vectors")))
__initialise_system()
{
#if defined(USE_STARTUP_FILES)
  // Copy the data segment from Flash to RAM.
  // This is also here since some library crt0 code (for example librdimon)
  // does not perform it, so make sure it is executed somewhere.
  __initialise_data(&_sidata, &_sdata, &_edata);
#endif

  // Call the CSMSIS system initialisation routine
  SystemInit();

#if defined (__VFP_FP__) && !defined (__SOFTFP__)

  // Enable the Cortex-M4 FPU only when -mfloat-abi=hard.
  // Code taken from Section 7.1, Cortex-M4 TRM (DDI0439C)
  asm volatile
  (
      " LDR.W           R0, =0xE000ED88 \n"// CPACR is located at address 0xE000ED88
      " LDR             R1, [R0] \n"// Read CPACR
      " ORR             R1, R1, #(0xF << 20) \n"// Set bits 20-23 to enable CP10 and CP11 coprocessor
      " STR             R1, [R0]"// Write back the modified value to the CPACR
      : /* out */
      : /* in */
      : /* clobber */
  );

#endif // (__VFP_FP__) && !(__SOFTFP__)
}

// The .preinit_array_sysinit section is defined in sections.ld as the first
// sub-section in the .preinit_array, so it is guaranteed that this function
// is executed before all other initialisations.
static void* __attribute__((section(".preinit_array_sysinit"),used))
__p_initialise_system = (void*) __initialise_system; // pointer to the above function

#if defined(USE_STARTUP_FILES)

// This function is required since the one included in newlib seems buggy
// and the startup files crash when using the semihosting configuration.
// The problem requires further investigations, but in the meantime
// considering that embedded applications rarely return from main,
// it is patched to return -1.

int
__register_exitproc(int type, void (*fn) (void), void *arg, void *d)
  {
    return -1;
  }

#endif
