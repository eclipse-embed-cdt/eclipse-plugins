package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class InstallJob extends Job {

	private static String DOWNLOAD_FOLDER = ".Download";

	private static boolean m_running = false;

	private MessageConsoleStream m_out;
	private TreeSelection m_selection;

	private String m_folderPath;
	private IProgressMonitor m_monitor;

	public InstallJob(String name, TreeSelection selection) {
		super(name);

		MessageConsole myConsole = Utils.findConsole();
		m_out = myConsole.newMessageStream();

		m_selection = selection;

		m_folderPath = PacksStorage.getFolderPath();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (m_running)
			return Status.CANCEL_STATUS;

		m_running = true;
		m_monitor = monitor;

		m_out.println("Install packs started.");

		List<TreeNode> packs = new ArrayList<TreeNode>();

		for (Object obj : m_selection.toArray()) {
			TreeNode n = (TreeNode) obj;
			String type = n.getType();

			if ("package".equals(type) & !n.isInstalled()) {
				packs.add(n.getChildren().get(0));
			} else if ("version".equals(type) & !n.isInstalled()) {
				packs.add(n);
			}
		}

		int workUnits = 1000;
		for (int i = 0; i < packs.size(); ++i) {
			try {
				workUnits += computeWorkUnits(packs.get(i));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// set total number of work units
		monitor.beginTask("Install packs", workUnits);

		int installedPacksCount = 0;

		for (int i = 0; i < packs.size(); ++i) {

			if (monitor.isCanceled()) {
				break;
			}

			final TreeNode versionNode = packs.get(i);
			final TreeNode packNode = versionNode.getParent();

			String vendor = packNode.getProperty(TreeNode.VENDOR_PROPERTY);

			String packName = vendor + "." + packNode.getName() + "."
					+ versionNode.getName() + ".pack";

			// Name the subtask with the pack name
			monitor.subTask(packName);
			m_out.println("Install \"" + packName + "\"");

			try {

				installPack(versionNode);
				installedPacksCount++;

				versionNode.setIsInstalled(true);
				packNode.setIsInstalled(true);

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						Activator.getPacksView().update(versionNode);
						Activator.getPacksView().update(packNode);
					}
				});

			} catch (IOException e) {
				m_out.println("Error " + e);
			}
		}

		if (monitor.isCanceled()) {
			m_out.println("Job cancelled.");
			m_running = false;
			return Status.CANCEL_STATUS;
		}

		// Write the tree to the cache.xml file in the packages folder
		// m_out.println("Tree written.");
		// PacksStorage.putCache(tree);

		if (installedPacksCount == 1) {
			m_out.println("1 pack installed.");
		} else {
			m_out.println(installedPacksCount + " packs installed.");
		}
		m_out.println();

		m_running = false;
		return Status.OK_STATUS;

	}

	private int computeWorkUnits(TreeNode versionNode) throws IOException {

		TreeNode packNode = versionNode.getParent();

		String vendor = packNode.getProperty("vendor");

		String url = packNode.getProperty(TreeNode.URL_PROPERTY);

		String pdscName = vendor + "." + packNode.getName() + ".pdsc";
		URL pdscUrl;
		pdscUrl = new URL(url + "/" + pdscName);

		String packName = vendor + "." + packNode.getName() + "."
				+ versionNode.getName() + ".pack";
		URL packUrl;
		packUrl = new URL(url + "/" + packName);

		int workUnits = 0;
		// Start with .pack
		workUnits += computeFileWorkUnits(packUrl);

		// If .pack is not there, this is not reached
		workUnits += computeFileWorkUnits(pdscUrl);

		return workUnits;
	}

	private int computeFileWorkUnits(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		return connection.getContentLength();
	}

	private void installPack(TreeNode versionNode) throws IOException {

		TreeNode packNode = versionNode.getParent();

		String vendor = packNode.getProperty("vendor");

		String url = packNode.getProperty(TreeNode.URL_PROPERTY);

		String pdscName = vendor + "." + packNode.getName() + ".pdsc";
		URL pdscUrl;
		pdscUrl = new URL(url + "/" + pdscName);
		File pdscFile = getFile(new Path(DOWNLOAD_FOLDER), pdscName);

		String packName = vendor + "." + packNode.getName() + "."
				+ versionNode.getName() + ".pack";
		URL packUrl;
		packUrl = new URL(url + "/" + packName);

		File packFile = getFile(new Path(DOWNLOAD_FOLDER), packName);

		// Read in the .pack file
		copyFile(packUrl, packFile);

		// Read in the .pdsc file
		// If .pack is not there, this is not reached
		copyFile(pdscUrl, pdscFile);

		Path path = new Path(vendor + "/" + packNode.getName() + "/"
				+ versionNode.getName());
		// extract files from archive
		unzip(path, packFile);

	}

	private void copyFile(URL sourceUrl, File destinationFile)
			throws IOException {

		URLConnection connection = sourceUrl.openConnection();
		int size = connection.getContentLength();
		String sizeString = convertSizeToString(size);
		m_out.println("Copy " + sizeString + " \"" + sourceUrl + "\" to \""
				+ destinationFile + "\"");

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

		if (m_folderPath.length() == 0)
			return null;

		IPath path = (new Path(m_folderPath)).append(pathPart).append(name);
		File file = path.toFile();
		new File(file.getParent()).mkdirs();

		return file; // may be null
	}

	private void unzip(Path pathPart, File archiveFile) throws IOException {

		m_out.println("Unzip \"" + archiveFile + "\"");

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

				File outFile = getFile(pathPart, fileName);
				m_out.println("Write \"" + outFile + "\"");

				OutputStream output = new FileOutputStream(outFile);

				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = zipInput.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
					countBytes += bytesRead;
				}
				output.close();
				++countFiles;
			}

			zipEntry = zipInput.getNextEntry();
		}

		zipInput.closeEntry();
		zipInput.close();
		m_out.println(countFiles + " files written, " + convertSizeToString(countBytes));
	}

	private String convertSizeToString(int size) {
		
		String sizeString;
		if (size < 1024) {
			sizeString = String.valueOf(size) + "B";
		} else if (size < 1024 * 1024) {
			sizeString = String.valueOf((size + (1024 / 2)) / 1024) + "kB";
		} else {
			sizeString = String.valueOf((size + ((1024 * 1024) / 2))
					/ (1024 * 1024))
					+ "MB";
		}
		return sizeString;
	}
}
