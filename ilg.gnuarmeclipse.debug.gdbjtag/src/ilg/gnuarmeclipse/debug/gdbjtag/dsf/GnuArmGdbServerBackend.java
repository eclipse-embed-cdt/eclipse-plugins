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
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;

public class GnuArmGdbServerBackend extends AbstractDsfService implements
		IGdbServerBackendService {

	public GnuArmGdbServerBackend(DsfSession session) {
		super(session);
	}

	public GnuArmGdbServerBackend(DsfSession session, ILaunchConfiguration lc) {
		super(session);

		System.out.println("GnuArmGdbServerBackend(" + session + ","
				+ lc.getName() + ")");

	}

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	@Override
	public void initialize(final RequestMonitor rm) {

		System.out.println("GnuArmGdbServerBackend.initialize()");

		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	// @SuppressWarnings("rawtypes")
	private void doInitialize(RequestMonitor rm) {
		// register(new String[] { IGdbServerBackendService.class.getName() },
		// new Hashtable());

		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		System.out.println("GnuArmGdbServerBackend.shutdown()");

		// Remove this service from DSF.
		// unregister();

		super.shutdown(rm);
	}

	@Override
	public InputStream getMIErrorStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getExitCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InputStream getMIInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getMIOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}
}
