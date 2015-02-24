package ilg.gnuarmeclipse.managedbuild.cross.properties;

import ilg.gnuarmeclipse.core.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnuarmeclipse.core.ui.FieldEditorPropertyPage;
import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.ui.Messages;
import ilg.gnuarmeclipse.managedbuild.cross.ui.PersistentPreferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;

public class ProjectToolsPathPropertyPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public ProjectToolsPathPropertyPage() {
		super(GRID);

		String toolchainName = PersistentPreferences.getToolchainName();
		setDescription(String.format(
				Messages.ProjectToolsPathsPropertyPage_description,
				toolchainName));
	}

	// ------------------------------------------------------------------------

	protected IPreferenceStore doGetPreferenceStore() {
		Object element = getElement();
		if (element instanceof IProject) {
			return new ScopedPreferenceStoreWithoutDefaults(new ProjectScope(
					(IProject) element), Activator.PLUGIN_ID);
		}
		return null;
	}

	@Override
	protected void createFieldEditors() {

		FieldEditor buildToolsPathField = new DirectoryFieldEditor(
				PersistentPreferences.BUILD_TOOLS_PATH_KEY,
				Messages.ToolsPaths_label, getFieldEditorParent());
		addField(buildToolsPathField);

		String toolchainName = PersistentPreferences.getToolchainName();
		String key = PersistentPreferences.getToolchainKey(toolchainName);
		FieldEditor toolchainPathField = new DirectoryFieldEditor(key,
				Messages.ToolchainPaths_label, getFieldEditorParent());
		addField(toolchainPathField);
	}

	// ------------------------------------------------------------------------
}
