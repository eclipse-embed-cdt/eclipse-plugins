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
 *     Jonah Graham - fix for Neon
 ******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.jlink.ui;

import java.io.File;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.debug.gdbjtag.data.CProjectAttributes;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.Configuration;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.ConfigurationAttributes;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.preferences.DefaultPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.preferences.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.ui.preferences.GlobalMcuPage;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.ui.preferences.WorkspaceMcuPage;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.ui.properties.ProjectMcuPage;

/**
 * @since 7.0
 */
public class TabDebugger extends AbstractLaunchConfigurationTab {

	// ------------------------------------------------------------------------

	private static final String TAB_NAME = "Debugger";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".ui.debuggertab";

	private static int COLUMN_WIDTH = 70;

	// ------------------------------------------------------------------------

	private ILaunchConfiguration fConfiguration;

	private Text fGdbClientExecutable;
	private Text fGdbClientOtherOptions;
	private Text fGdbClientPathLabel;
	private Text fGdbClientOtherCommands;

	private Button fDoStartGdbServer;
	private Text fGdbServerPathLabel;
	private Link fLink;

	private Text fTargetIpAddress;
	private Text fTargetPortNumber;
	private Text fGdbFlashDeviceName;
	private Button fGdbEndiannessLittle;

	private Button fGdbEndiannessBig;
	private Button fGdbInterfaceJtag;
	private Button fGdbInterfaceSwd;

	private Button fGdbServerConnectionUsb;
	private Button fGdbServerConnectionIp;
	private Text fGdbServerConnectionAddress;

	private Button fGdbServerSpeedAuto;
	private Button fGdbServerSpeedAdaptive;
	private Button fGdbServerSpeedFixed;
	private Text fGdbServerSpeedFixedValue;

	private Button fDoConnectToRunning;

	private Text fGdbServerGdbPort;
	private Text fGdbServerSwoPort;
	private Text fGdbServerTelnetPort;

	private Text fGdbServerExecutable;
	private Button fGdbServerBrowseButton;
	private Button fGdbServerVariablesButton;

	private Button fDoGdbServerVerifyDownload;
	private Button fDoGdbServerInitRegs;
	private Button fDoGdbServerLocalOnly;
	private Button fDoGdbServerSilent;

	private Text fGdbServerLog;
	private Button fGdbServerLogBrowse;
	private Text fGdbServerOtherOptions;

	private Button fDoGdbServerAllocateConsole;
	private Button fDoGdbServerAllocateSemihostingConsole;

	protected Button fUpdateThreadlistOnSuspend;
	protected String fSavedCmsisDeviceName;

	private TabStartup fTabStartup;

	private DefaultPreferences fDefaultPreferences;
	private PersistentPreferences fPersistentPreferences;

	// ------------------------------------------------------------------------

	protected TabDebugger(TabStartup tabStartup) {
		super();

		fTabStartup = tabStartup;
		fSavedCmsisDeviceName = "";

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
	}

