/*******************************************************************************
 *  Copyright (c) 2008 QNX Software Systems and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     QNX Software Systems - initial API and implementation
 *     Liviu Ionescu - Arm version
 *     Chris Reed - pyOCD changes
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui;

import org.eclipse.osgi.util.NLS;

public class Messages {

	// ------------------------------------------------------------------------

	private static final String MESSAGES = "org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.messages"; //$NON-NLS-1$

	public static String DebuggerTab_doStartGdbServer_Text;
	public static String DebuggerTab_doStartGdbServer_ToolTipText;
	public static String DebuggerTab_gdbCommand_Label;
	public static String DebuggerTab_gdbCommand_ToolTipText;
	public static String DebuggerTab_gdbCommandActualPath_Label;
	public static String DebuggerTab_gdbCommandBrowse_Title;
	public static String DebuggerTab_gdbCommandBrowse;
	public static String DebuggerTab_gdbCommandVariable;
	public static String DebuggerTab_gdbOtherCommands_Label;
	public static String DebuggerTab_gdbOtherCommands_ToolTipText;
	public static String DebuggerTab_gdbOtherOptions_Label;
	public static String DebuggerTab_gdbOtherOptions_ToolTipText;
	public static String DebuggerTab_gdbServerActualPath_Label;
	public static String DebuggerTab_gdbServerActualPath_link;
	public static String DebuggerTab_gdbServerAllocateConsole_Label;
	public static String DebuggerTab_gdbServerAllocateConsole_ToolTipText;
	public static String DebuggerTab_gdbServerAllocateTelnetConsole_Label;
	public static String DebuggerTab_gdbServerAllocateTelnetConsole_ToolTipText;
	public static String DebuggerTab_gdbServerBusSpeed_Label;
	public static String DebuggerTab_gdbServerBusSpeed_ToolTipText;
	public static String DebuggerTab_gdbServerBusSpeedUnits_Label;
	public static String DebuggerTab_gdbServerConnectMode_Label;
	public static String DebuggerTab_gdbServerConnectMode_ToolTipText;
	public static String DebuggerTab_gdbServerConnectMode_Attach;
	public static String DebuggerTab_gdbServerConnectMode_Halt;
	public static String DebuggerTab_gdbServerConnectMode_PreReset;
	public static String DebuggerTab_gdbServerConnectMode_UnderReset;
	public static String DebuggerTab_gdbServerDefaultTargetType_Label;
	public static String DebuggerTab_gdbServerDefaultTargetType_ToolTipText;
	public static String DebuggerTab_gdbServerEnableSemihosting_Label;
	public static String DebuggerTab_gdbServerEnableSemihosting_ToolTipText;
	public static String DebuggerTab_gdbServerExecutable_Label;
	public static String DebuggerTab_gdbServerExecutable_ToolTipText;
	public static String DebuggerTab_gdbServerExecutableBrowse_Title;
	public static String DebuggerTab_gdbServerExecutableBrowse;
	public static String DebuggerTab_gdbServerExecutableVariable;
	public static String DebuggerTab_gdbServerFlashMode_Label;
	public static String DebuggerTab_gdbServerFlashMode_ToolTipText;
	public static String DebuggerTab_gdbServerFlashMode_AutoErase;
	public static String DebuggerTab_gdbServerFlashMode_ChipErase;
	public static String DebuggerTab_gdbServerFlashMode_SectorErase;
	public static String DebuggerTab_gdbServerGdbPort_Label;
	public static String DebuggerTab_gdbServerGdbPort_ToolTipText;
	public static String DebuggerTab_gdbServerGroup_Text;
	public static String DebuggerTab_gdbServerHaltAtHardFault_Label;
	public static String DebuggerTab_gdbServerHaltAtHardFault_ToolTipText;
	public static String DebuggerTab_gdbServerLog_Label;
	public static String DebuggerTab_gdbServerLogBrowse_Button;
	public static String DebuggerTab_gdbServerLogBrowse_Title;
	public static String DebuggerTab_gdbServerOther_Label;
	public static String DebuggerTab_gdbServerOther_ToolTipText;
	public static String DebuggerTab_gdbServerOverrideTarget_Label;
	public static String DebuggerTab_gdbServerOverrideTarget_ToolTipText;
	public static String DebuggerTab_gdbServerProbeId_Label;
	public static String DebuggerTab_gdbServerProbeId_ToolTipText;
	public static String DebuggerTab_gdbServerRefreshing_Label;
	public static String DebuggerTab_gdbServerRefreshProbes_Label;
	public static String DebuggerTab_gdbServerResetType_Label;
	public static String DebuggerTab_gdbServerResetType_ToolTipText;
	public static String DebuggerTab_gdbServerResetType_Default;
	public static String DebuggerTab_gdbServerResetType_Hardware;
	public static String DebuggerTab_gdbServerResetType_SoftwareDefault;
	public static String DebuggerTab_gdbServerResetType_SoftwareEmulated;
	public static String DebuggerTab_gdbServerResetType_SoftwareSysResetReq;
	public static String DebuggerTab_gdbServerResetType_SoftwareVectReset;
	public static String DebuggerTab_gdbServerSelectProbe;
	public static String DebuggerTab_gdbServerSmartFlash_Label;
	public static String DebuggerTab_gdbServerSmartFlash_ToolTipText;
	public static String DebuggerTab_gdbServerStepIntoInterrupts_Label;
	public static String DebuggerTab_gdbServerStepIntoInterrupts_ToolTipText;
	public static String DebuggerTab_gdbServerTargetName_ToolTipText;
	public static String DebuggerTab_gdbServerTelnetPort_Label;
	public static String DebuggerTab_gdbServerTelnetPort_ToolTipText;
	public static String DebuggerTab_gdbServerUnconnectedProbe;
	public static String DebuggerTab_gdbServerUseGdbSyscallsForSemihosting_Label;
	public static String DebuggerTab_gdbServerUseGdbSyscallsForSemihosting_ToolTipText;
	public static String DebuggerTab_gdbSetupGroup_Text;
	public static String DebuggerTab_interfaceGroup_Text;
	public static String DebuggerTab_invalid_gdbclient_executable;
	public static String DebuggerTab_invalid_gdbserver_port;
	public static String DebuggerTab_invalid_pyocd_executable;
	public static String DebuggerTab_invalid_telnet_port;
	public static String DebuggerTab_ipAddressLabel;
	public static String DebuggerTab_ipAddressWarningDecoration;
	public static String DebuggerTab_loading_data_from_pyocd;
	public static String DebuggerTab_noReset_Text;
	public static String DebuggerTab_noReset_ToolTipText;
	public static String DebuggerTab_old_pyocd_executable;
	public static String DebuggerTab_portNumberLabel;
	public static String DebuggerTab_portNumberWarningDecoration;
	public static String DebuggerTab_probes_failure_invoking_pyocd;
	public static String DebuggerTab_probes_failure_parsing_output;
	public static String DebuggerTab_probes_pyocd_timeout;
	public static String DebuggerTab_remoteGroup_Text;
	public static String DebuggerTab_restoreDefaults_Link;
	public static String DebuggerTab_restoreDefaults_ToolTipText;
	public static String DebuggerTab_targets_failure_invoking_pyocd;
	public static String DebuggerTab_targets_failure_parsing_output;
	public static String DebuggerTab_targets_pyocd_timeout;
	public static String DebuggerTab_update_thread_list_on_suspend_Text;
	public static String DebuggerTab_update_thread_list_on_suspend_ToolTipText;
	public static String GDBJtagDebuggerTab_commandFactoryLabel;
	public static String GDBJtagDebuggerTab_connectionLabel;
	public static String GDBJtagDebuggerTab_gdbCommandBrowse_Title;
	public static String GDBJtagDebuggerTab_gdbCommandBrowse;
	public static String GDBJtagDebuggerTab_gdbCommandLabel;
	public static String GDBJtagDebuggerTab_gdbCommandVariable;
	public static String GDBJtagDebuggerTab_gdbSetupGroup_Text;
	public static String GDBJtagDebuggerTab_ipAddressLabel;
	public static String GDBJtagDebuggerTab_jtagDeviceLabel;
	public static String GDBJtagDebuggerTab_miProtocolLabel;
	public static String GDBJtagDebuggerTab_portNumberLabel;
	public static String GDBJtagDebuggerTab_remoteGroup_Text;
	public static String GDBJtagDebuggerTab_update_thread_list_on_suspend;
	public static String GDBJtagDebuggerTab_useRemote_Text;
	public static String GDBJtagDebuggerTab_verboseModeLabel;
	public static String GDBJtagStartupTab_doHalt_Text;
	public static String GDBJtagStartupTab_doReset_Text;
	public static String GDBJtagStartupTab_FileBrowse_Label;
	public static String GDBJtagStartupTab_FileBrowse_Message;
	public static String GDBJtagStartupTab_FileBrowseWs_Label;
	public static String GDBJtagStartupTab_FileBrowseWs_Message;
	public static String GDBJtagStartupTab_imageFileBrowse_Title;
	public static String GDBJtagStartupTab_imageFileBrowseWs_Title;
	public static String GDBJtagStartupTab_imageFileName_does_not_exist;
	public static String GDBJtagStartupTab_imageFileName_not_specified;
	public static String GDBJtagStartupTab_imageLabel_Text;
	public static String GDBJtagStartupTab_imageOffset_not_specified;
	public static String GDBJtagStartupTab_imageOffsetLabel_Text;
	public static String GDBJtagStartupTab_initGroup_Text;
	public static String GDBJtagStartupTab_loadGroup_Text;
	public static String GDBJtagStartupTab_loadImage_Text;
	public static String GDBJtagStartupTab_loadSymbols_Text;
	public static String GDBJtagStartupTab_pcRegister_not_specified;
	public static String GDBJtagStartupTab_runGroup_Text;
	public static String GDBJtagStartupTab_runOptionGroup_Text;
	public static String GDBJtagStartupTab_setPcRegister_Text;
	public static String GDBJtagStartupTab_setResume_Text;
	public static String GDBJtagStartupTab_setStopAt_Text;
	public static String GDBJtagStartupTab_stopAt_not_specified;
	public static String GDBJtagStartupTab_symbolsFileBrowse_Title;
	public static String GDBJtagStartupTab_symbolsFileBrowseWs_Title;
	public static String GDBJtagStartupTab_symbolsFileName_does_not_exist;
	public static String GDBJtagStartupTab_symbolsFileName_not_specified;
	public static String GDBJtagStartupTab_symbolsLabel_Text;
	public static String GDBJtagStartupTab_symbolsOffset_not_specified;
	public static String GDBJtagStartupTab_symbolsOffsetLabel_Text;
	public static String GDBJtagStartupTab_useFile_Label;
	public static String GDBJtagStartupTab_useProjectBinary_Label;
	public static String GDBJtagStartupTab_useProjectBinary_ToolTip;
	public static String GlobalMcuPagePropertyPage_description;
	public static String McuPage_executable_folder;
	public static String McuPage_executable_label;
	public static String ProjectMcuPagePropertyPage_description;
	public static String StartupTab_doContinue_Text;
	public static String StartupTab_doContinue_ToolTipText;
	public static String StartupTab_doDebugInRam_Text;
	public static String StartupTab_doDebugInRam_ToolTipText;
	public static String StartupTab_doFirstReset_Text;
	public static String StartupTab_doFirstReset_ToolTipText;
	public static String StartupTab_doSecondReset_Text;
	public static String StartupTab_doSecondReset_ToolTipText;
	public static String StartupTab_enableSemihosting_Text;
	public static String StartupTab_enableSemihosting_ToolTipText;
	public static String StartupTab_FileBrowse_Label;
	public static String StartupTab_FileBrowseWs_Label;
	public static String StartupTab_FileBrowseWs_Message;
	public static String StartupTab_firstResetType_Text;
	public static String StartupTab_firstResetType_ToolTipText;
	public static String StartupTab_imageFileBrowse_Title;
	public static String StartupTab_imageFileBrowseWs_Title;
	public static String StartupTab_imageFileName_does_not_exist;
	public static String StartupTab_imageFileName_not_specified;
	public static String StartupTab_imageOffset_not_specified;
	public static String StartupTab_imageOffsetLabel_Text;
	public static String StartupTab_initCommands_ToolTipText;
	public static String StartupTab_initGroup_Text;
	public static String StartupTab_loadGroup_Text;
	public static String StartupTab_loadImage_Text;
	public static String StartupTab_loadSymbols_Text;
	public static String StartupTab_pcRegister_not_specified;
	public static String StartupTab_pcRegister_ToolTipText;
	public static String StartupTab_runCommands_ToolTipText;
	public static String StartupTab_runGroup_Text;
	public static String StartupTab_runOptionGroup_Text;
	public static String StartupTab_secondResetType_Text;
	public static String StartupTab_secondResetType_ToolTipText;
	public static String StartupTab_secondResetWarning_Text;
	public static String StartupTab_setPcRegister_Text;
	public static String StartupTab_setPcRegister_ToolTipText;
	public static String StartupTab_setResume_Text;
	public static String StartupTab_setStopAt_Text;
	public static String StartupTab_setStopAt_ToolTipText;
	public static String StartupTab_stopAt_not_specified;
	public static String StartupTab_symbolsFileBrowse_Title;
	public static String StartupTab_symbolsFileBrowseWs_Title;
	public static String StartupTab_symbolsFileName_does_not_exist;
	public static String StartupTab_symbolsFileName_not_specified;
	public static String StartupTab_symbolsOffset_not_specified;
	public static String StartupTab_symbolsOffsetLabel_Text;
	public static String StartupTab_useFile_Label;
	public static String StartupTab_useProjectBinary_Label;
	public static String StartupTab_useProjectBinary_ToolTipText;
	public static String WorkspaceMcuPagePropertyPage_description;

	// ------------------------------------------------------------------------

	static {
		// initialise resource bundle
		NLS.initializeMessages(MESSAGES, Messages.class);
	}

	private Messages() {
	}

}
