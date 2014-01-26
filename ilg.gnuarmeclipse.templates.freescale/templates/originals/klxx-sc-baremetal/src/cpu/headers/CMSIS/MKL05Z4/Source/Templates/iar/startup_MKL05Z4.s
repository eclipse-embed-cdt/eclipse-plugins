;/*****************************************************************************
; * @file:    startup_MKL05Z4.s
; * @purpose: CMSIS Cortex-M0plus Core Device Startup File
; *           MKL05Z4
; * @version: 1.3
; * @date:    2012-10-4
; *----------------------------------------------------------------------------
; *
; * Copyright: 1997 - 2012 Freescale Semiconductor, Inc. All Rights Reserved.
; *
; ******************************************************************************/


;
; The modules in this file are included in the libraries, and may be replaced
; by any user-defined modules that define the PUBLIC symbol _program_start or
; a user defined start symbol.
; To override the cstartup defined in the library, simply add your modified
; version to the workbench project.
;
; The vector table is normally located at address 0.
; When debugging in RAM, it can be located in RAM, aligned to at least 2^6.
; The name "__vector_table" has special meaning for C-SPY:
; it is where the SP start value is found, and the NVIC vector
; table register (VTOR) is initialized to this address if != 0.
;
; Cortex-M version
;

        MODULE  ?cstartup

        ;; Forward declaration of sections.
        SECTION CSTACK:DATA:NOROOT(3)

        SECTION .intvec:CODE:NOROOT(2)

        EXTERN  __iar_program_start
        EXTERN  SystemInit
        PUBLIC  __vector_table
        PUBLIC  __vector_table_0x1c
        PUBLIC  __Vectors
        PUBLIC  __Vectors_End
        PUBLIC  __Vectors_Size

        DATA

__vector_table
        DCD     sfe(CSTACK)
        DCD     Reset_Handler

        DCD     NMI_Handler
        DCD     HardFault_Handler
        DCD     0
        DCD     0
        DCD     0
__vector_table_0x1c
        DCD     0
        DCD     0
        DCD     0
        DCD     0
        DCD     SVC_Handler
        DCD     0
        DCD     0
        DCD     PendSV_Handler
        DCD     SysTick_Handler

        ; External Interrupts
        DCD     DMA0_IRQHandler  ; DMA channel 0 transfer complete/error interrupt
        DCD     DMA1_IRQHandler  ; DMA channel 1 transfer complete/error interrupt
        DCD     DMA2_IRQHandler  ; DMA channel 2 transfer complete/error interrupt
        DCD     DMA3_IRQHandler  ; DMA channel 3 transfer complete/error interrupt
        DCD     Reserved20_IRQHandler  ; Reserved interrupt 20
        DCD     FTFA_IRQHandler  ; FTFA command complete/read collision interrupt
        DCD     LVD_LVW_IRQHandler  ; Low Voltage Detect, Low Voltage Warning
        DCD     LLW_IRQHandler  ; Low Leakage Wakeup
        DCD     I2C0_IRQHandler  ; I2C0 interrupt
        DCD     Reserved25_IRQHandler  ; Reserved interrupt 25
        DCD     SPI0_IRQHandler  ; SPI0 interrupt
        DCD     Reserved27_IRQHandler  ; Reserved interrupt 27
        DCD     UART0_IRQHandler  ; UART0 status/error interrupt
        DCD     Reserved29_IRQHandler  ; Reserved interrupt 29
        DCD     Reserved30_IRQHandler  ; Reserved interrupt 30
        DCD     ADC0_IRQHandler  ; ADC0 interrupt
        DCD     CMP0_IRQHandler  ; CMP0 interrupt
        DCD     TPM0_IRQHandler  ; TPM0 fault, overflow and channels interrupt
        DCD     TPM1_IRQHandler  ; TPM1 fault, overflow and channels interrupt
        DCD     Reserved35_IRQHandler  ; Reserved interrupt 35
        DCD     RTC_IRQHandler  ; RTC interrupt
        DCD     RTC_Seconds_IRQHandler  ; RTC seconds interrupt
        DCD     PIT_IRQHandler  ; PIT timer interrupt
        DCD     Reserved39_IRQHandler  ; Reserved interrupt 39
        DCD     Reserved40_IRQHandler  ; Reserved interrupt 40
        DCD     DAC0_IRQHandler  ; DAC0 interrupt
        DCD     TSI0_IRQHandler  ; TSI0 interrupt
        DCD     MCG_IRQHandler  ; MCG interrupt
        DCD     LPTimer_IRQHandler  ; LPTimer interrupt
        DCD     Reserved45_IRQHandler  ; Reserved interrupt 45
        DCD     PORTA_IRQHandler  ; Port A interrupt
        DCD     PORTB_IRQHandler  ; Port B interrupt
__Vectors_End
__FlashConfig
      	DCD	0xFFFFFFFF
      	DCD	0xFFFFFFFF
      	DCD	0xFFFFFFFF
      	DCD	0xFFFFFFFE
__FlashConfig_End

__Vectors       EQU   __vector_table
__Vectors_Size 	EQU 	__Vectors_End - __Vectors


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; Default interrupt handlers.
;;
        THUMB

        PUBWEAK Reset_Handler
        SECTION .text:CODE:REORDER(2)
Reset_Handler
        LDR     R0, =SystemInit
        BLX     R0
        LDR     R0, =__iar_program_start
        BX      R0

        PUBWEAK NMI_Handler
        SECTION .text:CODE:REORDER(1)
