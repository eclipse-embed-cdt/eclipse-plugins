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
import ilg.gnuarmeclipse.packs.Repos;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.storage.PacksStorage;
import ilg.gnuarmeclipse.packs.storage.PacksStorageEvent;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.console.MessageConsoleStream;

public class RemoveJob extends Job {

	private static boolean ms_running = false;

	private MessageConsoleStream m_out;
	private TreeSelection m_selection;

	// private String m_folderPath;
	private IProgressMonitor m_monitor;

	private Repos m_repos;

	private PacksStorage m_storage;

	public RemoveJob(String name, TreeSelection selection) {

		super(name);

		m_out = Activator.getConsoleOut();

		m_selection = selection;

		m_repos = Repos.getInstance();
		m_storage = PacksStorage.getInstance();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (ms_running) {
			return Status.CANCEL_STATUS;
		}

		ms_running = true;
		m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());

		m_out.println("Removing packs...");

		List<Node> packsToRemove = new LinkedList<Node>();

		for (Object obj : m_selection.toArray()) {

			Node node = (Node) obj;

			if (node.isType(Type.VERSION)
					& node.isBooleanProperty(Property.INSTALLED)) {

				// Filter installed versions only
				packsToRemove.add(node);
			}
		}

		int workUnits = packsToRemove.size();

		// set total number of work units
		monitor.beginTask("Remove packs", workUnits);

		List<Leaf> removedPacksList = new LinkedList<Leaf>();

		for (Node versionNode : packsToRemove) {

			if (monitor.isCanceled()) {
				break;
			}

			String packFullName = versionNode.getProperty(
					Property.ARCHIVE_NAME, "");

			// Name the subtask with the pack name
			monitor.subTask(packFullName);
			m_out.println("Remove \"" + packFullName + "\".");

			IPath versionFolderPath;
			try {

				String dest = versionNode.getProperty(Property.DEST_FOLDER, "");
				versionFolderPath = m_repos.getFolderPath().append(dest);

				// Remove the pack archived file
				m_out.println("Recursive erase \"" + versionFolderPath + "\".");

				Utils.deleteFolderRecursive(versionFolderPath.toFile());

				m_monitor.worked(1);

				// Mark node as 'not installed'
				versionNode.setBooleanProperty(Property.INSTALLED, false);

				// Add it to the list for final notifications
				removedPacksList.add(versionNode);

				Utils.reportInfo("Pack " + packFullName + " removed.");

			} catch (IOException e) {
				m_out.println(Utils.reportError(e.getMessage()));
				break;
			}
		}

		// Preserve the count, in case the list is modified by the notified
		// classes
		int count = removedPacksList.size();

		if (count > 0) {
			m_storage.notifyUpdateView(PacksStorageEvent.Type.UPDATE_VERSIONS,
					removedPacksList);
		}

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

			if (count == 1) {
				m_out.println("1 pack removed.");
			} else {
				m_out.println(count + " packs removed.");
			}
			m_out.print("Remove completed in ");
			m_out.println(duration + "ms.");

			status = Status.OK_STATUS;
		}

		ms_running = false;
		return status;
	}

}
