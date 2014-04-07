//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#include "BlinkLed.h"

// ----------------------------------------------------------------------------

void
blink_led_init()
{
  // Turn on clock for port module
  SIM->SCGC5 |= BLINK_SCGC5_MASKx(BLINK_PORT_NUMBER);

  // Set the pin multiplexer to GPIO mode
  BLINK_PORTx(BLINK_PORT_NUMBER)->PCR[BLINK_PIN_NUMBER] = PORT_PCR_MUX(1);

  // Set the pin as output
  BLINK_GPIOx(BLINK_PORT_NUMBER)->PDDR |= BLINK_PIN_MASK(BLINK_PIN_NUMBER);

  // Start with led turned off
  blink_led_off();
}

// ----------------------------------------------------------------------------
