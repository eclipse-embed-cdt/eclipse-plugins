/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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
import ilg.gnuarmeclipse.packs.ui.views.KeywordsView;
import ilg.gnuarmeclipse.packs.ui.views.PacksView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
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
	private static KeywordsView ms_keywordsView;

	private static MessageConsole ms_console;
	private static MessageConsoleStream ms_consoleOut;

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

		// Prepare & cache various variables
		Repos repos = Repos.getInstance();
		repos.getFolderPath();

		PacksStorage.getInstance();

		// Initial load of repositories summaries
		new Job("Load Repositories") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				return PacksStorage.getInstance().loadRepositories(monitor);

			}
		}.schedule();
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

	public static String CONSOLE_NAME = "Packs console";

	public static MessageConsoleStream getConsoleOut() {
		if (ms_consoleOut == null) {
			ms_consoleOut = getConsole().newMessageStream();
		}

		return ms_consoleOut;
	}

	private static MessageConsole getConsole() {
		if (ms_console == null) {
			ms_console = findConsole(CONSOLE_NAME);
		}
		return ms_console;
	}

	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
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

	public static void setKeywordsView(KeywordsView view) {
		ms_keywordsView = view;
	}

	public static KeywordsView getKeywordsView() {
		if (ms_keywordsView == null) {
			ms_keywordsView = (KeywordsView) findView(KeywordsView.ID);
		}
		return ms_keywordsView;
	}

}
