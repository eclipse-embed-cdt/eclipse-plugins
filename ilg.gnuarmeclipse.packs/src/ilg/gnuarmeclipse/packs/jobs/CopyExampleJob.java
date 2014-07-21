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
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.Repos;
import ilg.gnuarmeclipse.packs.data.Utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.console.MessageConsoleStream;

public class CopyExampleJob extends Job {

	private static boolean ms_running = false;

	private String m_param[];
	private MessageConsoleStream m_out;
	private TreeSelection m_selection;
	private IPath m_destFolderPath;

	private Repos m_repos;
	private IProgressMonitor m_monitor;

	private int m_sizeOfPrefixToStrip;

	public CopyExampleJob(String name, TreeSelection selection, String[] param) {

		super(name);

		m_out = ConsoleStream.getConsoleOut();

		m_selection = selection;
		m_param = param;

		m_sizeOfPrefixToStrip = 0;

		m_repos = Repos.getInstance();
		m_destFolderPath = new Path(m_param[0]);
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

		int exampleCount = 0;

		for (Object o : m_selection.toList()) {

			if (monitor.isCanceled()) {
				break;
			}

			PackNode exampleNode = (PackNode) o;
			PackNode versionNode = (PackNode) exampleNode.getParent();

			Leaf outlineExampleNode = exampleNode.getOutline().getChild(
					Type.EXAMPLE);

			String packRelativeFolder = versionNode
					.getProperty(Property.DEST_FOLDER);
			String exampleRelativeFolder = outlineExampleNode
					.getProperty(Node.FOLDER_PROPERTY);

			m_out.println("Copying example folder \""
					+ new Path(packRelativeFolder)
							.append(exampleRelativeFolder) + "\"...");

			File destFolder = m_destFolderPath.append(exampleRelativeFolder)
					.toFile();

			if (!destFolder.exists()) {
				if (!destFolder.mkdirs()) {
					m_out.println("Cannot create destination folder \""
							+ destFolder.toString() + "\".");
					return Status.CANCEL_STATUS;
				}
			}
			if (!destFolder.isDirectory()) {
				m_out.println("Destination \"" + destFolder.toString()
						+ "\" is not a folder.");
				return Status.CANCEL_STATUS;
			}

			if (destFolder.listFiles().length > 0) {
				m_out.println("Destination \"" + destFolder.toString()
						+ "\" is not an empty folder.");
				m_out.println("Deleting previous content...");
				int count = Utils.deleteFolderRecursive(destFolder);
				m_out.println(count + " files deleted.");
			}

			// Compute source folder

			IPath srcFolderPath;
			try {

				srcFolderPath = m_repos.getFolderPath();
				srcFolderPath = srcFolderPath.append(packRelativeFolder);
				m_sizeOfPrefixToStrip = srcFolderPath.toString().length();
				if (!srcFolderPath.hasTrailingSeparator()) {
					m_sizeOfPrefixToStrip++;
				}
				srcFolderPath = srcFolderPath.append(exampleRelativeFolder);

				int totalWork = (int) getFilesSizeRecursive(srcFolderPath
						.toFile());
				monitor.beginTask("Copy folder", totalWork);

				int count = 0;
				count = copyFolderRecursive(srcFolderPath.toFile());

				m_out.print("Example \"" + exampleNode.getName() + "\", ");
				m_out.print(Utils.convertSizeToString(totalWork) + " in ");
				if (count <= 1) {
					m_out.print("1 file");
				} else {
					m_out.print(count + " files");
				}
				m_out.println(" copied.");

				exampleCount++;

			} catch (IOException e) {
				m_out.print("Error: " + e.toString());
			}
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

			m_out.print("Copy completed in ");
			if (duration < 1000) {
				m_out.print(duration + "ms");
			} else {
				m_out.print((duration + 500) / 1000 + "s");
			}
			if (exampleCount <= 1) {
				m_out.print(" (1 example).");
			} else {
				m_out.print(" (" + exampleCount + " examples).");
			}
			m_out.println();

			status = Status.OK_STATUS;
		}

		ms_running = false;
		return status;
	}

	private long getFilesSizeRecursive(File folder) {

		File files[] = folder.listFiles();
		long countBytes = 0;
		for (File file : files) {
			if (file.isDirectory()) {
				countBytes += getFilesSizeRecursive(file);
			} else if (file.isFile()) {
				countBytes += file.length();
			} else {
				System.out.println("File " + file + " unaccounted.");
			}
		}

		return countBytes;
	}

	private int copyFolderRecursive(File folder) {

		File files[] = folder.listFiles();
		int countFiles = 0;
		for (File file : files) {

			if (m_monitor.isCanceled()) {
				break;
			}

			if (file.isDirectory()) {
				countFiles += copyFolderRecursive(file);
			} else if (file.isFile()) {

				try {
					copyFile(file);
				} catch (IOException e) {
					m_out.print("Error: " + e.toString());
				}
				countFiles += 1;
			}
		}

		return countFiles;
	}

	private void copyFile(File file) throws IOException {

		String path = file.getPath();
		String suffix = path.substring(m_sizeOfPrefixToStrip);
		IPath destPath = m_destFolderPath.append(suffix);

		File destFile = destPath.toFile();

		Utils.copyFile(file, destFile, m_out, m_monitor);
	}
}
