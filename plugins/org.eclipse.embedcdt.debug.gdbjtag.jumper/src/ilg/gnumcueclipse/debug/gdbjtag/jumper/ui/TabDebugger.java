/*******************************************************************************
 * Copyright (c) 2007, 2012 QNX Software Systems and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Andy Jin - Hardware debugging UI improvements, bug 229946
 *     Anna Dushistova (MontaVista) - bug 241279 
 *              - Hardware Debugging: Host name or ip address not saving in 
 *                the debug configuration
 *     Andy Jin (QNX) - Added DSF debugging, bug 248593
 *     Bruce Griffith, Sage Electronic Engineering, LLC - bug 305943
 *              - API generalization to become transport-independent (e.g. to
 *                allow connections via serial ports and pipes).
 *     Liviu Ionescu - Arm version
 *     Jonah Graham - fix for Neon
 ******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.jumper.ui;

import java.io.File;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import ilg.gnumcueclipse.debug.gdbjtag.data.CProjectAttributes;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.Configuration;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.ConfigurationAttributes;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences.DefaultPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences.PersistentPreferences;

/**
 * @since 7.0
 */
public class TabDebugger extends AbstractLaunchConfigurationTab {

	// ------------------------------------------------------------------------

	private static final String TAB_NAME = "Debugger";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".ui.debuggertab";

	// ------------------------------------------------------------------------

	private ILaunchConfiguration fConfiguration;

	private Text fGdbClientPathLabel;
	private Text fGdbClientExecutable;
	private Text fGdbClientOtherOptions;
	private Text fGdbClientOtherCommands;

	private Button fDoStartGdbServer;

//	private Button fEnableSemihosting;
//	private Text fSemihostingCmdline;

//	private Button fDisableGraphics;

	private Text fTargetIpAddress;
	private Text fTargetPortNumber;

	private Text fJumperBoardName;
	private Text fJumperDeviceName;

	private Text fGdbServerGdbPort;

	private Text fGdbServerOtherOptions;

	protected Button fUpdateThreadlistOnSuspend;

	// TODO: check usage
	protected String fSavedCmsisDeviceName;
	protected String fSavedCmsisBoardName;

	protected String fProjectName;

	private DefaultPreferences fDefaultPreferences;
	private PersistentPreferences fPersistentPreferences;

	// ------------------------------------------------------------------------

	protected TabDebugger(TabStartup tabStartup) {
		super();

		fSavedCmsisDeviceName = null;
		fSavedCmsisBoardName = null;

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
	}

	@Override
	public String getName() {
		return TAB_NAME;
	}

	@Override
	public Image getImage() {
		return GDBJtagImages.getDebuggerTabImage();
	}

