WARNING: Not recommended for new designs!

Please note that this will be the last 0.5.x version, and is marked "End of life"; 
the new 1.x version is included as a demo, if you like it, please update.

-----------------------------------------------------------------------

This is an archived version of the GNU ARM Eclipse plug-in update site.

To install it, point your Eclipse to it:
	Eclipse -> Install New Software -> Add -> Archive -> select .zip file.
	
This version (0.5.5) runs on CDT 8.0 (Eclipse 3.7 Indigo) or later, 
including Eclipse 4.3 Keppler.

Changes since previous 0.5.4 version:

- new ARM toolchains added:
	Tools for Embedded (arm-none-eabi-)
	Sourcery Lite Linux (arm-none-linux-gnueabi-)
	Linaro GNUEABIHF Linux (arm-linux-gnueabihf-)
- ARM64 (AArch64) toolchains added:
	Linaro AArch64 Bare (aarch64-none-elf-)
	Linaro AArch64 Linux (aarch64-linux-gnu-)

- C/C++ 'Hello ARM World' templates added

- flash binary output type in CreateFlash no longer locked to hex and computed according to selection
- elf32-littlearm, elf32-bigarm, elf32-little, elf32-big, tekhex removed from CreateFlash output type;

- new cpu added: arm720, arm710t, arm720t, arm740t, strongarm1110, arm1156t2f-s, cortex-a7, cortex-a15, cortex-a53, cortex-m0plus, marvell-pj4, fa526, fa626, fa606te, fa626te, fmp626, fa726te
- new FPU types added: ‘fp-armv8’, ‘neon-fp-armv8’, and ‘crypto-neon-fp-armv8’
- FPU Type enabled even for default Float ABI

- 'Other target flash' field added
 
- compiler -ffunction-sections, -fdata-sections and linker -Xlinker --gc-sections, defaults as enabled

- startfiles default as enabled in linker
- toolchain debug format as default instead of dwarf

- accept spaces in project names (requires a CDT patch, that will come with CDT 8.2.1)

