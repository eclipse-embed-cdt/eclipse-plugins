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

package ilg.gnuarmeclipse.packs.jobs;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.Repos;
import ilg.gnuarmeclipse.packs.Utils;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.MessageConsoleStream;

// Used when the plug-in is activated.
public class LoadReposSummariesJob extends Job {

	private static boolean ms_running = false;

	private MessageConsoleStream m_out;

	private Repos m_repos;
	private PacksStorage m_storage;

	public LoadReposSummariesJob(String name) {

		super(name);

		m_out = Activator.getConsoleOut();

		m_repos = Repos.getInstance();
		m_storage = PacksStorage.getInstance();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (ms_running) {
			return Status.CANCEL_STATUS;
		}

		ms_running = true;

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());

		List<Map<String, Object>> reposList;
		reposList = m_repos.getList();

		int workUnits = reposList.size();
		workUnits++; // For post processing
		monitor.beginTask("Load repos summaries", workUnits);

		m_storage.parseRepos(monitor);

		// Notify listeners (currently the views) that the packs changed
		// (for just in case this takes very long, normally the views are
		// not created at this moment)

		m_storage.notifyNewInput();

		IStatus status;

		if (monitor.isCanceled()) {

			m_out.println("Job cancelled.");
			status = Status.CANCEL_STATUS;

		} else {

			long endTime = System.currentTimeMillis();
			long duration = endTime - beginTime;
			if (duration == 0) {
				duration = 1;
			}

			m_out.print("Load completed in ");
			if (duration < 1000) {
				m_out.println(duration + "ms.");
			} else {
				m_out.println((duration + 500) / 1000 + "s.");
			}

			// System.out.println("loadRepositories() completed");

			status = Status.OK_STATUS;
		}

		ms_running = false;
		return status;
	}

}
