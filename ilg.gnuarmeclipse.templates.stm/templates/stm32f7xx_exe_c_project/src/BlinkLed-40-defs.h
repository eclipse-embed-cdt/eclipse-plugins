
#if defined(STM32F746xx)

#warning "Assume a STM32F746G-DISCO board, PI1, active high."

// STM32F746-DISCO definitions (the GREEN led, I1, active high)

#define BLINK_PORT_NUMBER               (9)
#define BLINK_PIN_NUMBER                (1)
#define BLINK_ACTIVE_LOW                (0)

#else

#warning "Unknown board, assume PA5, active high."

#define BLINK_PORT_NUMBER               (0)
#define BLINK_PIN_NUMBER                (5)
#define BLINK_ACTIVE_LOW                (0)

#endif

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHB1ENR_GPIOAEN << (_N))
