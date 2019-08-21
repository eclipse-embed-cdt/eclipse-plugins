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
    GPIO_ResetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#else
    GPIO_SetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#endif
  }

  inline void
  __attribute__((always_inline))
  turnOff()
  {
#if (BLINK_ACTIVE_LOW)
    GPIO_SetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#else
    GPIO_ResetBits(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_MASK(BLINK_PIN_NUMBER));
#endif
  }
};