	@Override
	public void createControl(Composite parent) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.createControl() ");
		}

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);

		createGdbServerGroup(comp);

		createGdbClientControls(comp);

		createRemoteControl(comp);

		fUpdateThreadlistOnSuspend = new Button(comp, SWT.CHECK);
		fUpdateThreadlistOnSuspend.setText(Messages.getString("DebuggerTab.update_thread_list_on_suspend_Text"));
		fUpdateThreadlistOnSuspend
				.setToolTipText(Messages.getString("DebuggerTab.update_thread_list_on_suspend_ToolTipText"));

		Link restoreDefaults;
		{
			restoreDefaults = new Link(comp, SWT.NONE);
			restoreDefaults.setText(Messages.getString("DebuggerTab.restoreDefaults_Link"));
			restoreDefaults.setToolTipText(Messages.getString("DebuggerTab.restoreDefaults_ToolTipText"));

			GridData gd = new GridData();
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.RIGHT;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			restoreDefaults.setLayoutData(gd);
		}

		// --------------------------------------------------------------------

		fUpdateThreadlistOnSuspend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		restoreDefaults.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				initializeFromDefaults();
				scheduleUpdateJob();
			}
		});
	}

	private void browseButtonSelected(String title, Text text) {
		FileDialog dialog = new FileDialog(getShell(), SWT.NONE);
		dialog.setText(title);
		String str = text.getText().trim();
		int lastSeparatorIndex = str.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1)
			dialog.setFilterPath(str.substring(0, lastSeparatorIndex));
		str = dialog.open();
		if (str != null)
			text.setText(str);
	}

	private void variablesButtonSelected(Text text) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
	}

	@SuppressWarnings("unused")
	private void createOptionsControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			group.setText(Messages.getString("DebuggerTab.interfaceGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			comp.setLayout(layout);
		}
	}

	private void createGdbServerGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
			group.setText(Messages.getString("DebuggerTab.gdbServerGroup_Text"));
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		{
			fDoStartGdbServer = new Button(comp, SWT.CHECK);
			fDoStartGdbServer.setText(Messages.getString("DebuggerTab.doStartGdbServer_Text"));
			fDoStartGdbServer.setToolTipText(Messages.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
			GridData gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			fDoStartGdbServer.setLayoutData(gd);
		}
		

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerBoard_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerBoard_ToolTipText"));

			fJumperBoardName = new Text(comp, SWT.SINGLE | SWT.BORDER);
			fJumperBoardName.setToolTipText(Messages.getString("DebuggerTab.gdbServerBoard_ToolTipText"));

			GridData gd = new GridData();
			gd.widthHint = 200;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fJumperBoardName.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerDevice_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerDevice_ToolTipText"));

			fJumperDeviceName = new Text(comp, SWT.SINGLE | SWT.BORDER);
			fJumperDeviceName.setToolTipText(Messages.getString("DebuggerTab.gdbServerDevice_ToolTipText"));

			GridData gd = new GridData();
			gd.widthHint = 200;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fJumperDeviceName.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerGdbPort_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

			fGdbServerGdbPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			fGdbServerGdbPort.setToolTipText(Messages.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

			GridData gd = new GridData();
			gd.widthHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerGdbPort.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerOther_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerOther_ToolTipText"));
			GridData gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			fGdbServerOtherOptions = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			fGdbServerOtherOptions.setToolTipText(Messages.getString("DebuggerTab.gdbServerOther_ToolTipText"));

			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerOtherOptions.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------

		ModifyListener scheduleUpdateJobModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		};

		fDoStartGdbServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doStartGdbServerChanged();
				if (fDoStartGdbServer.getSelection()) {
					fTargetIpAddress.setText(DefaultPreferences.REMOTE_IP_ADDRESS_LOCALHOST);
				}
				scheduleUpdateJob();
			}
		});

