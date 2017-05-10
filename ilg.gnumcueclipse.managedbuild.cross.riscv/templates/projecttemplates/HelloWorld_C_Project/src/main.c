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
 * Print a greeting message on standard output and exit.
 *
 * On embedded platforms this might require semi-hosting or similar.
 */

int
main(void)
{
  printf("$(messageriscv)" "\n");
  return 0;
}
