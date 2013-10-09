package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;

public class Option {

	private static final String OPTION_PREFIX = Activator.getOptionPrefix();

	private static final String OPTION_FAMILY = OPTION_PREFIX + ".family";

	private static final String FAMILY_ARM = "arm";
	private static final String FAMILY_AARCH64 = "aarch64";

	private static final String OPTION_TARGET = OPTION_PREFIX + ".target.";

	private static final String OPTION_ARM_TARGET = OPTION_PREFIX + "."
			+ FAMILY_ARM + ".target.";
	private static final String OPTION_ARM_TARGET_FAMILY = OPTION_ARM_TARGET
			+ "family";
	private static final String OPTION_ARM_TARGET_ARCHITECTURE = OPTION_ARM_TARGET
			+ "architecture";
	private static final String OPTION_ARM_TARGET_INSTRUCTIONSET = OPTION_ARM_TARGET
			+ "instructionset";
	private static final String OPTION_ARM_TARGET_THUMB_INTERWORK = OPTION_ARM_TARGET
			+ "thumbinterwork";
	private static final String OPTION_ARM_TARGET_ENDIANNESS = OPTION_ARM_TARGET
			+ "endianness";
	private static final String OPTION_ARM_TARGET_FLOAT_ABI = OPTION_ARM_TARGET
			+ "fpu.abi";
	private static final String OPTION_ARM_TARGET_FLOAT_UNIT = OPTION_ARM_TARGET
			+ "fpu.unit";
	private static final String OPTION_ARM_TARGET_UNALIGNEDACCESS = OPTION_ARM_TARGET
			+ "unalignedaccess";

	private static final String OPTION_AARCH64_TARGET = OPTION_PREFIX + "."
			+ FAMILY_AARCH64 + ".target.";
	private static final String OPTION_AARCH64_TARGET_FAMILY = OPTION_AARCH64_TARGET
			+ "family";
	private static final String OPTION_AARCH64_TARGET_FEATURE_CRC = OPTION_AARCH64_TARGET
			+ "feature.crc";
	private static final String OPTION_AARCH64_TARGET_FEATURE_CRYPTO = OPTION_AARCH64_TARGET
			+ "feature.crypto";
	private static final String OPTION_AARCH64_TARGET_FEATURE_FP = OPTION_AARCH64_TARGET
			+ "feature.fp";
	private static final String OPTION_AARCH64_TARGET_FEATURE_SIMD = OPTION_AARCH64_TARGET
			+ "feature.simd";
	private static final String OPTION_AARCH64_TARGET_CMODEL = OPTION_AARCH64_TARGET
			+ "cmodel";
	private static final String OPTION_AARCH64_TARGET_STRICTALIGN = OPTION_AARCH64_TARGET
			+ "strictalign";

	private static final String OPTION_TARGET_OTHER = OPTION_TARGET + "other";


	private static final String OPTION_OPTIMIZATION = OPTION_PREFIX + ".optimization.";

	private static final String OPTION_OPTIMIZATION_LEVEL = OPTION_OPTIMIZATION
			+ "level";
	private static final String OPTION_OPTIMIZATION_MESSAGELENGTH = OPTION_OPTIMIZATION
			+ "messagelength";
	private static final String OPTION_OPTIMIZATION_SIGNEDCHAR = OPTION_OPTIMIZATION
			+ "signedchar";
	private static final String OPTION_OPTIMIZATION_FUNCTIONSECTIONS = OPTION_OPTIMIZATION
			+ "functionsections";
	private static final String OPTION_OPTIMIZATION_DATASECTIONS = OPTION_OPTIMIZATION
			+ "datasections";
	private static final String OPTION_OPTIMIZATION_NOUSECXAATEXIT = OPTION_OPTIMIZATION
			+ "nousecxaatexit";
	private static final String OPTION_OPTIMIZATION_NOCOMMON = OPTION_OPTIMIZATION
			+ "nocommon";
	private static final String OPTION_OPTIMIZATION_NOINLINEFUNCTIONS = OPTION_OPTIMIZATION
			+ "noinlinefunctions";
	private static final String OPTION_OPTIMIZATION_FREESTANDING = OPTION_OPTIMIZATION
			+ "freestanding";
	private static final String OPTION_OPTIMIZATION_NOBUILTIN = OPTION_OPTIMIZATION
			+ "nobuiltin";
	private static final String OPTION_OPTIMIZATION_SPCONSTANT = OPTION_OPTIMIZATION
			+ "spconstant";
	private static final String OPTION_OPTIMIZATION_PIC = OPTION_OPTIMIZATION
			+ "PIC";
	private static final String OPTION_OPTIMIZATION_OTHER = OPTION_OPTIMIZATION
			+ "other";

	private static final String OPTION_WARNINGS = OPTION_PREFIX + ".warnings.";

