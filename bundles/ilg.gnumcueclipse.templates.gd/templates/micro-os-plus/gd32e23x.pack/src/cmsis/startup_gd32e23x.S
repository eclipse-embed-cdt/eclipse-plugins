/**
  ******************************************************************************
  * @file    startup_gd32e23x.s
  * @author  MCU SD
  * @version V1.0.0
  * @date    4-Jan-2019
  * @brief   GD32E23x startup code.
  ******************************************************************************
  */

  .syntax unified
  .cpu cortex-m23
  .fpu softvfp
  .thumb

.global  g_pfnVectors
.global  Default_Handler

/* start address for the initialization values of the .data section.
defined in linker script */
.word  _sidata
/* start address for the .data section. defined in linker script */
.word  _sdata
/* end address for the .data section. defined in linker script */
.word  _edata
/* start address for the .bss section. defined in linker script */
.word  _sbss
/* end address for the .bss section. defined in linker script */
.word  _ebss

    .section  .text.Reset_Handler
  .weak  Reset_Handler
  .type  Reset_Handler, %function
Reset_Handler:  
   ldr   r0, =_estack
   mov   sp, r0          /* set stack pointer */

/* Copy the data segment initializers from flash to SRAM */
  movs  r1, #0
  b  LoopCopyDataInit

CopyDataInit:
  ldr  r3, =_sidata
  ldr  r3, [r3, r1]
  str  r3, [r0, r1]
  adds  r1, r1, #4

LoopCopyDataInit:
  ldr  r0, =_sdata
  ldr  r3, =_edata
  adds  r2, r0, r1
  cmp  r2, r3
  bcc  CopyDataInit
  ldr  r2, =_sbss
  b  LoopFillZerobss
/* Zero fill the bss segment. */
FillZerobss:
  movs  r3, #0
  str  r3, [r2]
  adds r2, r2, #4


LoopFillZerobss:
  ldr  r3, = _ebss
  cmp  r2, r3
  bcc  FillZerobss

/* Call the clock system intitialization function.*/
  bl  SystemInit
/* Call static constructors */
    bl __libc_init_array
/* Call the application's entry point.*/
  bl  main

LoopForever:
    b LoopForever


.size  Reset_Handler, .-Reset_Handler

/**
 * @brief  This is the code that gets called when the processor receives an
 *         unexpected interrupt.  This simply enters an infinite loop, preserving
 *         the system state for examination by a debugger.
 *
 * @param  None
 * @retval : None
*/
    .section  .text.Default_Handler,"ax",%progbits
Default_Handler:
Infinite_Loop:
  b  Infinite_Loop
  .size  Default_Handler, .-Default_Handler
/******************************************************************************
*
* The minimal vector table for a Cortex M0.  Note that the proper constructs
* must be placed on this to ensure that it ends up at physical address
* 0x0000.0000.
*
******************************************************************************/
   .section  .isr_vector,"a",%progbits
  .type  g_pfnVectors, %object
  .size  g_pfnVectors, .-g_pfnVectors



