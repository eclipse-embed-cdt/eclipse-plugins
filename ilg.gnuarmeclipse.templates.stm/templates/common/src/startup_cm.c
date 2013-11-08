/**
 ******************************************************************************
 * @file      startup_cm.c
 * @author    Liviu Ionescu
 * @author    Code Red Technologies Ltd.
 * @brief     This module performs:
 *                - Set the vector table entries with the exceptions ISR address
 *                - Configure the clock system via the SystemInit()
 *                - Branches to main in the C library.
 *            After Reset the Cortex-M processor is in Thread mode,
 *            priority is Privileged, and the Stack is set to Main.
 *
 *            The normal configuration is standalone, with all support
 *            functions implemented locally.
 *
 *            If required, the standard startup files can be called to
 *            perform all initialisations (define USE_STARTUP_FILES).
 ******************************************************************************
 */

#include <sys/types.h>

#if defined (__cplusplus)
extern "C"
  {
#endif

void __attribute__((weak))
Reset_Handler(void);

//*****************************************************************************
//
// The entry point for the application.
// main() is the entry point for Newlib based applications
//
//*****************************************************************************

#if defined(USE_STARTUP_FILES)
extern void
_start(void);
#endif

extern void
SystemInit(void);

inline void
data_init(unsigned int romstart, unsigned int start, unsigned int len);

// start address for the initialisation values of the .data section.
// defined in linker script
extern unsigned int _sidata;
// start address for the .data section; defined in linker script
extern unsigned int _sdata;
// end address for the .data section; defined in linker script
extern unsigned int _edata;

#if !defined(USE_STARTUP_FILES)

inline void
bss_init(unsigned int start, unsigned int len);

// start address for the .bss section; defined in linker script
extern unsigned int __bss_start__;
// end address for the .bss section; defined in linker script
extern unsigned int __bss_end__;

extern void
__libc_init_array(void);

extern int
main(void);

extern void
_exit(int);

#endif

//*****************************************************************************
#if defined (__cplusplus)
} // extern "C"
#endif

inline void
__attribute__((always_inline))
data_init(unsigned int romstart, unsigned int start, unsigned int len)
{
  unsigned int *pulDest = (unsigned int*) start;
  unsigned int *pulSrc = (unsigned int*) romstart;
  unsigned int loop;
  for (loop = 0; loop < len; loop = loop + 4)
    *pulDest++ = *pulSrc++;
}

#if !defined(USE_STARTUP_FILES)

inline void
__attribute__((always_inline))
bss_init(unsigned int start, unsigned int len)
{
  unsigned int *pulDest = (unsigned int*) start;
  unsigned int loop;
  for (loop = 0; loop < len; loop = loop + 4)
    *pulDest++ = 0;
}

#endif

#if defined(USE_STARTUP_FILES)
void __attribute__ ((section(".after_vectors"), naked))
Reset_Handler(void)
  {
    // simply go to the startup code
    asm volatile
    (
        " b _start \n"
        :
        :
        :
    );
  }
#else

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
__libc_init_array(void)
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
__libc_fini_array(void)
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

void __attribute__ ((section(".after_vectors")))
Reset_Handler(void)
{

  // Use Old Style Data and BSS section initialisation.
  // This will only initialise a single RAM bank.
  unsigned int *ExeAddr;
  unsigned int *EndAddr;
  unsigned int SectionLen;

  // Zero fill the bss segment
  ExeAddr = &__bss_start__;
  EndAddr = &__bss_end__;
  SectionLen = (void*) EndAddr - (void*) ExeAddr;
  bss_init((unsigned int) ExeAddr, SectionLen);

  // Call the standard library initialisation (mandatory, SystemInit()
  // and C++ constructors are called from here).
  __libc_init_array();

  int r = main();

  __libc_fini_array();

  _exit(r);

}

#endif

void
__attribute__((section(".after_vectors")))
system_init()
{
  unsigned int *LoadAddr;
  unsigned int *ExeAddr;
  unsigned int *EndAddr;
  unsigned int SectionLen;

  // Copy the data segment from flash to RAM.
  LoadAddr = &_sidata;
  ExeAddr = &_sdata;
  EndAddr = &_edata;
  SectionLen = (void*) EndAddr - (void*) ExeAddr;
  data_init((unsigned int) LoadAddr, (unsigned int) ExeAddr, SectionLen);

  SystemInit();

}

// The .preinit_array_sysinit section is defined in sections.ld as the first
// sub-section in the .preinit_array, so it is guaranteed that this function
// is executed before all other initialisations.
void *
__attribute__((section(".preinit_array_sysinit")))
p_system_init = system_init; // pointer to the above function

