/*
 * File:        smc.c
 * Purpose:     Provides routines for entering low power modes.
 *
 * Notes:	Since the wakeup mechanism for low power modes
 *              will be application specific, these routines
 *              do not include code to setup interrupts to exit
 *              from the low power modes. The desired means of
 *              low power mode exit should be configured before
 *              calling any of these functions.
 *
 *              These routines do not include protection to
 *              prevent illegal state transitions in the mode
 *              controller, and all routines that write to the
 *              PMPROT register write a value to allow all
 *              possible low power modes (it is write once, so
 *              if only the currently requested mode is enabled
 *              a different mode couldn't be enabled later on).
 *              
 */

#include "common.h"
#include "smc.h"

/***************************************************************/
/*
 * Configures the ARM system control register for WAIT(sleep)mode
 * and then executes the WFI instruction to enter the mode.
 *
 * Parameters:
 * none
 *
 */

void sleep (void)
{
/* Clear the SLEEPDEEP bit to make sure we go into WAIT (sleep) 
 * mode instead of deep sleep.
 */
SCB_SCR &= ~SCB_SCR_SLEEPDEEP_MASK;

#ifdef CMSIS
  __wfi();
#else
  /* WFI instruction will start entry into WAIT mode */
  asm("WFI");
#endif 
}
/***************************************************************/
/*
 * Configures the ARM system control register for STOP  
 * (deepsleep) mode and then executes the WFI instruction 
 * to enter the mode.
 *
 * Parameters:
 * none
 *
 */

void deepsleep (void)
{
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;

#ifdef CMSIS
  __wfi();
#else
  /* WFI instruction will start entry into STOP mode */
  asm("WFI");
#endif
}
/********************************************************************/
/* WAIT mode entry routine. Puts the processor into wait mode.
 * In this mode the core clock is disabled (no code executing), but 
 * bus clocks are enabled (peripheral modules are operational). 
 *
 * Mode transitions:
 * RUN -> WAIT
 * VLPR -> VLPW
 *
 * This function can be used to enter normal wait mode or VLPW
 * mode. If you are executing in normal run mode when calling this
 * function, then you will enter normal wait mode. If you are in VLPR
 * mode when calling this function, then you will enter VLPW mode instead.
 *
 * NOTE: Some modules include a programmable option to disable them in 
 * wait mode. If those modules are programmed to disable in wait mode, 
 * they will not be able to generate interrupts to wake up the core.
 *
 * WAIT mode is exited using any enabled interrupt or RESET, so no
 * exit_wait routine is needed. 
 * 
 * 
 * Parameters:
 * none
 */
void enter_wait(void)
{
#ifdef CMSIS
    /* Clear the SLEEPDEEP bit to make sure we go into WAIT (sleep) mode instead
     * of deep sleep.
     */
	SCB_SCR &= ~SCB_SCR_SLEEPDEEP_MASK;
        
    __wfi();
#else
    wait();
#endif
}

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Wconversion"
#pragma GCC diagnostic ignored "-Wsign-conversion"
#pragma GCC diagnostic ignored "-Wunused-but-set-variable"
#endif

/********************************************************************/
/* STOP mode entry routine. Puts the processor into normal stop mode.
 * In this mode core, bus and peripheral clocks are disabled.
 *
 * Mode transitions:
 * RUN -> STOP
 *
 * This function can be used to enter normal stop mode. 
 * If you are executing in normal run mode when calling this
 * function and AVLP = 0, then you will enter normal stop mode. 
 * If AVLP = 1 with previous write to PMPROT
 * then you will enter VLPS mode instead.
 *
 * STOP mode is exited using any enabled interrupt or RESET, so no
 * exit_stop routine is needed.
 *
 * Parameters:
 * Partial Stop Option:  
 *  0x00 = STOP - Normal Stop Mode
 *  0x40 = PSTOP1 - Partial Stop with both system and bus clocks disabled
 *  0x80 = PSTOP2 - Partial Stop with system clock disabled and bus clock enabled
 *  0xC0 = Reserved
 */
