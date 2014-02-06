/*
 * File:	start.c
 * Purpose:	Kinetis L Family start up routines. 
 *
 * Notes:		
 */

#include "start.h"
#include "common.h"
#include "sysinit.h"
#include "rcm.h"

/********************************************************************/
/*!
 * \brief   Kinetis L Family Start
 * \return  None
 *
 * This function calls all of the necessary startup routines and then 
 * branches to the main process.
 */
void start(void)
{ 
		/* Disable the watchdog timer */
    SIM_COPC = 0x00;
#ifndef CMSIS    // If conforming to CMSIS, we do not need to perform this code


	/* Copy any vector or data sections that need to be in RAM */
	common_startup();
#endif

	/* Perform clock initialization, default UART initialization,
         * initializes clock out function, and enables the abort button
         */
			// common_startup();
	     sysinit();
        
        printf("\n\r\n\r");
	
	/* Determine the last cause(s) of reset */
	outSRS();	
	
	/* Determine specific Kinetis L Family device and revision */
	cpu_identify();
	
#ifndef CMSIS    // If conforming to CMSIS, we do not need to perform this code
	/* Jump to main process */
	main();

	/* No actions to perform after this so wait forever */
	while(1);
#endif 
}
/********************************************************************/
/*!
 * \brief   Kinetis L Family Identify
 * \return  None
 *
 * This is primarly a reporting function that displays information
 * about the specific CPU to the default terminal including:
 * - Kinetis family
 * - package
 * - die revision
 * - P-flash size
 * - Ram size
 */
void cpu_identify (void)
{
  uint16 temp;
    /* Determine the Kinetis family */
    switch((SIM_SDID & SIM_SDID_FAMID(0x7))>>SIM_SDID_FAMID_SHIFT) 
    {  
    	case 0x0:
    		printf("\n\rKL0");
    		break;
    	case 0x1:
    		printf("\n\rKL1");
    		break;
    	case 0x2:
    		printf("\n\rKL2");
    		break;
    	case 0x3:
    		printf("\n\rKL3");
    		break;
    	case 0x4:
    		printf("\n\rKL4");
    		break;
	default:
		printf("\n\rUnrecognized Kinetis family device.\n\r");  
		break;  	
    }
    
    /* Determine Sub-Family ID */
    switch((SIM_SDID & SIM_SDID_SUBFAMID(0x7))>>SIM_SDID_SUBFAMID_SHIFT) 
    {  
    	case 0x4:
    		printf("4");
    		break;
    	case 0x5:
    		printf("5");
    		break;
	default:
		printf("\n\rUnrecognized Kinetis sub-family device.\n\r");  
		break;  	
    }
    
    /* Determine the package size */
    switch((SIM_SDID & SIM_SDID_PINID(0xF))>>SIM_SDID_PINID_SHIFT) 
    {  
    	case 0x0:
    		printf("16pin       ");
    		break;
    	case 0x1:
    		printf("24pin       ");
    		break;
    	case 0x2:
    		printf("32pin      ");
    		break;
    	case 0x4:
    		printf("48pin      ");
    		break;
    	case 0x5:
    		printf("64pin      ");
        case 0x6:
    		printf("80pin      ");
        case 0x8:
    		printf("100pin      ");        
    		break;
	default:
		printf("\n\rUnrecognized Kinetis package code.      ");  
		break;  	
    }
    
    /* Determine Attribute ID */
    switch((SIM_SDID & SIM_SDID_SERIESID(0x7))>>SIM_SDID_SERIESID_SHIFT) 
    {  
    	case 0x1:
    		printf("\n\rLow Power Line with Cortex M0+\n\r\n\r");
    		break;
	default:
		printf("\n\rUnrecognized Kinetis family attribute.\n\r");  
		break;  	
    }
    
    /* Determine the System SRAM Size */
    switch((SIM_SDID & SIM_SDID_SRAMSIZE(0x7))>>SIM_SDID_SRAMSIZE_SHIFT) 
    {  
    	case 0x0:
          printf("SRAM Size: 0.5 KB\n\r");
    		break;
        case 0x1:
          printf("SRAM Size:  1 KB\n\r");
          break;
        case 0x2:
          printf("SRAM Size:  2 KB\n\r");
          break;
        case 0x3:
          printf("SRAM Size:  4 KB\n\r");
          break;
        case 0x4:
          printf("SRAM Size:  8 KB\n\r");
          break;
        case 0x5:
          printf("SRAM Size:  16 KB\n\r");
          break;
        case 0x6:
          printf("SRAM Size:  32 KB\n\r");
          break;
        case 0x7:
          printf("SRAM Size:  64 KB\n\r");
          break;
	default:
		printf("\n\rUnrecognized SRAM Size.\n\r");  
		break;  	
    }                

    /* Determine the revision ID */
    temp = ((SIM_SDID_REVID(0xF))>>SIM_SDID_REVID_SHIFT);
    printf("Silicon rev %d\n\r ", temp);
    
    /* Determine the flash revision */
    flash_identify();    
    
    /* Determine the P-flash size */
  switch((SIM_FCFG1 & SIM_FCFG1_PFSIZE(0xF))>>SIM_FCFG1_PFSIZE_SHIFT)
    {
  	case 0x0:
    		printf("Flash size:  8 KB program flash, 0.25 KB protection region");
    		break;
    	case 0x1:
    		printf("Flash size:  16 KB program flash, 0.5 KB protection region	");
    		break;
        case 0x3:
    		printf("Flash size:  32 KB program flash, 1 KB protection region	");
    		break;
    	case 0x5:
    		printf("Flash size:  64 KB program flash, 2 KB protection region	");
    		break;
        case 0x7:
    		printf("Flash size:  128 KB program flash, 4 KB protection region	");
    		break;
        case 0x9:
    		printf("Flash size:  256 KB program flash, 4 KB protection region	");
    		break;
        case 0xF:
    		printf("Flash size:  128 KB program flash, 4 KB protection region	");
    		break;
	default:
		printf("ERR!! Undefined flash size\n\r");  
		break;  	  		
    }
}
/********************************************************************/
/*!
 * \brief   flash Identify
 * \return  None
 *
 * This is primarly a reporting function that displays information
 * about the specific flash parameters and flash version ID for 
 * the current device. These parameters are obtained using a special
 * flash command call "read resource." The first four bytes returned
 * are the flash parameter revision, and the second four bytes are
 * the flash version ID.
 */
