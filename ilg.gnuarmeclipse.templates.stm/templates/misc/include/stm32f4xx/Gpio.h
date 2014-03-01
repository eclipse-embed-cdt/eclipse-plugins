//
// This file is part of the GNU ARM Eclipse distribution.
// Copyright (c) 2014 Liviu Ionescu.
//

#include "stm32f4xx.h"

#ifndef GPIO_H_
#define GPIO_H_

namespace stm32f
{
  // General definitions, should probably be moved to a higher header
  typedef uint32_t registerAddress_t;
  typedef uint32_t registerMask_t;
}

namespace stm32f
{
  namespace gpio
  {
    // GPIO specific definitions
    typedef uint32_t portNumber_t;
    typedef uint32_t portBitNumber_t;
    typedef uint32_t portBitMask_t;

    // ------------------------------------------------------------------------

    // Port definitions
    enum class Port
      : portNumber_t
        {
          A = 0u, //
      B,
      C,
      D,
      E,
      F,
      G,
      H,
      I,
      MAX = I
    };

    // ------------------------------------------------------------------------

    // Port bits definitions
    enum class PortBit
      : portBitNumber_t
        {

          _0 = 0u,
      _1,
      _2,
      _3,
      _4,
      _5,
      _6,
      _7,
      _8,
      _9,
      _10,
      _11,
      _12,
      _13,
      _14,
      _15,
      MAX = _15
    };

    // ------------------------------------------------------------------------

    typedef uint32_t mode_t;

    /// \brief Port modes (2 bits/pin).
    enum class Mode
      : mode_t
        {
          Input = 0u, //
      Output = 1u,
      Alternate = 2u,
      Analog = 3u
    };

    /// \brief Mask to isolate the port mode bits.
    constexpr mode_t MODE_MASK = 0x3u;

    // ------------------------------------------------------------------------

    typedef uint32_t outputType_t;

    /// \brief Output type (1 bit/pin).
    enum class OutputType
      : outputType_t
        {
          PushPull = 0u, //
      OpenDrain = 1u
    };

    constexpr outputType_t OUTPUT_TYPE_MASK = 0x1u;

    // ------------------------------------------------------------------------

    typedef uint32_t outputSpeed_t;

    /// \brief Output Speed (2 bits/pin).
    enum class OutputSpeed
      : outputSpeed_t
        {
          Low_2MHz = 0u, //
      Medium_25MHz = 1u,
      Fast_50MHz = 2u,
      High_100MHz = 3u
    };

    /// \brief Mask to isolate the port output speed bits.
    constexpr outputSpeed_t OUTPUT_SPEED_MASK = 0x3u;

    // ------------------------------------------------------------------------

    typedef uint32_t resistors_t;

    /// \brief Pull up/Pull down resistors (2 bits/pin).
    enum class Resistors
      : resistors_t
        {
          None = 0, //
      PullUp = 1,
      PullDown = 2
    };

    /// \brief Mask to isolate the port pull up/pull down bits.
    constexpr resistors_t RESISTORS_MASK = 0x3u;

    // ------------------------------------------------------------------------

    typedef uint32_t alternateFunction_t;

    /// \brief Alternate functions (4 bits/pin).
    enum class AlternateFunction
      : alternateFunction_t
        {
          AF0 = 0, //
      AF1,
      AF2,
      AF3,
      AF4,
      AF5,
      AF6,
      AF7,
      AF8,
      AF9,
      AF10,
      AF11,
      AF12,
      AF13,
      AF14,
      AF15
    };

    /// \brief Mask to isolate the alternate function bits.
    static constexpr alternateFunction_t ALTERNATE_FUNCTION_MASK = 0xFu;

  // ----------------------------------------------------------------------
  }

  template<const gpio::Port PORT_T, const gpio::PortBit PORT_BIT_T>
    class TGpioConst
    {
    private:
      // Validate template constant parameters
      static_assert(PORT_T <= gpio::Port::MAX, "Port number too high");
      static_assert(PORT_BIT_T <= gpio::PortBit::MAX, "Port bit number too high");

      // Define local numeric versions of the template enums parameters.
      static constexpr gpio::portNumber_t m_portNumber =
          static_cast<gpio::portNumber_t>(PORT_T);
      static constexpr gpio::portBitNumber_t m_portBitNumber =
          static_cast<gpio::portBitNumber_t>(PORT_BIT_T);

