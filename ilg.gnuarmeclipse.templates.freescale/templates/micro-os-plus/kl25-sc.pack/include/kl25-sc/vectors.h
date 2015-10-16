/******************************************************************************
* File:    vectors.h
*
* Purpose: Provide custom interrupt service routines for Kinetis L Family . 
*
* NOTE: This vector table is a superset table, so interrupt sources might be 
*       listed that are not available on the specific Kinetis L Family device you are 
*       using.
******************************************************************************/

#ifndef __VECTORS_H
#define __VECTORS_H     1

// function prototype for default_isr in vectors.c
void default_isr(void);
void abort_isr(void);
void SRTC_ISR(void);

void hard_fault_handler_c(unsigned int * hardfault_args);

/* Interrupt Vector Table Function Pointers */
                                    
/* Interrupt Vector Table Function Pointers */
#define  DMA0_irq_no            0  // Vector No 16
#define  DMA1_irq_no            1  // Vector No 17
#define  DMA2_irq_no            2  // Vector No 18
#define  DMA3_irq_no            3  // Vector No 19
#define  FTFA_irq_no            5  // Vector No 21
#define  LVD_irq_no             6  // Vector No 22
#define  LLWU_irq_no            7  // Vector No 23
#define  I2C0_irq_no            8  // Vector No 24
#define  I2C1_irq_no            9  // Vector No 25
#define  SPI0_irq_no            10  // Vector No 26
#define  SPI1_irq_no            11  // Vector No 27
#define  UART0SE_irq_no         12  // Vector No 28
#define  UART1SE_irq_no         13  // Vector No 29
#define  UART2SE_irq_no         14  // Vector No 30
#define  ADC0_irq_no            15  // Vector No 31
#define  CMP0_irq_no            16  // Vector No 32
#define  FTM0_irq_no            17  // Vector No 33
#define  FTM1_irq_no            18  // Vector No 34
#define  FTM2_irq_no            19  // Vector No 35
#define  RTCA_irq_no            20  // Vector No 36
#define  RTCS_irq_no            21  // Vector No 37
#define  PIT_irq_no             22  // Vector No 38
#define  USBOTG_irq_no          24  // Vector No 40
#define  DAC_irq_no             25  // Vector No 41
#define  TSI_irq_no             26  // Vector No 42
#define  MCG_irq_no             27  // Vector No 43
#define  LPTMR_irq_no           28  // Vector No 44
#define  PortA_irq_no           30  // Vector No 46
#define  PortD_irq_no           31  // Vector No 47

typedef void pointer(void);

#if (defined(IAR))
extern void __startup(void);
extern unsigned long __BOOT_STACK_ADDRESS[];
extern void __iar_program_start(void);

#elif (defined(KEIL))
extern void __startup(void);
extern unsigned long __BOOT_STACK_ADDRESS[];

#elif (defined(CW))
extern unsigned long _estack;
extern void __startup(void);
#define __BOOT_STACK_ADDRESS    &_estack

#endif
extern void SRTC_ISR(void);
                                        // Address   Vector IRQ   Source module   Source description
