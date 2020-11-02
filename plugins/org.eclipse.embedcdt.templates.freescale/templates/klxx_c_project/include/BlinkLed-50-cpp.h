
// ----------------------------------------------------------------------------

class BlinkLed
{
public:
  BlinkLed() = default;

  void
  powerUp();

  inline void
  __attribute__((always_inline))
  turnOn()
  {
#if (BLINK_ACTIVE_LOW)
    BLINK_GPIOx(BLINK_PORT_NUMBER)->PCOR = BLINK_PIN_MASK(BLINK_PIN_NUMBER);
#else
    BLINK_GPIOx(BLINK_PORT_NUMBER)->PSOR = BLINK_PIN_MASK(BLINK_PIN_NUMBER);
#endif
  }

  inline void
  __attribute__((always_inline))
  turnOff()
  {
#if (BLINK_ACTIVE_LOW)
    BLINK_GPIOx(BLINK_PORT_NUMBER)->PSOR = BLINK_PIN_MASK(BLINK_PIN_NUMBER);
#else
    BLINK_GPIOx(BLINK_PORT_NUMBER)->PCOR = BLINK_PIN_MASK(BLINK_PIN_NUMBER);
#endif
  }
};
