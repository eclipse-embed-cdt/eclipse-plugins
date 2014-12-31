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

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGnuArmDebuggerCommandsService;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.core.Messages;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.IGDBJtagDevice;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;

@SuppressWarnings("restriction")
public abstract class GnuArmDebuggerCommandsService extends AbstractDsfService
		implements IGnuArmDebuggerCommandsService {

	// ------------------------------------------------------------------------

	protected DsfSession fSession;
	protected ILaunchConfiguration fConfig;
	protected IGDBJtagDevice fJtagDevice;
	protected boolean fDoDoubleBackslash;
	protected DsfServicesTracker fTracker;
	protected IGDBBackend fGDBBackend;
	protected Map<String, Object> fAttributes;

	// ------------------------------------------------------------------------

	public GnuArmDebuggerCommandsService(DsfSession session,
			ILaunchConfiguration lc) {
		this(session, lc, false);
	}

	public GnuArmDebuggerCommandsService(DsfSession session,
			ILaunchConfiguration lc, boolean doubleBackslash) {
		super(session);

		fSession = session;
		fConfig = lc;

		fDoDoubleBackslash = doubleBackslash;
	}

	// ------------------------------------------------------------------------

	public void initialize(final RequestMonitor rm) {

		System.out.println("GnuArmDebuggerCommandsService.initialize()");

		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void doInitialize(RequestMonitor rm) {

		System.out.println("GnuArmDebuggerCommandsService.doInitialize()");

		// Get and remember the command control service
		// fCommandControl = ((ICommandControlService) getServicesTracker()
		// .getService(ICommandControlService.class));

		// Register this service to DSF.
		// For completeness, use both the interface and the class name.
		register(new String[] { IGnuArmDebuggerCommandsService.class.getName(),
				this.getClass().getName() }, new Hashtable());

		System.out.println(this.getClass().getName() + " registered ");

		fTracker = new DsfServicesTracker(GdbPlugin.getBundleContext(),
				fSession.getId());
		fGDBBackend = fTracker.getService(IGDBBackend.class);
		if (fGDBBackend == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot obtain GDBBackend service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		System.out.println("GnuArmDebuggerCommandsService.shutdown()");

		// Remove this service from DSF.
		unregister();

		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	@Override
	public void setJtagDevice(IGDBJtagDevice jtagDevice) {
		fJtagDevice = jtagDevice;
	};

	@Override
	public void setAttributes(Map<String, Object> attributes) {
		fAttributes = attributes;
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addGnuArmRestartCommands(List<String> commandsList) {

		return addStartRestartCommands(fAttributes, true,
				fGDBBackend.getProgramPath(), commandsList);
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addLoadSymbolsCommands(Map<String, Object> attributes,
			IPath programPath, List<String> commandsList) {

		if (!CDebugUtils.getAttribute(attributes,
				IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
				IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS)) {

			// Not required.
			return Status.OK_STATUS;
		}

		String symbolsFileName = null;

		// New setting in Helios. Default is true. Check for existence
		// in order to support older launch configs
		if (attributes
				.containsKey(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS)
				&& CDebugUtils.getAttribute(attributes,
						IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
						IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS)) {
			if (programPath != null) {
				symbolsFileName = programPath.toOSString();
			}
		} else {
			symbolsFileName = CDebugUtils.getAttribute(attributes,
					IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
					IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME);
			if (!symbolsFileName.isEmpty()) {
				symbolsFileName = DebugUtils.resolveAll(symbolsFileName,
						attributes);
			} else {
				symbolsFileName = null;
			}
		}

		if (symbolsFileName == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					Messages.getString("GDBJtagDebugger.err_no_img_file"), null); //$NON-NLS-1$
		}

		// if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
		// // Escape windows path separator characters TWICE, once for
		// // Java and once for GDB.
		//			symbolsFileName = StringUtils.duplicateBackslashes(symbolsFileName); //$NON-NLS-1$ //$NON-NLS-2$
		// }

		String symbolsOffset = CDebugUtils.getAttribute(attributes,
				IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
				IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);
		if (!symbolsOffset.isEmpty()) {
			symbolsOffset = "0x" + symbolsOffset;
		}
		fJtagDevice.doLoadSymbol(symbolsFileName, symbolsOffset, commandsList);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addLoadImageCommands(Map<String, Object> attributes,
			IPath programPath, List<String> commandsList) {

		String imageFileName = null;

		if (attributes
				.containsKey(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE)
				&& CDebugUtils.getAttribute(attributes,
						IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
						IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE)) {
			if (programPath != null) {
				imageFileName = programPath.toOSString();
			}
		} else {
			imageFileName = CDebugUtils.getAttribute(attributes,
					IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
					IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME);
			if (!imageFileName.isEmpty()) {
				imageFileName = DebugUtils
						.resolveAll(imageFileName, attributes);
			} else {
				imageFileName = null;
			}
		}

		if (imageFileName == null) {
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					Messages.getString("GDBJtagDebugger.err_no_img_file"), null); //$NON-NLS-1$
		}

		imageFileName = DebugUtils.resolveAll(imageFileName, attributes);

		// if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
		// // Escape windows path separator characters TWICE, once
		// // for Java and once for GDB.
		//			imageFileName = StringUtils.duplicateBackslashes(imageFileName); //$NON-NLS-1$ //$NON-NLS-2$
		// }

		String imageOffset = CDebugUtils.getAttribute(attributes,
				IGDBJtagConstants.ATTR_IMAGE_OFFSET,
				IGDBJtagConstants.DEFAULT_IMAGE_OFFSET);
		if (imageOffset.length() > 0) {
			imageOffset = (imageFileName.endsWith(".elf")) ? "" : "0x"
					+ CDebugUtils.getAttribute(attributes,
							IGDBJtagConstants.ATTR_IMAGE_OFFSET,
							IGDBJtagConstants.DEFAULT_IMAGE_OFFSET); //$NON-NLS-2$ 
		}
		fJtagDevice.doLoadImage(imageFileName, imageOffset, commandsList);

		return Status.OK_STATUS;
	}

	public IStatus addSetPcCommands(List<String> commandsList) {

		if (CDebugUtils.getAttribute(fAttributes,
				IGDBJtagConstants.ATTR_SET_PC_REGISTER,
				IGDBJtagConstants.DEFAULT_SET_PC_REGISTER)) {
			String pcRegister = CDebugUtils.getAttribute(
					fAttributes,
					IGDBJtagConstants.ATTR_PC_REGISTER,
					CDebugUtils.getAttribute(fAttributes,
							IGDBJtagConstants.ATTR_IMAGE_OFFSET,
							IGDBJtagConstants.DEFAULT_PC_REGISTER)).trim();
			if (!pcRegister.isEmpty()) {
				fJtagDevice.doSetPC(pcRegister, commandsList);
			}
		}

		return Status.OK_STATUS;
	}

	public IStatus addStopAtCommands(List<String> commandsList) {

		if (CDebugUtils.getAttribute(fAttributes,
				IGDBJtagConstants.ATTR_SET_STOP_AT,
				IGDBJtagConstants.DEFAULT_SET_STOP_AT)) {
			String stopAt = CDebugUtils.getAttribute(fAttributes,
					IGDBJtagConstants.ATTR_STOP_AT,
					IGDBJtagConstants.DEFAULT_STOP_AT).trim();

			if (!stopAt.isEmpty()) {
				// doAtopAt replaced by a simple tbreak
				commandsList.add("tbreak " + stopAt);
			}
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------
}
