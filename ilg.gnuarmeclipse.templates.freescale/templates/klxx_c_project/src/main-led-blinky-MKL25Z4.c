
/* ----- LED definitions --------------------------------------------------- */

/* FRDM-KL25Z definitions (the GREEN LED, active low). */
/* (adjust them for your own board) */

#define BLINK_PORT      		PTB
#define BLINK_PIN       		19
#define BLINK_SCGC5_MASK		SIM_SCGC5_PORTB_MASK

void
blink_led_on()
{
  BLINK_PORT->PCOR = (1 << BLINK_PIN);
}

void
blink_led_off()
{
  BLINK_PORT->PSOR = (1 << BLINK_PIN);
}

void
blink_led_init()
{
  /* Turn on clock for BLINK_PORT module */
  SIM->SCGC5 |= BLINK_SCGC5_MASK;
 
  /* Set the BLINK_PIN pin multiplexer to GPIO mode */
  PORTB->PCR[BLINK_PIN] = PORT_PCR_MUX(1);
 
  /* Set the BLINK_PIN as output */
  BLINK_PORT->PDDR |= (1 << BLINK_PIN);

  /* Turn off led */
  blink_led_off();
}
