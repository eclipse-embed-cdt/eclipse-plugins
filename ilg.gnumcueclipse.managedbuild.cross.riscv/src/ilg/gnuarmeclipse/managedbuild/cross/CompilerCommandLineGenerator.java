package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineInfo;

@SuppressWarnings("restriction")
public class CompilerCommandLineGenerator extends ManagedCommandLineGenerator {

	// ------------------------------------------------------------------------

	@Override
	public IManagedCommandLineInfo generateCommandLineInfo(ITool tool, String commandName, String[] flags,
			String outputFlag, String outputPrefix, String outputName, String[] inputResources,
			String commandLinePattern) {
		IManagedCommandLineInfo lineInfo;
		lineInfo = super.generateCommandLineInfo(tool, commandName, flags, outputFlag, outputPrefix, outputName,
				inputResources, commandLinePattern);

		String newCommandLine = lineInfo.getCommandLine();
		newCommandLine = updateMT(newCommandLine);
		String newFlags = lineInfo.getFlags();
		newFlags = updateMT(newFlags);

		return new ManagedCommandLineInfo(newCommandLine, lineInfo.getCommandLinePattern(), lineInfo.getCommandName(),
				newFlags, lineInfo.getOutputFlag(), lineInfo.getOutputPrefix(), lineInfo.getOutput(),
				lineInfo.getInputs());
	}

	private String updateMT(String s) {
		return s.replace("-MT\"$(@:%.o=%.d)\"", "-MT\"$@\"");
	}

	// ------------------------------------------------------------------------
}
