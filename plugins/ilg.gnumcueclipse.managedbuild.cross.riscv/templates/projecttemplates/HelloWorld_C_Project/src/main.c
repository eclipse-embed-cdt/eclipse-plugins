/*
 ============================================================================
 Name        : main.c
 Author      : $(author)
 Version     :
 Copyright   : $(copyright)
 Description : Hello RISC-V World in C
 ============================================================================
 */

#include <stdio.h>

/*
 * Demonstrate how to print a greeting message on standard output
 * and exit.
 *
 * WARNING: This is a build-only project. Do not try to run it on a
 * physical board, since it lacks the device specific startup.
 */

int
main(void)
{
  printf("$(messageriscv)" "\n");
  return 0;
}
