package ilg.gnuarmeclipse.managedbuild.cross;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.macros.BuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IConfigurationBuildMacroSupplier;

public class ConfigurationBuildMacroSupplier implements
		IConfigurationBuildMacroSupplier {

	@Override
	public IBuildMacro getMacro(String macroName, IConfiguration configuration,
			IBuildMacroProvider provider) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBuildMacro[] getMacros(IConfiguration configuration,
			IBuildMacroProvider provider) {

		ArrayList<IBuildMacro> oMacrosList = new ArrayList<IBuildMacro>();

		IToolChain toolchain = configuration.getToolChain();

		String[] cmds = { "prefix", "suffix", "c", "cpp", "ar", "objcopy",
				"objdump", "size", "make", "rm" };
		
		for (String cmd : cmds) {
			String sId = Activator.getOptionPrefix() + ".command." + cmd;

			IOption option = toolchain.getOptionBySuperClassId(sId); //$NON-NLS-1$
			if (option != null) {
				String sVal = (String) option.getValue();

				oMacrosList.add(new BuildMacro("cross_" + cmd,
						BuildMacro.VALUE_TEXT, sVal));
			}
		}

		return oMacrosList.toArray(new IBuildMacro[0]);
	}

}
