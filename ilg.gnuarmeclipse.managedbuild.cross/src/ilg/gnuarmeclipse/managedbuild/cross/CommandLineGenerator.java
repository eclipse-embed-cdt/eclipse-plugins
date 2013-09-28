package ilg.gnuarmeclipse.managedbuild.cross;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;
import org.eclipse.cdt.managedbuilder.core.IOptionCategory;
import org.eclipse.cdt.managedbuilder.core.IOptionCategoryApplicability;
import org.eclipse.cdt.managedbuilder.core.IResourceConfiguration;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.internal.core.ResourceConfiguration;

public class CommandLineGenerator extends ManagedCommandLineGenerator {

	// private static final boolean DEBUG_LOCAL = true;
	private static final boolean DEBUG_LOCAL = false;

	@Override
	public IManagedCommandLineInfo generateCommandLineInfo(ITool tool,
			String commandName, String[] flags, String outputFlag,
			String outputPrefix, String outputName, String[] inputResources,
			String commandLinePattern) {

		String[] newFlags = gatherFlags(tool, flags);

		return super.generateCommandLineInfo(tool, commandName, newFlags,
				outputFlag, outputPrefix, outputName, inputResources,
				commandLinePattern);
	}

	private static final String OPTION_SUFFIX = ".option.";

	private static final String OPTION_SUFFIX_TARGET = OPTION_SUFFIX
			+ "target.";
	private static final String OPTION_SUFFIX_ARM32_TARGET = OPTION_SUFFIX
			+ "arm32.target.";
	private static final String OPTION_SUFFIX_ARM32_TARGET_FAMILY = OPTION_SUFFIX_ARM32_TARGET
			+ "family";
	private static final String OPTION_SUFFIX_ARM32_TARGET_ARCHITECTURE = OPTION_SUFFIX_ARM32_TARGET
			+ "architecture";
	private static final String OPTION_SUFFIX_ARM32_TARGET_INSTRUCTIONSET = OPTION_SUFFIX_ARM32_TARGET
			+ "instructionset";
	private static final String OPTION_SUFFIX_ARM32_TARGET_THUMB_INTERWORK = OPTION_SUFFIX_ARM32_TARGET
			+ "thumbinterwork";
	private static final String OPTION_SUFFIX_ARM32_TARGET_ENDIANNESS = OPTION_SUFFIX_ARM32_TARGET
			+ "endianness";
	private static final String OPTION_SUFFIX_ARM32_TARGET_FLOAT_ABI = OPTION_SUFFIX_ARM32_TARGET
			+ "fpu.abi";
	private static final String OPTION_SUFFIX_ARM32_TARGET_FLOAT_UNIT = OPTION_SUFFIX_ARM32_TARGET
			+ "fpu.unit";
	private static final String OPTION_SUFFIX_ARM32_TARGET_UNALIGNEDACCESS = OPTION_SUFFIX_ARM32_TARGET
			+ "unalignedaccess";

	private static final String OPTION_SUFFIX_TARGET_OTHER = OPTION_SUFFIX_TARGET
			+ "other";

	private static final String OPTION_SUFFIX_DEBUGGING = OPTION_SUFFIX
			+ "debugging.";

	private static final String OPTION_SUFFIX_DEBUGGING_LEVEL = OPTION_SUFFIX_DEBUGGING
			+ "level";
	private static final String OPTION_SUFFIX_DEBUGGING_FORMAT = OPTION_SUFFIX_DEBUGGING
			+ "format";
	private static final String OPTION_SUFFIX_DEBUGGING_PROF = OPTION_SUFFIX_DEBUGGING
			+ "prof";
	private static final String OPTION_SUFFIX_DEBUGGING_GPROF = OPTION_SUFFIX_DEBUGGING
			+ "gprof";
	private static final String OPTION_SUFFIX_DEBUGGING_OTHER = OPTION_SUFFIX_DEBUGGING
			+ "other";

