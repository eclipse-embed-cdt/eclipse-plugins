#include "debug_impl.h"

int
BKPT(int op, void* p1, void* p2)
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

