// STM3220G-EVAL definitions (the GREEN led, G6, active high)
// (SEGGER J-Link device name: STM32F207IG).

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E, 5=F, 6=G, ...
#define BLINK_PORT_NUMBER               (6)
#define BLINK_PIN_NUMBER                (6)
#define BLINK_ACTIVE_LOW                (0)

#define BLINK_GPIOx(_N)                 ((GPIO_TypeDef *)(GPIOA_BASE + (GPIOB_BASE-GPIOA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_RCC_MASKx(_N)             (RCC_AHB1Periph_GPIOA << (_N))
