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

	public static void updateOptions(IConfiguration config,
			int toolchainIndex) throws BuildException {

		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		ToolchainDefinition td = ToolchainDefinition
				.getToolchain(toolchainIndex);

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".toolchain.name"); //$NON-NLS-1$
		// Do NOT use ManagedBuildManager.setOption() to avoid sending
		// events to the option. Also do not use option.setValue()
		// since this does not propagate notifications and the
		// values are not saved to .cproject.
		config.setOption(toolchain, option, td.getName());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.prefix"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getPrefix());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.suffix"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getSuffix());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.c"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdC());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.cpp"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdCpp());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.ar"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdAr());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.objcopy"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjcopy());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.objdump"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjdump());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.size"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdSize());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.make"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdMake());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".command.rm"); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdRm());

		option = toolchain.getOptionBySuperClassId(Activator.getOptionPrefix()
				+ ".family"); //$NON-NLS-1$
		// compose the family ID
		String sFamily = td.getFamily();
		val = Activator.getOptionPrefix() + ".family." + sFamily;
		config.setOption(toolchain, option, val);

		if ("arm".equals(sFamily)) {
			
			option = toolchain.getOptionBySuperClassId(Activator
					.getOptionPrefix() + ".base.arm.target.family");
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.arm.mcpu.cortex-m3");

			option = toolchain.getOptionBySuperClassId(Activator
					.getOptionPrefix() + ".base.arm.target.instructionset");
			config.setOption(toolchain, option, Activator.getOptionPrefix()
					+ ".base.arm.target.instructionset.thumb");

		} else if ("aarch64".equals(sFamily)) {
			option = toolchain.getOptionBySuperClassId(Activator
					.getOptionPrefix() + ".base.aarch64.target.family");
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
