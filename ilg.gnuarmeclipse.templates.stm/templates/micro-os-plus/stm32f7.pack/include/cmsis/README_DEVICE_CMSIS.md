The STM files are from the original `stm32cube_fw_f7_v120.zip`
archive, the folder:

* `STM32Cube_FW_F7_V1.2.0/Drivers/CMSIS/Device/ST/STM32F7xx/Include`

There should be no changes from the STM originals.

The notable exception is the `stm32f7xx.h` file which was braced with pragmas
to avoid `-Wpadded` warnings.

The `cmsis_device.h` was added for convenience.
