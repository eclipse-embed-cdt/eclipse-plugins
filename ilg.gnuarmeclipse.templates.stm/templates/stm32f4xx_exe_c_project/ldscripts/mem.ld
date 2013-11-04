/*
 * Linker script to configure memory regions.
 * Need modifying for a specific board. 
 *   FLASH.ORIGIN: starting address of flash
 *   FLASH.LENGTH: length of flash
 *   RAM.ORIGIN: starting address of RAM bank 0
 *   RAM.LENGTH: length of RAM bank 0
 */

/* Memory Spaces Definitions */

MEMORY
{
  RAM (xrw) : ORIGIN = 0x20000000, LENGTH = 128K
  CCMRAM (xrw) : ORIGIN = 0x10000000, LENGTH = 64K
  FLASH (rx) : ORIGIN = 0x08000000, LENGTH = $(STM32F4flashSize)K
  FLASHB1 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB0 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB1 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB2 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB3 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  MEMORY_ARRAY (xrw)  : ORIGIN = 0x20002000, LENGTH = 32
}

/* higher address of the user mode stack (end of 128K RAM on AHB bus)*/
_estack = 0x20000000 + 128*1024;

/*
 For external ram use 
   RAM (xrw) : ORIGIN = 0x64000000, LENGTH = 2048K

_estack = 0x64200000;
*/