	private static final String OPTION_WARNINGS_SYNTAXONLY = OPTION_WARNINGS
			+ "syntaxonly";
	private static final String OPTION_WARNINGS_PEDANTIC = OPTION_WARNINGS
			+ "pedantic";
	private static final String OPTION_WARNINGS_PEDANTICERRORS = OPTION_WARNINGS
			+ "pedanticerrors";
	private static final String OPTION_WARNINGS_ALLWARN = OPTION_WARNINGS
			+ "allwarn";
	private static final String OPTION_WARNINGS_NOWARN = OPTION_WARNINGS
			+ "nowarn";
	private static final String OPTION_WARNINGS_EXTRAWARN = OPTION_WARNINGS
			+ "extrawarn";
	private static final String OPTION_WARNINGS_CONVERSION = OPTION_WARNINGS
			+ "conversion";
	private static final String OPTION_WARNINGS_UNITIALIZED = OPTION_WARNINGS
			+ "unitialized";
	private static final String OPTION_WARNINGS_FLOATEQUAL = OPTION_WARNINGS
			+ "floatequal";
	private static final String OPTION_WARNINGS_SHADOW = OPTION_WARNINGS
			+ "shadow";
	private static final String OPTION_WARNINGS_POINTERARITH = OPTION_WARNINGS
			+ "pointerarith";
	private static final String OPTION_WARNINGS_BADFUNCTIONCAST = OPTION_WARNINGS
			+ "badfunctioncast";
	private static final String OPTION_WARNINGS_LOGICALOP = OPTION_WARNINGS
			+ "logicalop";
	private static final String OPTION_WARNINGS_AGREGGATERETURN = OPTION_WARNINGS
			+ "agreggatereturn";
	private static final String OPTION_WARNINGS_MISSINGDECLARATION = OPTION_WARNINGS
			+ "missingdeclaration";
	private static final String OPTION_WARNINGS_TOERRORS = OPTION_WARNINGS
			+ "toerrors";
	private static final String OPTION_WARNINGS_OTHER = OPTION_WARNINGS
			+ "other";

	private static final String OPTION_DEBUGGING = OPTION_PREFIX
			+ ".debugging.";

	private static final String OPTION_DEBUGGING_LEVEL = OPTION_DEBUGGING
			+ "level";
	private static final String OPTION_DEBUGGING_FORMAT = OPTION_DEBUGGING
			+ "format";
	private static final String OPTION_DEBUGGING_PROF = OPTION_DEBUGGING
			+ "prof";
	private static final String OPTION_DEBUGGING_GPROF = OPTION_DEBUGGING
			+ "gprof";
	private static final String OPTION_DEBUGGING_OTHER = OPTION_DEBUGGING
			+ "other";


	public static String getOptionStringValue(IConfiguration config,
			String sOptionId) {

		IOption option = config.getToolChain().getOptionBySuperClassId(
				sOptionId);
		String sReturn = null;
		if (option != null) {
			try {
				sReturn = option.getStringValue().trim();
			} catch (BuildException e) {
				System.out.println(sOptionId + " not value");
			}
		} else {
			System.out.println(sOptionId + " not found");
		}

		return sReturn;
	}

	public static String getOptionEnumCommand(IConfiguration config,
			String sOptionId) {

		IOption option = config.getToolChain().getOptionBySuperClassId(
				sOptionId);
		String sReturn = null;
		if (option != null) {
			try {
				String sEnumId = option.getStringValue();
				sReturn = option.getEnumCommand(sEnumId).trim();
			} catch (BuildException e) {
				System.out.println(sOptionId + " not value");
			}
		} else {
			System.out.println(sOptionId + " not found");
		}

		return sReturn;
	}

	public static Boolean getOptionBooleanValue(IConfiguration config,
			String sOptionId) {

		IOption option = config.getToolChain().getOptionBySuperClassId(
				sOptionId);
		Boolean bReturn = null;
		if (option != null) {
			try {
				bReturn = new Boolean(option.getBooleanValue());
			} catch (BuildException e) {
				System.out.println(sOptionId + " not value");
			}
		} else {
			System.out.println(sOptionId + " not found");
		}

		return bReturn;
	}

	public static String getOptionBooleanCommand(IConfiguration config,
			String sOptionId) {

		IOption option = config.getToolChain().getOptionBySuperClassId(
				sOptionId);
		String sReturn = null;
		if (option != null) {
			try {
				if (option.getBooleanValue())
					sReturn = option.getCommand().trim();
			} catch (BuildException e) {
				System.out.println(sOptionId + " not value");
			}
		} else {
			System.out.println(sOptionId + " not found");
		}

		return sReturn;
	}

