/*****************************************************************************/
/* startup_MKL15Z4.s: Startup file for MKL15Z4 device series                   */
/*****************************************************************************/
/* Version: CodeSourcery Sourcery G++ Lite (with CS3)                        */
/*****************************************************************************/


/*
//*** <<< Use Configuration Wizard in Context Menu >>> ***
*/


/*
// <h> Stack Configuration
//   <o> Stack Size (in Bytes) <0x0-0xFFFFFFFF:8>
// </h>
*/

    .equ    Stack_Size, 0x00000400
    .section ".stack", "w"
    .align  3
    .globl  __cs3_stack_mem
    .globl  __cs3_stack_size
__cs3_stack_mem:
    .if     Stack_Size
    .space  Stack_Size
    .endif
    .size   __cs3_stack_mem,  . - __cs3_stack_mem
    .set    __cs3_stack_size, . - __cs3_stack_mem


/*
// <h> Heap Configuration
//   <o>  Heap Size (in Bytes) <0x0-0xFFFFFFFF:8>
// </h>
*/

    .equ    Heap_Size,  0x00000000

    .section ".heap", "w"
    .align  3
    .globl  __cs3_heap_start
    .globl  __cs3_heap_end
__cs3_heap_start:
    .if     Heap_Size
    .space  Heap_Size
    .endif
__cs3_heap_end:


/* Vector Table */

    .section ".cs3.interrupt_vector"
    .globl  __cs3_interrupt_vector_cortex_m
    .type   __cs3_interrupt_vector_cortex_m, %object

__cs3_interrupt_vector_cortex_m:
    .long   __cs3_stack                 /* Top of Stack                 */
    .long   __cs3_reset                 /* Reset Handler                */
    .long   NMI_Handler                 /* NMI Handler                  */
    .long   HardFault_Handler           /* Hard Fault Handler           */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   SVC_Handler                 /* SVCall Handler               */
    .long   0                           /* Reserved                     */
    .long   0                           /* Reserved                     */
    .long   PendSV_Handler              /* PendSV Handler               */
    .long   SysTick_Handler             /* SysTick Handler              */

    /* External Interrupts */
    .long   DMA0_IRQHandler  /* DMA channel 0 transfer complete/error interrupt */
    .long   DMA1_IRQHandler  /* DMA channel 1 transfer complete/error interrupt */
    .long   DMA2_IRQHandler  /* DMA channel 2 transfer complete/error interrupt */
    .long   DMA3_IRQHandler  /* DMA channel 3 transfer complete/error interrupt */
    .long   Reserved20_IRQHandler  /* Reserved interrupt 20 */
    .long   FTFA_IRQHandler  /* FTFA command complete/read collision interrupt */
    .long   LVD_LVW_IRQHandler  /* Low Voltage Detect, Low Voltage Warning */
    .long   LLW_IRQHandler  /* Low Leakage Wakeup */
    .long   I2C0_IRQHandler  /* I2C0 interrupt */
    .long   I2C1_IRQHandler  /* I2C0 interrupt 25 */
    .long   SPI0_IRQHandler  /* SPI0 interrupt */
    .long   SPI1_IRQHandler  /* SPI1 interrupt */
    .long   UART0_IRQHandler  /* UART0 status/error interrupt */
    .long   UART1_IRQHandler  /* UART1 status/error interrupt */
    .long   UART2_IRQHandler  /* UART2 status/error interrupt */
    .long   ADC0_IRQHandler  /* ADC0 interrupt */
    .long   CMP0_IRQHandler  /* CMP0 interrupt */
    .long   TPM0_IRQHandler  /* TPM0 fault, overflow and channels interrupt */
    .long   TPM1_IRQHandler  /* TPM1 fault, overflow and channels interrupt */
    .long   TPM2_IRQHandler  /* TPM2 fault, overflow and channels interrupt */
    .long   RTC_IRQHandler  /* RTC interrupt */
    .long   RTC_Seconds_IRQHandler  /* RTC seconds interrupt */
    .long   PIT_IRQHandler  /* PIT timer interrupt */
    .long   Reserved39_IRQHandler  /* Reserved interrupt 39 */
    .long   USB0_IRQHandler  /* USB0 interrupt */
    .long   DAC0_IRQHandler  /* DAC0 interrupt */
    .long   TSI0_IRQHandler  /* TSI0 interrupt */
    .long   MCG_IRQHandler  /* MCG interrupt */
    .long   LPTimer_IRQHandler  /* LPTimer interrupt */
    .long   Reserved45_IRQHandler  /* Reserved interrupt 45 */
    .long   PORTA_IRQHandler  /* Port A interrupt */
    .long   PORTD_IRQHandler  /* Port D interrupt */


    .size   __cs3_interrupt_vector_cortex_m, . - __cs3_interrupt_vector_cortex_m

