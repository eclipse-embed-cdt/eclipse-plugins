/*
 * This file is part of the µOS++ distribution.
 *   (https://github.com/micro-os-plus)
 * Copyright (c) 2015 Liviu Ionescu.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

#include <micro-os-plus/startup/initialize-hooks.h>
#include <micro-os-plus/diag/trace.h>

#include <stdlib.h>
#include <stdbool.h>
#include "atexit.h"

// ----------------------------------------------------------------------------

void __attribute__((weak,noreturn))
abort (void)
{
  trace_puts ("abort(), exiting...");

  _Exit (1);
  /* NOTREACHED */
}

// ----------------------------------------------------------------------------

/**
 * @details
 * `exit()` does several cleanups before ending the application:
 *
 * - calls all application-defined cleanup functions enrolled with `atexit()`;
 * - files and streams are
 * cleaned up: any pending output is delivered to the host system, each
 * open file or stream is closed, and files created by `tmpfile()` are
 * deleted (wishful thinking, not implemented);
 * - call the static destructors (in reverse order of constructors)
 *
 * When all cleanups are done, `_Exit()` is called to perform
 * the actual termination.
 */
void
__attribute__ ((noreturn))
exit (int code)
{
  trace_printf ("%s(%d)\n", __func__, code);

  // Call the cleanup functions enrolled with atexit().
  __call_exitprocs (code, NULL);

  // Run the C++ static destructors.
  os_run_fini_array ();

  // This should normally be the end of it.
  _Exit (code);

  // Reset again, in case _Exit() did not kill it.
  // This normally should not happen, but since it can be
  // overloaded by the application, better safe than sorry.
  os_terminate (code);

  // If it does not want o die, loop.
  while (true)
    {
      ;
    }
  /* NOTREACHED */
}

// ----------------------------------------------------------------------------

#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wunused-parameter"

// On Release, call the hardware reset procedure.
// On Debug, use a breakpoint to notify the debugger.
//
// It can be redefined by the application, if more functionality
// is required. For example, when semihosting is used, this
// function sends the return code to the host.

void __attribute__((weak, noreturn))
_Exit (int code)
{
  trace_printf ("%s()\n", __func__);

  // Print some statistics about memory use.
  os_terminate_goodbye ();

  // Gracefully terminate the trace session.
  trace_flush ();

#if defined(DEBUG)

#if defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__)
  if ((CoreDebug->DHCSR & CoreDebug_DHCSR_C_DEBUGEN_Msk) != 0)
    {
      // Break only if the debugger is connected.
      __BKPT(0);
    }
#endif /* defined(__ARM_ARCH_7M__) || defined(__ARM_ARCH_7EM__) */

#endif /* defined(DEBUG) */

  // Reset hardware or terminate the semihosting session.
  os_terminate (code);

  while (true)
    {
      ;
    }
  /* NOTREACHED */
}

#pragma GCC diagnostic pop

void __attribute__((weak, alias ("_Exit")))
_exit (int status);

// ----------------------------------------------------------------------------