#define VECTOR_000      (pointer*)__BOOT_STACK_ADDRESS	//          ARM core        Initial Supervisor SP
#define VECTOR_001      __startup	// 0x0000_0004 1 -          ARM core        Initial Program Counter
#define VECTOR_002      default_isr     // 0x0000_0008 2 -          ARM core        Non-maskable Interrupt (NMI)
#define VECTOR_003      default_isr     // 0x0000_000C 3 -          ARM core        Hard Fault
#define VECTOR_004      default_isr     // 0x0000_0010 4 -
#define VECTOR_005      default_isr     // 0x0000_0014 5 -         
#define VECTOR_006      default_isr     // 0x0000_0018 6 -          
#define VECTOR_007      default_isr     // 0x0000_001C 7 -                           
#define VECTOR_008      default_isr     // 0x0000_0020 8 -                           
#define VECTOR_009      default_isr     // 0x0000_0024 9 -
#define VECTOR_010      default_isr     // 0x0000_0028 10 -
#define VECTOR_011      default_isr     // 0x0000_002C 11 -         ARM core         Supervisor call (SVCall)
#define VECTOR_012      default_isr     // 0x0000_0030 12 -        
#define VECTOR_013      default_isr     // 0x0000_0034 13 -                          
#define VECTOR_014      default_isr     // 0x0000_0038 14 -         ARM core         Pendable request for system service (PendableSrvReq)
#define VECTOR_015      default_isr     // 0x0000_003C 15 -         ARM core         System tick timer (SysTick)
#define VECTOR_016      default_isr     // 0x0000_0040 16     0     DMA              DMA Channel 0 transfer complete and error
#define VECTOR_017      default_isr     // 0x0000_0044 17     1     DMA              DMA Channel 1 transfer complete and error
#define VECTOR_018      default_isr     // 0x0000_0048 18     2     DMA              DMA Channel 2 transfer complete and error
#define VECTOR_019      default_isr     // 0x0000_004C 19     3     DMA              DMA Channel 3 transfer complete and error
#define VECTOR_020      default_isr     // 0x0000_0050 20     
#define VECTOR_021      default_isr     // 0x0000_0054 21     5     FTFA             Command complete and read collision
#define VECTOR_022      default_isr     // 0x0000_0058 22     6     PMC              Low Voltage detect, low-voltage warning
#define VECTOR_023      default_isr     // 0x0000_005C 23     7     LLWU             Low Leakage Wakeup
#define VECTOR_024      default_isr     // 0x0000_0060 24     8     IIC0
#define VECTOR_025      default_isr     // 0x0000_0064 25     9     IIC1             
#define VECTOR_026      default_isr     // 0x0000_0068 26    10     SPI0             Single interrupt vector for all sources 
#define VECTOR_027      default_isr     // 0x0000_006C 27    11     SPI1             Single interrupt vector for all sources
#define VECTOR_028      default_isr     // 0x0000_0070 28    12     UART0            Status and error
#define VECTOR_029      default_isr     // 0x0000_0074 29    13     UART1            Status and error
#define VECTOR_030      default_isr     // 0x0000_0078 30    14     UART2            Status and error
#define VECTOR_031      default_isr     // 0x0000_007C 31    15     ADC0             
#define VECTOR_032      default_isr     // 0x0000_0080 32    16     CMP0             
#define VECTOR_033      default_isr     // 0x0000_0084 33    17     TPM0             
#define VECTOR_034      default_isr     // 0x0000_0088 34    18     TPM1  
#define VECTOR_035      default_isr     // 0x0000_008C 35    19     TPM2
#define VECTOR_036      default_isr     // 0x0000_0090 36    20     RTC              Alarm interrupt
#define VECTOR_037      default_isr     // 0x0000_0094 37    21     RTC              Seconds interrupt
#define VECTOR_038      default_isr     // 0x0000_0098 38    22     PIT              Single interrupt vector for all channels
#define VECTOR_039      default_isr     // 0x0000_009C 39    23		
#define VECTOR_040      default_isr     // 0x0000_00A0 40    24     USB OTG
#define VECTOR_041      default_isr     // 0x0000_00A4 41    25     DAC0
#define VECTOR_042      default_isr     // 0x0000_00A8 42    26     TSI0             
#define VECTOR_043      default_isr     // 0x0000_00AC 43    27     MCG             
#define VECTOR_044      default_isr     // 0x0000_00B0 44    28     LPTMR0           
#define VECTOR_045      default_isr     // 0x0000_00B4 45    29     
#define VECTOR_046      default_isr     // 0x0000_00B8 46    30     Port Control Module  Pin detect (Port A)
#define VECTOR_047      default_isr     // 0x0000_00BC 47    31     Port Control Moudle  Pin detect (Port D)
#define VECTOR_PADDING  (pointer*)0xffffffff/*not used*/

/* Flash configuration field values below */
/* Please be careful when modifying any of
 * the values below as it can secure the 
* flash (possibly permanently): 0x400-0x409.
 */
#define CONFIG_1		(pointer*)0xffffffff 
#define CONFIG_2		(pointer*)0xffffffff 
#define CONFIG_3		(pointer*)0xffffffff
#define CONFIG_4		(pointer*)0xfffffffe //b5=1,b4=1,b0=1 div1 fast
//#define CONFIG_4	(pointer*)0xffffdffe //b5=0,b4=1,b0=1 div1 slow works
//#define CONFIG_4	(pointer*)0xffffcefe //b5=0,b4=0,b0=0;div8 slow
//#define CONFIG_4	(pointer*)0xffffeefe //b5=1,b4=0,b0=0 div8 fast 
//#define CONFIG_4	(pointer*)0xffffcffe //b5=0,b4=0,b0=1;div4 slow
//#define CONFIG_4	(pointer*)0xffffeffe //b5=1,b4=0,b0=1;div4 fast
//#define CONFIG_4	(pointer*)0xfffffefe //b5=1,b4=1,b0=0;div2 fast
//#define CONFIG_4	(pointer*)0xffffdefe //b5=0,b4=1,b0=0;div2 slow 

#endif /*__VECTORS_H*/

/* End of "vectors.h" */
