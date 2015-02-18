package ilg.gnuarmeclipse.managedbuild.cross.properties;

import ilg.gnuarmeclipse.core.ui.FieldEditorPropertyPage;
import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.ui.PersistentPreferences;
import ilg.gnuarmeclipse.managedbuild.cross.ui.Messages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ProjectToolsPathPropertyPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public ProjectToolsPathPropertyPage() {
		super(GRID);

		setDescription(Messages.ProjectToolsPathsPropertyPage_description);
	}

	// ------------------------------------------------------------------------

	protected IPreferenceStore doGetPreferenceStore() {
		Object element = getElement();
		if (element instanceof IProject) {
			return new ScopedPreferenceStore(new ProjectScope(
					(IProject) element), Activator.PLUGIN_ID);
		}
		return null;
	}

	@Override
	protected void createFieldEditors() {

		FieldEditor buildTooslPath = new DirectoryFieldEditor(
				PersistentPreferences.BUILD_TOOLS_PATH, Messages.ToolsPaths_label,
				getFieldEditorParent());
		addField(buildTooslPath);

	}

	// ------------------------------------------------------------------------
}