void enter_stop(unsigned char partial_stop_opt)
{
  volatile unsigned int dummyread;
  /* The PMPROT register may have already been written by init code
     If so then this next write is not done since  
     PMPROT is write once after RESET  
     this write-once bit allows the MCU to enter the
     normal STOP mode.
     If AVLP is already a 1, VLPS mode is entered instead of normal STOP*/
  SMC_PMPROT = 0;  

  /* Set the STOPM field to 0b000 for normal STOP mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK;
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0); 
  SMC_STOPCTRL &= ~SMC_STOPCTRL_PSTOPO_MASK;
  SMC_STOPCTRL |= partial_stop_opt;
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_PMCTRL;
  
#ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}
/****************************************************************/
/* VLPR mode entry routine.Puts the processor into very low power
 * run mode. In this mode all clocks are enabled, but the core clock limited. 
 * The flash clock is limited to 1MHz or less. 
 *
 * Mode transitions:
 * RUN -> VLPR
 *
 * exit_vlpr() function can be used 
 * to switch from VLPR back to RUN. 
 * 
 * while in VLPR,VLPW or VLPS the exit to VLPR is not possible  
 *
 *
 * Parameters:
 * Return value : PMSTAT value or error code
 *                PMSTAT = return_value = PMSTAT
 *                         000_0001 Current power mode is RUN
 *                         000_0100 Current power mode is VLPR
 *                ERROR Code =  0x14 - already in VLPR mode
 *                           =  0x24 - REGONS never clear indicating stop regulation
 */
int enter_vlpr(void)
{
  int i; 
  unsigned int return_value;
  if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 4){
         return_value = 0x14;
         }
  /* The PMPROT register may have already been written by init code
     If so then this next write is not done.  
     PMPROT is write once after RESET  
     this write-once bit allows the MCU to enter the
     very low power modes: VLPR, VLPW, and VLPS   */
  SMC_PMPROT = SMC_PMPROT_AVLP_MASK;  
    
  /* Set the (for MC1)LPLLSM or (for MC2)STOPM field 
     to 0b010 for VLPS mode -
     and RUNM bits to 0b010 for VLPR mode  */
  SMC_PMCTRL &= ~SMC_PMCTRL_RUNM_MASK; 
  //make sure SMC_PMCTRL is clear and MCU is in RUN mode
  while ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)!= 0x1){ }
    
  SMC_PMCTRL  |= SMC_PMCTRL_RUNM(0x2);
  /* Wait for VLPS regulator mode to be confirmed */
  for (i = 0 ; i < 10000 ; i++)
    {     /* check that the value of REGONS bit is not 0
             once it is a zero we can stop checking */      
      if ((PMC_REGSC & PMC_REGSC_REGONS_MASK) !=0x04){ //==
       /* 0 Regulator is in stop regulation or in transition 
            to/from it
          1 MCU is in Run regulation mode */
        //asm("nop");
        break;
      }  
     // else  break;
    }  
  if ((PMC_REGSC & PMC_REGSC_REGONS_MASK) ) //==0x04
    {  
      return_value = 0x24; 
    }
  /* SMC_PMSTAT register only exist in Mode Controller 2 MCU versions */
  if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK) == 4)  
    {
      return_value = (unsigned int)SMC_PMSTAT;
    }
  return (return_value);
}
/********************************************************************/
/* VLPR mode exit routine. Puts the processor into normal run mode
 * from VLPR mode. You can transition from VLPR to normal run using
 * this function.
 *
 * Mode transitions:
 * VLPR -> RUN
 *
 * Parameters:
 * none
 */
 /********************************************************************/

void exit_vlpr(void)
{
    int i;
    /* check to make sure in VLPR before exiting    */
    if  ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 4) {
      
       /* Clear RUNM */
       SMC_PMCTRL &= ~SMC_PMCTRL_RUNM_MASK;
                      
       /* Wait for normal RUN regulation mode to be confirmed */                   
       // 1 PMSTAT MCU is in RUN  mode
       // 4 PMSTAT MCU is in VLPR mode
       for (i=0;i<0xff;i++)
       {
           if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 1)
             if ((PMC_REGSC & PMC_REGSC_REGONS_MASK) == 1)
    	        break;
       }           
    }  //if in VLPR mode
     // else if not in VLPR ignore call
}
/***************************************************************/
/* VLPS mode entry routine. Puts the processor into VLPS mode 
 * directly from run or VLPR modes. 
 *
 * Mode transitions:
 * RUN  -> VLPS
 * VLPR -> VLPS
 *
 * Note, when VLPS is entered directly from RUN or VLPR mode, 
 * exit to VLPR is disabled by hardware and the system will
 * always exit back to RUN.
 *
 * Parameters:  
 * none
 */
 /****************************************************************/

void enter_vlps(void)
{
  volatile unsigned int dummyread;
  /* The PMPROT register may have already been written by init code
     If so then this next write is not done since  
     PMPROT is write once after RESET 
     allows the MCU to enter the VLPR, VLPW, and VLPS modes.
     If AVLP is already writen to 0 
     Stop is entered instead of VLPS*/
  SMC_PMPROT = SMC_PMPROT_AVLP_MASK;           
  /* Set the STOPM field to 0b010 for VLPS mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x2); 
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_PMCTRL;
  /* Now execute the stop instruction to go into VLPS */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}
