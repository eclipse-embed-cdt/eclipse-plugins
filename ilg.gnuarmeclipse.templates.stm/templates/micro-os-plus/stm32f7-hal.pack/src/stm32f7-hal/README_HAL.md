The STM files are from the original `stm32cube_fw_f7_v100.zip`
archive, the folder:

* `STM32Cube_FW_F7_V1.0.0/Drivers/STM32F7xx_HAL_Driver/Src`

There should be no changes from the STM originals.

To disable compilation warnings, the compiler settings were changed
to include `-Wno-*`.

Files excluded from build can be re-enabled by using:

* right click -> **Resource Configurations** -> **Exclude from build**
* uncheck Debug and/or Release
