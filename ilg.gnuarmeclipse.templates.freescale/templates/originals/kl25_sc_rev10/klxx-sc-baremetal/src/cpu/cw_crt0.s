/*
 * File:	cw_crt0.s
 * Purpose:	Lowest level routines for Kinetis.
 *
 * Notes:	This is a CodeWarrior specific version of crt0.s
 *
 */

	.extern start
	
	.global startup
	.global __startup

	.text
        .thumb_func
	.type __startup, %function
	
startup:
__startup:
    LDR     r0,=0;                    /*Initialize the GPRs*/
	LDR     r1,=0;
	LDR     r2,=0;
	LDR     r3,=0;
	LDR     r4,=0;
	LDR     r5,=0;
	LDR     r6,=0;
	LDR     r7,=0;
	CPSIE   i                       /* Unmask interrupts*/
    BL      start                  /* call the C code*/
done:
        B       done

	.end