/* Flash Configuration */

  	.long	0xFFFFFFFF
  	.long	0xFFFFFFFF
  	.long	0xFFFFFFFF
  	.long	0xFFFFFFFE

    .thumb


/* Reset Handler */

    .section .cs3.reset,"x",%progbits
    .thumb_func
    .globl  __cs3_reset_cortex_m
    .type   __cs3_reset_cortex_m, %function
__cs3_reset_cortex_m:
    .fnstart
    LDR     R0, =SystemInit
    BLX     R0
    LDR     R0,=_start
    BX      R0
    .pool
    .cantunwind
    .fnend
    .size   __cs3_reset_cortex_m,.-__cs3_reset_cortex_m

    .section ".text"

/* Exception Handlers */

    .weak   NMI_Handler
    .type   NMI_Handler, %function
NMI_Handler:
    B       .
    .size   NMI_Handler, . - NMI_Handler

    .weak   HardFault_Handler
    .type   HardFault_Handler, %function
HardFault_Handler:
    B       .
    .size   HardFault_Handler, . - HardFault_Handler

    .weak   MemManage_Handler
    .type   MemManage_Handler, %function
SVC_Handler:
    B       .
    .size   SVC_Handler, . - SVC_Handler

    .weak   DebugMon_Handler
    .type   DebugMon_Handler, %function
PendSV_Handler:
    B       .
    .size   PendSV_Handler, . - PendSV_Handler

    .weak   SysTick_Handler
    .type   SysTick_Handler, %function
SysTick_Handler:
    B       .
    .size   SysTick_Handler, . - SysTick_Handler


/* IRQ Handlers */

    .globl  Default_Handler
    .type   Default_Handler, %function
Default_Handler:
    B       .
    .size   Default_Handler, . - Default_Handler

    .macro  IRQ handler
    .weak   \handler
    .set    \handler, Default_Handler
    .endm

    IRQ     DMA0_IRQHandler
    IRQ     DMA1_IRQHandler
    IRQ     DMA2_IRQHandler
    IRQ     DMA3_IRQHandler
    IRQ     Reserved20_IRQHandler
    IRQ     FTFA_IRQHandler
    IRQ     LVD_LVW_IRQHandler
    IRQ     LLW_IRQHandler
    IRQ     I2C0_IRQHandler
    IRQ     I2C1_IRQHandler
    IRQ     SPI0_IRQHandler
    IRQ     SPI1_IRQHandler
    IRQ     UART0_IRQHandler
    IRQ     UART1_IRQHandler
    IRQ     UART2_IRQHandler
    IRQ     ADC0_IRQHandler
    IRQ     CMP0_IRQHandler
    IRQ     TPM0_IRQHandler
    IRQ     TPM1_IRQHandler
    IRQ     TPM2_IRQHandler
    IRQ     RTC_IRQHandler
    IRQ     RTC_Seconds_IRQHandler
    IRQ     PIT_IRQHandler
    IRQ     Reserved39_IRQHandler
    IRQ     USB0_IRQHandler
    IRQ     DAC0_IRQHandler
    IRQ     TSI0_IRQHandler
    IRQ     MCG_IRQHandler
    IRQ     LPTimer_IRQHandler
    IRQ     Reserved45_IRQHandler
    IRQ     PORTA_IRQHandler
    IRQ     PORTD_IRQHandler
    IRQ     DefaultISR

    .end
