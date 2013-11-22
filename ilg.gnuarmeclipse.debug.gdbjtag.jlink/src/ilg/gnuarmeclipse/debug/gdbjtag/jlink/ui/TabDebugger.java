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

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import ilg.gnuarmeclipse.debug.gdbjtag.jlink.PreferenceConstants;

import java.io.File;

import org.eclipse.cdt.debug.gdbjtag.core.Activator;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.GDBJtagDeviceContribution;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.GDBJtagDeviceContributionFactory;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.debug.mi.core.IMILaunchConfigurationConstants;
import org.eclipse.cdt.debug.mi.core.MIPlugin;
import org.eclipse.cdt.debug.mi.core.command.factories.CommandFactoryDescriptor;
import org.eclipse.cdt.debug.mi.core.command.factories.CommandFactoryManager;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

/**
 * @since 7.0
 */
public class TabDebugger extends AbstractLaunchConfigurationTab {

	private static final String TAB_NAME = "Debugger";
	private static final String TAB_ID = Activator.PLUGIN_ID
			+ ".ui.debuggertab";

	private Button doStartGdbServer;
	private Text gdbCommand;
	// private Button useRemote;
	// private Combo jtagDevice;
	// private Composite remoteConnectionParameters;
	// private StackLayout remoteConnectParmsLayout;
	// private Composite remoteTcpipBox;
	private Text ipAddress;
	private Text portNumber;
	// private Composite remoteConnectionBox;
	// private Text connection;
	// private String savedJtagDevice;
	private Text deviceName;
	private Button endiannessLittle;
	private Button endiannessBig;
	private Button interfaceJtag;
	private Button interfaceSwd;

	private Button interfaceSpeedAuto;
	private Button interfaceSpeedAdaptive;
	private Button interfaceSpeedFixed;
	private Text interfaceSpeedFixedValue;

	private Button noReset;

	protected Button fUpdateThreadlistOnSuspend;

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

		createCommandControl(comp);


		createRemoteControl(comp);
		createDeviceControl(comp);
		createInterfaceControl(comp);

		createGdbServerGroup(comp);
		
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

	private void variablesButtonSelected(Text text) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(
				getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
	}

