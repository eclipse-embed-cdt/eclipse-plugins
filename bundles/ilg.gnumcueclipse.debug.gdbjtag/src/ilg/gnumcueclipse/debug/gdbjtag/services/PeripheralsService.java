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

package ilg.gnumcueclipse.debug.gdbjtag.services;

import java.io.File;
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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

import ilg.gnumcueclipse.core.CProjectPacksStorage;
import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.ILaunchConfigurationProvider;
import ilg.gnumcueclipse.debug.gdbjtag.data.CProjectExtraDataManagerProxy;
import ilg.gnumcueclipse.debug.gdbjtag.datamodel.IPeripheralDMContext;
import ilg.gnumcueclipse.debug.gdbjtag.datamodel.PeripheralDMContext;
import ilg.gnumcueclipse.debug.gdbjtag.datamodel.PeripheralDMNode;
import ilg.gnumcueclipse.debug.gdbjtag.datamodel.SvdUtils;
import ilg.gnumcueclipse.debug.gdbjtag.properties.PersistentProperties;
import ilg.gnumcueclipse.packs.core.tree.Leaf;

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

			IProject project = EclipseUtils.getProjectByLaunchConfiguration(launchConfiguration);
			ICConfigurationDescription desc = EclipseUtils.getBuildConfigDescription(launchConfiguration);
			Preferences preferences = new ProjectScope(project).getNode(Activator.PLUGIN_ID);
			String key = PersistentProperties.getSvdAbsolutePathKey(desc.getId());
			String value = preferences.get(key, "").trim();

			IPath svdPath = null;
			if (!value.isEmpty()) {
				// System.out.println("Custom SVD path: " + value);
				File f = new File(value);
				if (f.exists() && !f.isDirectory()) {
					svdPath = new Path(value);
				}
			}

			String deviceName = null;
			// String vendorName = null;

			if (svdPath == null) {
				// The second step is to get the build configuration description.
				ICConfigurationDescription cConfigDescription = EclipseUtils
						.getBuildConfigDescription(launchConfiguration);

				if (cConfigDescription != null) {
					// System.out.println(cConfigDescription);

					// The third step is to get the CDT configuration.
					IConfiguration config = EclipseUtils.getConfigurationFromDescription(cConfigDescription);
					if (Activator.getInstance().isDebugging()) {
						System.out.println(config);
					}

					try {
						String vendorId = null;

						CProjectExtraDataManagerProxy dataManager = CProjectExtraDataManagerProxy.getInstance();
						Map<String, String> propertiesMap = dataManager.getExtraProperties(config);
						if (propertiesMap != null) {
							vendorId = propertiesMap.get(CProjectPacksStorage.DEVICE_VENDOR_ID);
							deviceName = propertiesMap.get(CProjectPacksStorage.DEVICE_NAME);
							// vendorName = propertiesMap.get(CProjectPacksStorage.DEVICE_VENDOR_NAME);
						}

						if (vendorId != null && deviceName != null) {

							// Leaf tree = SvdUtils.getTree(vendorId, deviceName);

							svdPath = SvdUtils.getSvdPath(vendorId, deviceName);

						} else {

							drm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID,
									"There is no peripheral description available, assign a device to the project."));
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

			if (Activator.getInstance().isDebugging()) {
				System.out.println("SVD path: " + svdPath);
			}

			if (svdPath != null) {
				try {
					Leaf tree = SvdUtils.getTree(svdPath);
					List<Leaf> list = SvdUtils.getPeripherals(tree);
					fPeripheralsDMContexts = createPeripheralsContexts(containerDMContext, list);

					drm.setData(fPeripheralsDMContexts);
					drm.done();
					return;
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
