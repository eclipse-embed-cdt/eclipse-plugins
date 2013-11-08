//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#ifndef _TRACE_IMPL_H
#define _TRACE_IMPL_H

// SWO is the ARM standard mechanism, running over SWD, and is the
// recommended setting.
// Choosing between semihosting stdout and debug depends on availability
// in your GDB server, and also on specific needs, STDOUT is buffered, so
// nothing is displayed until a \n; DEBUG is not buffered, but can be very
// slow.

#define INCLUDE_TRACE_SWO
//#define INCLUDE_TRACE_SEMIHOSTING_STDOUT
//#define INCLUDE_TRACE_SEMIHOSTING_DEBUG

#if defined (__cplusplus)
extern "C"
{
#endif

#if defined(INCLUDE_TRACE_SWO)
  int
  _write_trace_swo(char* ptr, int len);
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
