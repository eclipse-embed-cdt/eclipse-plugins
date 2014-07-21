package ilg.gnuarmeclipse.packs.jobs;

import ilg.gnuarmeclipse.packs.core.Preferences;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CopyExampleDialog extends Dialog {

	private TreeSelection m_selection;

	private Text m_folderText;
	private String m_outputFolder;
	private String m_inputFolder;
	// private String m_exampleName;

	private Button m_browseButton;

	public CopyExampleDialog(Shell parentShell, TreeSelection selection) {

		super(parentShell);
		m_selection = selection;

		// Node exampleNode = (Node) selection.getFirstElement();
		// m_exampleName = exampleNode.getProperty(Property.EXAMPLE_NAME,
		// exampleNode.getName());

		String folderPath = Preferences.getPreferenceStore().getString(
				Preferences.PACKS_FOLDER_PATH);
		// m_inputFolder = folderPath + "/../Examples/" + m_exampleName;
		m_inputFolder = new Path(folderPath).append("../Examples/")
				.toOSString();

		m_outputFolder = m_inputFolder;
	}

	public String[] getData() {
		return new String[] { m_outputFolder };
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);

		String s = "Copy ";
		if (m_selection.size() == 1) {
			s += "example";
		} else {
			s += m_selection.size() + " examples";
		}

		s += " to folder";
		shell.setText(s);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite comp = new Composite(parent, SWT.NULL);
		comp.setFont(parent.getFont());

		GridLayout layout;
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginTop = 10;
		layout.marginBottom = 10;
		layout.marginWidth = 11;
		comp.setLayout(layout);

		GridData layoutData = new GridData();
		layoutData.widthHint = 600;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		comp.setLayoutData(layoutData);

		{
			Label folderLabel = new Label(comp, SWT.LEFT);
			// typeLabel.setFont(comp.getFont());
			folderLabel.setText("Folder:");
			layoutData = new GridData();
			folderLabel.setLayoutData(layoutData);

			m_folderText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			m_folderText.setText(m_inputFolder);
			layoutData = new GridData();
			layoutData.horizontalAlignment = SWT.FILL;
			layoutData.grabExcessHorizontalSpace = true;
			m_folderText.setLayoutData(layoutData);

			m_browseButton = new Button(comp, SWT.PUSH);
			m_browseButton.setText("Browse...");
			layoutData = new GridData();
			layoutData.verticalAlignment = SWT.CENTER;
			layoutData.grabExcessHorizontalSpace = false;
			layoutData.horizontalAlignment = SWT.FILL;
			// layoutData.minimumWidth = 80;
			m_browseButton.setLayoutData(layoutData);
		}

		// -----

		m_folderText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				m_outputFolder = m_folderText.getText().trim();
			}
		});

		m_browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				DirectoryDialog dialog = new DirectoryDialog(getShell(),
						SWT.SAVE);
				dialog.setText("Destination folder");
				String str = dialog.open();
				if (str != null) {
					// System.out.println(str);
					m_folderText.setText(str.trim());
				}
			}
		});

		return comp;
	}

}
