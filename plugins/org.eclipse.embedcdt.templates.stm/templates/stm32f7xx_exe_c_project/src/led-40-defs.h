
// Ports numbers are: PA=0, PB=1, PC=2, PD=3, PE=4, PF=5, PG=6, PH=7, PI=8.

#if defined(STM32F746xx)

#if defined(STM32F746_EVAL)

//#warning "Assume a STM32F746G-EVAL board, PF12 & PB7, active low."

#define BLINK_PORT_NUMBER         (5)
#define BLINK_PIN_NUMBER          (12)

#define BLINK_PORT_NUMBER_RED     (1)
#define BLINK_PIN_NUMBER_RED      (7)

#define BLINK_ACTIVE_LOW          (1)

#else

#warning "Assume a STM32F746G-DISCO board, PI1, active high."

// STM32F746-DISCO definitions (the GREEN led, I1, active high)

#define BLINK_PORT_NUMBER               (8)
#define BLINK_PIN_NUMBER                (1)
#define BLINK_ACTIVE_LOW                (0)

#endif

#else

#warning "Unknown board, assume PA5, active high."

#define BLINK_PORT_NUMBER               (0)
#define BLINK_PIN_NUMBER                (5)
#define BLINK_ACTIVE_LOW                (0)

#endif

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHB1ENR_GPIOAEN << (_N))
