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

package ilg.gnuarmeclipse.packs.data;

import ilg.gnuarmeclipse.packs.data.jobs.LoadReposSummariesJob;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnuarmeclipse.packs.data"; //$NON-NLS-1$

	// The shared instance
	private static Activator sfInstance;

	private Job fLoadReposJob;

	/**
	 * The constructor
	 */
	public Activator() {
		;
	}

	public void start(BundleContext context) throws Exception {

		System.out.println("ilg.gnuarmeclipse.packs.data.Activator()");

		super.start(context);
		sfInstance = this;

		// Prepare & cache various variables
		Repos repos = Repos.getInstance();
		repos.getFolderPath();

		PacksStorage.getInstance();

		// Initial load of repositories summaries
		fLoadReposJob = new LoadReposSummariesJob("Load repos summaries");
		fLoadReposJob.schedule();
		// fLoadReposJob.join(); // does not work in this context

		System.out
				.println("ilg.gnuarmeclipse.packs.data.Activator() completed");

	}

	public void stop(BundleContext context) throws Exception {

		sfInstance = null;
		super.stop(context);

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return sfInstance;
	}

	public Job getLoadReposJob() {
		return fLoadReposJob;
	}

	public void waitLoadReposJob() {
		try {
			fLoadReposJob.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "Internal Error", e)); //$NON-NLS-1$
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, message, null)); //$NON-NLS-1$
	}

	// ------------------------------------------------------------------------

}