NMI_Handler
        B NMI_Handler

        PUBWEAK HardFault_Handler
        SECTION .text:CODE:REORDER(1)
HardFault_Handler
        B HardFault_Handler

        PUBWEAK SVC_Handler
        SECTION .text:CODE:REORDER(1)
SVC_Handler
        B SVC_Handler

        PUBWEAK PendSV_Handler
        SECTION .text:CODE:REORDER(1)
PendSV_Handler
        B PendSV_Handler

        PUBWEAK SysTick_Handler
        SECTION .text:CODE:REORDER(1)
SysTick_Handler
        B SysTick_Handler

        PUBWEAK DMA0_IRQHandler
        SECTION .text:CODE:REORDER(1)
DMA0_IRQHandler
        B DMA0_IRQHandler

        PUBWEAK DMA1_IRQHandler
        SECTION .text:CODE:REORDER(1)
DMA1_IRQHandler
        B DMA1_IRQHandler

        PUBWEAK DMA2_IRQHandler
        SECTION .text:CODE:REORDER(1)
DMA2_IRQHandler
        B DMA2_IRQHandler

        PUBWEAK DMA3_IRQHandler
        SECTION .text:CODE:REORDER(1)
DMA3_IRQHandler
        B DMA3_IRQHandler

        PUBWEAK Reserved20_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved20_IRQHandler
        B Reserved20_IRQHandler

        PUBWEAK FTFA_IRQHandler
        SECTION .text:CODE:REORDER(1)
FTFA_IRQHandler
        B FTFA_IRQHandler

        PUBWEAK LVD_LVW_IRQHandler
        SECTION .text:CODE:REORDER(1)
LVD_LVW_IRQHandler
        B LVD_LVW_IRQHandler

        PUBWEAK LLW_IRQHandler
        SECTION .text:CODE:REORDER(1)
LLW_IRQHandler
        B LLW_IRQHandler

        PUBWEAK I2C0_IRQHandler
        SECTION .text:CODE:REORDER(1)
I2C0_IRQHandler
        B I2C0_IRQHandler

        PUBWEAK Reserved25_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved25_IRQHandler
        B Reserved25_IRQHandler

        PUBWEAK SPI0_IRQHandler
        SECTION .text:CODE:REORDER(1)
SPI0_IRQHandler
        B SPI0_IRQHandler

        PUBWEAK Reserved27_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved27_IRQHandler
        B Reserved27_IRQHandler

        PUBWEAK UART0_IRQHandler
        SECTION .text:CODE:REORDER(1)
UART0_IRQHandler
        B UART0_IRQHandler

        PUBWEAK Reserved29_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved29_IRQHandler
        B Reserved29_IRQHandler

        PUBWEAK Reserved30_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved30_IRQHandler
        B Reserved30_IRQHandler

        PUBWEAK ADC0_IRQHandler
        SECTION .text:CODE:REORDER(1)
ADC0_IRQHandler
        B ADC0_IRQHandler

        PUBWEAK CMP0_IRQHandler
        SECTION .text:CODE:REORDER(1)
CMP0_IRQHandler
        B CMP0_IRQHandler

        PUBWEAK TPM0_IRQHandler
        SECTION .text:CODE:REORDER(1)
TPM0_IRQHandler
        B TPM0_IRQHandler

        PUBWEAK TPM1_IRQHandler
        SECTION .text:CODE:REORDER(1)
TPM1_IRQHandler
        B TPM1_IRQHandler

        PUBWEAK Reserved35_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved35_IRQHandler
        B Reserved35_IRQHandler

        PUBWEAK RTC_IRQHandler
        SECTION .text:CODE:REORDER(1)
RTC_IRQHandler
        B RTC_IRQHandler

        PUBWEAK RTC_Seconds_IRQHandler
        SECTION .text:CODE:REORDER(1)
RTC_Seconds_IRQHandler
        B RTC_Seconds_IRQHandler

        PUBWEAK PIT_IRQHandler
        SECTION .text:CODE:REORDER(1)
PIT_IRQHandler
        B PIT_IRQHandler

        PUBWEAK Reserved39_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved39_IRQHandler
        B Reserved39_IRQHandler

        PUBWEAK Reserved40_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved40_IRQHandler
        B Reserved40_IRQHandler

        PUBWEAK DAC0_IRQHandler
        SECTION .text:CODE:REORDER(1)
DAC0_IRQHandler
        B DAC0_IRQHandler

        PUBWEAK TSI0_IRQHandler
        SECTION .text:CODE:REORDER(1)
TSI0_IRQHandler
        B TSI0_IRQHandler

        PUBWEAK MCG_IRQHandler
        SECTION .text:CODE:REORDER(1)
MCG_IRQHandler
        B MCG_IRQHandler

        PUBWEAK LPTimer_IRQHandler
        SECTION .text:CODE:REORDER(1)
LPTimer_IRQHandler
        B LPTimer_IRQHandler

        PUBWEAK Reserved45_IRQHandler
        SECTION .text:CODE:REORDER(1)
Reserved45_IRQHandler
        B Reserved45_IRQHandler

        PUBWEAK PORTA_IRQHandler
        SECTION .text:CODE:REORDER(1)
PORTA_IRQHandler
        B PORTA_IRQHandler

        PUBWEAK PORTB_IRQHandler
        SECTION .text:CODE:REORDER(1)
PORTB_IRQHandler
        B PORTB_IRQHandler

        PUBWEAK DefaultISR
        SECTION .text:CODE:REORDER(1)
DefaultISR
        B DefaultISR

        END
