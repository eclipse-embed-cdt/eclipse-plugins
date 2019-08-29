// See LICENSE for license details

#ifndef ENTRY_S
#define ENTRY_S

#include "encoding.h"
#include "headers/bits.h"

#only save caller registers
.macro TRAP_ENTRY
  addi sp, sp, -16*REGBYTES

  STORE x1,  0*REGBYTES(sp)
  STORE x5,  1*REGBYTES(sp)
  STORE x6,  2*REGBYTES(sp)
  STORE x7,  3*REGBYTES(sp)
  STORE x10, 4*REGBYTES(sp)
  STORE x11, 5*REGBYTES(sp)
  STORE x12, 6*REGBYTES(sp)
  STORE x13, 7*REGBYTES(sp)
  STORE x14, 8*REGBYTES(sp)
  STORE x15, 9*REGBYTES(sp)
  STORE x16, 10*REGBYTES(sp)
  STORE x17, 11*REGBYTES(sp)
  STORE x28, 12*REGBYTES(sp)
  STORE x29, 13*REGBYTES(sp)
  STORE x30, 14*REGBYTES(sp)
  STORE x31, 15*REGBYTES(sp)
.endm

#restore caller registers
.macro TRAP_EXIT
## Remain in M-mode after mret
#  li t0, MSTATUS_MPP
#  csrs mstatus, t0

  LOAD x1,  0*REGBYTES(sp)
  LOAD x5,  1*REGBYTES(sp)
  LOAD x6,  2*REGBYTES(sp)
  LOAD x7,  3*REGBYTES(sp)
  LOAD x10, 4*REGBYTES(sp)
  LOAD x11, 5*REGBYTES(sp)
  LOAD x12, 6*REGBYTES(sp)
  LOAD x13, 7*REGBYTES(sp)
  LOAD x14, 8*REGBYTES(sp)
  LOAD x15, 9*REGBYTES(sp)
  LOAD x16, 10*REGBYTES(sp)
  LOAD x17, 11*REGBYTES(sp)
  LOAD x28, 12*REGBYTES(sp)
  LOAD x29, 13*REGBYTES(sp)
  LOAD x30, 14*REGBYTES(sp)
  LOAD x31, 15*REGBYTES(sp)

  addi sp, sp, 16*REGBYTES
  mret
.endm

  .section      .text.entry	
  .align 2
  .global trap_entry
.weak trap_entry
trap_entry:
  TRAP_ENTRY

  csrr a0, mcause
  csrr a1, mepc
  call handle_trap
  csrw mepc, a0

  TRAP_EXIT

.weak handle_trap
handle_trap:
1:
  j 1b
	
#endif
