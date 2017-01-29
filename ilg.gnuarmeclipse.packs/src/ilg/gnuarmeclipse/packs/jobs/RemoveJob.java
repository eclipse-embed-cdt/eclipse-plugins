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

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.data.Utils;

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

	private static boolean fgRunning = false;

	private MessageConsoleStream fOut;
	private TreeSelection fSelection;

	// private String m_folderPath;
	private IProgressMonitor fMonitor;

	// private PacksStorage fStorage;
	private DataManager fDataManager;

	public RemoveJob(String name, TreeSelection selection) {

		super(name);

		fOut = ConsoleStream.getConsoleOut();

		fSelection = selection;

		// fStorage = PacksStorage.getInstance();
		fDataManager = DataManager.getInstance();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (fgRunning) {
			return Status.CANCEL_STATUS;
		}

		fgRunning = true;
		fMonitor = monitor;

		long beginTime = System.currentTimeMillis();

		fOut.println();
		fOut.println(ilg.gnuarmeclipse.packs.core.Utils.getCurrentDateTime());

		fOut.println("Removing packs...");

		List<Node> packsToRemove = new LinkedList<Node>();

		for (Object obj : fSelection.toArray()) {

			Node node = (Node) obj;

			if (node.isType(Type.VERSION) & node.isBooleanProperty(Property.INSTALLED)) {

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

			String packFullName = versionNode.getProperty(Property.ARCHIVE_NAME);

			// Name the subtask with the pack name
			monitor.subTask(packFullName);
			fOut.println("Remove \"" + packFullName + "\".");

			IPath versionFolderPath;
			try {

				String dest = versionNode.getProperty(Property.DEST_FOLDER);
				versionFolderPath = PacksStorage.getFolderPath().append(dest);

				// Remove the pack archived file
				fOut.println("Recursive erase \"" + versionFolderPath + "\".");

				Utils.deleteFolderRecursive(versionFolderPath.toFile());

				fMonitor.worked(1);

				// Mark node as 'not installed'
				versionNode.setBooleanProperty(Property.INSTALLED, false);

				// Add it to the list for final notifications
				removedPacksList.add(versionNode);

				Utils.reportInfo("Pack " + packFullName + " removed.");

			} catch (IOException e) {
				fOut.println(Utils.reportError(e.getMessage()));
				break;
			}
		}

		// Preserve the count, in case the list is modified by the notified
		// classes
		int count = removedPacksList.size();

		if (count > 0) {
			fDataManager.notifyUpdateView(DataManagerEvent.Type.UPDATE_VERSIONS, removedPacksList);
			fDataManager.notifyInstallRemove();
		}

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

			if (count == 1) {
				fOut.println("1 pack removed.");
			} else {
				fOut.println(count + " packs removed.");
			}
			fOut.print("Remove completed in ");
			fOut.println(duration + "ms.");

			status = Status.OK_STATUS;
		}

		fgRunning = false;
		return status;
	}

}
