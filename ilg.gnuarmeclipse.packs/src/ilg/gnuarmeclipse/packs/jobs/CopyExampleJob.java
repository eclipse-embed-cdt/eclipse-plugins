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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

public class CopyExampleJob extends Job {

	private static boolean m_running = false;

	private MessageConsoleStream m_out;
	private TreeSelection m_selection;

	// private String m_folderPath;
	private IProgressMonitor m_monitor;

	public CopyExampleJob(String name, TreeSelection selection) {

		super(name);

		m_out = Activator.getConsoleOut();

		m_selection = selection;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (m_running)
			return Status.CANCEL_STATUS;

		m_running = true;
		m_monitor = monitor;

		m_out.println("Copy example started.");

		if (monitor.isCanceled()) {
			m_out.println("Job cancelled.");
			m_running = false;
			return Status.CANCEL_STATUS;
		}

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				System.out.println("g");
			}
		});

		m_out.println("Copy example completed.");
		m_out.println();

		m_running = false;
		return Status.OK_STATUS;

	}

}
