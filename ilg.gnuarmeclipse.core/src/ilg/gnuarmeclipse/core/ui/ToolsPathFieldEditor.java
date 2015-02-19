package ilg.gnuarmeclipse.core.ui;

import ilg.gnuarmeclipse.core.ScopedPreferenceStoreWithoutDefaults;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Custom field editor, that has a special processing of the default value.
 *
 */
public class ToolsPathFieldEditor extends DirectoryFieldEditor {

	// ------------------------------------------------------------------------

	public ToolsPathFieldEditor(String buildToolsPath, String toolsPaths_label,
			Composite fieldEditorParent) {
		super(buildToolsPath, toolsPaths_label, fieldEditorParent);
	}

	// ------------------------------------------------------------------------

	@Override
	protected void doLoadDefault() {

		if (getTextControl() != null) {
			String value = ((ScopedPreferenceStoreWithoutDefaults) getPreferenceStore())
					.getDefaultStringSuper(getPreferenceName());
			getTextControl().setText(value);
		}
		valueChanged();

		setPresentsDefaultValue(false);
	}

	// ------------------------------------------------------------------------
}
