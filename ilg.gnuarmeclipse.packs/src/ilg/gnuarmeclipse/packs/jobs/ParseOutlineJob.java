package ilg.gnuarmeclipse.packs.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.PdscParser;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.ui.views.OutlineView;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.xml.sax.SAXException;

public class ParseOutlineJob extends Job {

	private static boolean m_running = false;

	private MessageConsoleStream m_out;
	private TreeNode m_versionNode;
	private TreeViewer m_viewer;

	private String m_folderPath;
	private IProgressMonitor m_monitor;

	public ParseOutlineJob(String name, TreeNode node, TreeViewer viewer) {

		super(name);

		MessageConsole myConsole = Utils.findConsole();
		m_out = myConsole.newMessageStream();

		m_versionNode = node;
		m_viewer = viewer;

		try {
			m_folderPath = PacksStorage.getFolderPath();
		} catch (IOException e) {
			Activator.log(e);
		}

	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		if (m_folderPath == null) {
			return Status.CANCEL_STATUS;
		}

		if (m_running) {
			return Status.CANCEL_STATUS;
		}
		m_running = true;
		m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		TreeNode packNode = m_versionNode.getParent();
		String packName = packNode.getName();

		String vendorName = packNode.getProperty(TreeNode.VENDOR_PROPERTY);
		String versionName = m_versionNode.getName();

		String pdscName = vendorName + "." + packName + ".pdsc";
		m_out.println("Parse \"" + pdscName + "\" outline started.");

		IPath path = new Path(m_folderPath).append(vendorName).append(packName)
				.append(versionName).append(pdscName);

		TreeNode outlineNode = null;
		try {
			PdscParser parser = new PdscParser();
			outlineNode = parser.parsePdscFull(path);
			m_versionNode.setOutline(outlineNode);
		} catch (FileNotFoundException e) {
			m_out.println("Failed: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		m_out.println("Parse completed in " + duration + "ms.");
		m_out.println();

		if (outlineNode != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					m_viewer.setAutoExpandLevel(OutlineView.AUTOEXPAND_LEVEL);
					m_viewer.setInput(m_versionNode);
				}
			});
		}

		m_running = false;
		return Status.OK_STATUS;
	}

}
