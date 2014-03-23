
// ----------------------------------------------------------------------------
extern
void
blink_led_init();

inline void
__attribute__((always_inline))
blink_led_on()
{
#if (BLINK_ACTIVE_LOW)
    HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_NUMBER, GPIO_PIN_RESET);
#else
    HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_NUMBER, GPIO_PIN_SET);
#endif
}

inline void
__attribute__((always_inline))
blink_led_off()
{
#if (BLINK_ACTIVE_LOW)
    HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_NUMBER, GPIO_PIN_SET);
#else
    HAL_GPIO_WritePin(BLINK_GPIOx(BLINK_PORT_NUMBER),
        BLINK_PIN_NUMBER, GPIO_PIN_RESET);
#endif
}
