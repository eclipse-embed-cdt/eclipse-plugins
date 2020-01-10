/*
 * File:        common.h
 * Purpose:     File included by non-standard Freescale drivers.
 *              Do not use in your projects, use CMSIS files!
 *
 * Notes:
 */

#ifndef _COMMON_H_
#define _COMMON_H_

/********************************************************************/

/*
 * Debug prints ON (#define) or OFF (#undef)
 */

//#define DEBUG
#define DEBUG_PRINT

#define CMSIS
#define CPU_MKL25Z128LK4
#define FREEDOM
//#define INCLUDE_TINY_STDLIB

#ifndef CMSIS // If using CMSIS, do not include arm_cm0.h
  /* 
   * Include the generic CPU header file 
   */
  #include "arm_cm0.h"
#else
  #define enable_irq NVIC_EnableIRQ
  #define disable_irq NVIC_DisableIRQ
	#define EnableInterrupts __enable_irq()
	#define DisableInterrupts __disable_irq()

#define __wfi   __WFI
#endif

/* 
 * Include the platform specific header file 
 */
#if (defined(FIREBIRD))
  #include "firebird.h"
#elif (defined(TOWER))
  #include "tower.h"
#elif (defined(FREEDOM))
  #include "freedom.h"
#elif (defined(BACES))
  #include "baces.h"
#else
  #error "No valid platform defined"
#endif


#ifndef CMSIS // If not using CMSIS, use the usual header files
/* 
 * Include the cpu specific header file 
*/
#if (defined (CPU_MKL05Z32LF4))
  #include "MKL05Z4.h"
#elif (defined(CPU_MKL25Z128LK4))
  #include "MKL25Z4.h"
#else
  #error "No valid CPU defined"
#endif

#else

/* 
 * Include the cpu specific header file 
*/
#if (defined (CPU_MKL05Z32LF4))
  #include "MKL05Z4_CMSIS.h"
#elif (defined(CPU_MKL25Z128LK4))
  #include "MKL25Z4.h"
  #include "MemMapPtr_KL25Z4.h"
#else
  #error "No valid CPU defined"
#endif

#endif

/* 
 * Include any toolchain specfic header files 
 */
#if (defined(__MWERKS__))
  #include "mwerks.h"
#elif (defined(__DCC__))
  #include "build/wrs/diab.h"
#elif (defined(__ghs__))
  #include "build/ghs/ghs.h"
#elif (defined(__GNUC__))
  //#include "build/gnu/gnu.h"
#elif (defined(IAR))
  #include "iar.h"
#elif (defined(KEIL))
  
#else
#warning "No toolchain specific header included"
#endif

#if defined INCLUDE_TINY_STDLIB
/* 
 * Include common utilities
 */
#include "assert.h"
#include "io.h"
#include "startup.h"
#include "stdlib.h"
#ifdef IAR
#include "intrinsics.h"
#endif
#else
#include <stdio.h>
#include <stdlib.h>
#endif

#ifdef CMSIS  // If we are conforming to CMSIS, we need to include these definitions
// Include Merger Header file
#include "MemMapPtr_KL25Z4.h"

extern void wait(void);
extern void stop(void);

/*
 * Include common definitions
 *
 * !!! Added for CMSIS Project !!!
 */
#ifdef	FALSE
#undef	FALSE
#endif
#define FALSE	(0)

#ifdef	TRUE
#undef	TRUE
#endif
#define	TRUE	(1)

#ifdef	NULL
#undef	NULL
#endif
#define NULL	(0)

#ifdef  ON
#undef  ON
#endif
#define ON      (1)

#ifdef  OFF
#undef  OFF
#endif
#define OFF     (0)

#define SCB_SCR_SLEEPDEEP_MASK  (1<<2)

/***********************************************************************/
/*
 * The basic data types
 */
typedef unsigned char		uint8;  /*  8 bits */
typedef unsigned short int	uint16; /* 16 bits */
typedef unsigned long int	uint32; /* 32 bits */

typedef char			int8;   /*  8 bits */
typedef short int	        int16;  /* 16 bits */
typedef int		        int32;  /* 32 bits */

typedef volatile int8		vint8;  /*  8 bits */
typedef volatile int16		vint16; /* 16 bits */
typedef volatile int32		vint32; /* 32 bits */

typedef volatile uint8		vuint8;  /*  8 bits */
typedef volatile uint16		vuint16; /* 16 bits */
typedef volatile uint32		vuint32; /* 32 bits */

#endif
     
     
/* 
 * Global Variable
 */
     
     


/********************************************************************/

#endif /* _COMMON_H_ */
