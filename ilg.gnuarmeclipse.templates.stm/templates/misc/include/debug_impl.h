#ifndef _DEBUG_IMPL_H
#define _DEBUG_IMPL_H

// SWO is the ARM standard mechanism, running over SWD, and is the
// recommended setting.
// Choosing between semihosting stdout and debug depends on availability
// in your GDB server, and also on specific needs, STDOUT is buffered, so
// nothing is displayed until a \n; DEBUG is not buffered, but can be very
// slow.

#define INCLUDE_SWO
//#define INCLUDE_SEMIHOSTING_STDOUT
//#define INCLUDE_SEMIHOSTING_DEBUG

#if defined(INCLUDE_SWO)
int
swo_write(char* ptr, int len);
#endif

#if defined(INCLUDE_SEMIHOSTING_STDOUT)
int
semihostig_stdout_write(char* ptr, int len);
#endif

#if defined(INCLUDE_SEMIHOSTING_DEBUG)
int
semihostig_debug_write(char* ptr, int len);
#endif


#endif // _DEBUG_IMPL_H
