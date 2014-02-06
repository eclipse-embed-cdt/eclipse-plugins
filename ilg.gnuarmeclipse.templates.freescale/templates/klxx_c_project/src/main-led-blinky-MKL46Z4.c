
/* ----- LED definitions --------------------------------------------------- */

/* FRDM-KL465Z definitions (the GREEN LED, active low). */
/* (adjust them for your own board) */

#define BLINK_PORT      		PTE
#define BLINK_PIN       		29
#define BLINK_SCGC5_MASK		SIM_SCGC5_PORTE_MASK

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
