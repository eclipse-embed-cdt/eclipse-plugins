/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGnuArmDebuggerCommandsService;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.core.Messages;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

public abstract class GnuArmDebuggerCommandsService extends AbstractDsfService
		implements IGnuArmDebuggerCommandsService {

	// ------------------------------------------------------------------------

	protected DsfSession fSession;
	protected ILaunchConfiguration fConfig;
	protected boolean fDoDoubleBackslash;
	protected DsfServicesTracker fTracker;
	protected IGDBBackend fGdbBackend;
	protected Map<String, Object> fAttributes;
	protected String fMode;

	// protected static final String LINESEP = System
	// .getProperty("line.separator"); //$NON-NLS-1$

	// ------------------------------------------------------------------------

	public GnuArmDebuggerCommandsService(DsfSession session, ILaunchConfiguration lc, String mode) {
		this(session, lc, mode, false);
	}

	public GnuArmDebuggerCommandsService(DsfSession session, ILaunchConfiguration lc, String mode,
			boolean doubleBackslash) {
		super(session);

		fSession = session;
		fConfig = lc;

		fMode = mode;
		fDoDoubleBackslash = doubleBackslash;
	}

	// ------------------------------------------------------------------------

	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmDebuggerCommandsService.initialize()");
		}

		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void doInitialize(RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmDebuggerCommandsService.doInitialize()");
		}

		// Get and remember the command control service
		// fCommandControl = ((ICommandControlService) getServicesTracker()
		// .getService(ICommandControlService.class));

		// Register this service to DSF.
		// For completeness, use both the interface and the class name.
		register(new String[] { IGnuArmDebuggerCommandsService.class.getName(), this.getClass().getName() },
				new Hashtable());

		if (Activator.getInstance().isDebugging()) {
			System.out.println(this.getClass().getName() + " registered ");
		}

		fTracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(), fSession.getId());
		fGdbBackend = fTracker.getService(IGDBBackend.class);
		if (fGdbBackend == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot obtain GDBBackend service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmDebuggerCommandsService.shutdown()");
		}

		// Remove this service from DSF.
		unregister();

		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	@Override
	public void setAttributes(Map<String, Object> attributes) {
		fAttributes = attributes;
	}

	// ------------------------------------------------------------------------

	public IStatus addGnuArmSelectRemoteCommands(List<String> commandsList) {

		String remoteTcpHost = CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_IP_ADDRESS,
				IGDBJtagConstants.DEFAULT_IP_ADDRESS);
		Integer remoteTcpPort = CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_PORT_NUMBER,
				IGDBJtagConstants.DEFAULT_PORT_NUMBER);

		commandsList.add("-target-select remote " + remoteTcpHost + ":" + remoteTcpPort + "");

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuArmRestartCommands(List<String> commandsList) {

		return addStartRestartCommands(true, commandsList);
	}

	// ------------------------------------------------------------------------

	protected String escapeSpaces(String file) {
		if (file.indexOf(' ') >= 0) {
			return '"' + file + '"';
		}
		return file;
	}

	/**
	 * Add the "symbol-file <path>" command, using the jtag device definition.
	 * Always duplicate backslashes on Windows.
	 */
	@Override
	public IStatus addLoadSymbolsCommands(List<String> commandsList) {

		IPath programPath = fGdbBackend.getProgramPath();

		if (!CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
				IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS)) {

			// Not required.
			return Status.OK_STATUS;
		}

		String symbolsFileName = null;

		// New setting in Helios. Default is true. Check for existence
		// in order to support older launch configs
		if (fAttributes.containsKey(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS)
				&& CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
						IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS)) {
			if (programPath != null) {
				symbolsFileName = programPath.toOSString();
			}
		} else {
			symbolsFileName = CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
					IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME);
			if (!symbolsFileName.isEmpty()) {
				symbolsFileName = DebugUtils.resolveAll(symbolsFileName, fAttributes);
			} else {
				symbolsFileName = null;
			}
		}

		if (symbolsFileName == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					Messages.getString("GDBJtagDebugger.err_no_img_file"), null); //$NON-NLS-1$
		}

		if (EclipseUtils.isWindows()) {
			// Escape windows path separator characters TWICE, once for
			// Java and once for GDB.
			symbolsFileName = StringUtils.duplicateBackslashes(symbolsFileName);
		}

		String file = escapeSpaces(symbolsFileName);

		String symbolsOffset = CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
				IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);
		if (!symbolsOffset.isEmpty()) {
			symbolsOffset = "0x" + symbolsOffset;
			// addCmd(commandsList, "add-symbol-file " + file + " "
			// + symbolsOffset);
			commandsList.add("add-symbol-file " + file + " " + symbolsOffset);
		} else {
			// addCmd(commandsList, "symbol-file " + file);
			commandsList.add("symbol-file " + file);
		}

		return Status.OK_STATUS;
	}

	/**
	 * Add the "load <path>" command, using the jtag device definition. Always
	 * duplicate backslashes on Windows.
	 */
	@Override
	public IStatus addLoadImageCommands(List<String> commandsList) {

		IPath programPath = fGdbBackend.getProgramPath();

		String imageFileName = null;

		if (fAttributes.containsKey(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE)
				&& CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
						IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE)) {
			if (programPath != null) {
				imageFileName = programPath.toOSString();
			}
		} else {
			imageFileName = CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
					IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME);
			if (!imageFileName.isEmpty()) {
				imageFileName = DebugUtils.resolveAll(imageFileName, fAttributes);
			} else {
				imageFileName = null;
			}
		}

		if (imageFileName == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					Messages.getString("GDBJtagDebugger.err_no_img_file"), null); //$NON-NLS-1$
		}

		imageFileName = DebugUtils.resolveAll(imageFileName, fAttributes);

		if (EclipseUtils.isWindows()) {
			// Escape windows path separator characters TWICE, once
			// for Java and once for GDB.
			imageFileName = StringUtils.duplicateBackslashes(imageFileName);
		}

		String imageOffset = CDebugUtils
				.getAttribute(fAttributes, IGDBJtagConstants.ATTR_IMAGE_OFFSET, IGDBJtagConstants.DEFAULT_IMAGE_OFFSET)
				.trim();
		if (!imageOffset.isEmpty()) {
			imageOffset = (imageFileName.endsWith(".elf")) ? ""
					: "0x" + CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_IMAGE_OFFSET,
							IGDBJtagConstants.DEFAULT_IMAGE_OFFSET); // $NON-NLS-2$
		}

		String file = escapeSpaces(imageFileName);
		// addCmd(commandsList, "load " + file + ' ' + imageOffset);
		commandsList.add("load " + file + ' ' + imageOffset);

		return Status.OK_STATUS;
	}

	public IStatus addSetPcCommands(List<String> commandsList) {

		if (CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_SET_PC_REGISTER,
				IGDBJtagConstants.DEFAULT_SET_PC_REGISTER)) {
			String pcRegister = CDebugUtils
					.getAttribute(fAttributes, IGDBJtagConstants.ATTR_PC_REGISTER, CDebugUtils.getAttribute(fAttributes,
							IGDBJtagConstants.ATTR_IMAGE_OFFSET, IGDBJtagConstants.DEFAULT_PC_REGISTER))
					.trim();
			if (!pcRegister.isEmpty()) {
				commandsList.add("set $pc=0x" + pcRegister);
			}
		}

		return Status.OK_STATUS;
	}

	public IStatus addStopAtCommands(List<String> commandsList) {

		// This code is also used to start run configurations.
		// Set the breakpoint only for debug.
		if (fMode.equals(ILaunchManager.DEBUG_MODE)) {
			if (CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_SET_STOP_AT,
					IGDBJtagConstants.DEFAULT_SET_STOP_AT)) {
				String stopAt = CDebugUtils
						.getAttribute(fAttributes, IGDBJtagConstants.ATTR_STOP_AT, IGDBJtagConstants.DEFAULT_STOP_AT)
						.trim();

				if (!stopAt.isEmpty()) {
					// doAtopAt replaced by a simple tbreak
					commandsList.add("tbreak " + stopAt);
				}
			}
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------
}
