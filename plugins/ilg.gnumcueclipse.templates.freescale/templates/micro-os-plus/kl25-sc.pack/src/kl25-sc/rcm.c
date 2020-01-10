/*
 * File:        rcm.c
 * Purpose:     Provides routines for the reset controller module
 *              
 */

#include "common.h"
#include "rcm.h"

/* OutSRS routine - checks the value in the SRS registers and sends
 * messages to the terminal announcing the status at the start of the 
 * code.
 */
void outSRS(void){                         //[outSRS]

  
	if (RCM_SRS1 & RCM_SRS1_SACKERR_MASK)
		printf("\n\rStop Mode Acknowledge Error Reset");
	if (RCM_SRS1 & RCM_SRS1_MDM_AP_MASK)
		printf("\n\rMDM-AP Reset");
	if (RCM_SRS1 & RCM_SRS1_SW_MASK)
		printf("\n\rSoftware Reset");
	if (RCM_SRS1 & RCM_SRS1_LOCKUP_MASK)
		printf("\n\rCore Lockup Event Reset");
	
	if (RCM_SRS0 & RCM_SRS0_POR_MASK)
		printf("\n\rPower-on Reset");
	if (RCM_SRS0 & RCM_SRS0_PIN_MASK)
		printf("\n\rExternal Pin Reset");
	if (RCM_SRS0 & RCM_SRS0_WDOG_MASK)
		printf("\n\rWatchdog(COP) Reset");
	if (RCM_SRS0 & RCM_SRS0_LOC_MASK)
		printf("\n\rLoss of External Clock Reset");
	if (RCM_SRS0 & RCM_SRS0_LOL_MASK)
		printf("\n\rLoss of Lock in PLL Reset");
	if (RCM_SRS0 & RCM_SRS0_LVD_MASK)
		printf("\n\rLow-voltage Detect Reset");
	if (RCM_SRS0 & RCM_SRS0_WAKEUP_MASK)
        {
          printf("\n\r[outSRS]Wakeup bit set from low power mode ");
          if ((SMC_PMCTRL & SMC_PMCTRL_STOPM_MASK)== 3)
            printf("LLS exit ") ;
          if (((SMC_PMCTRL & SMC_PMCTRL_STOPM_MASK)== 4) && ((SMC_STOPCTRL & SMC_STOPCTRL_VLLSM_MASK)== 0))
            printf("VLLS0 exit ") ;
          if (((SMC_PMCTRL & SMC_PMCTRL_STOPM_MASK)== 4) && ((SMC_STOPCTRL & SMC_STOPCTRL_VLLSM_MASK)== 1))
            printf("VLLS1 exit ") ;
          if (((SMC_PMCTRL & SMC_PMCTRL_STOPM_MASK)== 4) && ((SMC_STOPCTRL & SMC_STOPCTRL_VLLSM_MASK)== 2))
            printf("VLLS2 exit") ;
          if (((SMC_PMCTRL & SMC_PMCTRL_STOPM_MASK)== 4) && ((SMC_STOPCTRL & SMC_STOPCTRL_VLLSM_MASK)== 3))
            printf("VLLS3 exit ") ; 
	}

        if ((RCM_SRS0 == 0) && (RCM_SRS1 == 0)) 
        {
	       printf("[outSRS]RCM_SRS0 is ZERO   = %#02X \r\n\r", (RCM_SRS0))  ;
	       printf("[outSRS]RCM_SRS1 is ZERO   = %#02X \r\n\r", (RCM_SRS1))  ;	 
        }
  }

