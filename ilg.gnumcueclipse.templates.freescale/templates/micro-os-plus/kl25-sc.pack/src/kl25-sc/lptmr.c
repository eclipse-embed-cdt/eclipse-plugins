/*
 * File:        lptmr.c
 * Purpose:     Provide common low power timer functions
 *
 * Notes:       Right now only function provided is used
 *              to generate a delay in ms. This driver
 *              could be expanded to include more functions
 *              in the future.
 *
 */


#include "common.h"
#include "lptmr.h"


extern int re_init_clk;
extern int clock_freq_hz;

/********************************************************************/
/*
 * Initialize the low power time to provide a delay measured in ms.
 *
 *
 * Parameters:
 *  count_val   number of ms to delay
 *
 * Returns:
 * None
 */
void time_delay_ms(unsigned int count_val)
{
  /* Make sure the clock to the LPTMR is enabled */
  SIM_SCGC5|=SIM_SCGC5_LPTMR_MASK;

  /* Reset LPTMR settings */
  LPTMR0_CSR=0;

  /* Set the compare value to the number of ms to delay */
  LPTMR0_CMR = count_val;

  /* Set up LPTMR to use 1kHz LPO with no prescaler as its clock source */
  LPTMR0_PSR = LPTMR_PSR_PCS(1)|LPTMR_PSR_PBYP_MASK;

  /* Start the timer */
  LPTMR0_CSR |= LPTMR_CSR_TEN_MASK;

  /* Wait for counter to reach compare value */
  while (!(LPTMR0_CSR & LPTMR_CSR_TCF_MASK));

  /* Disable counter and Clear Timer Compare Flag */
  LPTMR0_CSR &= ~LPTMR_CSR_TEN_MASK;

  return;
}

void lptmr_isr(void)
{
  
  printf("\n****LPT ISR entered*****\r\n");

  // enable timer
  // enable interrupts
  // clear the flag
  LPTMR0_CSR |=  LPTMR_CSR_TCF_MASK;   // write 1 to TCF to clear the LPT timer compare flag
  LPTMR0_CSR = ( LPTMR_CSR_TEN_MASK | LPTMR_CSR_TIE_MASK | LPTMR_CSR_TCF_MASK  );

}
/*******************************************************************************
*
*   PROCEDURE NAME:
*       lptmr_init -
*
*******************************************************************************/
void lptmr_init(int count, int clock_source)
{
    SIM_SCGC5 |= SIM_SCGC5_LPTMR_MASK;

    LPTMR0_PSR = ( LPTMR_PSR_PRESCALE(0) // 0000 is div 2
                 | LPTMR_PSR_PBYP_MASK  // LPO feeds directly to LPT
                 | LPTMR_PSR_PCS(clock_source)) ; // use the choice of clock
    if (clock_source== 0)
      printf("\n LPTMR Clock source is the MCGIRCLK \n\r");
    if (clock_source== 1)
      printf("\n LPTMR Clock source is the LPOCLK \n\r");
    if (clock_source== 2)
      printf("\n LPTMR Clock source is the ERCLK32 \n\r");
    if (clock_source== 3)
      printf("\n LPTMR Clock source is the OSCERCLK \n\r");
             
    LPTMR0_CMR = LPTMR_CMR_COMPARE(count);  //Set compare value

    LPTMR0_CSR =(  LPTMR_CSR_TCF_MASK   // Clear any pending interrupt
                 | LPTMR_CSR_TIE_MASK   // LPT interrupt enabled
                 | LPTMR_CSR_TPS(0)     //TMR pin select
                 |!LPTMR_CSR_TPP_MASK   //TMR Pin polarity
                 |!LPTMR_CSR_TFC_MASK   // Timer Free running counter is reset whenever TMR counter equals compare
                 |!LPTMR_CSR_TMS_MASK   //LPTMR0 as Timer
                );
    LPTMR0_CSR |= LPTMR_CSR_TEN_MASK;   //Turn on LPT and start counting
}
