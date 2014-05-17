/*******************************************************************************
 * Copyright (c) 2014 Liviu ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs;

import ilg.gnuarmeclipse.packs.ui.views.BoardsView;
import ilg.gnuarmeclipse.packs.ui.views.DevicesView;
import ilg.gnuarmeclipse.packs.ui.views.PacksView;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnuarmeclipse.packs"; //$NON-NLS-1$

	// The shared instance
	private static Activator ms_plugin;

	private static PacksView ms_packsView;
	private static DevicesView ms_devicesView;
	private static BoardsView ms_boardsView;

	/**
	 * The constructor
	 */
	public Activator() {
		System.out.println("ilg.gnuarmeclipse.packs.Activator()");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

		super.start(context);
		ms_plugin = this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		ms_plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return ms_plugin;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "Internal Error", e)); //$NON-NLS-1$
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, null)); //$NON-NLS-1$
	}

	// -----

	public static IViewPart findView(String viewId) {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().findView(viewId);
		} catch (NullPointerException e) {
			log(e);
			return null;
		}
	}

	public static void setPacksView(PacksView view) {
		ms_packsView = view;
	}

	public static PacksView getPacksView() {
		if (ms_packsView == null) {
			ms_packsView = (PacksView) findView(PacksView.ID);
		}
		return ms_packsView;
	}

	public static void setDevicesView(DevicesView view) {
		ms_devicesView = view;
	}

	public static DevicesView getDevicesView() {
		if (ms_devicesView == null) {
			ms_devicesView = (DevicesView) findView(DevicesView.ID);
		}
		return ms_devicesView;
	}

	public static void setBoardsView(BoardsView view) {
		ms_boardsView = view;
	}

	public static BoardsView getBoardsView() {
		if (ms_boardsView == null) {
			ms_boardsView = (BoardsView) findView(BoardsView.ID);
		}
		return ms_boardsView;
	}


}
