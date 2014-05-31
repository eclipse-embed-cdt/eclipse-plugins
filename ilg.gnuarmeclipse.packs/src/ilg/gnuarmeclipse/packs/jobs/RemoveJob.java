package ilg.gnuarmeclipse.packs.jobs;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.TreeNode;

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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

public class RemoveJob extends Job {

	private static boolean m_running = false;

	private MessageConsoleStream m_out;
	private TreeSelection m_selection;

	// private String m_folderPath;
	private IProgressMonitor m_monitor;

	public RemoveJob(String name, TreeSelection selection) {

		super(name);

		m_out = Activator.getConsole().newMessageStream();

		m_selection = selection;

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (m_running)
			return Status.CANCEL_STATUS;

		m_running = true;
		m_monitor = monitor;

		m_out.println("Remove packs started.");

		List<TreeNode> packs = new ArrayList<TreeNode>();

		for (Object obj : m_selection.toArray()) {
			TreeNode n = (TreeNode) obj;
			String type = n.getType();

			if (TreeNode.VERSION_TYPE.equals(type) & n.isInstalled()) {
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

			final TreeNode versionNode = packs.get(i);
			final TreeNode packNode = versionNode.getParent();

			String packName = versionNode.getProperty(
					TreeNode.ARCHIVENAME_PROPERTY, "");

			// Name the subtask with the pack name
			monitor.subTask("Remove \"" + packName + "\"");
			m_out.println("Remove \"" + packName + "\".");

			IPath versionFolderPath;
			IPath packFilePath;
			try {

				String dest = versionNode.getProperty(TreeNode.FOLDER_PROPERTY,
						"");
				versionFolderPath = PacksStorage.getFolderPath().append(dest);

				// Remove the pack archived file
				packFilePath = PacksStorage.getFolderPath()
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

			List<TreeNode> deviceNodes = new LinkedList<TreeNode>();
			List<TreeNode> boardNodes = new LinkedList<TreeNode>();

			@SuppressWarnings("unchecked")
			final List<TreeNode>[] lists = (List<TreeNode>[]) (new List<?>[] {
					deviceNodes, boardNodes });

			PacksStorage.updateInstalledVersionNode(versionNode, false, lists);

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
		List<TreeNode> installedVersions = PacksStorage.getInstalledVersions();

		List<TreeNode> deviceNodes = new LinkedList<TreeNode>();
		List<TreeNode> boardNodes = new LinkedList<TreeNode>();

		@SuppressWarnings("unchecked")
		final List<TreeNode>[] lists = (List<TreeNode>[]) (new List<?>[] {
				deviceNodes, boardNodes });

		for (TreeNode versionNode : installedVersions) {
			PacksStorage.updateInstalledVersionNode(versionNode, true, lists);

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

		if (removedPacksCount == 1) {
			m_out.println("1 pack removed.");
		} else {
			m_out.println(removedPacksCount + " packs removed.");
		}
		m_out.println();

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