void flash_identify (void)
{
    /* Get the flash parameter version */

    /* Write the flash FCCOB registers with the values for a read resource command */
    FTFA_FCCOB0 = 0x03;
    FTFA_FCCOB1 = 0x00;
    FTFA_FCCOB2 = 0x00;
    FTFA_FCCOB3 = 0x00;
    FTFA_FCCOB8 = 0x01;

    /* All required FCCOBx registers are written, so launch the command */
    FTFA_FSTAT = FTFA_FSTAT_CCIF_MASK;

    /* Wait for the command to complete */
    while(!(FTFA_FSTAT & FTFA_FSTAT_CCIF_MASK));
    
    printf("Flash parameter version %d ",FTFA_FCCOB4);
    printf(" %d ",FTFA_FCCOB5);
    printf(" %d ",FTFA_FCCOB6);
    printf(" %d\n\r",FTFA_FCCOB7);

    /* Get the flash version ID */   

    /* Write the flash FCCOB registers with the values for a read resource command */
    FTFA_FCCOB0 = 0x03;
    FTFA_FCCOB1 = 0x00;
    FTFA_FCCOB2 = 0x00;
    FTFA_FCCOB3 = 0x04;
    FTFA_FCCOB8 = 0x01;

    /* All required FCCOBx registers are written, so launch the command */
    FTFA_FSTAT = FTFA_FSTAT_CCIF_MASK;

    /* Wait for the command to complete */
    while(!(FTFA_FSTAT & FTFA_FSTAT_CCIF_MASK));

    printf("Flash version ID %d ",FTFA_FCCOB4);  
    printf(" %d",FTFA_FCCOB5);  
    printf(" %d ",FTFA_FCCOB6);  
    printf("%d\n\r",FTFA_FCCOB7);  
}
/********************************************************************/

