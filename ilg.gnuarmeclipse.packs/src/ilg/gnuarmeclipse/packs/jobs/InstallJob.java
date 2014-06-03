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
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

public class InstallJob extends Job {

	private static boolean ms_running = false;

	private MessageConsoleStream m_out;
	private TreeSelection m_selection;

	// private String m_folderPath;
	private IProgressMonitor m_monitor;

	private Repos m_repos;
	private PacksStorage m_storage;

	public InstallJob(String name, TreeSelection selection) {

		super(name);

		m_out = Activator.getConsoleOut();

		m_selection = selection;

		m_repos = Repos.getInstance();
		m_storage = PacksStorage.getInstance();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (ms_running)
			return Status.CANCEL_STATUS;

		ms_running = true;
		m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		m_out.println("Install packs started.");

		List<TreeNode> packs = new ArrayList<TreeNode>();

		// Iterate selection and build list of nodes
		for (Object obj : m_selection.toArray()) {
			TreeNode n = (TreeNode) obj;
			if (!n.isInstalled()) {

				String type = n.getType();
				if (TreeNode.PACKAGE_TYPE.equals(type)) {

					// For package nodes, install the top most version
					packs.add(n.getChildren().get(0));

				} else if (TreeNode.VERSION_TYPE.equals(type)) {

					// For version nodes, install the given version
					packs.add(n);
				}
			}
		}

		int workUnits = 0;
		for (int i = 0; i < packs.size(); ++i) {
			workUnits += computeWorkUnits(packs.get(i));
		}

		workUnits++;

		// set total number of work units
		monitor.beginTask("Install packs", workUnits);

		int installedPacksCount = 0;

		for (int i = 0; i < packs.size(); ++i) {

			if (monitor.isCanceled()) {
				break;
			}

			final TreeNode versionNode = packs.get(i);
			final TreeNode packNode = versionNode.getParent();

			String packName = versionNode.getProperty(
					TreeNode.ARCHIVENAME_PROPERTY, "");

			// Name the subtask with the pack name
			monitor.subTask(packName);
			m_out.println("Install \"" + packName + "\".");

			try {

				installPack(versionNode);
				installedPacksCount++;

				List<TreeNode> deviceNodes = new LinkedList<TreeNode>();
				List<TreeNode> boardNodes = new LinkedList<TreeNode>();

				@SuppressWarnings("unchecked")
				final List<TreeNode>[] lists = (List<TreeNode>[]) (new List<?>[] {
						deviceNodes, boardNodes });

				m_storage.updateInstalledVersionNode(versionNode, true, lists);

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						Activator.getPacksView().update(versionNode);
						Activator.getPacksView().update(packNode);

						ISelection selection = new StructuredSelection(
								versionNode);
						Activator.getPacksView().getTreeViewer()
								.setSelection(selection);
						Activator.getPacksView().updateButtonsEnableStatus(
								m_selection);

						Activator.getDevicesView().update(lists[0]);
						Activator.getBoardsView().update(lists[1]);
					}
				});

			} catch (IOException e) {
				m_out.println("Error " + e);
			}
		}

		if (monitor.isCanceled()) {
			m_out.println("Job cancelled.");
			ms_running = false;
			return Status.CANCEL_STATUS;
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		if (installedPacksCount == 1) {
			m_out.print("1 pack");
		} else {
			m_out.print(installedPacksCount + " packs");
		}
		m_out.println(" installed.");
		if (duration < 1000) {
			m_out.println("Completed in " + duration + "ms.");
		} else {
			m_out.println("Completed in " + (duration + 500) / 1000 + "s.");
		}
		m_out.println();

		ms_running = false;
		return Status.OK_STATUS;
	}

	private int computeWorkUnits(TreeNode versionNode) {

		int workUnits = 0;

		String size = versionNode.getProperty(TreeNode.SIZE_PROPERTY, "0");
		try {
			workUnits += Integer.valueOf(size);
		} catch (NumberFormatException e) {
			;
		}

		TreeNode packNode = versionNode.getParent();
		String pdscUrl = packNode.getProperty(TreeNode.PDSCURL_PROPERTY, "");
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

	private void installPack(TreeNode versionNode) throws IOException {

		// Package node
		URL packUrl = new URL(versionNode.getProperty(
				TreeNode.ARCHIVEURL_PROPERTY, ""));

		String packName = versionNode.getProperty(
				TreeNode.ARCHIVENAME_PROPERTY, "");

		File packFile = getFile(new Path(PacksStorage.DOWNLOAD_FOLDER),
				packName);

		// Read in the .pack file to a local file
		copyFile(packUrl, packFile);

		// Version node
		TreeNode packNode = versionNode.getParent();

		URL pdscUrl = new URL(packNode.getProperty(TreeNode.PDSCURL_PROPERTY,
				""));
		String pdscName = packNode.getProperty(TreeNode.PDSCNAME_PROPERTY, "");

		File pdscFile = getFile(new Path(PacksStorage.DOWNLOAD_FOLDER),
				pdscName);

		// Read in the .pdsc file
		// If .pack is not there, this is not reached
		copyFile(pdscUrl, pdscFile);

		String dest = versionNode.getProperty(TreeNode.FOLDER_PROPERTY, "");
		Path destRelPath = new Path(dest);
		// extract files from archive to local folder
		unzip(packFile, destRelPath);

		makeFolderReadOnlyRecursive(destRelPath.toFile());

		m_out.println("All files set to read only.");
	}

	private void copyFile(URL sourceUrl, File destinationFile)
			throws IOException {

		URLConnection connection = sourceUrl.openConnection();
		int size = connection.getContentLength();
		String sizeString = Utils.convertSizeToString(size);
		m_out.println("Copy " + sizeString + " \"" + sourceUrl + "\" to \""
				+ destinationFile + "\".");

		InputStream input = connection.getInputStream();
		OutputStream output = new FileOutputStream(destinationFile);

		byte[] buf = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buf)) > 0) {
			output.write(buf, 0, bytesRead);
			m_monitor.worked(bytesRead);
		}
		output.close();
		input.close();
	}

	private File getFile(Path pathPart, String name) {

		IPath path;
		try {
			path = m_repos.getFolderPath().append(pathPart).append(name);
		} catch (IOException e) {
			return null;
		}
		File file = path.toFile();
		new File(file.getParent()).mkdirs();

		return file; // may be null
	}

	private void unzip(File archiveFile, Path destRelativePath)
			throws IOException {

		m_out.println("Unzip \"" + archiveFile + "\".");

		// Get the zip file content
		ZipInputStream zipInput;
		zipInput = new ZipInputStream(new FileInputStream(archiveFile));
		// Get the zipped file list entry
		ZipEntry zipEntry = zipInput.getNextEntry();

		int countFiles = 0;
		int countBytes = 0;
		while (zipEntry != null) {

			// Skip the folder definitions, we automatically create them
			if (!zipEntry.isDirectory()) {

				String fileName = zipEntry.getName();

				// if (fileName.endsWith(".s")) {
				// fileName = fileName.substring(0, fileName.length() - 1)
				// + "S";
				// }

				File outFile = getFile(destRelativePath, fileName);
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

	private static void makeFolderReadOnlyRecursive(File path) {

		if (path == null)
			return;
		if (path.exists()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory()) {
					makeFolderReadOnlyRecursive(f);
					f.setWritable(false, false);
				} else {
					f.setWritable(false, false);
				}
			}
			path.setWritable(false, false);
		}
	}

}
