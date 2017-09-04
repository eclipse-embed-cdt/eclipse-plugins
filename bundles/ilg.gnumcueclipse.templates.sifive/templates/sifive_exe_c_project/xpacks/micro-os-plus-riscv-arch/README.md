## RISC-V architecture definitions

From a top down approach, in µOS++ the RISC-V definitions are grouped by several criteria:

- board
- device (RISC-V platform)
- core
- hart (hardware thread)

### Board

The board level refers to a device and adds board specific definitions, like what GPIO pins are used for various LEDs, buttons, etc.

The portable way to include board specifc definitions in an application is:

```
#include <micro-os-plus/board.h>
```

In µOS++, the board specific definitions are grouped in the `riscv::board` namespace.
 
An example of a board package is **sifive-hifive1** with the SiFive HiFive1 board.

### Device

The RISC-V documentation introduces the term _platform_ as:

> A RISC-V hardware _platform_ can contain one or more RISC-V-compatible processing cores together with other non-RISC-V-compatible cores, fixed-function accelerators, various physical memory structures, I/O devices, and an interconnect structure to allow the components to communicate.

In modern implementations, this is generally either a physical chip or a synthesised one.

In other contexts, _platform_ has a broader meaning and may refer to the environment in which a piece of software is executed; it may be the hardware or the operating system (OS); to avoid confusions, in µOS++ the term **device** is used to identify the vendor specific RISC-V details.

Please note that RISC-V defines some common MMIO registers (lime `mtime` and `mtimecmp`), but, for more flexibility, leaves the implementation to define the actual address. Unfortunately this increases the software complexity, since the device specific headers must define some fixed symbols and the header files must be included in a careful order, to avoid circular references.

In µOS++, the device specific definitions are grouped in the `riscv::device` namespace.

The portable way to include device specifc definitions in an application is:

```
#include <micro-os-plus/device.h>
```

Example of device packages are **sifive-freedom-e310** with the SiFive Freedom E310 device.

### Core

The RISC-V documentation introduces the term _core_ as:

> A component is termed a core if it contains an independent instruction fetch unit. A RISC-V-compatible core might support multiple RISC-V-compatible hardware threads, or harts, through multithreading.

In µOS++, the core specific definitions are grouped in the `riscv::core` namespace.  

The portable way to include architecture specifc definitions in an application is:

```
#include <micro-os-plus/architecture.h>
```

### Hart

Harware threads are the working horses of the software threads; each hardware thread has its own set of general registers and Control and Status Registers (CSRs); the OS may schedule a maximum number of software threads equal with the number of hardware threads, possibly with some grouping constrains.

In RISC-V, **Control and Status Registers** (**CSR**s) are a special group of registers, available via specific `csr*` instructions from a separate addressing space not visible in the memory space. 

The `hart` specific definitions are grouped under the `riscv::csr` namespace.

### Other namespaces

Interrupts and exceptions are grouped under `riscv::irq` and `riscv::exc`.


