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
import ilg.gnuarmeclipse.packs.PacksStorageEvent;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.console.MessageConsoleStream;

public class InstallJob extends Job {

	private static boolean ms_running = false;

	private MessageConsoleStream m_out;
	private TreeSelection m_selection;

	// private String m_folderPath;
	private IProgressMonitor m_monitor;

	// private Repos m_repos;
	private PacksStorage m_storage;

	public InstallJob(String name, TreeSelection selection) {

		super(name);

		m_out = Activator.getConsoleOut();

		m_selection = selection;

		// m_repos = Repos.getInstance();
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

		m_out.println("Installing packs...");

		List<Node> packsToInstall = new ArrayList<Node>();

		// Iterate selection and build the list of versions to be installed
		for (Object obj : m_selection.toArray()) {

			Node node = (Node) obj;
			// Model properties are passed to view, so we can test them here
			if (!node.isBooleanProperty(Property.INSTALLED)) {

				String type = node.getType();
				if (Type.PACKAGE.equals(type)) {

					// For package nodes, install the top most version
					packsToInstall.add((Node) node.getChildren().get(0));

				} else if (Type.VERSION.equals(type)) {

					// For version nodes, install the given version
					packsToInstall.add(node);
				}
			}
		}

		int workUnits = 0;
		for (int i = 0; i < packsToInstall.size(); ++i) {
			workUnits += computeWorkUnits(packsToInstall.get(i));
		}

		workUnits++;

		// Set the total number of work units
		monitor.beginTask("Install packs", workUnits);

		List<Leaf> installedPacksList = new LinkedList<Leaf>();

		for (Node versionNode : packsToInstall) {

			if (monitor.isCanceled()) {
				break;
			}

			String packFullName = versionNode.getProperty(
					Property.ARCHIVE_NAME, "");

			// Name the subtask with the pack name
			monitor.subTask(packFullName);
			m_out.println("Install \"" + packFullName + "\".");

			try {

				installPack(versionNode);
				installedPacksList.add(versionNode);

				// Mark node as 'installed'.
				versionNode.setBooleanProperty(Property.INSTALLED, true);

			} catch (IOException e) {
				m_out.println("Error " + e);
			}
		}

		if (installedPacksList.size() > 0) {
			m_storage.notifyUpdateView(PacksStorageEvent.Type.UPDATE_VERSIONS,
					installedPacksList);
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

			if (installedPacksList.size() == 1) {
				m_out.print("1 pack");
			} else {
				m_out.print(installedPacksList.size() + " packs");
			}
			m_out.println(" installed.");

			m_out.print("Install completed in ");
			if (duration < 1000) {
				m_out.println(duration + "ms.");
			} else {
				m_out.println((duration + 500) / 1000 + "s.");
			}

			status = Status.OK_STATUS;
		}

		ms_running = false;
		return status;
	}

	private int computeWorkUnits(Node versionNode) {

		int workUnits = 0;

		String size = versionNode.getProperty(Property.ARCHIVE_SIZE, "0");
		try {
			workUnits += Integer.valueOf(size);
		} catch (NumberFormatException e) {
			;
		}

		Node packNode = versionNode.getParent();
		String pdscUrl = packNode.getProperty(Property.ARCHIVE_URL, "");
		if (pdscUrl.length() > 0) {
			try {
				workUnits += Utils.getRemoteFileSize(new URL(pdscUrl));
			} catch (MalformedURLException e) {
				;
			} catch (IOException e) {
				;
			}
		}

		return workUnits;
	}

	private void installPack(Node versionNode) throws IOException {

		// Package node
		URL packUrl = new URL(versionNode.getProperty(Property.ARCHIVE_URL, ""));

		String archiveName = versionNode.getProperty(Property.ARCHIVE_NAME, "");

		File archiveFile = m_storage.getFile(
				new Path(PacksStorage.CACHE_FOLDER), archiveName);

		if (!archiveFile.exists()) {

			// Read in the .pack file from url to a local file.
			File archiveFileDownload = m_storage.getFile(new Path(
					PacksStorage.CACHE_FOLDER), archiveName + ".download");

			// To minimise incomplete file risks, first use a temporary
			// file, then rename to final name.
			copyFile(packUrl, archiveFileDownload);

			archiveFileDownload.renameTo(archiveFile);
		} else {
			m_monitor.worked((int) archiveFile.length());
		}

		String dest = versionNode.getProperty(Property.DEST_FOLDER, "");
		Path destRelPath = new Path(dest);

		File destFolder = destRelPath.toFile();
		if (destFolder.exists()) {

			// Be sure the place is clean (remove possible folder).
			m_out.println("Remove existing \"" + destRelPath + "\".");
			Utils.deleteFolderRecursive(destFolder);
		}

		// Extract all files from the archive to the local folder.
		unzip(archiveFile, destRelPath);

		Utils.makeFolderReadOnlyRecursive(destRelPath.toFile());

		m_out.println("All files write protected.");
	}

	private void copyFile(URL sourceUrl, File destinationFile)
			throws IOException {

		Utils.copyFile(sourceUrl, destinationFile, m_out, m_monitor);
	}

	private void unzip(File archiveFile, Path destRelativePath)
			throws IOException {

		m_out.println("Unzip \"" + archiveFile + "\".");

		// Get the zip file content.
		ZipInputStream zipInput;
		zipInput = new ZipInputStream(new FileInputStream(archiveFile));
		// Get the zipped file list entry
		ZipEntry zipEntry = zipInput.getNextEntry();

		int countFiles = 0;
		int countBytes = 0;
		while (zipEntry != null) {

			// Skip the folder definitions, we automatically create them.
			if (!zipEntry.isDirectory()) {

				String fileName = zipEntry.getName();

				// if (fileName.endsWith(".s")) {
				// fileName = fileName.substring(0, fileName.length() - 1)
				// + "S";
				// }

				File outFile = m_storage.getFile(destRelativePath, fileName);
				m_out.println("Write \"" + outFile + "\".");

				OutputStream output = new FileOutputStream(outFile);

				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = zipInput.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
					countBytes += bytesRead;
				}
				output.close();

				outFile.setReadOnly();
				++countFiles;
			}

			zipEntry = zipInput.getNextEntry();
		}

		m_monitor.worked(1);

		zipInput.closeEntry();
		zipInput.close();
		m_out.println(countFiles + " files written, "
				+ Utils.convertSizeToString(countBytes) + ".");
	}

}
