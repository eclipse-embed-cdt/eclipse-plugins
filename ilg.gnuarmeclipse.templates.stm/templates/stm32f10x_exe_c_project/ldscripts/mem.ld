/* Linker script to configure memory regions. 
 * Need modifying for a specific board. 
 *   FLASH.ORIGIN: starting address of flash
 *   FLASH.LENGTH: length of flash
 *   RAM.ORIGIN: starting address of RAM bank 0
 *   RAM.LENGTH: length of RAM bank 0
 */

MEMORY
{
  FLASH (rx) : ORIGIN = 0x00000000, LENGTH = $(flashSize)K
  RAM (rwx)  : ORIGIN = 0x20000000, LENGTH = $(ramSize)K
}
