
# Cortex-M scripts

## generate-all-template-vectors.sh

Script to generate the vectors for all templates.

Edit it for the current CubeMX versions.

```bash
bash generate-all-template-vectors.sh
```

## generate-vectors-from-arm-startup.sh

Scipt to generate the vectors_xxx.c files for a family.

Provide the path to the CMSIS assembly files, like
`STM32Cube/Repository/STM32Cube_FW_F0_V1.11.1/Drivers/CMSIS/Device/ST/STM32F0xx/Source/Templates/arm` 
and the path to the destination folder, like
`eclipse-embed-cdt.github/eclipse-plugins.git/plugins/org.eclipse.embedcdt.templates.stm/templates/micro-os-plus/stm32f0.pack/src/cmsis`:

```bash
bash generate-vectors-from-arm-startup.sh ${from} ${to}
```

