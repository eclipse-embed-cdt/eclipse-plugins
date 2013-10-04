package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class PathManagedOptionValueHandler extends ManagedOptionValueHandler {

	@Override
	public boolean handleValue(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument,
			int event) {

		ManagedOptionValueHandlerDebug.dump(configuration, holder, option,
				extraArgument, event);

		if (event == EVENT_OPEN) {

			IConfiguration config = Utils.getConfiguration(configuration);
			String path = getPersistent(config);
			if (path != null && path.length() > 0) {
				// do not overwrite the .cproject definition if the
				// workspace definition is not useful
				IOption optionToSet;
				try {
					optionToSet = holder.getOptionToSet(option, false);
					optionToSet.setValue(path);
				} catch (BuildException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if (event == EVENT_APPLY) {
			// Clear discovered includes and macros, to make room for
			// new ones
			SpecsProvider.clear();

			// save (quite often to my taste) the value
			String path = (String) option.getValue();
			IConfiguration config = Utils.getConfiguration(configuration);
			putPersistent(config, path);

			// the event was handled
			return true;
		}

		return false;
	}

	public static String getPersistent(IConfiguration config) {
		IProject project = (IProject) config.getManagedProject().getOwner();

		String value;
		try {
			value = project.getPersistentProperty(new QualifiedName(config
					.getId(), "path"));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return value;
	}

	public static boolean putPersistent(IConfiguration config, String value) {
		IProject project = (IProject) config.getManagedProject().getOwner();

		try {
			project.setPersistentProperty(new QualifiedName(config.getId(),
					"path"), value);
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