      // Compute bit mask.
      static constexpr gpio::portBitMask_t m_portBitMask = (1u
          << m_portBitNumber);

      // Compute port address
      static constexpr registerAddress_t m_gpioAddress = (GPIOA_BASE
          + (GPIOB_BASE - GPIOA_BASE) * (m_portNumber));

    public:
      // Deleted constructor.
      TGpioConst() = default;

      // Warning: this might be moved to a separate RCC template
      inline void
      __attribute__((always_inline))
      enableClock(void) const
      {
        RCC->AHB1ENR |= (RCC_AHB1Periph_GPIOA << (m_portNumber));
      }

      // Warning: this might be moved to a separate RCC template
      inline void
      __attribute__((always_inline))
      disableClock(void) const
      {
        RCC->AHB1ENR &= ~(RCC_AHB1Periph_GPIOA << (m_portNumber));
      }

      inline void
      __attribute__((always_inline))
      configureMode(gpio::Mode value) const
      {
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->MODER &=
            ~(gpio::MODE_MASK << (m_portBitNumber * 2));

        gpio::mode_t mode = static_cast<gpio::mode_t>(value) & gpio::MODE_MASK;

        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->MODER |= ((mode)
            << (m_portBitNumber * 2));
      }

      inline gpio::Mode
      __attribute__((always_inline))
      retrieveMode(void) const
      {
        // TBD
        return gpio::Mode::Input;
      }

      inline void
      __attribute__((always_inline))
      configureOutputType(gpio::OutputType value) const
      {
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->OTYPER &=
            ~((gpio::OUTPUT_TYPE_MASK) << (m_portBitNumber));

        gpio::outputType_t outputType = static_cast<gpio::outputType_t>(value)
            & gpio::OUTPUT_TYPE_MASK;

        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->OTYPER |=
            ((outputType) << (m_portBitNumber));
      }

      inline gpio::OutputType
      __attribute__((always_inline))
      retrieveOutputType(void) const
      {
        // TBD
        return gpio::OutputType::PushPull;
      }

      inline void
      __attribute__((always_inline))
      configureOutputSpeed(gpio::OutputSpeed value) const
      {
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->OTYPER &=
            ~((gpio::OUTPUT_SPEED_MASK) << (m_portBitNumber));

        gpio::outputSpeed_t outputSpeed =
            static_cast<gpio::outputSpeed_t>(value) & gpio::OUTPUT_SPEED_MASK;

        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->OTYPER |=
            ((outputSpeed) << (m_portBitNumber * 2));
      }

      inline gpio::OutputSpeed
      __attribute__((always_inline))
      retrieveOutputSpeed(void) const
      {
        // TBD
        return gpio::OutputSpeed::Low_2MHz;
      }

      inline void
      __attribute__((always_inline))
      configureResistors(gpio::Resistors value) const
      {
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->PUPDR &=
            ~(gpio::RESISTORS_MASK << (m_portBitNumber * 2));

        gpio::resistors_t resistors = static_cast<gpio::resistors_t>(value)
            & gpio::RESISTORS_MASK;

        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->PUPDR |= ((resistors)
            << (m_portBitNumber * 2));
      }

      inline gpio::Resistors
      __attribute__((always_inline))
      retrieveResistors(void) const
      {
        // TBD
        return gpio::Resistors::None;
      }

      inline void
      __attribute__((always_inline))
      setHigh(void) const
      {
        // Set a bit in the set register.
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->BSRRL = m_portBitMask;
      }

      inline void
      __attribute__((always_inline))
      setLow(void) const
      {
        // Set a bit in the clear register.
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->BSRRH = m_portBitMask;
      }

      inline void
      __attribute__((always_inline))
      toggle(void) const
      {
        (reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->ODR =
            ((reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->IDR
                ^ m_portBitMask);
      }

      inline bool
      __attribute__((always_inline))
      isLow(void) const
      {
        // Test if bit in input register is 0.
        return (((reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->IDR
            & m_portBitMask) == 0);
      }

      inline bool
      __attribute__((always_inline))
      isHigh(void) const
      {
        // Test if bit in input register is 1.
        return (((reinterpret_cast<GPIO_TypeDef*>(m_gpioAddress))->IDR
            & m_portBitMask) != 0);
      }

    };
}

#endif // GPIO_PIN_H_
