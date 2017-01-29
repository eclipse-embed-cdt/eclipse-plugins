/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.services;

import ilg.gnuarmeclipse.core.CProjectPacksStorage;
import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.ILaunchConfigurationProvider;
import ilg.gnuarmeclipse.debug.gdbjtag.data.CProjectExtraDataManagerProxy;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.IPeripheralDMContext;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMContext;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.SvdUtils;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
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

		if (fPeripheralsDMContexts != null) {

			// On subsequent calls, return existing array.
			drm.setData(fPeripheralsDMContexts);
			drm.done();
			return;
		}

		if (fCommandControl instanceof ILaunchConfigurationProvider) {

			// The launch configuration is obtained from the command control,
			// using a dedicated interface.
			ILaunchConfiguration launchConfiguration = ((ILaunchConfigurationProvider) fCommandControl)
					.getLaunchConfiguration();

			// The second step is to get the build configuration description.
			ICConfigurationDescription cConfigDescription = EclipseUtils.getBuildConfigDescription(launchConfiguration);

			if (cConfigDescription != null) {
				// System.out.println(cConfigDescription);

				// The third step is to get the CDT configuration.
				IConfiguration config = EclipseUtils.getConfigurationFromDescription(cConfigDescription);
				if (Activator.getInstance().isDebugging()) {
					System.out.println(config);
				}

				try {
					String vendorId = null;
					String deviceName = null;

					CProjectExtraDataManagerProxy dataManager = CProjectExtraDataManagerProxy.getInstance();
					Map<String, String> propertiesMap = dataManager.getExtraProperties(config);
					if (propertiesMap != null) {
						vendorId = propertiesMap.get(CProjectPacksStorage.DEVICE_VENDOR_ID);
						deviceName = propertiesMap.get(CProjectPacksStorage.DEVICE_NAME);
					}

					if (vendorId != null && deviceName != null) {

						Leaf tree = SvdUtils.getTree(vendorId, deviceName);
						List<Leaf> list = SvdUtils.getPeripherals(tree);

						fPeripheralsDMContexts = createPeripheralsContexts(containerDMContext, list);

						drm.setData(fPeripheralsDMContexts);
						drm.done();
						return;

					} else {

						drm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID,
								"There are no peripheral descriptions available, assign a device to the project."));
						drm.done();
						return;
					}
				} catch (CoreException e) {
					drm.setStatus(e.getStatus());
					drm.done();
					return;
				}
			}
		}

		drm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID, "No peripherals available."));
		drm.done();
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
