/* Linker script to configure memory regions. 
 * Need modifying for a specific board. 
 *   FLASH.ORIGIN: starting address of flash
 *   FLASH.LENGTH: length of flash
 *   RAM.ORIGIN: starting address of RAM bank 0
 *   RAM.LENGTH: length of RAM bank 0
 */

/* Memory Spaces Definitions */

MEMORY
{
  RAM (xrw) : ORIGIN = 0x20000000, LENGTH = $(STM32F1ramSize)K
  FLASH (rx) : ORIGIN = 0x08000000, LENGTH = $(STM32F1flashSize)K
  FLASHB1 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB0 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB1 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB2 (rx) : ORIGIN = 0x00000000, LENGTH = 0
  EXTMEMB3 (rx) : ORIGIN = 0x00000000, LENGTH = 0
}

/* higher address of the user mode stack */
_estack = 0x20000000 + $(STM32F1ramSize)*1024;

/*
 For external ram use 
  RAM (xrw) : ORIGIN = 0x68000000, LENGTH = $(ramSize)K

_estack = 0x68000000 + $(STM32F1ramSize)*1024;
*/