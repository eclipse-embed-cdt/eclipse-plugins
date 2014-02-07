
/* ----- Timing definitions ------------------------------------------------ */

#define SYSTICK_FREQUENCY_HZ            1000

#define BLINK_TICKS                     SYSTICK_FREQUENCY_HZ/3

/* ----- Delay function -----------------------------------------------------*/

static void
delay(__IO uint32_t nTime);

/* ------------------------------------------------------------------------- */