/****************************************************************/
/* LLS mode entry routine. Puts the processor into LLS mode from
 * normal run mode or VLPR. 
 *
 * Mode transitions:
 * RUN -> LLS
 * VLPR -> LLS
 *
 * NOTE: LLS mode will always exit to RUN mode even if you were 
 * in VLPR mode before entering LLS.
 *
 * Wakeup from LLS mode is controlled by the LLWU module. Most
 * modules cannot issue a wakeup interrupt in LLS mode, so make
 * sure to setup the desired wakeup sources in the LLWU before 
 * calling this function.
 *
 * Parameters:
 * none
 */
 /********************************************************************/

void enter_lls(void)
{
  volatile unsigned int dummyread;
  /* Write to PMPROT to allow LLS power modes this write-once 
     bit allows the MCU to enter the LLS low power mode*/
  SMC_PMPROT = SMC_PMPROT_ALLS_MASK;   
  /* Set the STOPM field to 0b011 for LLS mode  */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x3); 
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_PMCTRL;
  /* Now execute the stop instruction to go into LLS */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}
/***************************************************************/
/* VLLS3 mode entry routine. Puts the processor into 
 * VLLS3 mode from normal run mode or VLPR. 
 *
 * Mode transitions:
 * RUN -> VLLS3
 * VLPR -> VLLS3
 *
 * NOTE: VLLSx modes will always exit to RUN mode even if you were 
 * in VLPR mode before entering VLLSx.
 *
 * Wakeup from VLLSx mode is controlled by the LLWU module. Most
 * modules cannot issue a wakeup interrupt in VLLSx mode, so make
 * sure to setup the desired wakeup sources in the LLWU before 
 * calling this function.
 *
 * Parameters:
 * none  
 */
 /********************************************************************/

void enter_vlls3(void)
{
  volatile unsigned int dummyread;
  /* Write to PMPROT to allow VLLS3 power modes */
  SMC_PMPROT = SMC_PMPROT_AVLLS_MASK;      
  /* Set the STOPM field to 0b100 for VLLS3 mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x4); 
  /* set VLLSM = 0b11 */
  SMC_STOPCTRL =  SMC_STOPCTRL_VLLSM(3);          
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_STOPCTRL;
  /* Now execute the stop instruction to go into VLLS3 */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}
/***************************************************************/
/* VLLS2 mode entry routine. Puts the processor into 
 * VLLS2 mode from normal run mode or VLPR. 
 *
 * Mode transitions:
 * RUN -> VLLS2
 * VLPR -> VLLS2
 *
 * NOTE: VLLSx modes will always exit to RUN mode even  
 *       if you werein VLPR mode before entering VLLSx.
 *
 * Wakeup from VLLSx mode is controlled by the LLWU module. Most
 * modules cannot issue a wakeup interrupt in VLLSx mode, so make
 * sure to setup the desired wakeup sources in the LLWU before 
 * calling this function.
 *
 * Parameters:
 * none  
 */
 /********************************************************************/

void enter_vlls2(void)
{
  volatile unsigned int dummyread;
  /* Write to PMPROT to allow VLLS2 power modes */
  SMC_PMPROT = SMC_PMPROT_AVLLS_MASK;      
  /* Set the STOPM field to 0b100 for VLLS2 mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x4); 
  /* set VLLSM = 0b10 */
  SMC_STOPCTRL =  SMC_STOPCTRL_VLLSM(2);                
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_STOPCTRL;
  /* Now execute the stop instruction to go into VLLS2 */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}
/***************************************************************/
/* VLLS1 mode entry routine. Puts the processor into 
 * VLLS1 mode from normal run mode or VLPR. 
 *
 * Mode transitions:
 * RUN -> VLLS1
 * VLPR -> VLLS1
 *
 * NOTE: VLLSx modes will always exit to RUN mode even if you were 
 * in VLPR mode before entering VLLSx.
 *
 * Wakeup from VLLSx mode is controlled by the LLWU module. Most
 * modules cannot issue a wakeup interrupt in VLLSx mode, so make
 * sure to setup the desired wakeup sources in the LLWU before 
 * calling this function.
 *
 * Parameters:
 * none  
 */
 /********************************************************************/

void enter_vlls1(void)
{
  volatile unsigned int dummyread;
  SMC_PMPROT = SMC_PMPROT_AVLLS_MASK;
    
  /* Write to PMPROT to allow all possible power modes */
  /* Set the STOPM field to 0b100 for VLLS1 mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x4); 
  /* set VLLSM = 0b01 */
  SMC_STOPCTRL =  SMC_STOPCTRL_VLLSM(1);          
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_STOPCTRL;
  /* Now execute the stop instruction to go into VLLS1 */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}

