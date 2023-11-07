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

package org.eclipse.embedcdt.debug.gdbjtag.core.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.eclipse.embedcdt.debug.gdbjtag.core.ILaunchConfigurationProvider;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.IPeripheralDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.IPeripheralGroupDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.PeripheralDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.PeripheralDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.PeripheralGroupDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.SvdUtils;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;
import org.eclipse.embedcdt.packs.core.data.DurationMonitor;
import org.eclipse.embedcdt.packs.core.tree.Leaf;
import org.osgi.framework.BundleContext;

public class PeripheralsService extends AbstractDsfService implements IPeripheralsService {

	private ICommandControlService fCommandControl;

	private PeripheralGroupDMContext[] fPeripheralgroupsDMContexts = null;
	private Map<String, PeripheralDMContext[]> groupNameToPeripheralsMap = new HashMap<>();

	// ------------------------------------------------------------------------

	public PeripheralsService(DsfSession session) {
		super(session);

	}

	@Override
	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsService.initialize()");
		}

		super.initialize(new RequestMonitor(getExecutor(), rm) {

			@Override
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
		fCommandControl = (getServicesTracker().getService(ICommandControlService.class));

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
	public void getPeripheralGroups(IContainerDMContext context, DataRequestMonitor<IPeripheralGroupDMContext[]> drm) {

		// First time here, get the contexts.
		if (fPeripheralgroupsDMContexts == null) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("PeripheralsService.getPeripheralGroups() First time get PeripheralGroups");
			}

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
				fPeripheralgroupsDMContexts = createPeripheralGroupsContexts(context, list);

			} catch (CoreException e) {
				drm.setStatus(e.getStatus());
				drm.done();
				return;
			}
		}

		drm.setData(fPeripheralgroupsDMContexts);
		drm.done();
		return;
	}

	@Override
	public void getPeripherals(IPeripheralGroupDMContext groupDmc, DataRequestMonitor<IPeripheralDMContext[]> drm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsService.getPeripherals()");
		}

		PeripheralDMContext[] fPeripheralsDMContexts = groupNameToPeripheralsMap.get(groupDmc.getName());

		if (fPeripheralsDMContexts == null) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("PeripheralsService.getPeripheralGroups() First time get Peripherals");
			}

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
				Map<String, List<Leaf>> groupNameToPeripherals = SvdUtils.getPeripherals(tree).stream()
						.collect(Collectors.groupingBy(node -> node.getProperty("groupName")));

				// Prepare the data model context for the Peripherals view.
				for (PeripheralGroupDMContext parent : fPeripheralgroupsDMContexts) {

					String groupName = parent.getName();
					List<Leaf> list = groupNameToPeripherals.get(groupName);

					PeripheralDMContext[] createPeripheralsContexts = createPeripheralsContexts(parent, list);
					groupNameToPeripheralsMap.put(groupName, createPeripheralsContexts);
				}

				fPeripheralsDMContexts = groupNameToPeripheralsMap.get(groupDmc.getName());

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

	private PeripheralGroupDMContext[] createPeripheralGroupsContexts(IDMContext parentIDMContext, List<Leaf> list) {

		Set<String> groupNames = list.stream().map(node -> node.getProperty("groupName")).collect(Collectors.toSet());

		PeripheralGroupDMContext contexts[] = new PeripheralGroupDMContext[groupNames.size()];
		IDMContext[] parents;
		if (parentIDMContext != null) {
			parents = new IDMContext[] { parentIDMContext };
		} else {
			parents = new IDMContext[0];
		}
		int i = 0;
		for (String groupName : groupNames) {
			contexts[i] = new PeripheralGroupDMContext(getSession(), parents, groupName);
			++i;
		}
		Arrays.sort(contexts);

		return contexts;
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
