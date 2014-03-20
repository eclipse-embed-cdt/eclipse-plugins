//
// This file is part of the GNU ARM Eclipse Plug-ins project.
// Copyright (c) 2014 Liviu Ionescu
//

#ifndef SEMIHOSTING_H_
#define SEMIHOSTING_H_

enum OperationNumber
{
  SEMIHOSTING_EnterSVC = 0x17,
  SEMIHOSTING_ReportException = 0x18,
  SEMIHOSTING_SYS_CLOSE = 0x02,
  SEMIHOSTING_SYS_CLOCK = 0x10,
  SEMIHOSTING_SYS_ELAPSED = 0x30,
  SEMIHOSTING_SYS_ERRNO = 0x13,
  SEMIHOSTING_SYS_FLEN = 0x0C,
  SEMIHOSTING_SYS_GET_CMDLINE = 0x15,
  SEMIHOSTING_SYS_HEAPINFO = 0x16,
  SEMIHOSTING_SYS_ISERROR = 0x08,
  SEMIHOSTING_SYS_ISTTY = 0x09,
  SEMIHOSTING_SYS_OPEN = 0x01,
  SEMIHOSTING_SYS_READ = 0x06,
  SEMIHOSTING_SYS_READC = 0x07,
  SEMIHOSTING_SYS_REMOVE = 0x0E,
  SEMIHOSTING_SYS_RENAME = 0x0F,
  SEMIHOSTING_SYS_SEEK = 0x0A,
  SEMIHOSTING_SYS_SYSTEM = 0x12,
  SEMIHOSTING_SYS_TICKFREQ = 0x31,
  SEMIHOSTING_SYS_TIME = 0x11,
  SEMIHOSTING_SYS_TMPNAM = 0x0D,
  SEMIHOSTING_SYS_WRITE = 0x05,
  SEMIHOSTING_SYS_WRITEC = 0x03,
  SEMIHOSTING_SYS_WRITE0 = 0x04,

  ADP_Stopped_ApplicationExit = ((2 << 16) + 38),
  ADP_Stopped_RunTimeError = ((2 << 16) + 35),

};

/* Now the SWI numbers and reason codes for RDI (Angel) monitors.  */
#define AngelSWI_ARM 			0x123456
#ifdef __thumb__
#define AngelSWI 			0xAB
#else
#define AngelSWI 			AngelSWI_ARM
#endif
/* For thumb only architectures use the BKPT instruction instead of SWI.  */
#if defined(__ARM_ARCH_7M__)     \
    || defined(__ARM_ARCH_7EM__) \
    || defined(__ARM_ARCH_6M__)
#define AngelSWIInsn			"bkpt"
#define AngelSWIAsm			bkpt
#else
#define AngelSWIInsn			"swi"
#define AngelSWIAsm			swi
#endif

static inline int
__attribute__ ((always_inline))
call_host (int reason, void* arg)
{
  int value;
  asm volatile (

      " mov r0, %1  \n"
      " mov r1, %2  \n"
      " " AngelSWIInsn " %a3 \n"
      " mov %0, r0"

      : "=r" (value) /* Outputs */
      : "r" (reason), "r" (arg), "i" (AngelSWI) /* Inputs */
      : "r0", "r1", "r2", "r3", "ip", "lr", "memory", "cc"
      /* Clobbers r0 and r1, and lr if in supervisor mode */
  );

  /* Accordingly to page 13-77 of ARM DUI 0040D other registers
   can also be clobbered.  Some memory positions may also be
   changed by a system call, so they should not be kept in
   registers. Note: we are assuming the manual is right and
   Angel is respecting the APCS.  */
  return value;
}

static inline void
__attribute__ ((always_inline,noreturn))
report_exception (int reason)
{
  call_host (SEMIHOSTING_ReportException, (void*) reason);

  for (;;)
    ;
}

#endif /* SEMIHOSTING_H_ */