/********************************************************************/
/* VLLS0 mode entry routine. Puts the processor into VLLS0 mode from
 * normal run mode or VLPR. 
 *
 * Mode transitions:
 * RUN -> VLLS0
 * VLPR -> VLLS0
 *
 * NOTE: VLLSx modes will always exit to RUN mode even if you were 
 * in VLPR mode before entering VLLSx.
 *
 * Wakeup from VLLSx mode is controlled by the LLWU module. Most
 * modules cannot issue a wakeup interrupt in VLLSx mode, so make
 * sure to setup the desired wakeup sources in the LLWU before 
 * calling this function.
 *
 * Parameters:
 * PORPO_value - 0 POR detect circuit is enabled in VLLS0
 *               1 POR detect circuit is disabled in VLLS0
 */
 /***************************************************************/

void enter_vlls0(unsigned char PORPO_value )
{
  volatile unsigned int dummyread;
  int i;
  /* Write to PMPROT to allow all possible power modes */
  SMC_PMPROT = SMC_PMPROT_AVLLS_MASK;   
  if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 4){
     SMC_PMCTRL &= ~SMC_PMCTRL_RUNM_MASK;   // go back to RUN mode temporarily
     for (i=0;i<0xff;i++)
       {
       if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 1)
    	 break;
       }
  }
  /* Set the STOPM field to 0b100 for VLLS0 mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x4); 
  /* set VLLSM = 0b00 */
  SMC_STOPCTRL &= ~SMC_STOPCTRL_VLLSM_MASK;
  SMC_STOPCTRL &= ~SMC_STOPCTRL_PORPO_MASK;
  SMC_STOPCTRL |=  (PORPO_value <<SMC_STOPCTRL_PORPO_SHIFT) 
                 | SMC_STOPCTRL_VLLSM(0);          
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_STOPCTRL;
  /* Now execute the stop instruction to go into VLLS0 */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}
/***************************************************************/
/* VLLS0 mode entry routine. Puts the processor into VLLS0 mode from
 * normal run mode or VLPR with the POR circuit disabled
 *
 * Mode transitions:
 * RUN -> VLLS0
 * VLPR -> VLLS0
 *
 * NOTE: VLLSx modes will always exit to RUN mode even if you were 
 * in VLPR mode before entering VLLSx.
 *
 * Wakeup from VLLSx mode is controlled by the LLWU module. Most
 * modules cannot issue a wakeup interrupt in VLLSx mode, so make
 * sure to setup the desired wakeup sources in the LLWU before 
 * calling this function.
 *
 * Parameters:
 * PORPO = 1-  POR detect circuit is disabled in VLLS0
 */
/***************************************************************/
void enter_vlls0_nopor(void)
{
  volatile unsigned int dummyread;
  int i;
  /* Write to PMPROT to allow all possible power modes */
  SMC_PMPROT = SMC_PMPROT_AVLLS_MASK;   
  if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 4){
     SMC_PMCTRL &= ~SMC_PMCTRL_RUNM_MASK;   // go back to RUN mode temporarily
     for (i=0;i<0xff;i++)
       {
       if ((SMC_PMSTAT & SMC_PMSTAT_PMSTAT_MASK)== 1)
    	 break;
       }
  }
  /* Set the STOPM field to 0b100 for VLLS0 mode */
  SMC_PMCTRL &= ~SMC_PMCTRL_STOPM_MASK; 
  SMC_PMCTRL |=  SMC_PMCTRL_STOPM(0x4); 
  /* set VLLSM = 00 * and PORPO = 1 */
  SMC_STOPCTRL &= ~SMC_STOPCTRL_VLLSM_MASK;
  SMC_STOPCTRL =  SMC_STOPCTRL_VLLSM(0) | SMC_STOPCTRL_PORPO_MASK;  
  /*wait for write to complete to SMC before stopping core */  
  dummyread = SMC_STOPCTRL;
  /* Now execute the stop instruction to go into VLLS0 */
  #ifdef CMSIS
  /* Set the SLEEPDEEP bit to enable deep sleep mode (STOP) */
  SCB_SCR |= SCB_SCR_SLEEPDEEP_MASK;
  __wfi();
#else
  stop();
#endif
}

// [ILG]
#if defined ( __GNUC__ )
#pragma GCC diagnostic pop
#endif

/********************************************************************/
/********************End of Functions *******************************/
/********************************************************************/



