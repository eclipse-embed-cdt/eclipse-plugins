/*
 * Memory Spaces Definitions for the SiFive HiFive1 board.
 *
 * The values below can be addressed in further linker scripts
 * using functions like 'ORIGIN(RAM)' or 'LENGTH(RAM)'.
 */

MEMORY
{
  FLASH (rxai!w) : ORIGIN = 0x20400000, LENGTH = 12M
  RAM (wxa!ri) : ORIGIN = 0x80000000, LENGTH = 16K
}
