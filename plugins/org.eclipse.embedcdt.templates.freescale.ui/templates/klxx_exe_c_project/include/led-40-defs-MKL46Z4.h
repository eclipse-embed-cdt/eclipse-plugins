// FRDM-KL46Z definitions (the GREEN LED, active low, D5).
// (SEGGER J-Link device name: MKL46Z256xxx4).

// Port numbers: 0=A, 1=B, 2=C, 3=D, 4=E
#define BLINK_PORT_NUMBER               (3)
#define BLINK_PIN_NUMBER                (5)
#define BLINK_ACTIVE_LOW                (1)

#define BLINK_PORTx(_N)                 ((PORT_Type *)(PORTA_BASE + (PORTB_BASE-PORTA_BASE)*(_N)))
#define BLINK_GPIOx(_N)                 ((GPIO_Type *)(PTA_BASE + (PTB_BASE-PTA_BASE)*(_N)))
#define BLINK_PIN_MASK(_N)              (1 << (_N))
#define BLINK_SCGC5_MASKx(_N)           (1 << ((SIM_SCGC5_PORTA_SHIFT) + (_N)))
