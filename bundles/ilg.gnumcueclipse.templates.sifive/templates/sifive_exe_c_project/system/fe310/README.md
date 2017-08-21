## SiFive Freedom E310 device specific files

### Device vs platform

The RISC-V documentation introduces the term _platform_ as:

> A RISC-V hardware _platform_ can contain one or more RISC-V-compatible processing cores together with other non-RISC-V-compatible cores, fixed-function accelerators, various physical memory structures, I/O devices, and an interconnect structure to allow the components to communicate.

In modern implementations, this is generally either a physical chip or a synthesised one.

In other contexts, _platform_ has a broader meaning and may refer to the environment in which a piece of software is executed; it may be the hardware or the operating system (OS); to avoid confusions, in µOS++ the term **device** is used to identify the vendor specific RISC-V details.

### How to use

The standard way to include the device files is

```c
#include <micro-os-plus/device.h>
```

