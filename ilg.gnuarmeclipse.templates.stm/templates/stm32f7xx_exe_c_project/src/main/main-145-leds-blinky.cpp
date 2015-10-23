
// ----- LED definitions ------------------------------------------------------

// Ports numbers are: PA=0, PB=1, PC=2, PD=3, PE=4, PF=5, PG=6, PH=7, PI=8.

#if defined(STM32F746xx)

// For this to be used, define STM32F746_EVAL in the Symbols Settings tab.
#if defined(STM32F746_EVAL)

//#warning "Assume a STM32F746G-EVAL board, PF12 & PB7, active low."

#define BLINK_PORT_NUMBER_GREEN   (5)
#define BLINK_PIN_NUMBER_GREEN    (12)

#define BLINK_PORT_NUMBER_RED     (1)
#define BLINK_PIN_NUMBER_RED      (7)

#define BLINK_ACTIVE_LOW          (true)

BlinkLed blinkLeds[2] =
  {
    { BLINK_PORT_NUMBER_GREEN, BLINK_PIN_NUMBER_GREEN, BLINK_ACTIVE_LOW },
    { BLINK_PORT_NUMBER_RED, BLINK_PIN_NUMBER_RED, BLINK_ACTIVE_LOW },
  };

#else

#warning "Assume a STM32F746G-DISCO board, PI1, active high."

#define BLINK_PORT_NUMBER         (8)
#define BLINK_PIN_NUMBER_GREEN    (1)
#define BLINK_ACTIVE_LOW          (false)

BlinkLed blinkLeds[1] =
  {
    { BLINK_PORT_NUMBER, BLINK_PIN_NUMBER_GREEN, BLINK_ACTIVE_LOW },
  };

#endif

#else

#warning "Unknown board, assume PA5, active high."

#define BLINK_PORT_NUMBER         (0)
#define BLINK_PIN_NUMBER          (5)
#define BLINK_ACTIVE_LOW          (false)

BlinkLed blinkLeds[1] =
  {
    { BLINK_PORT_NUMBER, BLINK_PIN_NUMBER, BLINK_ACTIVE_LOW },
  };

#endif
