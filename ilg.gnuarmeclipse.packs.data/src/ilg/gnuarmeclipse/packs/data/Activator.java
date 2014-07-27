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

import ilg.gnuarmeclipse.core.AbstractActivator;
import ilg.gnuarmeclipse.packs.data.jobs.LoadReposSummariesJob;

import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnuarmeclipse.packs.data"; //$NON-NLS-1$

	@Override
	public String getBundleId() {
		return PLUGIN_ID;
	}

	// ------------------------------------------------------------------------

	// The shared instance
	private static Activator sfInstance;

	public static Activator getInstance() {
		return sfInstance;
	}

	public Activator() {

		super();
		sfInstance = this;
	}

	// ------------------------------------------------------------------------

	private Job fLoadReposJob;

	public Job getLoadReposJob() {
		return fLoadReposJob;
	}

	public void start(BundleContext context) throws Exception {

		super.start(context);

		// Initial load of repositories summaries
		fLoadReposJob = new LoadReposSummariesJob("Load repos summaries");
		fLoadReposJob.schedule();
		// fLoadReposJob.join(); // does not work in this context

		System.out.println(getBundleId() + ".start() completed");
	}

	public void waitLoadReposJob() {

		try {
			fLoadReposJob.join();

			System.out.println("LoadRepos joined");

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	// ------------------------------------------------------------------------
}
