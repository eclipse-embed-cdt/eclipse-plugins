//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#ifndef _TRACE_IMPL_H
#define _TRACE_IMPL_H

// ITM is the ARM standard mechanism, running over SWD/SWO on Cortex-M devices,
// and is the recommended setting, if available.
//
// The JLink probe and the GDB server fully support SWD/SWO
// and the JLink Debugging plug-in enables it by default.
// The current OpenOCD does not support SWD/SWO, so this configuration
// will not work on OpenOCD (will not crash, but nothing will be displayed).
//
// Choosing between semihosting stdout and debug depends on the capabilities
// of your GDB server, and also on specific needs; STDOUT is buffered, so
// nothing is displayed until a \n; DEBUG is not buffered, but can be very
// slow.
//
// The JLink GDB server fully support semihosting, and both configurations
// are available; to activate it, use "monitor semihosting enable" or check
// the corresponding button in the JLink Debugging plug-in.
// In OpenOCD, support for semihosting can be enabled using
// "monitor arm semihosting enable".
//
// Note: Applications built with semihosting output active cannot be
// executed without the debugger connected and active, since they use
// BKPT to communicate with the host. Attempts to run them standalone or
// without semihosting enabled will usually be terminated with hardware faults.

#define INCLUDE_TRACE_ITM
//#define INCLUDE_TRACE_SEMIHOSTING_STDOUT
//#define INCLUDE_TRACE_SEMIHOSTING_DEBUG

#if defined (__cplusplus)
extern "C"
{
#endif

#if defined(INCLUDE_TRACE_ITM)
  int
  _write_trace_itm(char* ptr, int len);
#endif

#if defined(INCLUDE_TRACE_SEMIHOSTING_STDOUT)
int
_write_trace_semihosting_stdout(char* ptr, int len);
#endif

#if defined(INCLUDE_TRACE_SEMIHOSTING_DEBUG)
int
_write_trace_semihosting_debug(char* ptr, int len);
#endif

#if defined (__cplusplus)
} // extern "C"
#endif

#endif // _TRACE_IMPL_H
