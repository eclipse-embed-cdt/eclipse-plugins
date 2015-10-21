# STM32F4 CMSIS

This project, available from [GitHub](https://github.com/xpacks), includes the CMSIS files for STM32F4.

## Version

    v1.8.0

## Original files

The original files are available from the `originals` branch.

These files were extracted from `stm32cube_fw_f4_v180.zip`.

To save space, only the following folder was preserved:

    Drivers/CMSIS/Device/ST/STM32F4xx

In addition, the following files were removed:

* none

## Changes

The actual files used by the package are in the `master` branch.

Most of the files should be unchanged.

The `cmsis_device.h` is added for convenience.

## Vectors

The `vectors_*.c` files were generated using the ARM assembly files.

```
$ bash scripts/generate-vectors.sh
startup_stm32f401xc.s -> vectors_stm32f401xc.c
startup_stm32f401xe.s -> vectors_stm32f401xe.c
startup_stm32f405xx.s -> vectors_stm32f405xx.c
startup_stm32f407xx.s -> vectors_stm32f407xx.c
startup_stm32f410cx.s -> vectors_stm32f410cx.c
startup_stm32f410rx.s -> vectors_stm32f410rx.c
startup_stm32f410tx.s -> vectors_stm32f410tx.c
startup_stm32f411xe.s -> vectors_stm32f411xe.c
startup_stm32f415xx.s -> vectors_stm32f415xx.c
startup_stm32f417xx.s -> vectors_stm32f417xx.c
startup_stm32f427xx.s -> vectors_stm32f427xx.c
startup_stm32f429xx.s -> vectors_stm32f429xx.c
startup_stm32f437xx.s -> vectors_stm32f437xx.c
startup_stm32f439xx.s -> vectors_stm32f439xx.c
startup_stm32f446xx.s -> vectors_stm32f446xx.c
startup_stm32f469xx.s -> vectors_stm32f469xx.c
startup_stm32f479xx.s -> vectors_stm32f479xx.c
```

(this requires the `xpacks/scripts.git` project to be available in
the same folder as the packs projects).
