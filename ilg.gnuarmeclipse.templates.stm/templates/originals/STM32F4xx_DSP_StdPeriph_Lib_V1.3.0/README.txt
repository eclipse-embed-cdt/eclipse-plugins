Content
-------

The files copied to these folders are the original ones, from the 
STM stsw-stm32065 stm32f4_dsp_stdperiph_lib.zip v1.3.0 archive,
with the following changes:

- stm32f4xx_fmc.c: compiles only on STM32F427_437xx or STM32F429_439xx
- stm32f4xx_fsmc.c: compiles only on STM32F40_41xxx
- system_stm32f4xx.c: was updated for the configurable clock settings

Startup
-------

The original multiple assembly startup files were replaced by 
- a portable startup_cm.c 
- a family initialisation file (startup_stm32f4xx.c)
- the interrupt vectors (vectors_stm32f4xx.c)


Application interrupt handlers
------------------------------

It is recommended that interrupt handlers redefined by applications
to be located in the same files where the application drivers are; 
there is no need to use the stm32f4xx_it.* files, since the default
system handlers are already defined in the vectors_stm32f4xx.c file.


Manuals and samples
-------------------

For more details, manuals and the complete set of samples, please 
refer to the original STM archives.
