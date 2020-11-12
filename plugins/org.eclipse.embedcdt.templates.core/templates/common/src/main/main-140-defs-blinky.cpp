
// Definitions visible only within this translation unit.
namespace
{
  // ----- Timing definitions -------------------------------------------------

  // Keep the LED on for 2/3 of a second.
  constexpr timer::ticks_t BLINK_ON_TICKS = timer::FREQUENCY_HZ * 3 / 4;
  constexpr timer::ticks_t BLINK_OFF_TICKS = timer::FREQUENCY_HZ
      - BLINK_ON_TICKS;
}
