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
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

public class RemoveJob extends Job {

	private static boolean m_running = false;

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

		if (m_running)
			return Status.CANCEL_STATUS;

		m_running = true;
		m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());

		m_out.println("Remove packs job started.");

		List<Node> packs = new ArrayList<Node>();

		for (Object obj : m_selection.toArray()) {
			Node n = (Node) obj;
			String type = n.getType();

			if (Type.VERSION.equals(type) & n.isInstalled()) {
				packs.add(n);
			}
		}

		int workUnits = packs.size();

		// set total number of work units
		monitor.beginTask("Remove packs", workUnits);

		int removedPacksCount = 0;

		for (int i = 0; i < packs.size(); ++i) {

			if (monitor.isCanceled()) {
				break;
			}

			final Node versionNode = packs.get(i);
			final Node packNode = versionNode.getParent();

			String packName = versionNode.getProperty(
					Node.ARCHIVENAME_PROPERTY, "");

			// Name the subtask with the pack name
			monitor.subTask("Remove \"" + packName + "\"");
			m_out.println("Remove \"" + packName + "\".");

			IPath versionFolderPath;
			IPath packFilePath;
			try {

				String dest = versionNode.getProperty(Node.FOLDER_PROPERTY, "");
				versionFolderPath = m_repos.getFolderPath().append(dest);

				// Remove the pack archived file
				packFilePath = m_repos.getFolderPath()
						.append(PacksStorage.DOWNLOAD_FOLDER).append(packName);

			} catch (IOException e) {
				m_running = false;
				return Status.CANCEL_STATUS;
			}

			removeFolderRecursive(versionFolderPath.toFile());

			File packFile = packFilePath.toFile();
			packFile.setWritable(true, false);
			packFile.delete();

			m_monitor.worked(1);

			removedPacksCount++;

			List<Node> deviceNodes = new LinkedList<Node>();
			List<Node> boardNodes = new LinkedList<Node>();

			@SuppressWarnings("unchecked")
			final List<Node>[] lists = (List<Node>[]) (new List<?>[] {
					deviceNodes, boardNodes });

			m_storage.updateInstalledVersionNode(versionNode, false, lists);

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					Activator.getPacksView().update(versionNode);
					Activator.getPacksView().update(packNode);

					Activator.getPacksView().getTreeViewer()
							.setSelection(m_selection);
					Activator.getPacksView().updateButtonsEnableStatus(
							m_selection);

					Activator.getDevicesView().update(lists[0]);
					Activator.getBoardsView().update(lists[1]);
				}
			});

		}

		if (monitor.isCanceled()) {
			m_out.println("Job cancelled.");
			m_running = false;
			return Status.CANCEL_STATUS;
		}

		//
		List<Node> installedVersions = m_storage.getInstalledVersions();

		List<Node> deviceNodes = new LinkedList<Node>();
		List<Node> boardNodes = new LinkedList<Node>();

		@SuppressWarnings("unchecked")
		final List<Node>[] lists = (List<Node>[]) (new List<?>[] { deviceNodes,
				boardNodes });

		for (Node versionNode : installedVersions) {
			m_storage.updateInstalledVersionNode(versionNode, true, lists);

			// Clear children
		}

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Activator.getDevicesView().update(lists[0]);
				Activator.getBoardsView().update(lists[1]);
				Activator.getPacksView().refresh();
			}
		});

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		if (removedPacksCount == 1) {
			m_out.println("1 pack removed.");
		} else {
			m_out.println(removedPacksCount + " packs removed.");
		}
		m_out.print("Job completed in ");
		m_out.println(duration + "ms.");

		m_running = false;
		return Status.OK_STATUS;

	}

	private static void removeFolderRecursive(File path) {
		if (path == null)
			return;
		if (path.exists()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory()) {
					removeFolderRecursive(f);
					f.setWritable(true, false);
					f.delete();
				} else {
					f.setWritable(true, false);
					f.delete();
				}
			}
			path.setWritable(true, false);
			path.delete();
		}
	}
}
