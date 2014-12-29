/*******************************************************************************
 * Copyright (c) 2007, 2012 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 *     Liviu Ionescu - ARM version
 ******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.openocd.ui;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.ConfigurationAttributes;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.WorkspacePersistentValues;

import java.io.File;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.debug.mi.core.IMILaunchConfigurationConstants;
import org.eclipse.cdt.debug.mi.core.MIPlugin;
import org.eclipse.cdt.debug.mi.core.command.factories.CommandFactoryDescriptor;
import org.eclipse.cdt.debug.mi.core.command.factories.CommandFactoryManager;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Text;

/**
 * @since 7.0
 */
public class TabDebugger extends AbstractLaunchConfigurationTab {

	private static final String TAB_NAME = "Debugger";
	private static final String TAB_ID = Activator.PLUGIN_ID
			+ ".ui.debuggertab";

	private static final boolean DEBUG = false;

	private Button doStartGdbServer;
	private Text gdbClientExecutable;
	private Text gdbClientOtherOptions;
	private Text gdbClientOtherCommands;

	private Text targetIpAddress;
	private Text targetPortNumber;

	private Button doConnectToRunning;

	private Text gdbServerGdbPort;
	private Text gdbServerTelnetPort;

	private Text gdbServerExecutable;
	private Button gdbServerBrowseButton;
	private Button gdbServerVariablesButton;

	private Text gdbServerLog;
	private Button gdbServerLogBrowse;
	private Text gdbServerOtherOptions;

	private Button doGdbServerAllocateConsole;
	private Button doGdbServerAllocateTelnetConsole;

	protected Button fUpdateThreadlistOnSuspend;

	private TabStartup tabStartup;

	protected TabDebugger(TabStartup tabStartup) {
		super();
		this.tabStartup = tabStartup;
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

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL
				| SWT.H_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		setControl(sc);

		Composite comp = new Composite(sc, SWT.NONE);
		sc.setContent(comp);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);

		createGdbServerGroup(comp);

		createGdbClientControls(comp);

		createOptionsControl(comp);

		createRemoteControl(comp);

		fUpdateThreadlistOnSuspend = new Button(comp, SWT.CHECK);
		fUpdateThreadlistOnSuspend.setText(Messages
				.getString("DebuggerTab.update_thread_list_on_suspend_Text"));
		fUpdateThreadlistOnSuspend
				.setToolTipText(Messages
						.getString("DebuggerTab.update_thread_list_on_suspend_ToolTipText"));

		fUpdateThreadlistOnSuspend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
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

