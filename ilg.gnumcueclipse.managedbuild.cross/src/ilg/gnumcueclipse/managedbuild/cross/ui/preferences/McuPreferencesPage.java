package ilg.gnumcueclipse.managedbuild.cross.ui.preferences;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ilg.gnumcueclipse.managedbuild.cross.ui.Messages;

public class McuPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

	public McuPreferencesPage() {
		super();
		// setPreferenceStore(CUIPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.McuPropertiesPage_description);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		container.setLayout(layout);

		Dialog.applyDialogFont(container);
		return container;
	}
}
