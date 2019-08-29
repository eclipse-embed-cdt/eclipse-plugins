// See LICENSE file for licence details

#ifndef SOC_FUNC_H
#define SOC_FUNC_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

__BEGIN_DECLS

void uart_init(size_t baud_rate);

void soc_init();

__END_DECLS

#endif
