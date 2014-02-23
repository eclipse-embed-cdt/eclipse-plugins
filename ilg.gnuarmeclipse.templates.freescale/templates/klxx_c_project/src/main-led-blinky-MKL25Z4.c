
// ----- LED definitions ------------------------------------------------------

// FRDM-KL25Z definitions (the GREEN LED, active low, B19).
// (adjust them for your own board)

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E
#define BLINK_PORT_NUMBER               (1)
#define BLINK_PIN_NUMBER                (19)

#define BLINK_PORTx(_N)                 ((PORT_Type *)(PORTA_BASE + (PORTB_BASE-PORTA_BASE)*(_N)))
#define BLINK_GPIOx(_N)                 ((GPIO_Type *)(PTA_BASE + (PTB_BASE-PTA_BASE)*(_N)))
#define BLINK_SCGC5_MASKx(_N)           (1 << ((SIM_SCGC5_PORTA_SHIFT) + (_N)))

void
blink_led_on()
{
  BLINK_GPIOx(BLINK_PORT_NUMBER)->PCOR = (1 << BLINK_PIN_NUMBER);
}

void
blink_led_off()
{
  BLINK_GPIOx(BLINK_PORT_NUMBER)->PSOR = (1 << BLINK_PIN_NUMBER);
}

void
blink_led_init()
{
  // Turn on clock for port module
  SIM->SCGC5 |= BLINK_SCGC5_MASKx(BLINK_PORT_NUMBER);
 
  // Set the pin multiplexer to GPIO mode
  BLINK_PORTx(BLINK_PORT_NUMBER)->PCR[BLINK_PIN_NUMBER] = PORT_PCR_MUX(1);
 
  // Set the pin as output
  BLINK_GPIOx(BLINK_PORT_NUMBER)->PDDR |= (1 << BLINK_PIN_NUMBER);

  // Turn off led
  blink_led_off();
}