	// ------------------------------------------------------------------------

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
			System.out.println("jlink.TabDebugger.createControl() ");
		}

		// gdbPrevUsbAddress = "";
		// gdbPrevIpAddress = "";

		if (EclipseUtils.isLinux()) {
			COLUMN_WIDTH = 85;
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
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
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

		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
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
			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);

			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			local.setLayoutData(gd);

			{
				fDoStartGdbServer = new Button(local, SWT.CHECK);
				fDoStartGdbServer.setText(Messages.getString("DebuggerTab.doStartGdbServer_Text"));
				fDoStartGdbServer.setToolTipText(Messages.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fDoStartGdbServer.setLayoutData(gd);

				fDoConnectToRunning = new Button(local, SWT.CHECK);
				fDoConnectToRunning.setText(Messages.getString("DebuggerTab.noReset_Text"));
				fDoConnectToRunning.setToolTipText(Messages.getString("DebuggerTab.noReset_ToolTipText"));
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fDoConnectToRunning.setLayoutData(gd);
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerExecutable_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerExecutable_ToolTipText"));

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
				fGdbServerExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbServerExecutable.setLayoutData(gd);

				fGdbServerBrowseButton = new Button(local, SWT.NONE);
				fGdbServerBrowseButton.setText(Messages.getString("DebuggerTab.gdbServerExecutableBrowse"));

				fGdbServerVariablesButton = new Button(local, SWT.NONE);
				fGdbServerVariablesButton.setText(Messages.getString("DebuggerTab.gdbServerExecutableVariable"));
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerActualPath_Label"));

			fGdbServerPathLabel = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, 0, true, false);
			gd.horizontalSpan = 4;
			fGdbServerPathLabel.setLayoutData(gd);

			fGdbServerPathLabel.setEnabled(true);
			fGdbServerPathLabel.setEditable(false);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText("");

			fLink = new Link(comp, SWT.NONE);
			fLink.setText(Messages.getString("DebuggerTab.gdbServerActualPath_link"));
			GridData gd = new GridData();
			gd.horizontalSpan = 4;
			fLink.setLayoutData(gd);
		}

		Link link;
		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.deviceName_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.deviceName_ToolTipText"));

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
				fGdbFlashDeviceName = new Text(local, SWT.BORDER);
				gd = new GridData();
				gd.widthHint = 180;
				fGdbFlashDeviceName.setLayoutData(gd);

				Composite empty = new Composite(local, SWT.NONE);
				layout = new GridLayout();
				layout.numColumns = 1;
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				empty.setLayout(layout);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				empty.setLayoutData(gd);

				link = new Link(local, SWT.NONE);
				link.setText(Messages.getString("DebuggerTab.deviceName_Link"));
				gd = new GridData(SWT.RIGHT);
				link.setLayoutData(gd);
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.endianness_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.endianness_ToolTipText")); //$NON-NLS-1$

			Composite radio = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			radio.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			radio.setLayoutData(gd);
			{
				fGdbEndiannessLittle = new Button(radio, SWT.RADIO);
				fGdbEndiannessLittle.setText(Messages.getString("DebuggerTab.endiannesslittle_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbEndiannessLittle.setLayoutData(gd);

				fGdbEndiannessBig = new Button(radio, SWT.RADIO);
				fGdbEndiannessBig.setText(Messages.getString("DebuggerTab.endiannessBig_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbEndiannessBig.setLayoutData(gd);
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerConnection_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerConnection_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			layout.marginHeight = 0;
			// layout.marginWidth = 0;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				fGdbServerConnectionUsb = new Button(local, SWT.RADIO);
				fGdbServerConnectionUsb.setText(Messages.getString("DebuggerTab.connectionUsb_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbServerConnectionUsb.setLayoutData(gd);

				fGdbServerConnectionIp = new Button(local, SWT.RADIO);
				fGdbServerConnectionIp.setText(Messages.getString("DebuggerTab.connectionTcp_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbServerConnectionIp.setLayoutData(gd);

				fGdbServerConnectionAddress = new Text(local, SWT.BORDER);
				gd = new GridData();
				gd.widthHint = 145;
				fGdbServerConnectionAddress.setLayoutData(gd);

				label = new Label(local, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.connectionAfter_Text")); //$NON-NLS-1$

			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.interface_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.interface_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			// layout.marginWidth = 0;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				fGdbInterfaceSwd = new Button(local, SWT.RADIO);
				fGdbInterfaceSwd.setText(Messages.getString("DebuggerTab.interfaceSWD_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbInterfaceSwd.setLayoutData(gd);

				fGdbInterfaceJtag = new Button(local, SWT.RADIO);
				fGdbInterfaceJtag.setText(Messages.getString("DebuggerTab.interfaceJtag_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbInterfaceJtag.setLayoutData(gd);
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerSpeed_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerSpeed_ToolTipText"));

			Composite radio = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			radio.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			radio.setLayoutData(gd);
			{
				fGdbServerSpeedAuto = new Button(radio, SWT.RADIO);
				fGdbServerSpeedAuto.setText(Messages.getString("DebuggerTab.gdbServerSpeedAuto_Text"));
				gd = new GridData();
				gd.widthHint = COLUMN_WIDTH;
				fGdbServerSpeedAuto.setLayoutData(gd);

				fGdbServerSpeedAdaptive = new Button(radio, SWT.RADIO);
				fGdbServerSpeedAdaptive.setText(Messages.getString("DebuggerTab.gdbServerSpeedAdaptive_Text"));
				gd.widthHint = COLUMN_WIDTH;
				fGdbServerSpeedAdaptive.setLayoutData(gd);

				fGdbServerSpeedFixed = new Button(radio, SWT.RADIO);
				fGdbServerSpeedFixed.setText(Messages.getString("DebuggerTab.gdbServerSpeedFixed_Text"));
				gd.widthHint = COLUMN_WIDTH;
				fGdbServerSpeedFixed.setLayoutData(gd);

				fGdbServerSpeedFixedValue = new Text(radio, SWT.BORDER);
				gd = new GridData();
				gd.widthHint = 40;
				fGdbServerSpeedFixedValue.setLayoutData(gd);

				label = new Label(radio, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.gdbServerSpeedFixedUnit_Text")); //$NON-NLS-1$
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerGdbPort_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

			fGdbServerGdbPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData();
			gd.widthHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerGdbPort.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerSwoPort_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerSwoPort_ToolTipText"));

			fGdbServerSwoPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData();
			gd.widthHint = 60;
			fGdbServerSwoPort.setLayoutData(gd);

			Composite empty = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			empty.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			empty.setLayoutData(gd);

			fDoGdbServerVerifyDownload = new Button(comp, SWT.CHECK);
			fDoGdbServerVerifyDownload.setText(Messages.getString("DebuggerTab.gdbServerVerifyDownload_Label"));
			fDoGdbServerVerifyDownload
					.setToolTipText(Messages.getString("DebuggerTab.gdbServerVerifyDownload_ToolTipText"));
			gd = new GridData();
			gd.horizontalIndent = 60;
			fDoGdbServerVerifyDownload.setLayoutData(gd);

			fDoGdbServerInitRegs = new Button(comp, SWT.CHECK);
			fDoGdbServerInitRegs.setText(Messages.getString("DebuggerTab.gdbServerInitRegs_Label"));
			fDoGdbServerInitRegs.setToolTipText(Messages.getString("DebuggerTab.gdbServerInitRegs_ToolTipText"));
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerTelnetPort_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerTelnetPort_ToolTipText"));

			fGdbServerTelnetPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData();
			gd.widthHint = 60;
			fGdbServerTelnetPort.setLayoutData(gd);

			Composite empty = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			empty.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			empty.setLayoutData(gd);

			fDoGdbServerLocalOnly = new Button(comp, SWT.CHECK);
			fDoGdbServerLocalOnly.setText(Messages.getString("DebuggerTab.gdbServerLocalOnly_Label"));
			fDoGdbServerLocalOnly.setToolTipText(Messages.getString("DebuggerTab.gdbServerLocalOnly_ToolTipText"));
			gd = new GridData();
			gd.horizontalIndent = 60;
			fDoGdbServerLocalOnly.setLayoutData(gd);

			fDoGdbServerSilent = new Button(comp, SWT.CHECK);
			fDoGdbServerSilent.setText(Messages.getString("DebuggerTab.gdbServerSilent_Label"));
			fDoGdbServerSilent.setToolTipText(Messages.getString("DebuggerTab.gdbServerSilent_ToolTipText"));
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerLog_Label")); //$NON-NLS-1$

			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				fGdbServerLog = new Text(local, SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbServerLog.setLayoutData(gd);

				fGdbServerLogBrowse = new Button(local, SWT.NONE);
				fGdbServerLogBrowse.setText(Messages.getString("DebuggerTab.gdbServerLogBrowse_Button"));
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerOther_Label")); //$NON-NLS-1$

			fGdbServerOtherOptions = new Text(comp, SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerOtherOptions.setLayoutData(gd);
		}

		{
			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.makeColumnsEqualWidth = true;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			local.setLayoutData(gd);

			fDoGdbServerAllocateConsole = new Button(local, SWT.CHECK);
			fDoGdbServerAllocateConsole.setText(Messages.getString("DebuggerTab.gdbServerAllocateConsole_Label"));
			fDoGdbServerAllocateConsole
					.setToolTipText(Messages.getString("DebuggerTab.gdbServerAllocateConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			fDoGdbServerAllocateConsole.setLayoutData(gd);

			fDoGdbServerAllocateSemihostingConsole = new Button(local, SWT.CHECK);
			fDoGdbServerAllocateSemihostingConsole
					.setText(Messages.getString("DebuggerTab.gdbServerAllocateSemihostingConsole_Label"));
			fDoGdbServerAllocateSemihostingConsole
					.setToolTipText(Messages.getString("DebuggerTab.gdbServerAllocateSemihostingConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			fDoGdbServerAllocateSemihostingConsole.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------

		VerifyListener numericVerifyListener = new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character) || Character.isISOControl(e.character));
			}
		};

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

		fDoConnectToRunning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// updateLaunchConfigurationDialog();

				propagateConnectToRunningChanged();
				fTabStartup.doConnectToRunningChanged(fDoConnectToRunning.getSelection());

				scheduleUpdateJob();
			}
		});

		fGdbServerExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
				updateGdbServerActualPath();
			}
		});

		fGdbServerBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("DebuggerTab.gdbServerExecutableBrowse_Title"),
						fGdbServerExecutable);
			}
		});

		fGdbServerVariablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(fGdbServerExecutable);
			}
		});

		fLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String text = e.text;
				if (Activator.getInstance().isDebugging()) {
					System.out.println(text);
				}

				int ret = -1;
				if ("global".equals(text)) {
					ret = PreferencesUtil.createPreferenceDialogOn(parent.getShell(), GlobalMcuPage.ID, null, null)
							.open();
				} else if ("workspace".equals(text)) {
					ret = PreferencesUtil.createPreferenceDialogOn(parent.getShell(), WorkspaceMcuPage.ID, null, null)
							.open();
				} else if ("project".equals(text)) {
					assert (fConfiguration != null);
					IProject project = EclipseUtils.getProjectByLaunchConfiguration(fConfiguration);
					ret = PreferencesUtil
							.createPropertyDialogOn(parent.getShell(), project, ProjectMcuPage.ID, null, null, 0)
							.open();
				}

				if (ret == Window.OK) {
					updateGdbServerActualPath();
				}
			}
		});

		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				fGdbServerConnectionAddress.setText("");
				scheduleUpdateJob();
			}
		};

		fGdbServerConnectionUsb.addSelectionListener(selectionAdapter);
		fGdbServerConnectionIp.addSelectionListener(selectionAdapter);

		fGdbServerConnectionAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fGdbServerConnectionAddress.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {

				if (fGdbServerConnectionUsb.getSelection()) {
					e.doit = Character.isDigit(e.character) || Character.isISOControl(e.character);
				} else if (fGdbServerConnectionIp.getSelection()) {
					e.doit = Character.isLetterOrDigit(e.character) || e.character == '.' || e.character == ':'
							|| e.character == '-' || e.character == '_' || Character.isISOControl(e.character);
				} else {
					e.doit = false;
				}
			}
		});

		fGdbInterfaceSwd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				fTabStartup.doInterfaceSwdChanged(true);
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fGdbInterfaceJtag.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				fTabStartup.doInterfaceSwdChanged(false);
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fGdbServerSpeedAuto.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fGdbServerSpeedFixedValue.setEnabled(false);
				scheduleUpdateJob();
			}
		});

		fGdbServerSpeedAdaptive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fGdbServerSpeedFixedValue.setEnabled(false);
				scheduleUpdateJob();
			}
		});

		fGdbServerSpeedFixed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fGdbServerSpeedFixedValue.setEnabled(true);
				scheduleUpdateJob();
			}
		});

		fGdbServerSpeedFixedValue.addVerifyListener(numericVerifyListener);

		fGdbServerSpeedFixedValue.addModifyListener(scheduleUpdateJobModifyListener);

		fGdbFlashDeviceName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// this will open the hyperlink in the default web browser
				Program.launch(event.text);
			}
		});

		fGdbEndiannessLittle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fGdbEndiannessBig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fGdbServerGdbPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// make the target port the same
				fTargetPortNumber.setText(fGdbServerGdbPort.getText());
				scheduleUpdateJob();
			}
		});

		fGdbServerGdbPort.addVerifyListener(numericVerifyListener);

		fGdbServerSwoPort.addModifyListener(scheduleUpdateJobModifyListener);
		fGdbServerSwoPort.addVerifyListener(numericVerifyListener);

		fGdbServerTelnetPort.addModifyListener(scheduleUpdateJobModifyListener);
		fGdbServerTelnetPort.addVerifyListener(numericVerifyListener);

		fDoGdbServerVerifyDownload.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		fDoGdbServerInitRegs.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		fDoGdbServerLocalOnly.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		fDoGdbServerSilent.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fGdbServerLog.addModifyListener(scheduleUpdateJobModifyListener);
		fGdbServerLogBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseSaveButtonSelected(Messages.getString("DebuggerTab.gdbServerLogBrowse_Title"), fGdbServerLog);
			}
		});

		fGdbServerOtherOptions.addModifyListener(scheduleUpdateJobModifyListener);

		fDoGdbServerAllocateConsole.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		fDoGdbServerAllocateSemihostingConsole.addSelectionListener(scheduleUpdateJobSelectionAdapter);
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

		fGdbClientOtherCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fGdbClientOtherOptions.addModifyListener(new ModifyListener() {
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

	private void updateGdbServerActualPath() {

		assert (fConfiguration != null);
		String fullCommand = Configuration.getGdbServerCommand(fConfiguration, fGdbServerExecutable.getText());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.updateActualpath() \"" + fullCommand + "\"");
		}
		fGdbServerPathLabel.setText(fullCommand);
	}

	private void updateGdbClientActualPath() {

		assert (fConfiguration != null);
		String fullCommand = Configuration.getGdbClientCommand(fConfiguration, fGdbClientExecutable.getText());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.updateGdbClientActualPath() \"" + fullCommand + "\"");
		}
		fGdbClientPathLabel.setText(fullCommand);
	}

	private void doStartGdbServerChanged() {

		boolean enabled = fDoStartGdbServer.getSelection();

		// gdbServerSpeedAuto.setSelection(enabled);
		// gdbServerSpeedAdaptive.setSelection(enabled);
		// gdbServerSpeedFixed.setSelection(enabled);
		// gdbServerSpeedFixedValue.setEnabled(enabled);

		fGdbServerExecutable.setEnabled(enabled);
		fGdbServerBrowseButton.setEnabled(enabled);
		fGdbServerVariablesButton.setEnabled(enabled);

		fDoConnectToRunning.setEnabled(enabled);
		// doConnectToRunning.setEnabled(false);

		fGdbFlashDeviceName.setEnabled(enabled);

		fGdbServerConnectionAddress.setEnabled(enabled);

		fGdbServerGdbPort.setEnabled(enabled);
		fGdbServerSwoPort.setEnabled(enabled);
		fGdbServerTelnetPort.setEnabled(enabled);

		fDoGdbServerVerifyDownload.setEnabled(enabled);
		fDoGdbServerInitRegs.setEnabled(enabled);
		fDoGdbServerLocalOnly.setEnabled(enabled);
		fDoGdbServerSilent.setEnabled(enabled);

		fGdbServerOtherOptions.setEnabled(enabled);

		fGdbServerLog.setEnabled(enabled);
		fGdbServerLogBrowse.setEnabled(enabled);

		// [#312]
		// Temporarily disable it, the logic to detect when 
		// the server is ready needs the console.
		// fDoGdbServerAllocateConsole.setEnabled(enabled);
		fDoGdbServerAllocateConsole.setEnabled(false);

		fDoGdbServerAllocateSemihostingConsole.setEnabled(enabled);

		// Disable remote target params when the server is started
		fTargetIpAddress.setEnabled(!enabled);
		fTargetPortNumber.setEnabled(!enabled);

		fGdbServerPathLabel.setEnabled(enabled);
		fLink.setEnabled(enabled);
	}

	private void propagateConnectToRunningChanged() {

		if (fDoStartGdbServer.getSelection()) {

			boolean enabled = fDoConnectToRunning.getSelection();

			fDoGdbServerInitRegs.setEnabled(!enabled);
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.initializeFrom() " + configuration.getName());
		}

		fConfiguration = configuration;

		try {
			Boolean booleanDefault;
			String stringDefault;

			// J-Link GDB Server Setup
			{
				// Start server locally
				booleanDefault = fPersistentPreferences.getGdbServerDoStart();
				fDoStartGdbServer.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanDefault));

				fDoConnectToRunning
						.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
								DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT));

				// Executable
				stringDefault = fPersistentPreferences.getGdbServerExecutable();
				fGdbServerExecutable.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringDefault));

				// If the project has assigned a device name, use it
				stringDefault = CProjectAttributes.getCmsisDeviceName(configuration);
				fSavedCmsisDeviceName = stringDefault;

				// Device name
				if (stringDefault == null || stringDefault.isEmpty()) {
					// Otherwise try the name used previously
					stringDefault = fPersistentPreferences.getFlashDeviceName();
				}

				String defaultDeviceName = configuration.getAttribute(ConfigurationAttributes.FLASH_DEVICE_NAME_COMPAT,
						stringDefault);
				String deviceName = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME,
						defaultDeviceName);
				fGdbFlashDeviceName.setText(deviceName);

				// Endianness
				stringDefault = fPersistentPreferences.getGdbServerEndianness();
				String defaultEndianness = configuration.getAttribute(ConfigurationAttributes.ENDIANNESS_COMPAT,
						stringDefault);
				String endianness = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_ENDIANNESS,
						defaultEndianness);
				if (DefaultPreferences.ENDIANNESS_LITTLE.equals(endianness))
					fGdbEndiannessLittle.setSelection(true);
				else if (DefaultPreferences.ENDIANNESS_BIG.equals(endianness))
					fGdbEndiannessBig.setSelection(true);
				else {
					String message = "unknown endianness " + endianness + ", using little";
					Activator.log(message);
					fGdbEndiannessLittle.setSelection(true);
				}

				// Connection
				stringDefault = fPersistentPreferences.getGdbServerConnection();

				String connection = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION,
						stringDefault);

				if (DefaultPreferences.GDB_SERVER_CONNECTION_USB.equals(connection)) {
					fGdbServerConnectionUsb.setSelection(true);
					fGdbServerConnectionIp.setSelection(false);
				} else if (DefaultPreferences.GDB_SERVER_CONNECTION_IP.equals(connection)) {
					fGdbServerConnectionUsb.setSelection(false);
					fGdbServerConnectionIp.setSelection(true);
				}

				// Connection address
				stringDefault = fPersistentPreferences.getGdbServerConnectionAddress();

				String connectionAddress = configuration
						.getAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS, stringDefault);
				fGdbServerConnectionAddress.setText(connectionAddress);

				// Interface
				stringDefault = fPersistentPreferences.getGdbServerInterface();

				String defaultPhysicalInterface = configuration.getAttribute(ConfigurationAttributes.INTERFACE_COMPAT,
						stringDefault);
				String physicalInterface = configuration
						.getAttribute(ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE, defaultPhysicalInterface);

				if (DefaultPreferences.INTERFACE_SWD.equals(physicalInterface)) {
					fGdbInterfaceSwd.setSelection(true);
					fTabStartup.doInterfaceSwdChanged(true);
				} else if (DefaultPreferences.INTERFACE_JTAG.equals(physicalInterface)) {
					fGdbInterfaceJtag.setSelection(true);
					fTabStartup.doInterfaceSwdChanged(false);
				} else {
					String message = "unknown interface " + physicalInterface + ", using swd";
					Activator.log(message);
					fGdbInterfaceSwd.setSelection(true);
				}

				// Initial speed
				stringDefault = fPersistentPreferences.getGdbServerInitialSpeed();

				String defaultPhysicalInterfaceSpeed = configuration
						.getAttribute(ConfigurationAttributes.GDB_SERVER_SPEED_COMPAT, stringDefault);

				String physicalInterfaceSpeed = configuration
						.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_SPEED, defaultPhysicalInterfaceSpeed);

				if (DefaultPreferences.INTERFACE_SPEED_AUTO.equals(physicalInterfaceSpeed)) {
					fGdbServerSpeedAuto.setSelection(true);
					fGdbServerSpeedAdaptive.setSelection(false);
					fGdbServerSpeedFixed.setSelection(false);

					fGdbServerSpeedFixedValue.setEnabled(false);

				} else if (DefaultPreferences.INTERFACE_SPEED_ADAPTIVE.equals(physicalInterfaceSpeed)) {
					fGdbServerSpeedAuto.setSelection(false);
					fGdbServerSpeedAdaptive.setSelection(true);
					fGdbServerSpeedFixed.setSelection(false);

					fGdbServerSpeedFixedValue.setEnabled(false);
				} else {
					try {
						Integer.parseInt(physicalInterfaceSpeed);
						fGdbServerSpeedAuto.setSelection(false);
						fGdbServerSpeedAdaptive.setSelection(false);
						fGdbServerSpeedFixed.setSelection(true);

						fGdbServerSpeedFixedValue.setEnabled(true);
						fGdbServerSpeedFixedValue.setText(physicalInterfaceSpeed);
					} catch (NumberFormatException e) {
						String message = "unknown interface speed " + physicalInterfaceSpeed + ", using auto";
						Activator.log(message);
						fGdbServerSpeedAuto.setSelection(true);
						fGdbServerSpeedFixedValue.setEnabled(false);
					}
				}

				// Ports
				fGdbServerGdbPort.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

				fGdbServerSwoPort.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT)));

				fGdbServerTelnetPort.setText(Integer
						.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

				// Flags
				fDoGdbServerVerifyDownload
						.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
								DefaultPreferences.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT));

				fDoGdbServerInitRegs
						.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
								DefaultPreferences.DO_GDB_SERVER_INIT_REGS_DEFAULT));

				fDoGdbServerLocalOnly
						.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
								DefaultPreferences.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT));

				fDoGdbServerSilent.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_SILENT,
						DefaultPreferences.DO_GDB_SERVER_SILENT_DEFAULT));

				// Log file
				fGdbServerLog.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
						DefaultPreferences.GDB_SERVER_LOG_DEFAULT));

				// Other options
				stringDefault = fPersistentPreferences.getGdbServerOtherOptions();
				fGdbServerOtherOptions
						.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringDefault));

				// Allocate server console
				fDoGdbServerAllocateConsole
						.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
								DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT));

				// Allocate semihosting console
				fDoGdbServerAllocateSemihostingConsole.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
								DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT));
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

			// Remote Target
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

				// useRemoteChanged();
			}

			doStartGdbServerChanged();
			propagateConnectToRunningChanged();

			// Force thread update
			boolean updateThreadsOnSuspend = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
					DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.initializeFrom() completed " + configuration.getName());
		}
	}

	public void initializeFromDefaults() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.initializeFromDefaults()");
		}

		String stringDefault;
		boolean booleanDefault;

		// J-Link GDB Server Setup
		{
			// Start server locally
			booleanDefault = fDefaultPreferences.getGdbServerDoStart();
			fDoStartGdbServer.setSelection(booleanDefault);

			fDoConnectToRunning.setSelection(DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT);

			// Executable
			stringDefault = fDefaultPreferences.getGdbServerExecutable();
			fGdbServerExecutable.setText(stringDefault);

			stringDefault = fSavedCmsisDeviceName != null ? fSavedCmsisDeviceName : "";
			fGdbFlashDeviceName.setText(stringDefault);

			// Endianness
			String endianness = fDefaultPreferences.getGdbServerEndianness();
			if (DefaultPreferences.ENDIANNESS_LITTLE.equals(endianness))
				fGdbEndiannessLittle.setSelection(true);
			else if (DefaultPreferences.ENDIANNESS_BIG.equals(endianness))
				fGdbEndiannessBig.setSelection(true);
			else {
				String message = "unknown endianness " + endianness + ", using little";
				Activator.log(message);
				fGdbEndiannessLittle.setSelection(true);
			}

			// Connection
			String connection = fDefaultPreferences.getGdbServerConnection();

			if (DefaultPreferences.GDB_SERVER_CONNECTION_USB.equals(connection)) {
				fGdbServerConnectionUsb.setSelection(true);
				fGdbServerConnectionIp.setSelection(false);
			} else if (DefaultPreferences.GDB_SERVER_CONNECTION_IP.equals(connection)) {
				fGdbServerConnectionUsb.setSelection(false);
				fGdbServerConnectionIp.setSelection(true);
			}

			// Connection address
			stringDefault = fDefaultPreferences.getGdbServerConnectionAddress();
			fGdbServerConnectionAddress.setText(stringDefault);

			// Interface
			String physicalInterface = fDefaultPreferences.getGdbServerInterface();

			if (DefaultPreferences.INTERFACE_SWD.equals(physicalInterface)) {
				fGdbInterfaceSwd.setSelection(true);
				fTabStartup.doInterfaceSwdChanged(true);
			} else if (DefaultPreferences.INTERFACE_JTAG.equals(physicalInterface)) {
				fGdbInterfaceJtag.setSelection(true);
				fTabStartup.doInterfaceSwdChanged(false);
			} else {
				String message = "unknown interface " + physicalInterface + ", using swd";
				Activator.log(message);
				fGdbInterfaceSwd.setSelection(true);
			}

			// Initial speed
			String physicalInterfaceSpeed = fDefaultPreferences.getGdbServerInitialSpeed();

			if (DefaultPreferences.INTERFACE_SPEED_AUTO.equals(physicalInterfaceSpeed)) {
				fGdbServerSpeedAuto.setSelection(true);
				fGdbServerSpeedAdaptive.setSelection(false);
				fGdbServerSpeedFixed.setSelection(false);

				fGdbServerSpeedFixedValue.setEnabled(false);

			} else if (DefaultPreferences.INTERFACE_SPEED_ADAPTIVE.equals(physicalInterfaceSpeed)) {
				fGdbServerSpeedAuto.setSelection(false);
				fGdbServerSpeedAdaptive.setSelection(true);
				fGdbServerSpeedFixed.setSelection(false);

				fGdbServerSpeedFixedValue.setEnabled(false);
			} else {
				try {
					Integer.parseInt(physicalInterfaceSpeed);
					fGdbServerSpeedAuto.setSelection(false);
					fGdbServerSpeedAdaptive.setSelection(false);
					fGdbServerSpeedFixed.setSelection(true);

					fGdbServerSpeedFixedValue.setEnabled(true);
					fGdbServerSpeedFixedValue.setText(physicalInterfaceSpeed);
				} catch (NumberFormatException e) {
					String message = "unknown interface speed " + physicalInterfaceSpeed + ", using auto";
					Activator.log(message);
					fGdbServerSpeedAuto.setSelection(true);
					fGdbServerSpeedFixedValue.setEnabled(false);
				}
			}

			// Ports
			fGdbServerGdbPort.setText(Integer.toString(DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT));

			fGdbServerSwoPort.setText(Integer.toString(DefaultPreferences.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT));

			fGdbServerTelnetPort.setText(Integer.toString(DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT));

			// Flags
			fDoGdbServerVerifyDownload.setSelection(DefaultPreferences.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT);

			fDoGdbServerInitRegs.setSelection(DefaultPreferences.DO_GDB_SERVER_INIT_REGS_DEFAULT);

			fDoGdbServerLocalOnly.setSelection(DefaultPreferences.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT);

			fDoGdbServerSilent.setSelection(DefaultPreferences.DO_GDB_SERVER_SILENT_DEFAULT);

			// Log file
			fGdbServerLog.setText(DefaultPreferences.GDB_SERVER_LOG_DEFAULT);

			// Other options
			stringDefault = fDefaultPreferences.getGdbServerOtherOptions();
			fGdbServerOtherOptions.setText(stringDefault);

			// Allocate server console
			fDoGdbServerAllocateConsole.setSelection(DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			// Allocate semihosting console
			fDoGdbServerAllocateSemihostingConsole
					.setSelection(DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
		}

		// GDB Client Setup
		{
			// Executable
			stringDefault = fDefaultPreferences.getGdbClientExecutable();
			fGdbClientExecutable.setText(stringDefault);

			// Other options
			stringDefault = fDefaultPreferences.getGdbClientOtherOptions();
			fGdbClientOtherOptions.setText(stringDefault);

			stringDefault = fDefaultPreferences.getGdbClientCommands();
			fGdbClientOtherCommands.setText(stringDefault);
		}

		// Remote Target
		{
			fTargetIpAddress.setText(DefaultPreferences.REMOTE_IP_ADDRESS_DEFAULT); // $NON-NLS-1$

			String portString = Integer.toString(DefaultPreferences.REMOTE_PORT_NUMBER_DEFAULT); // $NON-NLS-1$
			fTargetPortNumber.setText(portString);

			// useRemoteChanged();
		}

		doStartGdbServerChanged();
		propagateConnectToRunningChanged();

		// Force thread update
		fUpdateThreadlistOnSuspend.setSelection(DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
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
			System.out.println("jlink.TabDebugger.activated() " + workingCopy.getName());
		}
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.deactivated() " + workingCopy.getName());
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.isValid() " + launchConfig.getName());
		}

		setErrorMessage(null);
		setMessage(null);

		boolean result = true;

		if (fDoStartGdbServer != null && fDoStartGdbServer.getSelection()) {

			if (fGdbServerExecutable != null && fGdbServerExecutable.getText().trim().isEmpty()) {
				setErrorMessage("GDB server executable path?");
				result = false;
			}

			if (fGdbFlashDeviceName != null && fGdbFlashDeviceName.getText().trim().isEmpty()) {
				setErrorMessage("Device name?");
				result = false;
			}

			if (fGdbServerGdbPort != null && fGdbServerGdbPort.getText().trim().isEmpty()) {
				setErrorMessage("GDB port?");
				result = false;
			}

			if (fGdbServerSwoPort != null && fGdbServerSwoPort.getText().trim().isEmpty()) {
				setErrorMessage("SWO port?");
				result = false;
			}

			if (fGdbServerTelnetPort != null && fGdbServerTelnetPort.getText().trim().isEmpty()) {
				setErrorMessage("Telenet port?");
				result = false;
			}
		}

		if (fGdbClientExecutable != null && fGdbClientExecutable.getText().trim().isEmpty()) {
			setErrorMessage("GDB client executable name?");
			result = false;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.isValid() " + launchConfig.getName() + " = " + result);
		}

		return result;
	}

	@Override
	public boolean canSave() {
		if (fDoStartGdbServer != null && fDoStartGdbServer.getSelection()) {
			if (fGdbServerExecutable != null && fGdbServerExecutable.getText().trim().isEmpty())
				return false;

			if (fGdbFlashDeviceName != null && fGdbFlashDeviceName.getText().trim().isEmpty())
				return false;

			if (fGdbServerGdbPort != null && fGdbServerGdbPort.getText().trim().isEmpty())
				return false;

			if (fGdbServerSwoPort != null && fGdbServerSwoPort.getText().trim().isEmpty())
				return false;

			if (fGdbServerTelnetPort != null && fGdbServerTelnetPort.getText().trim().isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.performApply() " + configuration.getName() + ", dirty=" + isDirty());
		}

		{
			// legacy definition; although the jtag device class is not used,
			// it must be there, to avoid NPEs
			configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);
		}

		boolean booleanValue;
		String stringValue;

		// J-Link GDB server
		{
			// Start server
			booleanValue = fDoStartGdbServer.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanValue);
			fPersistentPreferences.putGdbServerDoStart(booleanValue);

			// Connect to running
			configuration.setAttribute(ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					fDoConnectToRunning.getSelection());

			// Executable
			stringValue = fGdbServerExecutable.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringValue);
			fPersistentPreferences.putGdbServerExecutable(stringValue);

			// Device name
			stringValue = fGdbFlashDeviceName.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, stringValue);
			fPersistentPreferences.putFlashDeviceName(stringValue);

			// Endianness
			stringValue = fDefaultPreferences.getGdbServerEndianness();
			if (fGdbEndiannessLittle.getSelection()) {
				stringValue = DefaultPreferences.ENDIANNESS_LITTLE;
			} else if (fGdbEndiannessBig.getSelection()) {
				stringValue = DefaultPreferences.ENDIANNESS_BIG;
			} else {
				String message = "endianness not selected, setting little";
				Activator.log(message);
			}
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_ENDIANNESS, stringValue);
			fPersistentPreferences.putGdbServerEndianness(stringValue);

			// Connection
			stringValue = fDefaultPreferences.getGdbServerConnection();
			if (fGdbServerConnectionUsb.getSelection()) {
				stringValue = DefaultPreferences.GDB_SERVER_CONNECTION_USB;

			} else if (fGdbServerConnectionIp.getSelection()) {
				stringValue = DefaultPreferences.GDB_SERVER_CONNECTION_IP;
			}
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION, stringValue);
			fPersistentPreferences.putGdbServerConnection(stringValue);

			// Connection address
			stringValue = fGdbServerConnectionAddress.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS, stringValue);
			fPersistentPreferences.putGdbServerConnectionAddress(stringValue);

			// Interface
			stringValue = fDefaultPreferences.getGdbServerInterface();
			if (fGdbInterfaceSwd.getSelection()) {
				stringValue = DefaultPreferences.INTERFACE_SWD;
			} else if (fGdbInterfaceJtag.getSelection()) {
				stringValue = DefaultPreferences.INTERFACE_JTAG;
			} else {
				String message = "interface not selected, setting swd";
				Activator.log(message);
				fGdbInterfaceSwd.setSelection(true);
			}
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE, stringValue);
			fPersistentPreferences.putGdbServerInterface(stringValue);

			// Initial speed
			stringValue = fDefaultPreferences.getGdbServerInitialSpeed();
			if (fGdbServerSpeedAuto.getSelection()) {
				stringValue = DefaultPreferences.INTERFACE_SPEED_AUTO;
			} else if (fGdbServerSpeedAdaptive.getSelection()) {
				stringValue = DefaultPreferences.INTERFACE_SPEED_ADAPTIVE;
			} else if (fGdbServerSpeedFixed.getSelection()) {
				if (!fGdbServerSpeedFixedValue.getText().trim().isEmpty()) {
					stringValue = fGdbServerSpeedFixedValue.getText().trim();
				} else {
					Activator.log("empty fGdbServerSpeedFixedValue");
				}
			}
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_SPEED, stringValue);
			fPersistentPreferences.putGdbServerInitialSpeed(stringValue);

			// Ports
			int port;
			if (!fGdbServerGdbPort.getText().trim().isEmpty()) {
				port = Integer.parseInt(fGdbServerGdbPort.getText().trim());
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER, port);
			} else {
				Activator.log("empty fGdbServerGdbPort");
			}

			if (!fGdbServerSwoPort.getText().trim().isEmpty()) {
				port = Integer.parseInt(fGdbServerSwoPort.getText().trim());
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER, port);
			} else {
				Activator.log("empty fGdbServerSwoPort");
			}

			if (!fGdbServerTelnetPort.getText().trim().isEmpty()) {
				port = Integer.parseInt(fGdbServerTelnetPort.getText().trim());
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER, port);
			} else {
				Activator.log("empty fGdbServerTelnetPort");
			}

			// Flags
			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
					fDoGdbServerVerifyDownload.getSelection());

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
					fDoGdbServerInitRegs.getSelection());

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
					fDoGdbServerLocalOnly.getSelection());

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_SILENT, fDoGdbServerSilent.getSelection());

			// Log file
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG, fGdbServerLog.getText().trim());

			// Other options
			stringValue = fGdbServerOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringValue);
			fPersistentPreferences.putGdbServerOtherOptions(stringValue);

			// Allocate server console
			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					fDoGdbServerAllocateConsole.getSelection());

			// Allocate semihosting console
			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
					fDoGdbServerAllocateSemihostingConsole.getSelection());
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

		// Remote target
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
					"jlink.TabDebugger.performApply() completed " + configuration.getName() + ", dirty=" + isDirty());
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.TabDebugger.setDefaults() " + configuration.getName());
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

		// J-Link GDB server setup
		{
			defaultBoolean = fPersistentPreferences.getGdbServerDoStart();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, defaultBoolean);

			configuration.setAttribute(ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT);

			defaultString = fPersistentPreferences.getGdbServerExecutable();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, defaultString);

			String sharedName = CProjectAttributes.getCmsisDeviceName(configuration);
			if (sharedName == null || sharedName.isEmpty()) {
				sharedName = fPersistentPreferences.getFlashDeviceName();
			}
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, sharedName);

			defaultString = fPersistentPreferences.getGdbServerEndianness();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_ENDIANNESS, defaultString);

			defaultString = fPersistentPreferences.getGdbServerConnection();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION, defaultString);

			defaultString = fPersistentPreferences.getGdbServerConnectionAddress();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS, defaultString);

			defaultString = fPersistentPreferences.getGdbServerInterface();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE, defaultString);

			defaultString = fPersistentPreferences.getGdbServerInitialSpeed();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_SPEED, defaultString);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
					DefaultPreferences.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
					DefaultPreferences.DO_GDB_SERVER_INIT_REGS_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
					DefaultPreferences.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_SILENT,
					DefaultPreferences.DO_GDB_SERVER_SILENT_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					DefaultPreferences.GDB_SERVER_LOG_DEFAULT);

			defaultString = fPersistentPreferences.getGdbServerOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, defaultString);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
		}

		// J-Link GDB client setup
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
				DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
