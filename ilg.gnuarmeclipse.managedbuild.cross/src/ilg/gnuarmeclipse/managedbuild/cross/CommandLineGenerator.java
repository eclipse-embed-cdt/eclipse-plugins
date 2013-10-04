/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

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

	private static final String OPTION_SUFFIX_FAMILY = OPTION_SUFFIX + "family";

	private static final String FAMILY_ARM = "arm";
	private static final String FAMILY_AARCH64 = "aarch64";

	private static final String OPTION_SUFFIX_TARGET = OPTION_SUFFIX
			+ "target.";

	private static final String OPTION_SUFFIX_ARM_TARGET = OPTION_SUFFIX
			+ FAMILY_ARM + ".target.";
	private static final String OPTION_SUFFIX_ARM_TARGET_FAMILY = OPTION_SUFFIX_ARM_TARGET
			+ "family";
	private static final String OPTION_SUFFIX_ARM_TARGET_ARCHITECTURE = OPTION_SUFFIX_ARM_TARGET
			+ "architecture";
	private static final String OPTION_SUFFIX_ARM_TARGET_INSTRUCTIONSET = OPTION_SUFFIX_ARM_TARGET
			+ "instructionset";
	private static final String OPTION_SUFFIX_ARM_TARGET_THUMB_INTERWORK = OPTION_SUFFIX_ARM_TARGET
			+ "thumbinterwork";
	private static final String OPTION_SUFFIX_ARM_TARGET_ENDIANNESS = OPTION_SUFFIX_ARM_TARGET
			+ "endianness";
	private static final String OPTION_SUFFIX_ARM_TARGET_FLOAT_ABI = OPTION_SUFFIX_ARM_TARGET
			+ "fpu.abi";
	private static final String OPTION_SUFFIX_ARM_TARGET_FLOAT_UNIT = OPTION_SUFFIX_ARM_TARGET
			+ "fpu.unit";
	private static final String OPTION_SUFFIX_ARM_TARGET_UNALIGNEDACCESS = OPTION_SUFFIX_ARM_TARGET
			+ "unalignedaccess";

	private static final String OPTION_SUFFIX_AARCH64_TARGET = OPTION_SUFFIX
			+ FAMILY_AARCH64 + ".target.";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_FAMILY = OPTION_SUFFIX_AARCH64_TARGET
			+ "family";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_FEATURE_CRC = OPTION_SUFFIX_AARCH64_TARGET
			+ "feature.crc";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_FEATURE_CRYPTO = OPTION_SUFFIX_AARCH64_TARGET
			+ "feature.crypto";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_FEATURE_FP = OPTION_SUFFIX_AARCH64_TARGET
			+ "feature.fp";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_FEATURE_SIMD = OPTION_SUFFIX_AARCH64_TARGET
			+ "feature.simd";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_CMODEL = OPTION_SUFFIX_AARCH64_TARGET
			+ "cmodel";
	private static final String OPTION_SUFFIX_AARCH64_TARGET_STRICTALIGN = OPTION_SUFFIX_AARCH64_TARGET
			+ "strictalign";

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

			String sTarget32Family = null;
			String sTarget32Architecture = null;
			String sTarget32InstructionSet = null;
			String sTarget32ThumbInterwork = null;
			String sTarget32ProcessorEndianness = null;
			String sTarget32FloatAbi = null;
			String sTarget32FloatUnit = null;
			String sTarget32UnalignedAccess = null;

			String sTarget64Family = null;
			String sTarget64FeatureCrc = null;
			String sTarget64FeatureCrypto = null;
			String sTarget64FeatureFp = null;
			String sTarget64FeatureSimd = null;
			String sTarget64Cmodel = null;
			String sTarget64StrictAlign = null;

			String sTargetOther = null;

			String sDebugLevel = null;
			String sDebugFormat = null;
			String sDebugOther = null;
			String sDebugProf = null;
			String sDebugGProf = null;

			boolean isArm32 = false;
			boolean isArm64 = false;

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

						if (sID.endsWith(OPTION_SUFFIX_FAMILY)
								|| sID.indexOf(OPTION_SUFFIX_FAMILY + ".") > 0) {
							if (sVal.endsWith("." + FAMILY_ARM))
								isArm32 = true;
							else if (sVal.endsWith("." + FAMILY_AARCH64))
								isArm64 = true;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_FAMILY)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_FAMILY
										+ ".") > 0) {
							sTarget32Family = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_ARCHITECTURE)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_ARCHITECTURE
										+ ".") > 0) {
							sTarget32Architecture = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_INSTRUCTIONSET)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_INSTRUCTIONSET
										+ ".") > 0) {
							sTarget32InstructionSet = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_ENDIANNESS)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_ENDIANNESS
										+ ".") > 0) {
							sTarget32ProcessorEndianness = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_FLOAT_ABI)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_FLOAT_ABI
										+ ".") > 0) {
							sTarget32FloatAbi = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_FLOAT_UNIT)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_FLOAT_UNIT
										+ ".") > 0) {
							sTarget32FloatUnit = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_ARM_TARGET_UNALIGNEDACCESS)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_UNALIGNEDACCESS
										+ ".") > 0) {
							sTarget32UnalignedAccess = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_FAMILY)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_FAMILY
										+ ".") > 0) {
							sTarget64Family = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_CRC)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_CRC
										+ ".") > 0) {
							sTarget64FeatureCrc = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_CRYPTO)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_CRYPTO
										+ ".") > 0) {
							sTarget64FeatureCrypto = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_FP)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_FP
										+ ".") > 0) {
							sTarget64FeatureFp = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_SIMD)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_FEATURE_SIMD
										+ ".") > 0) {
							sTarget64FeatureSimd = sEnumCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_CMODEL)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_CMODEL
										+ ".") > 0) {
							sTarget64Cmodel = sEnumCommand;

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

						if (sID.endsWith(OPTION_SUFFIX_ARM_TARGET_THUMB_INTERWORK)
								|| sID.indexOf(OPTION_SUFFIX_ARM_TARGET_THUMB_INTERWORK
										+ ".") > 0) {
							if (bVal)
								sTarget32ThumbInterwork = sCommand;
						} else if (sID
								.endsWith(OPTION_SUFFIX_AARCH64_TARGET_STRICTALIGN)
								|| sID.indexOf(OPTION_SUFFIX_AARCH64_TARGET_STRICTALIGN
										+ ".") > 0) {
							if (bVal)
								sTarget64StrictAlign = sCommand;
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

			if (isArm32) {
				// ARM32
				if (sTarget32Family != null && sTarget32Family.length() > 0)
					oList.add(sTarget32Family);
				if (sTarget32Architecture != null
						&& sTarget32Architecture.length() > 0)
					oList.add(sTarget32Architecture);
				if (sTarget32InstructionSet != null
						&& sTarget32InstructionSet.length() > 0)
					oList.add(sTarget32InstructionSet);
				if (sTarget32ThumbInterwork != null
						&& sTarget32ThumbInterwork.length() > 0)
					oList.add(sTarget32ThumbInterwork);
				if (sTarget32ProcessorEndianness != null
						&& sTarget32ProcessorEndianness.length() > 0)
					oList.add(sTarget32ProcessorEndianness);
				if (sTarget32FloatAbi != null && sTarget32FloatAbi.length() > 0) {
					oList.add(sTarget32FloatAbi);

					if (sTarget32FloatUnit != null
							&& sTarget32FloatUnit.length() > 0)
						oList.add(sTarget32FloatUnit);
				}

				if (sTarget32UnalignedAccess != null
						&& sTarget32UnalignedAccess.length() > 0)
					oList.add(sTarget32UnalignedAccess);
			} else if (isArm64) {
				// ARM64
				if (sTarget64Family != null && sTarget64Family.length() > 0) {
					String s = sTarget64Family;

					if (sTarget64FeatureCrc != null
							&& sTarget64FeatureCrc.length() > 0)
						s += "+" + sTarget64FeatureCrc;
					if (sTarget64FeatureCrypto != null
							&& sTarget64FeatureCrypto.length() > 0)
						s += "+" + sTarget64FeatureCrypto;
					if (sTarget64FeatureFp != null
							&& sTarget64FeatureFp.length() > 0)
						s += "+" + sTarget64FeatureFp;
					if (sTarget64FeatureSimd != null
							&& sTarget64FeatureSimd.length() > 0)
						s += "+" + sTarget64FeatureSimd;

					oList.add(s);
				}

				if (sTarget64Cmodel != null && sTarget64Cmodel.length() > 0)
					oList.add(sTarget64Cmodel);

				if (sTarget64StrictAlign != null
						&& sTarget64StrictAlign.length() > 0)
					oList.add(sTarget64StrictAlign);

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
