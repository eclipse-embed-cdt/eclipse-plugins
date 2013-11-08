Content
-------

The files copied to these folders are the original ones, from the 
STM stsw-stm32108 stm32f30x_dsp_stdperiph_lib.zip archive.
with the following changes:

- none


Startup
-------

The original multiple assembly startup files were replaced by 
- a portable startup_cm.c 
- a family initialisation file (startup_stm32f30x.c)
- the interrupt vectors (vectors_stm32f30x.c)


Application interrupt handlers
------------------------------

It is recommended that interrupt handlers redefined by applications
to be located in the same files where the application drivers are; 
there is no need to use the stm32f30x_it.* files, since the default
system handlers are already defined in the vectors_stm32f30x.c file.


Manuals and samples
-------------------

For more details, manuals and the complete set of samples, please 
refer to the original STM archives.
