/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.services;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.debug.gdbjtag.Activator;
import org.eclipse.embedcdt.debug.gdbjtag.ILaunchConfigurationProvider;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.IPeripheralDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.PeripheralDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.PeripheralDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdUtils;
import org.eclipse.embedcdt.packs.core.data.DurationMonitor;
import org.eclipse.embedcdt.packs.core.tree.Leaf;
import org.osgi.framework.BundleContext;

public class PeripheralsService extends AbstractDsfService implements IPeripheralsService {

	private ICommandControlService fCommandControl;
	private PeripheralDMContext[] fPeripheralsDMContexts = null;

	// ------------------------------------------------------------------------

	public PeripheralsService(DsfSession session) {
		super(session);

	}

	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsService.initialize()");
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
			System.out.println("PeripheralsService.doInitialize()");
		}

		// Get and remember the command control service
		fCommandControl = ((ICommandControlService) getServicesTracker().getService(ICommandControlService.class));

		// Register this service to DSF.
		// For completeness, use both the interface and the class name.
		// Used in PeripheralVMNode by interface name.
		register(new String[] { IPeripheralsService.class.getName(), PeripheralsService.class.getName() },
				new Hashtable());

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsService registered " + this);
		}

		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsService.shutdown()");
		}

		// Remove this service from DSF.
		unregister();

		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	@Override
	public void getPeripherals(IContainerDMContext containerDMContext, DataRequestMonitor<IPeripheralDMContext[]> drm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsService.getPeripherals()");
		}

		if (fPeripheralsDMContexts == null) {

			// First time here, get the contexts.

			if (!(fCommandControl instanceof ILaunchConfigurationProvider)) {
				drm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID, "No peripherals available."));
				drm.done();
				return;
			}

			// The launch configuration is obtained from the command control, using a
			// dedicated interface.
			ILaunchConfiguration launchConfiguration = ((ILaunchConfigurationProvider) fCommandControl)
					.getLaunchConfiguration();

			try {

				// Search for the SVD/XSVD in various locations (packages, extension points,
				// etc)
				IPath svdPath = SvdUtils.getSvdPath(launchConfiguration);

				if (Activator.getInstance().isDebugging()) {
					System.out.println("SVD path: " + svdPath);
				}

				Leaf tree;
				{
					DurationMonitor dm = new DurationMonitor();
					dm.start();
					// Read in the file, parse the original format (XML or JSON) and build the
					// internal tree.
					tree = SvdUtils.getTree(svdPath);
					dm.stop();
				}

				// Extract the peripherals in a separate list.
				List<Leaf> list = SvdUtils.getPeripherals(tree);

				// Prepare the data model context for the Peripherals view.
				fPeripheralsDMContexts = createPeripheralsContexts(containerDMContext, list);

			} catch (CoreException e) {
				drm.setStatus(e.getStatus());
				drm.done();
				return;
			}
		}

		drm.setData(fPeripheralsDMContexts);
		drm.done();
		return;
	}

	private PeripheralDMContext[] createPeripheralsContexts(IDMContext parentIDMContext, List<Leaf> list) {

		PeripheralDMContext contexts[] = new PeripheralDMContext[list.size()];
		IDMContext[] parents;
		if (parentIDMContext != null) {
			parents = new IDMContext[] { parentIDMContext };
		} else {
			parents = new IDMContext[0];
		}
		int i = 0;
		for (Leaf child : list) {
			PeripheralDMNode node = new PeripheralDMNode(child);
			contexts[i] = new PeripheralDMContext(getSession(), parents, node);
			++i;
		}
		Arrays.sort(contexts);
		return contexts;
	}

	// ------------------------------------------------------------------------
}