	private void createCommandControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout groupLayout = new GridLayout();
		group.setLayout(groupLayout);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);
		group.setText(Messages.getString("DebuggerTab.gdbSetupGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		comp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalSpan = 1;
		comp.setLayoutData(gd);

		Label label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.gdbCommandLabel"));
		gd = new GridData();
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		gdbCommand = new Text(comp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gdbCommand.setLayoutData(gd);
		gdbCommand.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		Button button = new Button(comp, SWT.NONE);
		button.setText(Messages.getString("DebuggerTab.gdbCommandBrowse"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages
						.getString("DebuggerTab.gdbCommandBrowse_Title"),
						gdbCommand);
			}
		});

		button = new Button(comp, SWT.NONE);
		button.setText(Messages.getString("DebuggerTab.gdbCommandVariable"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(gdbCommand);
			}
		});
	}

	private void createRemoteControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout groupLayout = new GridLayout();
		group.setLayout(groupLayout);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);
		group.setText(Messages.getString("DebuggerTab.remoteGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		comp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalSpan = 1;
		comp.setLayoutData(gd);

		// remoteConnectionParameters = new Composite(group, SWT.NO_TRIM
		// | SWT.NO_FOCUS);
		// remoteConnectParmsLayout = new StackLayout();
		// remoteConnectionParameters.setLayout(remoteConnectParmsLayout);

		//
		// Create entry fields for TCP/IP connections
		//
		Label label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.ipAddressLabel")); //$NON-NLS-1$

		ipAddress = new Text(comp, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 125;
		ipAddress.setLayoutData(gd);

		label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.portNumberLabel")); //$NON-NLS-1$

		portNumber = new Text(comp, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 125;
		portNumber.setLayoutData(gd);

		//
		// Add watchers for user data entry
		//
		ipAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
		portNumber.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character)
						|| Character.isISOControl(e.character);
			}
		});
		portNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

	}

	private void createDeviceControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout groupLayout = new GridLayout();
		group.setLayout(groupLayout);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);
		group.setText(Messages.getString("DebuggerTab.deviceGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		comp.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalSpan = 1;
		comp.setLayoutData(gd);

		Label label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.deviceName_Label")); //$NON-NLS-1$
		label.setToolTipText(Messages
				.getString("DebuggerTab.deviceName_ToolTipText"));

		deviceName = new Text(comp, SWT.BORDER);
		gd = new GridData();
		gd.widthHint = 180;
		// gd.horizontalSpan = 1;
		deviceName.setLayoutData(gd);

		Link link = new Link(comp, SWT.NONE);
		link.setText(Messages.getString("DebuggerTab.deviceName_Link"));
		gd = new GridData(SWT.RIGHT);
		link.setLayoutData(gd);
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				// this will open the hyperlink in the default web browser
				Program.launch(event.text);
			}

		});

		label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.endianness_Label")); //$NON-NLS-1$

		Composite endiannessGroup = new Composite(comp, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		endiannessGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		endiannessGroup.setLayout(layout);

		endiannessLittle = new Button(endiannessGroup, SWT.RADIO);
		endiannessLittle.setText(Messages
				.getString("DebuggerTab.endiannesslittle_Text"));
		gd = new GridData();
		endiannessLittle.setLayoutData(gd);

		endiannessBig = new Button(endiannessGroup, SWT.RADIO);
		endiannessBig.setText(Messages
				.getString("DebuggerTab.endiannessBig_Text"));
		gd = new GridData();
		endiannessBig.setLayoutData(gd);

		// Label label;
	}

	private void createInterfaceControl(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout groupLayout = new GridLayout();
		group.setLayout(groupLayout);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);
		group.setText(Messages.getString("DebuggerTab.interfaceGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		
		comp.setLayout(layout);
		GridData gd = new GridData();
		comp.setLayoutData(gd);

		Label label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.interface_Label")); //$NON-NLS-1$
		label.setToolTipText(Messages
				.getString("DebuggerTab.interface_ToolTipText"));

		Composite interfaceGroup = new Composite(comp, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;

		gd = new GridData();
		interfaceGroup.setLayoutData(gd);
		interfaceGroup.setLayout(layout);

		interfaceSwd = new Button(interfaceGroup, SWT.RADIO);
		interfaceSwd.setText(Messages
				.getString("DebuggerTab.interfaceSWD_Text"));
		interfaceSwd.setLayoutData(new GridData());

		interfaceJtag = new Button(interfaceGroup, SWT.RADIO);
		interfaceJtag.setText(Messages
				.getString("DebuggerTab.interfaceJtag_Text"));
		interfaceJtag.setLayoutData(new GridData());

		label = new Label(comp, SWT.NONE);
		label.setText(Messages.getString("DebuggerTab.interfaceSpeed_Label")); //$NON-NLS-1$
		label.setToolTipText(Messages
				.getString("DebuggerTab.interfaceSpeed_ToolTipText"));

		Composite interfaceSpeedGroup = new Composite(comp, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 4;
		interfaceSpeedGroup.setLayoutData(new GridData());
		interfaceSpeedGroup.setLayout(layout);

		interfaceSpeedAuto = new Button(interfaceSpeedGroup, SWT.RADIO);
		interfaceSpeedAuto.setText(Messages
				.getString("DebuggerTab.interfaceSpeedAuto_Text"));
		interfaceSpeedAuto.setLayoutData(new GridData());

		interfaceSpeedAdaptive = new Button(interfaceSpeedGroup, SWT.RADIO);
		interfaceSpeedAdaptive.setText(Messages
				.getString("DebuggerTab.interfaceSpeedAdaptive_Text"));
		interfaceSpeedAdaptive.setLayoutData(new GridData());

		interfaceSpeedFixed = new Button(interfaceSpeedGroup, SWT.RADIO);
		interfaceSpeedFixed.setText(Messages
				.getString("DebuggerTab.interfaceSpeedFixed_Text"));
		interfaceSpeedFixed.setLayoutData(new GridData());

		interfaceSpeedFixedValue = new Text(interfaceSpeedGroup, SWT.BORDER);
		interfaceSpeedFixedValue.setLayoutData(new GridData());

		noReset = new Button(comp, SWT.CHECK);
		noReset.setText(Messages.getString("DebuggerTab.noReset_Text"));
		noReset.setToolTipText(Messages
				.getString("DebuggerTab.noReset_ToolTipText"));
		gd = new GridData();
		gd.horizontalSpan = 2;
		noReset.setLayoutData(gd);

	}
	
	

	private void createGdbServerGroup(Composite parent) {
		
		Group group = new Group(parent, SWT.NONE);
		GridLayout groupLayout = new GridLayout();
		group.setLayout(groupLayout);
		GridData groupGrid = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGrid);
		group.setText(Messages.getString("DebuggerTab.gdbServerGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		comp.setLayout(layout);
		GridData gd = new GridData();
		comp.setLayoutData(gd);

		doStartGdbServer = new Button(comp, SWT.CHECK);
		doStartGdbServer.setText(Messages
				.getString("DebuggerTab.doStartGdbServer_Text"));
		doStartGdbServer.setToolTipText(Messages
				.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
		gd = new GridData();
		gd.horizontalSpan = 3;
		doStartGdbServer.setLayoutData(gd);

	}
	
	/**
	 * @param text
	 */
	// @SuppressWarnings("deprecation")
	// protected void updateDeviceIpPort(String selectedDeviceName) {
	// if (selectedDeviceName.equals(savedJtagDevice)) {
	// return;
	// }
	// GDBJtagDeviceContribution[] availableDevices =
	// GDBJtagDeviceContributionFactory
	// .getInstance().getGDBJtagDeviceContribution();
	// IGDBJtagDevice selectedDevice = null;
	// for (int i = 0; i < availableDevices.length; i++) {
	// String name = availableDevices[i].getDeviceName();
	// if (name.equals(selectedDeviceName)) {
	// selectedDevice = availableDevices[i].getDevice();
	// if (selectedDevice != null) {
	// if (selectedDevice instanceof IGDBJtagConnection) {
	// IGDBJtagConnection connectionDevice = (IGDBJtagConnection)
	// selectedDevice;
	// connection.setText(connectionDevice
	// .getDefaultDeviceConnection());
	// } else {
	// // support for deprecated TCP/IP based methods
	// ipAddress.setText(selectedDevice.getDefaultIpAddress());
	// portNumber.setText(selectedDevice
	// .getDefaultPortNumber());
	// }
	// useRemoteChanged();
	// updateLaunchConfigurationDialog();
	// break;
	// }
	// }
	// }
	// }

	private void useRemoteChanged() {
		boolean enabled = true; // useRemote.getSelection();
		// jtagDevice.setEnabled(enabled);
		ipAddress.setEnabled(enabled);
		portNumber.setEnabled(enabled);
		// connection.setEnabled(enabled);

		// GDBJtagDeviceContribution selectedDeviceEntry =
		// findJtagDeviceByName(jtagDevice
		// .getText());
		// if ((selectedDeviceEntry == null)
		// || (selectedDeviceEntry.getDevice() == null)) {
		// remoteConnectParmsLayout.topControl = null;
		// remoteConnectionParameters.layout();
		// } else {
		// IGDBJtagDevice device = selectedDeviceEntry.getDevice();
		// if (device instanceof IGDBJtagConnection) {
		// remoteConnectParmsLayout.topControl = remoteConnectionBox;
		// remoteConnectionBox.getParent().layout();
		// } else {

		// remoteConnectParmsLayout.topControl = remoteTcpipBox;
		// remoteTcpipBox.getParent().layout();
		// }
		// }
	}

	private GDBJtagDeviceContribution findJtagDeviceByName(String name) {
		GDBJtagDeviceContribution[] availableDevices = GDBJtagDeviceContributionFactory
				.getInstance().getGDBJtagDeviceContribution();
		for (GDBJtagDeviceContribution device : availableDevices) {
			if (device.getDeviceName().equals(name)) {
				return device;
			}
		}
		return null;
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String defaultGdbCommand = Platform
					.getPreferencesService()
					.getString(
							GdbPlugin.PLUGIN_ID,
							IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND,
							"", null); //$NON-NLS-1$
			String gdbCommandAttr = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					defaultGdbCommand);
			gdbCommand.setText(gdbCommandAttr);

			// boolean useRemoteAttr = configuration.getAttribute(
			// IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
			// IGDBJtagConstants.DEFAULT_USE_REMOTE_TARGET);
			// useRemote.setSelection(useRemoteAttr);

			// savedJtagDevice = configuration.getAttribute(
			// IGDBJtagConstants.ATTR_JTAG_DEVICE, "");
			// if (savedJtagDevice.length() == 0) {
			// jtagDevice.select(0);
			// } else {
			String storedAddress = ""; //$NON-NLS-1$
			int storedPort = 0;
			//				String storedConnection = ""; //$NON-NLS-1$

			// for (int i = 0; i < jtagDevice.getItemCount(); i++) {
			// if (jtagDevice.getItem(i).equals(savedJtagDevice)) {
			storedAddress = configuration.getAttribute(
					IGDBJtagConstants.ATTR_IP_ADDRESS, ""); //$NON-NLS-1$
			storedPort = configuration.getAttribute(
					IGDBJtagConstants.ATTR_PORT_NUMBER, 0);
			// storedConnection = configuration.getAttribute(
			//								IGDBJtagConstants.ATTR_CONNECTION, ""); //$NON-NLS-1$
			// jtagDevice.select(i);
			// break;
			// }
			// }

			// if (storedConnection != null) {
			// try {
			// connection.setText(new URI(storedConnection)
			// .getSchemeSpecificPart());
			// } catch (URISyntaxException e) {
			// Activator.log(e);
			// }
			// }
			if (storedAddress != null && storedAddress.length() > 0) {
				// Treat as legacy network probe
				ipAddress.setText(storedAddress);
				String portString = (0 < storedPort) && (storedPort <= 65535) ? Integer
						.valueOf(storedPort).toString() : ""; //$NON-NLS-1$
				portNumber.setText(portString);
			} else {
				ipAddress.setText("localhost");
				portNumber.setText("2331");
			}
			// }
			boolean updateThreadsOnSuspend = configuration
					.getAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
							IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

			noReset.setSelection(false);
			doStartGdbServer.setEnabled(false);
			
			useRemoteChanged();
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
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
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbCommand
						.getText().trim());
		configuration.setAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, gdbCommand
						.getText().trim()); // DSF
		// savedJtagDevice = jtagDevice.getText();
		// configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE,
		// savedJtagDevice);

		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
				true); // useRemote.getSelection());

		// if (savedJtagDevice.length() > 0) {
		try {
			// IGDBJtagDevice device = findJtagDeviceByName(
			// jtagDevice.getText()).getDevice();
			// if (device instanceof IGDBJtagConnection) {
			// String conn = connection.getText().trim();
			//					URI uri = new URI("gdb", conn, ""); //$NON-NLS-1$ //$NON-NLS-2$
			// configuration.setAttribute(
			// IGDBJtagConstants.ATTR_CONNECTION, uri.toString());
			// } else {
			String ip = ipAddress.getText().trim();
			configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, ip);
			int port = Integer.valueOf(portNumber.getText().trim()).intValue();
			configuration
					.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
			// }
			// } catch (URISyntaxException e) {
			// Activator.log(e);
		} catch (NumberFormatException e) {
			Activator.log(e);
		}
		// }
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						fUpdateThreadlistOnSuspend.getSelection());
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		String defaultGdbCommand = Platform.getPreferencesService().getString(
				GdbPlugin.PLUGIN_ID, PreferenceConstants.DEFAULT_GDB_COMMAND,
				"", null); //$NON-NLS-1$
		configuration.setAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
				defaultGdbCommand);

		CommandFactoryManager cfManager = MIPlugin.getDefault()
				.getCommandFactoryManager();
		CommandFactoryDescriptor defDesc = cfManager
				.getDefaultDescriptor(IGDBJtagConstants.DEBUGGER_ID);
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUGGER_COMMAND_FACTORY,
				defDesc.getName());
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUGGER_PROTOCOL,
				defDesc.getMIVersions()[0]);
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUGGER_VERBOSE_MODE,
				IMILaunchConfigurationConstants.DEBUGGER_VERBOSE_MODE_DEFAULT);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
				IGDBJtagConstants.DEFAULT_USE_REMOTE_TARGET);
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
	}

}