g_pfnVectors:
                    .word _estack
                    .word Reset_Handler              /* Vector Number 1,Reset Handler */
                    .word NMI_Handler                /* Vector Number 2,NMI Handler */
                    .word HardFault_Handler          /* Vector Number 3,Hard Fault Handler */
                    .word 0                          /* Reserved */
                    .word 0                          /* Reserved */
                    .word 0                          /* Reserved */
                    .word 0                          /* Reserved  */
                    .word 0                          /* Reserved */
                    .word 0                          /* Reserved */
                    .word 0                          /* Reserved */
                    .word SVC_Handler                /* Vector Number 11,SVCall Handler */
                    .word 0                          /* Reserved */
                    .word 0                          /* Reserved */
                    .word PendSV_Handler             /* Vector Number 14,PendSV Handler */
                    .word SysTick_Handler            /* Vector Number 15,SysTick Handler */

                    /* External Interrupts */
                    .word WWDGT_IRQHandler               /* Vector Number 16,Window Watchdog Timer */
                    .word LVD_IRQHandler                 /* Vector Number 17,LVD through EXTI Line detect */
                    .word RTC_IRQHandler                 /* Vector Number 18,RTC through EXTI Line */
                    .word FMC_IRQHandler                 /* Vector Number 19,FMC */
                    .word RCU_IRQHandler                 /* Vector Number 20,RCU */
                    .word EXTI0_1_IRQHandler             /* Vector Number 21,EXTI Line 0 and EXTI Line 1 */
                    .word EXTI2_3_IRQHandler             /* Vector Number 22,EXTI Line 2 and EXTI Line 3 */
                    .word EXTI4_15_IRQHandler            /* Vector Number 23,EXTI Line 4 to EXTI Line 15 */
                    .word 0                              /* Reserved */
                    .word DMA_Channel0_IRQHandler        /* Vector Number 25,DMA Channel 0 */
                    .word DMA_Channel1_2_IRQHandler      /* Vector Number 26,DMA Channel 1 and DMA Channel 2 */
                    .word DMA_Channel3_4_IRQHandler      /* Vector Number 27,DMA Channel 3 and DMA Channel 4 */
                    .word ADC_CMP_IRQHandler             /* Vector Number 28,ADC and Comparator  */
                    .word TIMER0_BRK_UP_TRG_COM_IRQHandler             /* Vector Number 29,TIMER0 Break,Update,Trigger and Commutation  */
                    .word TIMER0_Channel_IRQHandler      /* Vector Number 30,TIMER0 Channel Capture Compare  */
                    .word 0                              /* Reserved  */
                    .word TIMER2_IRQHandler              /* Vector Number 32,TIMER2  */
                    .word TIMER5_IRQHandler              /* Vector Number 33,TIMER5  */
                    .word 0                              /* Reserved  */
                    .word TIMER13_IRQHandler             /* Vector Number 35,TIMER13  */
                    .word TIMER14_IRQHandler             /* Vector Number 36,TIMER14  */
                    .word TIMER15_IRQHandler             /* Vector Number 37,TIMER15 */
                    .word TIMER16_IRQHandler             /* Vector Number 38,TIMER16 */
                    .word I2C0_EV_IRQHandler             /* Vector Number 39,I2C0 Event */

                    .word I2C1_EV_IRQHandler             /* Vector Number 40,I2C1 Event */
                    .word SPI0_IRQHandler                /* Vector Number 41,SPI0 */
                    .word SPI1_IRQHandler                /* Vector Number 42,SPI1 */
                    .word USART0_IRQHandler              /* Vector Number 43,USART0 */
                    .word USART1_IRQHandler              /* Vector Number 44,USART1 */
                    .word 0                              /* Reserved */
                    .word 0                              /* Reserved */
                    .word 0                              /* Reserved */
		    .word I2C0_ER_IRQHandler             /* Vector Number 48,I2C0 Error */
                    .word 0                              /* Reserved */		    
		    .word I2C1_ER_IRQHandler             /* Vector Number 50,I2C1 Error */


  .weak NMI_Handler
  .thumb_set NMI_Handler,Default_Handler

  .weak HardFault_Handler
  .thumb_set HardFault_Handler,Default_Handler

 
  .weak SVC_Handler
  .thumb_set SVC_Handler,Default_Handler
  
  .weak PendSV_Handler
  .thumb_set PendSV_Handler,Default_Handler

  .weak SysTick_Handler
  .thumb_set SysTick_Handler,Default_Handler

  .weak WWDGT_IRQHandler
  .thumb_set WWDGT_IRQHandler,Default_Handler

  .weak LVD_IRQHandler
  .thumb_set LVD_IRQHandler,Default_Handler

  .weak TAMPER_IRQHandler
  .thumb_set TAMPER_IRQHandler,Default_Handler
  
  .weak RTC_IRQHandler
  .thumb_set RTC_IRQHandler,Default_Handler
  
  .weak FMC_IRQHandler
  .thumb_set FMC_IRQHandler,Default_Handler

  .weak RCU_IRQHandler
  .thumb_set RCU_IRQHandler,Default_Handler
  
  .weak EXTI0_1_IRQHandler
  .thumb_set EXTI0_1_IRQHandler,Default_Handler

  .weak EXTI2_3_IRQHandler
  .thumb_set EXTI2_3_IRQHandler,Default_Handler

  .weak EXTI4_15_IRQHandler
  .thumb_set EXTI4_15_IRQHandler,Default_Handler

  .weak DMA_Channel0_IRQHandler
  .thumb_set DMA_Channel0_IRQHandler,Default_Handler

  .weak DMA_Channel1_2_IRQHandler
  .thumb_set DMA_Channel1_2_IRQHandler,Default_Handler

  .weak DMA_Channel3_4_IRQHandler
  .thumb_set DMA_Channel3_4_IRQHandler,Default_Handler

 
  .weak ADC_CMP_IRQHandler
  .thumb_set ADC_CMP_IRQHandler,Default_Handler

  .weak TIMER0_BRK_UP_TRG_COM_IRQHandler
  .thumb_set TIMER0_BRK_UP_TRG_COM_IRQHandler,Default_Handler

  .weak TIMER0_Channel_IRQHandler
  .thumb_set TIMER0_Channel_IRQHandler,Default_Handler

  .weak TIMER2_IRQHandler
  .thumb_set TIMER2_IRQHandler,Default_Handler

  .weak TIMER5_IRQHandler
  .thumb_set TIMER5_IRQHandler,Default_Handler

  .weak TIMER13_IRQHandler
  .thumb_set TIMER13_IRQHandler,Default_Handler

  .weak TIMER14_IRQHandler
  .thumb_set TIMER14_IRQHandler,Default_Handler

  .weak TIMER15_IRQHandler
  .thumb_set TIMER15_IRQHandler,Default_Handler

  .weak TIMER16_IRQHandler
  .thumb_set TIMER16_IRQHandler,Default_Handler

  .weak I2C0_EV_IRQHandler
  .thumb_set I2C0_EV_IRQHandler,Default_Handler
  
  .weak I2C1_EV_IRQHandler
  .thumb_set I2C1_EV_IRQHandler,Default_Handler
  
  .weak SPI0_IRQHandler
  .thumb_set SPI0_IRQHandler,Default_Handler
  
  .weak SPI1_IRQHandler
  .thumb_set SPI1_IRQHandler,Default_Handler
  
  .weak USART0_IRQHandler
  .thumb_set USART0_IRQHandler,Default_Handler
  
  .weak USART1_IRQHandler
  .thumb_set USART1_IRQHandler,Default_Handler

  .weak I2C0_ER_IRQHandler
  .thumb_set I2C0_ER_IRQHandler,Default_Handler

  .weak I2C1_ER_IRQHandler
  .thumb_set I2C1_ER_IRQHandler,Default_Handler


/******************* (C) COPYRIGHT 2018 GIGADEVICE *****END OF FILE****/
