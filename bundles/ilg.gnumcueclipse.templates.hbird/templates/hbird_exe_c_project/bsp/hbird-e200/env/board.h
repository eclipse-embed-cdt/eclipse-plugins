// See LICENSE for license details.

#ifndef _HBIRD_E200_H
#define _HBIRD_E200_H

#include <stdint.h>

/****************************************************************************
 * GPIO Connections
 *****************************************************************************/

// These are the GPIO bit offsets for the RGB LED on Board.
// These are also mapped to RGB LEDs on the FPGA Dev Kit.


#define RED_LED_GPIO_OFFSET   22
#define GREEN_LED_GPIO_OFFSET 19
#define BLUE_LED_GPIO_OFFSET  21



#define BUTTON_0_GPIO_OFFSET 15
#define BUTTON_1_GPIO_OFFSET 30
#define BUTTON_2_GPIO_OFFSET 31

#define PLIC_INT_DEVICE_BUTTON_0 (PLIC_INT_GPIO_BASE + BUTTON_0_GPIO_OFFSET)
#define PLIC_INT_DEVICE_BUTTON_1 (PLIC_INT_GPIO_BASE + BUTTON_1_GPIO_OFFSET)
#define PLIC_INT_DEVICE_BUTTON_2 (PLIC_INT_GPIO_BASE + BUTTON_2_GPIO_OFFSET)



#define RTC_FREQ 32768

void write_hex(int fd, unsigned long int hex);

#endif /* _HBIRD_E200_H */
