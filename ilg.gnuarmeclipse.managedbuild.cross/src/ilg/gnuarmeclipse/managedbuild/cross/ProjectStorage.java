package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

public class ProjectStorage {

	private static String PATH_ID = "path";

	public static String getPath(IConfiguration config) {

		IProject project = (IProject) config.getManagedProject().getOwner();

		String value;
		try {
			value = project.getPersistentProperty(new QualifiedName(config
					.getId(), PATH_ID));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		if (value == null)
			value = "";
		
		return value.trim();
	}

	public static boolean putPath(IConfiguration config, String value) {

		IProject project = (IProject) config.getManagedProject().getOwner();

		try {
			project.setPersistentProperty(new QualifiedName(config.getId(),
					PATH_ID), value.trim());
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
