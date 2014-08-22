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

package ilg.gnuarmeclipse.debug.core.gdbjtag.dsf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.cdt.dsf.concurrent.Immutable;
import org.eclipse.cdt.dsf.debug.ui.viewmodel.SteppingController;
import org.eclipse.cdt.dsf.gdb.internal.ui.GdbAdapterFactory;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IColumnPresentationFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelProxyFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerInputProvider;

/**
 * Custom adapter factory, used by adaptable type
 * "ilg.gnuarmeclipse.debug.core.gdbjtag.dsf.GnuArmLaunch" to provide
 * GnuArmViewModelAdapter() in addition to those provided by parent.
 */
@SuppressWarnings("restriction")
public class GnuArmAdapterFactory extends GdbAdapterFactory {

	// ------------------------------------------------------------------------

	/**
	 * Adapters used by a launcher session.
	 */
	@Immutable
	class SessionAdapters {

		final GdbLaunch fLaunch;
		final SteppingController fSteppingController;
		final GnuArmViewModelAdapter fViewModelAdapter;

		SessionAdapters(GdbLaunch launch) {
			fLaunch = launch;
			DsfSession session = launch.getSession();

			fSteppingController = new SteppingController(session);
			session.registerModelAdapter(SteppingController.class,
					fSteppingController);

			fViewModelAdapter = new GnuArmViewModelAdapter(session,
					fSteppingController);
			session.registerModelAdapter(IViewerInputProvider.class,
					fViewModelAdapter);
		}

		void dispose() {

			DsfSession session = fLaunch.getSession();

			session.unregisterModelAdapter(IViewerInputProvider.class);
			fViewModelAdapter.dispose();

			session.unregisterModelAdapter(SteppingController.class);
			fSteppingController.dispose();

		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Map of session adapters, by launcher.
	 */
	private static Map<GdbLaunch, SessionAdapters> sfLaunchAdaptersMap = Collections
			.synchronizedMap(new HashMap<GdbLaunch, SessionAdapters>());

	/**
	 * Map of disposed session adapters, by launcher.
	 */
	private static Map<ILaunch, SessionAdapters> sfDisposedLaunchAdaptersMap = new WeakHashMap<ILaunch, SessionAdapters>();

	// ------------------------------------------------------------------------

	static void disposeAdapterSet(ILaunch launch) {

		synchronized (sfLaunchAdaptersMap) {
			if (sfLaunchAdaptersMap.containsKey(launch)) {
				sfLaunchAdaptersMap.remove(launch).dispose();
				sfDisposedLaunchAdaptersMap.put(launch, null);
			}
		}
	}

	// ------------------------------------------------------------------------

	public GnuArmAdapterFactory() {
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {

		if (!(adaptableObject instanceof GdbLaunch)) {
			return null;
		}

		GdbLaunch launch = (GdbLaunch) adaptableObject;
		DsfSession session = launch.getSession();
		if (session == null) {
			return null;
		}

		SessionAdapters sessionAdapters;
		synchronized (sfLaunchAdaptersMap) {

			if (sfDisposedLaunchAdaptersMap.containsKey(launch)) {
				return null;
			}

			sessionAdapters = sfLaunchAdaptersMap.get(launch);
			if (sessionAdapters == null) {
				if (!session.isActive()) {
					return null;
				}
				sessionAdapters = new SessionAdapters(launch);
				sfLaunchAdaptersMap.put(launch, sessionAdapters);
			}
		}

		if (adapterType.equals(IElementContentProvider.class))
			return sessionAdapters.fViewModelAdapter;
		if (adapterType.equals(IModelProxyFactory.class))
			return sessionAdapters.fViewModelAdapter;
		if (adapterType.equals(IColumnPresentationFactory.class))
			return sessionAdapters.fViewModelAdapter;

		return super.getAdapter(adaptableObject, adapterType);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList() {

		return new Class[] { IElementContentProvider.class,
				IModelProxyFactory.class, IColumnPresentationFactory.class };
	}

	@Override
	public void launchesAdded(ILaunch[] launches) {
		;
	}

	@Override
	public void launchesChanged(ILaunch[] launches) {
		;
	}

	@Override
	public void launchesRemoved(ILaunch[] launches) {
		for (ILaunch launch : launches)
			if ((launch instanceof GdbLaunch))
				disposeAdapterSet(launch);
	}

	@Override
	public void launchesTerminated(ILaunch[] launches) {
		;
	}

	// ------------------------------------------------------------------------

}