	private static String getArmTargetFlags(IConfiguration config) {

		String sReturn = "";
		String sValue;

		sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_FAMILY);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_ARCHITECTURE);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_INSTRUCTIONSET);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config,
				OPTION_ARM_TARGET_THUMB_INTERWORK);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_ENDIANNESS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_FLOAT_ABI);
		if (sValue != null && sValue.length() > 0) {
			sReturn += " " + sValue;

			sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_FLOAT_UNIT);
			if (sValue != null && sValue.length() > 0)
				sReturn += " " + sValue;
		}

		sValue = getOptionEnumCommand(config, OPTION_ARM_TARGET_UNALIGNEDACCESS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

	private static String getAarch64TargetFlags(IConfiguration config) {

		String sReturn = "";
		String sValue;

		sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_FAMILY);
		if (sValue != null && sValue.length() > 0) {
			sReturn += " " + sValue;

			sValue = getOptionEnumCommand(config,
					OPTION_AARCH64_TARGET_FEATURE_CRC);
			if (sValue != null && sValue.length() > 0)
				sReturn += "+" + sValue;

			sValue = getOptionEnumCommand(config,
					OPTION_AARCH64_TARGET_FEATURE_CRYPTO);
			if (sValue != null && sValue.length() > 0)
				sReturn += "+" + sValue;

			sValue = getOptionEnumCommand(config,
					OPTION_AARCH64_TARGET_FEATURE_FP);
			if (sValue != null && sValue.length() > 0)
				sReturn += "+" + sValue;

			sValue = getOptionEnumCommand(config,
					OPTION_AARCH64_TARGET_FEATURE_SIMD);
			if (sValue != null && sValue.length() > 0)
				sReturn += "+" + sValue;

		}

		sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_CMODEL);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config,
				OPTION_AARCH64_TARGET_STRICTALIGN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

	private static String getOptimizationFlags(IConfiguration config) {

		String sReturn = "";
		String sValue;
		if (sReturn != null)
			sReturn = sReturn.trim();

		sValue = getOptionEnumCommand(config, OPTION_OPTIMIZATION_LEVEL);
		if (sValue != null && sValue.length() > 0) {
			sReturn += " " + sValue;
		}

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_MESSAGELENGTH);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_SIGNEDCHAR);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_FUNCTIONSECTIONS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_DATASECTIONS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_NOUSECXAATEXIT);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_NOCOMMON);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_NOINLINEFUNCTIONS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_FREESTANDING);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_NOBUILTIN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_SPCONSTANT);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_OPTIMIZATION_PIC);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionStringValue(config, OPTION_OPTIMIZATION_OTHER);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

	private static String getWarningFlags(IConfiguration config) {

		String sReturn = "";
		String sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_SYNTAXONLY);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_PEDANTIC);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_PEDANTICERRORS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_ALLWARN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_EXTRAWARN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_CONVERSION);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_UNITIALIZED);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_FLOATEQUAL);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_SHADOW);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_POINTERARITH);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config,
				OPTION_WARNINGS_BADFUNCTIONCAST);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_LOGICALOP);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config,
				OPTION_WARNINGS_AGREGGATERETURN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config,
				OPTION_WARNINGS_MISSINGDECLARATION);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_TOERRORS);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_WARNINGS_NOWARN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionStringValue(config, OPTION_WARNINGS_OTHER);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

	private static String getDebuggingFlags(IConfiguration config) {

		String sReturn = "";
		String sValue;
		if (sReturn != null)
			sReturn = sReturn.trim();

		sValue = getOptionEnumCommand(config, OPTION_DEBUGGING_LEVEL);
		if (sValue != null && sValue.length() > 0) {
			sReturn += " " + sValue;

			sValue = getOptionEnumCommand(config, OPTION_DEBUGGING_FORMAT);
			if (sValue != null && sValue.length() > 0)
				sReturn += " " + sValue;
		}

		sValue = getOptionStringValue(config, OPTION_DEBUGGING_OTHER);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_DEBUGGING_PROF);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_DEBUGGING_GPROF);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		return sReturn;
	}

	public static String getToolChainFlags(IConfiguration config) {
		String sFamilyId = getOptionStringValue(config, OPTION_FAMILY);
		String sReturn = "";
		String sValue;

		if (sFamilyId != null) {
			sValue = null;
			if (sFamilyId.endsWith("." + FAMILY_ARM)) {
				sValue = getArmTargetFlags(config);
			} else if (sFamilyId.endsWith("." + FAMILY_ARM)) {
				sValue = getAarch64TargetFlags(config);
			}
			if (sValue != null && sValue.length() > 0)
				sReturn += " " + sValue;
		}

		sValue = getOptionStringValue(config, OPTION_TARGET_OTHER);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptimizationFlags(config);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getWarningFlags(config);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getDebuggingFlags(config);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

}
