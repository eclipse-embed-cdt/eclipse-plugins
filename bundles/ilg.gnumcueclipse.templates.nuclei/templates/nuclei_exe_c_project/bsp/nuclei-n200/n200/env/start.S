// See LICENSE for license details.

#include "n200/drivers/riscv_encoding.h"

	.section .init

	.globl _start
	.type _start,@function
_start:
	csrc CSR_MSTATUS, MSTATUS_MIE
    	/* Set the the NMI base to share with mtvec by setting CSR_MMISC_CTL */
    	li t0, (0x1 << 9);
    	csrs CSR_MMISC_CTL, t0

	/* Intial the mtvt*/
    	la t0, vector_base
    	csrw CSR_MTVT, t0

	/* Intial the mtvt2 and enable it*/
    	la t0, irq_entry
    	csrw CSR_MTVT2, t0
    	csrs CSR_MTVT2, 0x1

    	/* Intial the CSR MTVEC for the Trap ane NMI base addr*/
    	la t0, trap_entry
    	csrw CSR_MTVEC, t0


#ifdef __riscv_flen
	/* Enable FPU */
	li t0, MSTATUS_FS
	csrs mstatus, t0 
	csrw fcsr, x0   
#endif

    	.option push
    	.option norelax
	la gp, __global_pointer$
    	.option pop
	la sp, _sp

	/* Bob: Load code section from flash to ILM */
	la a0, _ilm_lma
	la a1, _ilm
        beq a0, a1, 2f  /*If the ILM phy-address same as the logic-address, then quit*/
	la a2, _eilm
	bgeu a1, a2, 2f
1:
	lw t0, (a0)
	sw t0, (a1)
	addi a0, a0, 4
	addi a1, a1, 4
	bltu a1, a2, 1b
2:

	/* Load data section */
	la a0, _data_lma
	la a1, _data
	la a2, _edata
	bgeu a1, a2, 2f
1:
	lw t0, (a0)
	sw t0, (a1)
	addi a0, a0, 4
	addi a1, a1, 4
	bltu a1, a2, 1b
2:

	/* Clear bss section */
	la a0, __bss_start
	la a1, _end
	bgeu a0, a1, 2f
1:
	sw zero, (a0)
	addi a0, a0, 4
	bltu a0, a1, 1b
2:
	/*enable mcycle_minstret*/
        csrci CSR_MCOUNTINHIBIT, 0x5
	/* Call global constructors */
	la a0, __libc_fini_array
	call atexit
	call __libc_init_array


	/* argc = argv = 0 */
	li a0, 0
	li a1, 0
	call main
	tail exit

1:
	j 1b

	.global disable_mcycle_minstret
disable_mcycle_minstret:
        csrsi CSR_MCOUNTINHIBIT, 0x5
	ret

	.global enable_mcycle_minstret
enable_mcycle_minstret:
        csrci CSR_MCOUNTINHIBIT, 0x5
	ret

	.global core_wfe
core_wfe:
    	csrc CSR_MSTATUS, MSTATUS_MIE
    	csrs CSR_WFE, 0x1
    	wfi
    	csrc CSR_WFE, 0x1
    	csrs CSR_MSTATUS, MSTATUS_MIE
	ret
