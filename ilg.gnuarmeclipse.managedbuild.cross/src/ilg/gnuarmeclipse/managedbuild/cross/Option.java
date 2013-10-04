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

	public static String getArmTargetOptions(IConfiguration config) {
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

	public static String getAarch64TargetOptions(IConfiguration config) {
		String sReturn = "";
		String sValue;

		sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_FAMILY);
		if (sValue != null && sValue.length() > 0) {
			sReturn += " " + sValue;

			sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_FEATURE_CRC);
			if (sValue != null && sValue.length() > 0) 
				sReturn += "+" + sValue;

			sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_FEATURE_CRYPTO);
			if (sValue != null && sValue.length() > 0) 
				sReturn += "+" + sValue;

			sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_FEATURE_FP);
			if (sValue != null && sValue.length() > 0) 
				sReturn += "+" + sValue;

			sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_FEATURE_SIMD);
			if (sValue != null && sValue.length() > 0) 
				sReturn += "+" + sValue;

		}
		
		sValue = getOptionEnumCommand(config, OPTION_AARCH64_TARGET_CMODEL);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		sValue = getOptionBooleanCommand(config, OPTION_AARCH64_TARGET_STRICTALIGN);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

	public static String getToolChainFlags(IConfiguration config) {
		String sFamilyId = getOptionStringValue(config, OPTION_FAMILY);
		String sReturn = "";
		String sValue;

		if (sFamilyId != null) {
			sValue = null;
			if (sFamilyId.endsWith("." + FAMILY_ARM)) {
				sValue = getArmTargetOptions(config);
			} else if (sFamilyId.endsWith("." + FAMILY_ARM)) {
				sValue = getAarch64TargetOptions(config);
			}
			if (sValue != null && sValue.length() > 0)
				sReturn += " " + sValue;
		}

		sValue = getOptionStringValue(config, OPTION_TARGET_OTHER);
		if (sValue != null && sValue.length() > 0)
			sReturn += " " + sValue;

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

		if (sReturn != null)
			sReturn = sReturn.trim();

		return sReturn;
	}

}
