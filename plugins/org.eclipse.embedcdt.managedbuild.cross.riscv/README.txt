Supported arch & cpu:

TODO: get from GCC manual

RISC-V Options
-mbranch-cost=N-instruction -mmemcpy -mno-memcpy
-mplt -mno-plt -mabi=ABI-string
-mfdiv -mno-fdiv
-mdiv -mno-div
-march=ISA-string -mtune=processor-string -msmall-data-limit=N-bytes -msave-restore -mno-save-restore -mcmodel=code-model
-mexplicit-relocs -mno-explicit-relocs

3.18.37 RISC-V Options
These command-line options are de ned for RISC-V targets:

-mbranch-cost=n
	Set the cost of branches to roughly n instructions.
	
-mmemcpy
-mno-memcpy
	Don’t optimize block moves.
	
-mno-plt 
	When generating PIC code, allow the use of PLTs. Ignored for non-PIC.
	
-mabi=ABI-string
	Specify integer and  oating-point calling convention. This defaults to the nat- ural calling convention: e.g. LP64 for RV64I, ILP32 for RV32I, LP64D for RV64G.
	
-mplt
-mfdiv
-mno-fdiv
-mdiv
-mno-div
	Use hardware  oating-point divide and square root instructions. This requires the F or D extensions for  oating-point registers.
	Use hardware instructions for integer division. This requires the M extension.
	
-march=ISA-string
	Generate code for given RISC-V ISA (e.g. ‘rv64im’). ISA strings must be lower-case. Examples include ‘rv64i’, ‘rv32g’, and ‘rv32imaf’.

-mtune=processor-string
	Optimize the output for the given processor, speci ed by microarchitecture name.
	
-msmall-data-limit=n
	Put global and static data smaller than n bytes into a special section (on some targets).
	
-msave-restore
-mno-save-restore
	Use smaller but slower prologue and epilogue code.
	
-mcmodel=code-model
	Specify the code model.
	

