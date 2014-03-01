//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#ifndef LED_ACTIVE_LOW_H_
#define LED_ACTIVE_LOW_H_

namespace stm32f
{
  template<typename GpioPin_T>
    class TLedActiveLowConst
    {
    private:
      using GpioPin = GpioPin_T;

      // Normally this object has no members, it is fully static, we use
      // it here as a more flexible method to access the member functions.
      GpioPin m_gpioPin;

    public:
      TLedActiveLowConst() = default;

      void
      powerUp(void) const
      {
        m_gpioPin.powerUp();
        m_gpioPin.setHigh();
        m_gpioPin.configureDirectionOutput();
      }

      void
      powerDown(void) const
      {
        m_gpioPin.setHigh();
        m_gpioPin.powerDown();
      }

      inline void
      __attribute__((always_inline))
      turnOn(void) const
      {
        m_gpioPin.setLow();
      }

      inline void
      __attribute__((always_inline))
      turnOff(void) const
      {
        m_gpioPin.setHigh();
      }

      inline void
      __attribute__((always_inline))
      toggle(void) const
      {
        m_gpioPin.toggle();
      }

      inline bool
      __attribute__((always_inline))
      isOn(void) const
      {
        return m_gpioPin.isLow();
      }
    };
}

#endif // LED_ACTIVE_LOW_H_