	private String[] gatherFlags(ITool oTool, String[] asFlags) {

		// create a local list to gather options
		ArrayList<String> oList = new ArrayList<String>();

		// climb up the hierarchy and search for IToolChain
		Object oParent = oTool.getParent();
		while (oParent != null && !(oParent instanceof IToolChain)) {
			Object oSuper;
			oSuper = oTool.getSuperClass();
			if (oSuper != null && (oSuper instanceof ITool)) {
				oParent = ((ITool) oSuper).getParent();
			} else {
				oParent = null;
			}
		}

		if (oParent != null && (oParent instanceof IToolChain)) {
			IToolChain oToolChain = (IToolChain) oParent;
			// IConfiguration iconfiguration = itoolchain.getParent();

			// get the toolchain options, where common settings are
			IOption aoOptions[] = oToolChain.getOptions();

			if (DEBUG_LOCAL) {
				System.out.println();
				System.out.println(oTool.getName() + " tool of toolchain "
						+ oToolChain.getName());
			}

			String sTargetFamily = null;
			String sTargetArchitecture = null;
			String sTargetInstructionSet = null;
			String sTargetThumbInterwork = null;
			String sTargetProcessorEndianness = null;
			String sTargetFloatAbi = null;
			String sTargetFloatUnit = null;
			String sTargetUnalignedAccess = null;
			String sTargetOther = null;

			String sDebugLevel = null;
			String sDebugFormat = null;
			String sDebugOther = null;
			String sDebugProf = null;
			String sDebugGProf = null;

			for (IOption oOption : aoOptions) {
				if (oOption == null)
					continue;

				// check to see if the option has an applicability calculator
				IOptionApplicability applicabilityCalculator = oOption
						.getApplicabilityCalculator();
				IOptionCategory oCategory = oOption.getCategory();
				IOptionCategoryApplicability oCategoryApplicabilityCalculator = oCategory
						.getApplicabilityCalculator();

				IBuildObject oConfig = null;
				IBuildObject oToolParent = oTool.getParent();
				if (oToolParent instanceof IResourceConfiguration) {
					oConfig = oToolParent;
				} else if (oToolParent instanceof IToolChain) {
					oConfig = ((IToolChain) oToolParent).getParent();
				}

				if ((oCategoryApplicabilityCalculator == null || oCategoryApplicabilityCalculator
						.isOptionCategoryVisible(oConfig, oToolChain, oCategory))
						&& (applicabilityCalculator == null || applicabilityCalculator
								.isOptionUsedInCommandLine(oConfig, oToolChain,
										oOption))) {

					String sID = oOption.getId();
					Object oValue = oOption.getValue();
					String sCommand = oOption.getCommand();

					if (oValue instanceof String) {
						String sVal;
						try {
							sVal = oOption.getStringValue();
						} catch (BuildException e) {
							sVal = null;
						}

						String sEnumCommand;
						try {
							sEnumCommand = oOption.getEnumCommand(sVal);
						} catch (BuildException e1) {
							sEnumCommand = null;
						}

						if (DEBUG_LOCAL)
							System.out.println(oOption.getName() + " id=" + sID
									+ " val=" + sVal + " cmd=" + sCommand
									+ " enum=" + sEnumCommand);

						if (sID.endsWith(OPTION_SUFFIX_ARM32_TARGET_FAMILY)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_FAMILY
										+ ".") > 0) {
							sTargetFamily = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM32_TARGET_ARCHITECTURE)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_ARCHITECTURE
										+ ".") > 0) {
							sTargetArchitecture = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM32_TARGET_INSTRUCTIONSET)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_INSTRUCTIONSET
										+ ".") > 0) {
							sTargetInstructionSet = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM32_TARGET_ENDIANNESS)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_ENDIANNESS
										+ ".") > 0) {
							sTargetProcessorEndianness = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM32_TARGET_FLOAT_ABI)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_FLOAT_ABI
										+ ".") > 0) {
							sTargetFloatAbi = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM32_TARGET_FLOAT_UNIT)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_FLOAT_UNIT
										+ ".") > 0) {
							sTargetFloatUnit = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM32_TARGET_UNALIGNEDACCESS)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_UNALIGNEDACCESS
										+ ".") > 0) {
							sTargetUnalignedAccess = sEnumCommand;
						} else if (sID.endsWith(OPTION_SUFFIX_TARGET_OTHER)
								|| sID.indexOf(OPTION_SUFFIX_TARGET_OTHER + ".") > 0) {
							sTargetOther = sVal;
						} else if (sID.endsWith(OPTION_SUFFIX_DEBUGGING_LEVEL)
								|| sID.indexOf(OPTION_SUFFIX_DEBUGGING_LEVEL
										+ ".") > 0) {
							sDebugLevel = sEnumCommand;
						} else if (sID.endsWith(OPTION_SUFFIX_DEBUGGING_FORMAT)
								|| sID.indexOf(OPTION_SUFFIX_DEBUGGING_FORMAT
										+ ".") > 0) {
							sDebugFormat = sEnumCommand;
						} else if (sID.endsWith(OPTION_SUFFIX_DEBUGGING_OTHER)
								|| sID.indexOf(OPTION_SUFFIX_DEBUGGING_OTHER
										+ ".") > 0) {
							sDebugOther = sVal;
						}
					} else if (oValue instanceof Boolean) {
						boolean bVal;
						try {
							bVal = oOption.getBooleanValue();
						} catch (BuildException e) {
							bVal = false;
						}

						if (DEBUG_LOCAL)
							System.out.println(oOption.getName() + " id=" + sID
									+ " val=" + bVal + " cmd=" + sCommand);

						if (sID.endsWith(OPTION_SUFFIX_ARM32_TARGET_THUMB_INTERWORK)
								|| sID.indexOf(OPTION_SUFFIX_ARM32_TARGET_THUMB_INTERWORK
										+ ".") > 0) {
							if (bVal)
								sTargetThumbInterwork = sCommand;
						} else if (sID.endsWith(OPTION_SUFFIX_DEBUGGING_PROF)
								|| sID.indexOf(OPTION_SUFFIX_DEBUGGING_PROF
										+ ".") > 0) {
							if (bVal)
								sDebugProf = sCommand;
						} else if (sID.endsWith(OPTION_SUFFIX_DEBUGGING_GPROF)
								|| sID.indexOf(OPTION_SUFFIX_DEBUGGING_GPROF
										+ ".") > 0) {
							if (bVal)
								sDebugGProf = sCommand;
						}
					} else {
						if (true) // DEBUG_LOCAL)
							System.out.println(oOption.getName() + " " + sID
									+ " " + oValue + " " + sCommand
									+ " not processed");
					}
				}
			}

			if (true) {
				// ARM32
				if (sTargetFamily != null && sTargetFamily.length() > 0)
					oList.add(sTargetFamily);
				if (sTargetArchitecture != null
						&& sTargetArchitecture.length() > 0)
					oList.add(sTargetArchitecture);
				if (sTargetInstructionSet != null
						&& sTargetInstructionSet.length() > 0)
					oList.add(sTargetInstructionSet);
				if (sTargetThumbInterwork != null
						&& sTargetThumbInterwork.length() > 0)
					oList.add(sTargetThumbInterwork);
				if (sTargetProcessorEndianness != null
						&& sTargetProcessorEndianness.length() > 0)
					oList.add(sTargetProcessorEndianness);
				if (sTargetFloatAbi != null && sTargetFloatAbi.length() > 0) {
					oList.add(sTargetFloatAbi);

					if (sTargetFloatUnit != null
							&& sTargetFloatUnit.length() > 0)
						oList.add(sTargetFloatUnit);
				}

				if (sTargetUnalignedAccess != null
						&& sTargetUnalignedAccess.length() > 0)
					oList.add(sTargetUnalignedAccess);
			} else {
				// ARM64
			}
			if (sTargetOther != null && sTargetOther.length() > 0)
				oList.add(sTargetOther);

			if (sDebugLevel != null && sDebugLevel.length() > 0) {
				oList.add(sDebugLevel);

				if (sDebugFormat != null && sDebugFormat.length() > 0)
					oList.add(sDebugFormat);
			}
			if (sDebugOther != null && sDebugOther.length() > 0)
				oList.add(sDebugOther);
			if (sDebugProf != null && sDebugProf.length() > 0)
				oList.add(sDebugProf);
			if (sDebugGProf != null && sDebugGProf.length() > 0)
				oList.add(sDebugGProf);

		}

		// The initial options are added to the end of the list,
		// to give chance of individual tools to override common options
		oList.addAll(((java.util.Collection<String>) (Arrays
				.asList(((asFlags))))));

		// convert list to array of strings, as required by CDT
		return oList.toArray(new String[0]);
	}
}
