//
// This file is part of the GNU ARM Eclipse Plug-in
// Copyright (c) 2013 Liviu Ionescu
//

#include "trace_impl.h"

#if defined(INCLUDE_TRACE_ITM)

// Define everything here, to pass standalone compilations

// Stimulus Port Register word access
#define ITM_STIM_U32 (*(volatile unsigned int*)0xE0000000)
// Stimulus Port Register byte access
#define ITM_STIM_U8  (*(volatile         char*)0xE0000000)
// Control Register
#define ITM_ENA      (*(volatile unsigned int*)0xE0000E00)
// Trace control register
#define ITM_TCR      (*(volatile unsigned int*)0xE0000E80)

int
_write_trace_itm(char* ptr, int len)
{
  static int isInitialised;
  if (isInitialised == 0) {

      // TODO: enable SWO
      // For the SEGGER J-Link GDB server, this is not required...

      isInitialised = 1;
  }

  // Use Stimulus port channel 0
  int i = 0;
  for (; i < len; i++)
    {

      // Check if ITM_TCR.ITMENA is set
      if ((ITM_TCR & 1) == 0)
        {
          return i; // return the number of sent characters (may be 0)
        }

      // Check if stimulus port 0 is enabled
      if ((ITM_ENA & 1) == 0)
        {
          return i; // return the number of sent characters (may be 0)
        }

      // Wait until STIMx is ready,
      while ((ITM_STIM_U8 & 1) == 0)
        ;
      // then send data
      ITM_STIM_U8 = *ptr++;

    }

  return len; // all characters successfully sent
}

#endif // INCLUDE_TRACE_ITM

#if defined(INCLUDE_TRACE_SEMIHOSTING_DEBUG) || defined(INCLUDE_TRACE_SEMIHOSTING_STDOUT)

#define SYS_OPEN        0x01
#define SYS_WRITEC      0x03
#define SYS_WRITE0      0x04
#define SYS_WRITE       0x05

int
semihosting_bkpt(int op, void* p1, void* p2)
{
  register int r0 asm("r0");
  register int r1 asm("r1") __attribute__((unused));
  register int r2 asm("r2") __attribute__((unused));

  r0 = op;
  r1 = (int) p1;
  r2 = (int) p2;

  asm volatile(
      " bkpt 0xAB \n"
      : "=r"(r0) // out
      :// in
      :// clobber
  );
  return r0;
}

#endif // INCLUDE_TRACE_SEMIHOSTING_*


#if defined(INCLUDE_TRACE_SEMIHOSTING_STDOUT)

int
_write_trace_semihosting_stdout(char* ptr, int len)
{
  static int handle;
  void* block[3];
  int ret;

  if (handle == 0)
    {
      // On the first call get the file handle from the host
      block[0] = ":tt"; // special filename to be used for stdin/out/err
      block[1] = (void*) 4; // mode "w"
      // length of ":tt", except null terminator
      block[2] = (void*) sizeof(":tt")-1;

      ret = semihosting_bkpt(SYS_OPEN, (void*) block, (void*) 0);
      if (ret == -1)
        return -1;

      handle = ret;
    }

  block[0] = (void*) handle;
  block[1] = ptr;
  block[2] = (void*) len;
  // send character array to host file/device
  ret = semihosting_bkpt(SYS_WRITE, (void*) block, (void*) 0);
  // this call returns the number of bytes NOT written (0 if all ok)

  // -1 is not a legal value, but SEGGER seems to return it
  if (ret == -1)
    return -1;

  // The compliant way of returning errors
  if (ret == len)
    return -1;

  return len - ret;
}

#endif // INCLUDE_TRACE_SEMIHOSTING_STDOUT

#if defined(INCLUDE_TRACE_SEMIHOSTING_DEBUG)

int
_write_trace_semihosting_debug(char* ptr, int len)
{
  // Since the single character debug channel is quite slow, try to
  // optimise and send a null terminated string, if possible.
  if (ptr[len] == '\0')
    {
      // send string
      semihosting_bkpt(SYS_WRITE0, (void*) ptr, (void*) 0);
    }
  else
    {
      int i;
      for (i = 0; i < len; ++i, ++ptr)
        {
          // send character
          semihosting_bkpt(SYS_WRITEC, (void*) ptr, (void*) 0);
        }
    }
  return len;
}

#endif // INCLUDE_TRACE_SEMIHOSTING_DEBUG

