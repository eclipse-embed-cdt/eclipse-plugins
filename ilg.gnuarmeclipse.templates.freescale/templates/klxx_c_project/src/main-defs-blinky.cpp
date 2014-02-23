
namespace
{
  // ----- Timing definitions -------------------------------------------------

  typedef uint32_t ticks_t;

  constexpr ticks_t SYSTICK_FREQUENCY_HZ = 1000u;
  constexpr ticks_t BLINK_TICKS = SYSTICK_FREQUENCY_HZ / 3u;

  // ----- Delay definition ---------------------------------------------------

  void
  delay(ticks_t ticks);
}
