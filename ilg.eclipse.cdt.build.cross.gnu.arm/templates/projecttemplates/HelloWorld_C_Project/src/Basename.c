/*
 ============================================================================
 Name        : $(baseName).c
 Author      : $(author)
 Version     :
 Copyright   : $(copyright)
 Description : Hello World in C
 ============================================================================
 */

#include <stdio.h>

/*

 Print a greeting on standard output and exit.

 On embedded platforms this might need to enable semi-hosting or similar.

 For example, for toolchains derived from GNU Tools for Embedded,
 the following should be added to the linker:

 --specs=rdimon.specs -Wl,--start-group -lgcc -lc -lc -lm -lrdimon -Wl,--end-group

 */

int
main(void)
{
  printf("$(messagearm)");
  return 0;
}
