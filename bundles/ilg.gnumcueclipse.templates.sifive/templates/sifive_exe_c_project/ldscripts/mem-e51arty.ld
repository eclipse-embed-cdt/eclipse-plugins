/*
 * Memory Spaces Definitions for the SiFive Coreplex IP E51 Arty board.
 *
 * The values below can be addressed in further linker scripts
 * using functions like 'ORIGIN(RAM)' or 'LENGTH(RAM)'.
 */

MEMORY
{
  FLASH (rxai!w) : ORIGIN = 0x40400000, LENGTH = 12M
  RAM (wxa!ri) : ORIGIN = 0x80000000, LENGTH = 16K
}

/*

Table 5.1: E51 Coreplex Coreplex Series Physical Memory Map.

= Debug (4 KiB) =
0x0000 0000 	0x0000 00FF		Reserved
0x0000 0100 					Halt Notification
0x0000 0104 					Start Notification 
0x0000 0108 					Resume Notification
0x0000 010C 					Exception Notification
0x0000 0110 	0x0000 02FF		Reserved
0x0000 0300 	0x0000 03FF		Debug RAM (<= 256 B)
0x0000 0400 	0x0000 07FF		Debug Flags (<= 1 KiB) 
0x0000 0800		0x0000 0FFF		Debug ROM (<= 2 KiB)
 
0x0000 1000		0x01FF FFFF		Reserved

= On-Coreplex Devices (224 MiB) = 
0x0200 0000		0x0200 FFFF		Coreplex-Local Interrupts (CLINT) (<= 64 KiB)
0x0201 0000		0x07FF FFFF		Reserved
0x0800 0000		0x0800 1FFF		Instruction Tightly Integrated Memory (ITIM) (8 KiB)
0x0800 2000		0x0BFF FFFF		Reserved
0x0C00 0000		0x0FFF FFFF		Platform-Level Interrupt Control (PLIC) (64 MiB)

= Off-Coreplex address space for external I/O =
0x1000 0000		0x1FFF FFFF		Reserved
0x2000 0000 	0x3FFF FFFF		Peripheral Bus (512 MiB)
0x4000 0000		0x5FFF FFFF		System Bus (512 MiB)
0x6000 0000		0x7FFF FFFF		Reserved
0x8000 0000		0x8000 FFFF		Data Tightly Integrated Memory (DTIM) (64 KiB)
0x8001 0000		0xFFFF FFFF		Reserved
*/
