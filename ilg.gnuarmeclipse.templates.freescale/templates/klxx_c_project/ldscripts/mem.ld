/*
 * Memory Spaces Definitions.
 *
 * Need modifying for a specific board. 
 *
 * The values below can be addressed in further linker scripts
 * using functions like 'ORIGIN(RAM)' or 'LENGTH(RAM)'.
 */

MEMORY
{
  /* 
   * Unusual Freescale KL family configuration, one quarter of the 
   * entire RAM is below 0x2000000, three quarters above.
   * The RAM size must be exactly the size of the physical memory,  
   * not more, not less.
   */
  RAM (xrw) : ORIGIN = 0x20000000 - $(KLramSize)K / 4, LENGTH = $(KLramSize)K

  VECTORS (rx) : ORIGIN = 0x00000000, LENGTH = 0x00000400
  FCFROM (rx) : ORIGIN = 0x00000400, LENGTH = 0x00000010
  FLASH (rx) : ORIGIN = 0x00000410, LENGTH = $(KLflashSize)K - LENGTH(VECTORS) - LENGTH(FCFROM)
}