//		fDisableGraphics.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fJumperBoardName.addModifyListener(scheduleUpdateJobModifyListener);

		fJumperDeviceName.addModifyListener(scheduleUpdateJobModifyListener);

		fGdbServerGdbPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// make the target port the same
				fTargetPortNumber.setText(fGdbServerGdbPort.getText());
				scheduleUpdateJob();
			}
		});

		fGdbServerOtherOptions.addModifyListener(scheduleUpdateJobModifyListener);
	}

	private void createGdbClientControls(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			group.setText(Messages.getString("DebuggerTab.gdbSetupGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		Button browseButton;
		Button variableButton;
		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbCommand_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbCommand_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				fGdbClientExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbClientExecutable.setLayoutData(gd);

				browseButton = new Button(local, SWT.NONE);
				browseButton.setText(Messages.getString("DebuggerTab.gdbCommandBrowse"));

				variableButton = new Button(local, SWT.NONE);
				variableButton.setText(Messages.getString("DebuggerTab.gdbCommandVariable"));
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbCommandActualPath_Label"));

			fGdbClientPathLabel = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, 0, true, false);
			gd.horizontalSpan = 4;
			fGdbClientPathLabel.setLayoutData(gd);

			fGdbClientPathLabel.setEnabled(true);
			fGdbClientPathLabel.setEditable(false);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbOtherOptions_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbOtherOptions_ToolTipText"));
			GridData gd = new GridData();
			label.setLayoutData(gd);

			fGdbClientOtherOptions = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbClientOtherOptions.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbOtherCommands_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbOtherCommands_ToolTipText"));
			GridData gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			fGdbClientOtherCommands = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbClientOtherCommands.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------

		fGdbClientExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
				updateGdbClientActualPath();
			}
		});

		fGdbClientOtherOptions.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fGdbClientOtherCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("DebuggerTab.gdbCommandBrowse_Title"), fGdbClientExecutable);
			}
		});

		variableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(fGdbClientExecutable);
			}
		});
	}

	private void createRemoteControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			group.setText(Messages.getString("DebuggerTab.remoteGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		// Create entry fields for TCP/IP connections
		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.ipAddressLabel")); //$NON-NLS-1$

			fTargetIpAddress = new Text(comp, SWT.BORDER);
			GridData gd = new GridData();
			gd.widthHint = 125;
			fTargetIpAddress.setLayoutData(gd);

			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.portNumberLabel")); //$NON-NLS-1$

			fTargetPortNumber = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			fTargetPortNumber.setLayoutData(gd);
		}

		// ---- Actions -------------------------------------------------------

		// Add watchers for user data entry
		fTargetIpAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
		fTargetPortNumber.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character) || Character.isISOControl(e.character);
			}
		});
		fTargetPortNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

	}

	private void updateGdbClientActualPath() {

		assert (fConfiguration != null);
		String fullCommand = Configuration.getGdbClientCommand(fConfiguration, fGdbClientExecutable.getText());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.updateGdbClientActualPath() \"" + fullCommand + "\"");
		}
		fGdbClientPathLabel.setText(fullCommand);
	}

	private void doStartGdbServerChanged() {

		boolean enabled = fDoStartGdbServer.getSelection();

		fGdbServerOtherOptions.setEnabled(enabled);

		fGdbServerGdbPort.setEnabled(enabled);

//		fDisableGraphics.setEnabled(enabled);

		fJumperBoardName.setEnabled(enabled);

		// Disable remote target params when the server is started
		fTargetIpAddress.setEnabled(!enabled);
		fTargetPortNumber.setEnabled(!enabled);
	}

	/**
	 * Get the project name associated to this launch configuration.
	 * 
	 * @param configuration
	 * @return a String with the project name, or empty.
	 */
	private String getProjectName(ILaunchConfiguration configuration) {

		if (configuration != null) {
			// Get project name from launch configuration.
			try {
				String str = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME,
						(String) null);
				if (str == null) {
					str = configuration.getName();
				}
				fProjectName = str.replace(' ', '_');
			} catch (CoreException e) {
				;
			}
		}

		if (fProjectName == null) {
			fProjectName = "";
		}

		return fProjectName;
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.initializeFrom() " + configuration.getName());
		}

		fConfiguration = configuration;

		try {
			Boolean booleanDefault;
			String stringDefault;

			// Jumper GDB server
			{
				// Start server locally
				booleanDefault = fPersistentPreferences.getGdbServerDoStart();
				fDoStartGdbServer.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanDefault));

				// Executable

				String boardName = CProjectAttributes.getCmsisBoardName(configuration);
				fSavedCmsisBoardName = boardName;
				String deviceName = CProjectAttributes.getCmsisDeviceName(configuration);
				fSavedCmsisDeviceName = deviceName;

				// If the project has assigned either a board name,
				// or a device name, use them.
				if (((boardName == null || boardName.isEmpty()) && (deviceName == null || deviceName.isEmpty()))) {

					// Otherwise try the names used previously
					boardName = fPersistentPreferences.getJumperBoardName();
					deviceName = fPersistentPreferences.getJumperDeviceName();
				}

				fJumperBoardName.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME,
						boardName == null ? "" : boardName));
				fJumperDeviceName.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME,
						deviceName == null ? "" : deviceName));

				// Ports
				fGdbServerGdbPort.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
								DefaultPreferences.SERVER_GDB_PORT_NUMBER_DEFAULT)));

				// Other options
				stringDefault = fPersistentPreferences.getGdbServerOtherOptions();
				fGdbServerOtherOptions
						.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringDefault));

			}

			// GDB Client Setup
			{
				// Executable
				stringDefault = fPersistentPreferences.getGdbClientExecutable();
				String gdbCommandAttr = configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
						stringDefault);
				fGdbClientExecutable.setText(gdbCommandAttr);

				// Other options
				stringDefault = fPersistentPreferences.getGdbClientOtherOptions();
				fGdbClientOtherOptions.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, stringDefault));

				stringDefault = fPersistentPreferences.getGdbClientCommands();
				fGdbClientOtherCommands.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, stringDefault));
			}

			// Remote target
			{
				fTargetIpAddress.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS,
						DefaultPreferences.REMOTE_IP_ADDRESS_DEFAULT)); // $NON-NLS-1$

				int storedPort = 0;
				storedPort = configuration.getAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, 0); // Default
																								// 0

				// 0 means undefined, use default
				if ((storedPort <= 0) || (65535 < storedPort)) {
					storedPort = DefaultPreferences.REMOTE_PORT_NUMBER_DEFAULT;
				}

				String portString = Integer.toString(storedPort); // $NON-NLS-1$
				fTargetPortNumber.setText(portString);

			}

			doStartGdbServerChanged();

			// Force thread update
			boolean updateThreadsOnSuspend = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
					ConfigurationAttributes.UPDATE_THREAD_LIST_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.initializeFrom() completed " + configuration.getName());
		}
	}

	public void initializeFromDefaults() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.initializeFromDefaults()");
		}

		String stringDefault;
		boolean booleanDefault;

		// Jumper GDB server
		{
			// Start server locally
			booleanDefault = fDefaultPreferences.getGdbServerDoStart();
			fDoStartGdbServer.setSelection(booleanDefault);


			String boardName;
			String deviceName;
			if (fSavedCmsisBoardName != null || fSavedCmsisDeviceName != null) {
				// If the project has assigned either a board name,
				// or a device name, use them.
				boardName = fSavedCmsisBoardName != null ? fSavedCmsisBoardName : "";
				deviceName = fSavedCmsisDeviceName != null ? fSavedCmsisDeviceName : "";
			} else {
				boardName = DefaultPreferences.JUMPER_BOARD_NAME_DEFAULT;
				deviceName = DefaultPreferences.JUMPER_DEVICE_NAME_DEFAULT;
			}

			fJumperBoardName.setText(boardName);
			fJumperDeviceName.setText(deviceName);

			// Ports
			fGdbServerGdbPort.setText(Integer.toString(DefaultPreferences.SERVER_GDB_PORT_NUMBER_DEFAULT));

			// Other options
			stringDefault = fDefaultPreferences.getGdbServerOtherOptions();
			fGdbServerOtherOptions.setText(stringDefault);
		}

		// GDB Client Setup
		{
			stringDefault = fDefaultPreferences.getGdbClientExecutable();
			// Executable
			fGdbClientExecutable.setText(stringDefault);

			// Other options
			stringDefault = fDefaultPreferences.getGdbClientOtherOptions();
			fGdbClientOtherOptions.setText(stringDefault);

			stringDefault = fDefaultPreferences.getGdbClientCommands();
			fGdbClientOtherCommands.setText(stringDefault);
		}

		// Remote target
		{
			fTargetIpAddress.setText(DefaultPreferences.REMOTE_IP_ADDRESS_DEFAULT); // $NON-NLS-1$

			String portString = Integer.toString(DefaultPreferences.REMOTE_PORT_NUMBER_DEFAULT); // $NON-NLS-1$
			fTargetPortNumber.setText(portString);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
	 */
	@Override
	public String getId() {
		return TAB_ID;
	}

	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.activated() " + workingCopy.getName());
		}
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.deactivated() " + workingCopy.getName());
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.isValid() " + launchConfig.getName());
		}

		setErrorMessage(null);
		setMessage(null);

		boolean result = true;

		if (fDoStartGdbServer != null && fDoStartGdbServer.getSelection()) {

			if (fGdbServerGdbPort != null && fGdbServerGdbPort.getText().trim().isEmpty()) {
				setErrorMessage("GDB port?");
				result = false;
			}

			if (fJumperBoardName != null && fJumperBoardName.getText().trim().isEmpty()) {
				setErrorMessage("Board name?");
				result = false;
			}
		}

		if (fGdbClientExecutable != null && fGdbClientExecutable.getText().trim().isEmpty()) {
			setErrorMessage("GDB client executable name?");
			result = false;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.isValid() " + launchConfig.getName() + " = " + result);
		}

		return result;
	}

	@Override
	public boolean canSave() {
		if (fDoStartGdbServer != null && fDoStartGdbServer.getSelection()) {
			if (fJumperBoardName != null && fJumperBoardName.getText().trim().isEmpty())
				return false;

			if (fGdbServerGdbPort != null && fGdbServerGdbPort.getText().trim().isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.performApply() " + configuration.getName());
		}

		{
			// legacy definition; although the jtag device class is not used,
			// it must be there, to avoid NPEs
			configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);
		}

		boolean booleanValue;
		String stringValue;

		// Jumper server
		{
			// Start server
			booleanValue = fDoStartGdbServer.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanValue);
			fPersistentPreferences.putGdbServerDoStart(booleanValue);

			// Executable
			stringValue = "jumper";
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringValue);
			fPersistentPreferences.putGdbServerExecutable(stringValue);

			// Ports
			int port;
			if (!fGdbServerGdbPort.getText().trim().isEmpty()) {
				port = Integer.parseInt(fGdbServerGdbPort.getText().trim());
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER, port);
			} else {
				Activator.log("empty fGdbServerGdbPort");
			}

			// Board name
			stringValue = fJumperBoardName.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME, stringValue);
			fPersistentPreferences.putJumperBoardName(stringValue);

			// Device name
			stringValue = fJumperDeviceName.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, stringValue);
			fPersistentPreferences.putJumperDeviceName(stringValue);

			// Other options
			stringValue = fGdbServerOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringValue);
			fPersistentPreferences.putGdbServerOtherOptions(stringValue);
		}

		// GDB client
		{
			// always use remote
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					DefaultPreferences.USE_REMOTE_TARGET_DEFAULT);

			stringValue = fGdbClientExecutable.getText().trim();
			// configuration.setAttribute(
			// IMILaunchConfigurationConstants.ATTR_DEBUG_NAME,
			// clientExecutable);
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, stringValue); // DSF
			fPersistentPreferences.putGdbClientExecutable(stringValue);

			stringValue = fGdbClientOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, stringValue);
			fPersistentPreferences.putGdbClientOtherOptions(stringValue);

			stringValue = fGdbClientOtherCommands.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, stringValue);
			fPersistentPreferences.putGdbClientCommands(stringValue);
		}

		{
			if (fDoStartGdbServer.getSelection()) {
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, "localhost");

				String str = fGdbServerGdbPort.getText().trim();
				if (!str.isEmpty()) {
					try {
						int port;
						port = Integer.parseInt(str);
						configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
					} catch (NumberFormatException e) {
						Activator.log(e);
					}
				}
			} else {
				String ip = fTargetIpAddress.getText().trim();
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, ip);

				String str = fTargetPortNumber.getText().trim();
				if (!str.isEmpty()) {
					try {
						int port = Integer.valueOf(str).intValue();
						configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
					} catch (NumberFormatException e) {
						Activator.log(e);
					}
				}
			}
		}

		// Force thread update
		configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
				fUpdateThreadlistOnSuspend.getSelection());

		fPersistentPreferences.flush();

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"jumper.TabDebugger.performApply() completed " + configuration.getName() + ", dirty=" + isDirty());
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.TabDebugger.setDefaults() " + configuration.getName());
		}

		boolean defaultBoolean;
		String defaultString;

		configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);

		// These are inherited from the generic implementation.
		// Some might need some trimming.
		{
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
					IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
		}

		// Jumper GDB server setup
		{
			defaultBoolean = fPersistentPreferences.getGdbServerDoStart();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, defaultBoolean);

			defaultString = fPersistentPreferences.getGdbServerExecutable();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, defaultString);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					DefaultPreferences.SERVER_GDB_PORT_NUMBER_DEFAULT);

			String boardName = CProjectAttributes.getCmsisBoardName(configuration);
			String deviceName = CProjectAttributes.getCmsisDeviceName(configuration);

			// If the project has assigned either a board name,
			// or a device name, use them.
			if (((boardName == null || boardName.isEmpty()) && (deviceName == null || deviceName.isEmpty()))) {

				// Otherwise try the names used previously
				boardName = fPersistentPreferences.getJumperBoardName();
				deviceName = fPersistentPreferences.getJumperDeviceName();
			}

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME,
					boardName == null ? "" : boardName);
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME,
					deviceName == null ? "" : deviceName);

			defaultString = fPersistentPreferences.getGdbServerOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, defaultString);

			defaultBoolean = fPersistentPreferences.getJumperEnableSemihosting();
			configuration.setAttribute(ConfigurationAttributes.ENABLE_SEMIHOSTING, defaultBoolean);

			configuration.setAttribute(ConfigurationAttributes.SEMIHOSTING_CMDLINE, getProjectName(configuration));

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			defaultBoolean = fPersistentPreferences.getJumperIsVerbose();
			configuration.setAttribute(ConfigurationAttributes.IS_GDB_SERVER_VERBOSE, defaultBoolean);
		}

		// GDB client setup
		{
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					fPersistentPreferences.getGdbClientExecutable());

			defaultString = fPersistentPreferences.getGdbClientOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					fDefaultPreferences.getGdbClientOtherOptions());

			defaultString = fPersistentPreferences.getGdbClientCommands();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, defaultString);
		}

		// Remote Target
		{
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					DefaultPreferences.USE_REMOTE_TARGET_DEFAULT);

			configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER,
					DefaultPreferences.REMOTE_PORT_NUMBER_DEFAULT);
		}

		// Force thread update
		configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
				ConfigurationAttributes.UPDATE_THREAD_LIST_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
