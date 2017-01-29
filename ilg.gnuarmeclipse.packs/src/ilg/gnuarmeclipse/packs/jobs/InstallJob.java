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

import ilg.gnuarmeclipse.core.Activator;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.data.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.console.MessageConsoleStream;

public class InstallJob extends Job {

	private static boolean fgRunning = false;

	private MessageConsoleStream fOut;
	private TreeSelection fSelection;

	// private String m_folderPath;
	private IProgressMonitor fMonitor;

	// private Repos m_repos;
	// private PacksStorage fStorage;
	private DataManager fDataManager;

	public InstallJob(String name, TreeSelection selection) {

		super(name);

		fOut = ConsoleStream.getConsoleOut();

		fSelection = selection;

		// m_repos = Repos.getInstance();
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

		fOut.println("Installing packs...");

		List<Node> packsToInstall = new ArrayList<Node>();

		// Iterate selection and build the list of versions to be installed
		for (Object obj : fSelection.toArray()) {

			Node node = (Node) obj;
			// Model properties are passed to view, so we can test them here
			if (!node.isBooleanProperty(Property.INSTALLED)) {

				String type = node.getType();
				if (Type.PACKAGE.equals(type)) {

					// For package nodes, install the top most version
					packsToInstall.add((Node) node.getFirstChild());

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

			String packFullName = versionNode.getProperty(Property.ARCHIVE_NAME);

			// Name the subtask with the pack name
			monitor.subTask(packFullName);
			fOut.println("Install \"" + packFullName + "\".");

			try {

				if (installPack(versionNode)) {
					installedPacksList.add(versionNode);

					// Mark node as 'installed'.
					versionNode.setBooleanProperty(Property.INSTALLED, true);
				}

			} catch (IOException e) {
				fOut.println(Utils.reportError(e.toString()));
			}
		}

		if (installedPacksList.size() > 0) {
			fDataManager.notifyUpdateView(DataManagerEvent.Type.UPDATE_VERSIONS, installedPacksList);
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

			int n = installedPacksList.size();
			if (n == 0) {
				fOut.print("No packs");
			} else if (n == 1) {
				fOut.print("1 pack");
			} else {
				fOut.print(installedPacksList.size() + " packs");
			}
			fOut.println(" installed.");

			fOut.print("Install completed in ");
			if (duration < 1500) {
				fOut.println(duration + "ms.");
			} else {
				fOut.println((duration + 500) / 1000 + "s.");
			}

			status = Status.OK_STATUS;
		}

		fgRunning = false;
		return status;
	}

	private int computeWorkUnits(Node versionNode) {

		int workUnits = 0;

		String size = versionNode.getProperty(Property.ARCHIVE_SIZE, "0");
		try {
			workUnits += Integer.valueOf(size);
		} catch (NumberFormatException e) {
			Activator.log(e);
		}

		Node packNode = versionNode.getParent();
		String pdscUrl = packNode.getProperty(Property.ARCHIVE_URL);
		if (pdscUrl.length() > 0) {
			try {
				int sz = Utils.getRemoteFileSize(new URL(pdscUrl));
				if (sz > 0) {
					workUnits += sz;
				}
			} catch (IOException e) {
				Activator.log(e);
			}
		}

		return workUnits;
	}

	/**
	 * Try to install a specific version of a pack.
	 * 
	 * @param versionNode
	 * @return true if the pack was correctly downloaded and expanded, false it
	 *         the user decided to ignore this pack.
	 * @throws IOException
	 *             for download errors.
	 */
	private boolean installPack(Node versionNode) throws IOException {

		// Package node
		URL packUrl = new URL(versionNode.getProperty(Property.ARCHIVE_URL));

		String archiveName = versionNode.getProperty(Property.ARCHIVE_NAME);

		File archiveFile = PacksStorage.getCachedFileObject(archiveName);

		if (archiveFile == null || !archiveFile.exists()) {

			// Read in the .pack file from url to a local file.
			File archiveFileDownload = PacksStorage.getCachedFileObject(archiveName + ".download");

			// To minimise incomplete file risks, first use a temporary
			// file, then rename to final name.
			if (copyFile(packUrl, archiveFileDownload)) {

				archiveFileDownload.renameTo(archiveFile);

				Utils.reportInfo("Pack " + archiveName + " downloaded.");
			} else {
				return false;
			}
		} else {
			fMonitor.worked((int) archiveFile.length());
		}

		String dest = versionNode.getProperty(Property.DEST_FOLDER);
		Path destRelPath = new Path(dest);

		File destFolder = PacksStorage.getFileObject(dest);
		if (destFolder.exists()) {

			// Be sure the place is clean (remove possible folder).
			fOut.println("Remove existing \"" + destRelPath + "\".");
			Utils.deleteFolderRecursive(destFolder);
		}

		boolean flag = false;
		try {
			flag = unzip(archiveFile, destRelPath);
		} catch (IOException e) {
			String msg = e.getMessage() + ", file: " + archiveFile.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
		}

		// Extract all files from the archive to the local folder.
		if (!flag) {
			fOut.println("Install cancelled due to errors.");

			// Remove partial install
			Utils.deleteFolderRecursive(destFolder);

			// Remove the broken archive file from the cache
			archiveFile.delete();

			return false;
		}

		Utils.makeFolderReadOnlyRecursive(destRelPath.toFile());

		Utils.reportInfo("Pack " + archiveName + " installed.");

		fOut.println("All files write protected.");

		return true;
	}

	/**
	 * Try to download a file from a URL.
	 * 
	 * @param sourceUrl
	 * @param destinationFile
	 * @return true for successful download; false if the user decided to ignore
	 *         it.
	 * @throws IOException
	 *             for download errors.
	 */
	private boolean copyFile(URL sourceUrl, File destinationFile) throws IOException {

		Utils.copyFile(sourceUrl, destinationFile, fOut, fMonitor);
		return true;
	}

	private boolean unzip(File archiveFile, IPath destRelativePath) throws IOException {

		fOut.println("Unzip \"" + archiveFile + "\".");

		boolean result = true;

		// Get the zip file content.
		ZipInputStream zipInput;
		zipInput = new ZipInputStream(new FileInputStream(archiveFile));
		// Get the zipped file list entry
		ZipEntry zipEntry = zipInput.getNextEntry();

		int countFiles = 0;
		int countBytes = 0;
		while (zipEntry != null && (result == true)) {

			// Skip the folder definitions, we automatically create them.
			if (!zipEntry.isDirectory()) {

				String fileName = zipEntry.getName();

				IPath path = destRelativePath.append(fileName);
				File outFile = PacksStorage.getFileObject(path.toString());
				if (!outFile.getParentFile().exists()) {
					outFile.getParentFile().mkdirs();
				}
				fOut.println("Write \"" + outFile + "\".");

				OutputStream output = new FileOutputStream(outFile);

				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = zipInput.read(buf)) > 0) {
					try {
						output.write(buf, 0, bytesRead);
					} catch (IOException e) {
						String msg = e.getMessage() + ", file: " + outFile.getName();
						fOut.println("Error: " + msg);
						Utils.reportError(msg);

						result = false;
						break;
					}
					countBytes += bytesRead;
				}
				try {
					output.close();
				} catch (IOException e) {
					String msg = e.getMessage() + ", file: " + outFile.getName();
					fOut.println("Error: " + msg);
					Utils.reportError(msg);

					result = false;
				}

				outFile.setReadOnly();
				++countFiles;

			}

			zipEntry = zipInput.getNextEntry();
		}

		fMonitor.worked(1);

		zipInput.closeEntry();
		zipInput.close();
		if (countBytes > 0) {
			fOut.println(countFiles + " files written, " + StringUtils.convertSizeToString(countBytes) + ".");
		} else {
			fOut.println("No files written.");
			result = false;
		}
		return result;
	}

}