	private void browseSaveButtonSelected(String title, Text text) {
		FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
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
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(
				getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
	}

	private void createOptionsControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.interfaceGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		comp.setLayout(layout);

		{
			doConnectToRunning = new Button(comp, SWT.CHECK);
			doConnectToRunning.setText(Messages
					.getString("DebuggerTab.noReset_Text"));
			doConnectToRunning.setToolTipText(Messages
					.getString("DebuggerTab.noReset_ToolTipText"));
		}

		// ----- Actions ------------------------------------------------------

		doConnectToRunning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();

				doConnectToRunningChanged();
				tabStartup.doConnectToRunningChanged(doConnectToRunning
						.getSelection());
			}
		});

	}

	private void createGdbServerGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setText(Messages.getString("DebuggerTab.gdbServerGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 5;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		Label label;
		{
			doStartGdbServer = new Button(comp, SWT.CHECK);
			doStartGdbServer.setText(Messages
					.getString("DebuggerTab.doStartGdbServer_Text"));
			doStartGdbServer.setToolTipText(Messages
					.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
			gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			doStartGdbServer.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerExecutable_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerExecutable_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				gdbServerExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbServerExecutable.setLayoutData(gd);

				gdbServerBrowseButton = new Button(local, SWT.NONE);
				gdbServerBrowseButton.setText(Messages
						.getString("DebuggerTab.gdbServerExecutableBrowse"));

				gdbServerVariablesButton = new Button(local, SWT.NONE);
				gdbServerVariablesButton.setText(Messages
						.getString("DebuggerTab.gdbServerExecutableVariable"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerGdbPort_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

			gdbServerGdbPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			gdbServerGdbPort.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerTelnetPort_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerTelnetPort_ToolTipText"));

			gdbServerTelnetPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			gdbServerTelnetPort.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerLog_Label")); //$NON-NLS-1$

			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				gdbServerLog = new Text(local, SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbServerLog.setLayoutData(gd);

				gdbServerLogBrowse = new Button(local, SWT.NONE);
				gdbServerLogBrowse.setText(Messages
						.getString("DebuggerTab.gdbServerLogBrowse_Button"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerOther_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerOther_ToolTipText"));
			gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			gdbServerOtherOptions = new Text(comp, SWT.MULTI | SWT.WRAP
					| SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			gdbServerOtherOptions.setLayoutData(gd);
		}

		{
			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.makeColumnsEqualWidth = true;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			local.setLayoutData(gd);

			doGdbServerAllocateConsole = new Button(local, SWT.CHECK);
			doGdbServerAllocateConsole.setText(Messages
					.getString("DebuggerTab.gdbServerAllocateConsole_Label"));
			doGdbServerAllocateConsole
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerAllocateConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			doGdbServerAllocateConsole.setLayoutData(gd);

			doGdbServerAllocateTelnetConsole = new Button(local, SWT.CHECK);
			doGdbServerAllocateTelnetConsole
					.setText(Messages
							.getString("DebuggerTab.gdbServerAllocateTelnetConsole_Label"));
			doGdbServerAllocateTelnetConsole
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerAllocateTelnetConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			doGdbServerAllocateTelnetConsole.setLayoutData(gd);

			// update doStartGdbServerChanged() too
			doGdbServerAllocateTelnetConsole.setEnabled(false);

		}
		// ----- Actions ------------------------------------------------------

		ModifyListener scheduleUpdateJobModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		};

		SelectionAdapter scheduleUpdateJobSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleUpdateJob();
			}
		};

		doStartGdbServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doStartGdbServerChanged();
				if (doStartGdbServer.getSelection()) {
					targetIpAddress
							.setText(ConfigurationAttributes.REMOTE_IP_ADDRESS_LOCALHOST);
				}
				scheduleUpdateJob();
			}
		});

		gdbServerExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		gdbServerBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(
						Messages.getString("DebuggerTab.gdbServerExecutableBrowse_Title"),
						gdbServerExecutable);
			}
		});

		gdbServerVariablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(gdbServerExecutable);
			}
		});

		gdbServerGdbPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// make the target port the same
				targetPortNumber.setText(gdbServerGdbPort.getText());
				scheduleUpdateJob();
			}
		});

		gdbServerTelnetPort.addModifyListener(scheduleUpdateJobModifyListener);

		gdbServerLog.addModifyListener(scheduleUpdateJobModifyListener);
		gdbServerLogBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseSaveButtonSelected(Messages
						.getString("DebuggerTab.gdbServerLogBrowse_Title"),
						gdbServerLog);
			}
		});

		gdbServerOtherOptions
				.addModifyListener(scheduleUpdateJobModifyListener);

		doGdbServerAllocateConsole
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		doGdbServerAllocateTelnetConsole
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
	}

	private void createGdbClientControls(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.gdbSetupGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		Label label;
		Button browseButton;
		Button variableButton;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbCommand_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbCommand_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			local.setLayoutData(gd);
			{
				gdbClientExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbClientExecutable.setLayoutData(gd);

				browseButton = new Button(local, SWT.NONE);
				browseButton.setText(Messages
						.getString("DebuggerTab.gdbCommandBrowse"));

				variableButton = new Button(local, SWT.NONE);
				variableButton.setText(Messages
						.getString("DebuggerTab.gdbCommandVariable"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbOtherOptions_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbOtherOptions_ToolTipText"));
			gd = new GridData();
			label.setLayoutData(gd);

			gdbClientOtherOptions = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gdbClientOtherOptions.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbOtherCommands_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbOtherCommands_ToolTipText"));
			gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			gdbClientOtherCommands = new Text(comp, SWT.MULTI | SWT.WRAP
					| SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gdbClientOtherCommands.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------
		gdbClientExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		gdbClientOtherOptions.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		gdbClientOtherCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages
						.getString("DebuggerTab.gdbCommandBrowse_Title"),
						gdbClientExecutable);
			}
		});

		variableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(gdbClientExecutable);
			}
		});
	}

	private void createRemoteControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.remoteGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		// Create entry fields for TCP/IP connections
		Label label;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.ipAddressLabel")); //$NON-NLS-1$

			targetIpAddress = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			targetIpAddress.setLayoutData(gd);

			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.portNumberLabel")); //$NON-NLS-1$

			targetPortNumber = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			targetPortNumber.setLayoutData(gd);
		}

		// ---- Actions -------------------------------------------------------
		// Add watchers for user data entry
		targetIpAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
		targetPortNumber.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character)
						|| Character.isISOControl(e.character);
			}
		});
		targetPortNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

	}

	private void doStartGdbServerChanged() {

		boolean enabled = doStartGdbServer.getSelection();

		// gdbServerSpeedAuto.setSelection(enabled);
		// gdbServerSpeedAdaptive.setSelection(enabled);
		// gdbServerSpeedFixed.setSelection(enabled);
		// gdbServerSpeedFixedValue.setEnabled(enabled);

		gdbServerExecutable.setEnabled(enabled);
		gdbServerBrowseButton.setEnabled(enabled);
		gdbServerVariablesButton.setEnabled(enabled);
		gdbServerOtherOptions.setEnabled(enabled);

		gdbServerLog.setEnabled(enabled);
		gdbServerLogBrowse.setEnabled(enabled);

		gdbServerGdbPort.setEnabled(enabled);
		gdbServerTelnetPort.setEnabled(enabled);

		if (EclipseUtils.isWindows()) {
			// Prevent disable it on Windows
			doGdbServerAllocateConsole.setEnabled(false);
		} else {
			doGdbServerAllocateConsole.setEnabled(enabled);
		}

		// doGdbServerAllocateTelnetConsole.setEnabled(enabled);

		// Disable remote target params when the server is started
		targetIpAddress.setEnabled(!enabled);
		targetPortNumber.setEnabled(!enabled);
	}

	private void doConnectToRunningChanged() {

		if (doStartGdbServer.getSelection()) {

			// boolean enabled = doConnectToRunning.getSelection();

		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			Boolean booleanDefault;
			String stringDefault;

			// OpenOCD GDB server
			{
				// Start server locally
				booleanDefault = WorkspacePersistentValues
						.getGdbServerDoStart(ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);
				doStartGdbServer.setSelection(configuration.getAttribute(
						ConfigurationAttributes.DO_START_GDB_SERVER,
						booleanDefault));

				// Executable
				stringDefault = WorkspacePersistentValues
						.getGdbServerExecutable(ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT);
				gdbServerExecutable.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
						stringDefault));

				// Ports
				gdbServerGdbPort
						.setText(Integer.toString(configuration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
										ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

				gdbServerTelnetPort
						.setText(Integer.toString(configuration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
										ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

				// Log file
				gdbServerLog.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_SERVER_LOG,
						ConfigurationAttributes.GDB_SERVER_LOG_DEFAULT));

				// Other options
				stringDefault = WorkspacePersistentValues
						.getGdbServerOtherOptions(ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT);
				gdbServerOtherOptions.setText(configuration
						.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER,
								stringDefault));

				// Allocate server console
				if (EclipseUtils.isWindows()) {
					doGdbServerAllocateConsole.setSelection(true);
				} else {
					doGdbServerAllocateConsole
							.setSelection(configuration
									.getAttribute(
											ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
											ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT));
				}

				// Allocate telnet console
				doGdbServerAllocateTelnetConsole
						.setSelection(configuration
								.getAttribute(
										ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE,
										ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE_DEFAULT));

			}

			// GDB Client Setup
			{
				// Executable
				stringDefault = WorkspacePersistentValues
						.getGdbClientExecutable(ConfigurationAttributes.GDB_CLIENT_EXECUTABLE_DEFAULT);
				String gdbCommandAttr = configuration.getAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
						stringDefault);
				gdbClientExecutable.setText(gdbCommandAttr);

				// Other options
				stringDefault = WorkspacePersistentValues
						.getGdbClientOtherOptions(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS_DEFAULT);
				gdbClientOtherOptions.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
						stringDefault));

				stringDefault = WorkspacePersistentValues
						.getGdbClientCommands(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
				gdbClientOtherCommands.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
						stringDefault));
			}

			// Connect to running
			{
				doConnectToRunning.setSelection(configuration.getAttribute(
						ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
						ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT));
			}

			// Remote target
			{
				targetIpAddress.setText(configuration.getAttribute(
						IGDBJtagConstants.ATTR_IP_ADDRESS,
						ConfigurationAttributes.REMOTE_IP_ADDRESS_DEFAULT)); //$NON-NLS-1$

				int storedPort = 0;
				storedPort = configuration.getAttribute(
						IGDBJtagConstants.ATTR_PORT_NUMBER, 0); // Default 0

				// 0 means undefined, use default
				if ((storedPort <= 0) || (65535 < storedPort)) {
					storedPort = ConfigurationAttributes.REMOTE_PORT_NUMBER_DEFAULT;
				}

				String portString = Integer.toString(storedPort); //$NON-NLS-1$
				targetPortNumber.setText(portString);

				// useRemoteChanged();
			}

			doStartGdbServerChanged();
			doConnectToRunningChanged();

			// Force thread update
			boolean updateThreadsOnSuspend = configuration
					.getAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
							ConfigurationAttributes.UPDATE_THREAD_LIST_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

		} catch (CoreException e) {
			Activator.log(e.getStatus());
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
		if (DEBUG) {
			System.out.println("TabDebugger: activated() "
					+ workingCopy.getName());
		}
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (DEBUG) {
			System.out.println("TabDebugger: deactivated() "
					+ workingCopy.getName());
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		{
			// legacy definition; although the jtag device class is not used,
			// it must be there, to avoid NPEs
			configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE,
					ConfigurationAttributes.JTAG_DEVICE);
		}

		boolean booleanValue;
		String stringValue;

		// OpenOCD server
		{
			// Start server
			booleanValue = doStartGdbServer.getSelection();
			configuration.setAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER, booleanValue);
			WorkspacePersistentValues.putGdbServerDoStart(booleanValue);

			// Executable
			stringValue = gdbServerExecutable.getText().trim();
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringValue);
			WorkspacePersistentValues.putGdbServerExecutable(stringValue);

			// Ports
			int port;
			port = Integer.parseInt(gdbServerGdbPort.getText().trim());
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER, port);

			port = Integer.parseInt(gdbServerTelnetPort.getText().trim());
			configuration
					.setAttribute(
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
							port);

			// Log file
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					gdbServerLog.getText().trim());

			// Other options
			stringValue = gdbServerOtherOptions.getText().trim();
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_OTHER, stringValue);
			WorkspacePersistentValues.putGdbServerOtherOptions(stringValue);

			// Allocate server console
			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					doGdbServerAllocateConsole.getSelection());

			// Allocate semihosting console
			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE,
							doGdbServerAllocateTelnetConsole.getSelection());
		}

		// GDB client
		{
			// always use remote
			configuration.setAttribute(
					IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					ConfigurationAttributes.USE_REMOTE_TARGET_DEFAULT);

			stringValue = gdbClientExecutable.getText().trim();
			// configuration.setAttribute(
			// IMILaunchConfigurationConstants.ATTR_DEBUG_NAME,
			// clientExecutable);
			configuration.setAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					stringValue); // DSF
			WorkspacePersistentValues.putGdbClientExecutable(stringValue);

			stringValue = gdbClientOtherOptions.getText().trim();
			configuration.setAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					stringValue);
			WorkspacePersistentValues.putGdbClientOtherOptions(stringValue);

			stringValue = gdbClientOtherCommands.getText().trim();
			configuration.setAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
					stringValue);
			WorkspacePersistentValues.putGdbClientCommands(stringValue);
		}

		// Connect to running
		{
			configuration.setAttribute(
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					doConnectToRunning.getSelection());
		}
		// Remote target
		{
			if (doStartGdbServer.getSelection()) {
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS,
						"localhost");

				try {
					int port;
					port = Integer.parseInt(gdbServerGdbPort.getText().trim());
					configuration.setAttribute(
							IGDBJtagConstants.ATTR_PORT_NUMBER, port);
				} catch (NumberFormatException e) {
					Activator.log(e);
				}
			} else {
				String ip = targetIpAddress.getText().trim();
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS,
						ip);

				try {
					int port = Integer.valueOf(
							targetPortNumber.getText().trim()).intValue();
					configuration.setAttribute(
							IGDBJtagConstants.ATTR_PORT_NUMBER, port);
				} catch (NumberFormatException e) {
					Activator.log(e);
				}
			}
		}

		// Force thread update
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						fUpdateThreadlistOnSuspend.getSelection());

		WorkspacePersistentValues.flush();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE,
				ConfigurationAttributes.JTAG_DEVICE);

		// These are inherited from the generic implementation.
		// Some might need some trimming.
		{
			CommandFactoryManager cfManager = MIPlugin.getDefault()
					.getCommandFactoryManager();
			CommandFactoryDescriptor defDesc = cfManager
					.getDefaultDescriptor(IGDBJtagConstants.DEBUGGER_ID);
			configuration
					.setAttribute(
							IMILaunchConfigurationConstants.ATTR_DEBUGGER_COMMAND_FACTORY,
							defDesc.getName());
			configuration.setAttribute(
					IMILaunchConfigurationConstants.ATTR_DEBUGGER_PROTOCOL,
					defDesc.getMIVersions()[0]);
			configuration
					.setAttribute(
							IMILaunchConfigurationConstants.ATTR_DEBUGGER_VERBOSE_MODE,
							IMILaunchConfigurationConstants.DEBUGGER_VERBOSE_MODE_DEFAULT);
			configuration
					.setAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
							IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
		}

		{
			configuration.setAttribute(
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		}

		// OpenOCD GDB server setup
		{
			configuration.setAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);

			String sharedName = WorkspacePersistentValues
					.getGdbServerExecutable(ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT);
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_EXECUTABLE, sharedName);

			configuration
					.setAttribute(
							ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS,
							ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					ConfigurationAttributes.GDB_SERVER_LOG_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_OTHER,
					ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE,
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE_DEFAULT);
		}

		// GDB client setup
		{
			configuration
					.setAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
							WorkspacePersistentValues
									.getGdbClientExecutable(ConfigurationAttributes.GDB_CLIENT_EXECUTABLE_DEFAULT));

			configuration.setAttribute(
					IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					ConfigurationAttributes.USE_REMOTE_TARGET_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
		}

		// Force thread update
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						ConfigurationAttributes.UPDATE_THREAD_LIST_DEFAULT);
	}

}
