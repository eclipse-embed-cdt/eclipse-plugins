package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFileInfo;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.IToolChain;

public class Utils {

	private static final String PROPERTY_OS_NAME = "os.name"; //$NON-NLS-1$

	public static boolean isPlatform(String sPlatform) {
		return (System.getProperty(PROPERTY_OS_NAME).toLowerCase()
				.startsWith(sPlatform));
	}

	/**
	 * Extracts a resource info from a build object. If no resource info can be
	 * found, it returns null.
	 * 
	 * @param configuration
	 * @return
	 */
	public static IResourceInfo getResourceInfo(IBuildObject configuration) {
		if (configuration instanceof IFolderInfo)
			return (IFolderInfo) configuration;
		if (configuration instanceof IFileInfo)
			return (IFileInfo) configuration;
		if (configuration instanceof IConfiguration)
			return ((IConfiguration) configuration).getRootFolderInfo();
		return null;
	}

	public static IConfiguration getConfiguration(IBuildObject configuration) {
		if (configuration instanceof IFolderInfo)
			return ((IFolderInfo) configuration).getParent();
		if (configuration instanceof IFileInfo)
			return ((IFileInfo) configuration).getParent();
		if (configuration instanceof IConfiguration)
			return (IConfiguration) configuration;
		return null;
	}

	public static IConfiguration getConfiguration(IHoldsOptions holder) {
		if (holder instanceof IToolChain)
			return ((IToolChain) holder).getParent();
		return null;
	}

	public static void updateOptions(IConfiguration config, int toolchainIndex)
			throws BuildException {

		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		ToolchainDefinition td = ToolchainDefinition
				.getToolchain(toolchainIndex);

		// Do NOT use ManagedBuildManager.setOption() to avoid sending
		// events to the option. Also do not use option.setValue()
		// since this does not propagate notifications and the
		// values are not saved to .cproject.
		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getName());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getPrefix());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getSuffix());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdC());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdCpp());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdAr());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjcopy());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjdump());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdSize());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdMake());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdRm());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); //$NON-NLS-1$
		// compose the architecture ID
		String sArchitecture = td.getArchitecture();
		val = Option.OPTION_ARCHITECTURE + "." + sArchitecture;
		config.setOption(toolchain, option, val);

		if ("arm".equals(sArchitecture)) {

			option = toolchain.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_FAMILY);
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.arm.mcpu.cortex-m3");

			option = toolchain.getOptionBySuperClassId(Activator
					.getOptionPrefix() + ".base.arm.target.instructionset");
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.arm.target.instructionset.thumb");

		} else if ("aarch64".equals(sArchitecture)) {
			option = toolchain.getOptionBySuperClassId(Option.OPTION_AARCH64_TARGET_FAMILY);
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.aarch64.target.mcpu.generic");

			option = toolchain.getOptionBySuperClassId(Activator
					.getOptionPrefix() + ".base.aarch64.target.feature.simd");
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.aarch64.target.cmodel.small");

			option = toolchain.getOptionBySuperClassId(Activator
					.getOptionPrefix() + ".base.aarch64.target.cmodel");
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.aarch64.target.cmodel.small");

		}
	}
}
