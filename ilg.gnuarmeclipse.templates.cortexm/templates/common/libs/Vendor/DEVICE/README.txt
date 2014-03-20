These files are provided only as a functional sample.

For real applications they must be replaced with the files 
provided by the vendor.

Extensions to the ARM CMSIS files:

- the file cmsis_device.h was added, as a portable method to include the 
vendor device header file in library sources. 

- the assembly startup file was reimplemented in C, and split into 
multiple files, portable for the entire Cortex-M family:

	newlib/src/cmsis_startup.c
	newlib/src/cortexm_handlers.c
	
- the chip interrupt handlers must be added to the vectors_XXX.c file

Use of assembly files

The current version of the Eclipse managed build plug-in does not
process .s, but only .S. If you want to use the assembly startup_Device.s,
you must remove the _startup.c and cortex_handlers.c from build, and
rename the vendor provided assembly file to startup_XXX.S where XXX is the
actual device name.
  