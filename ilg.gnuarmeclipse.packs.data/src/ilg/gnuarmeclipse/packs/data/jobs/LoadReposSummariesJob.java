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

package ilg.gnuarmeclipse.packs.data.jobs;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.PacksStorage;
import ilg.gnuarmeclipse.packs.data.Repos;
import ilg.gnuarmeclipse.packs.data.Utils;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.MessageConsoleStream;

// Used when the plug-in is activated.
public class LoadReposSummariesJob extends Job {

	private static boolean sfRunning = false;

	private MessageConsoleStream fOut;

	private Repos fRepos;
	private PacksStorage fStorage;
	private DataManager fDataManager;

	public LoadReposSummariesJob(String name) {

		super(name);

		fOut = ConsoleStream.getConsoleOut();

		fRepos = Repos.getInstance();
		fStorage = PacksStorage.getInstance();
		fDataManager = DataManager.getInstance();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (sfRunning) {
			return Status.CANCEL_STATUS;
		}

		sfRunning = true;

		long beginTime = System.currentTimeMillis();

		fOut.println();
		fOut.println(Utils.getCurrentDateTime());

		List<Map<String, Object>> reposList;
		reposList = fRepos.getList();

		int workUnits = reposList.size();
		workUnits++; // For post processing
		monitor.beginTask("Load repos summaries", workUnits);

		fStorage.parseRepos(monitor);

		// Notify listeners (currently the views) that the packs changed
		// (for just in case this takes very long, normally the views are
		// not created at this moment)

		fDataManager.notifyNewInput();

		IStatus status;

		if (monitor.isCanceled()) {

			fOut.println("Job cancelled.");
			status = Status.CANCEL_STATUS;

		} else {

			long endTime = System.currentTimeMillis();
			long duration = endTime - beginTime;
			if (duration == 0) {
				duration = 1;
			}

			fOut.print("Load completed in ");
			if (duration < 1000) {
				fOut.println(duration + "ms.");
			} else {
				fOut.println((duration + 500) / 1000 + "s.");
			}

			System.out.println("LoadReposSummariesJob.run() completed");

			status = Status.OK_STATUS;
		}

		sfRunning = false;
		return status;
	}

}
