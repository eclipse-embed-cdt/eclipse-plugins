package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.PdscParser;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshHandler extends AbstractHandler {

	private MessageConsoleStream m_out;
	private boolean m_running;

	/**
	 * The constructor.
	 */
	public RefreshHandler() {
		System.out.println("RefreshHandler()");
		m_running = false;
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		m_out = Activator.getConsole().newMessageStream();

		Job job = new Job("Refresh Packs") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return myRun(monitor);
			}
		};

		job.schedule();
		return null;
	}

	// ------------------------------------------------------------------------

	private IStatus myRun(IProgressMonitor monitor) {

		if (m_running)
			return Status.CANCEL_STATUS;

		m_running = true;
		m_out.println("Refresh packs started.");

		try {

			int workUnits = 100;
			int worked = 0;
			// set total number of work units
			monitor.beginTask("Refresh packs", workUnits);

			// String[] { type, indexUrl }
			List<String[]> sitesList = PacksStorage.getSites();

			// String[] { url, name, version }
			List<String[]> pdscList = new ArrayList<String[]>();

			for (String[] site : sitesList) {

				if (monitor.isCanceled()) {
					break;
				}

				String type = site[0];
				String indexUrl = site[1];
				if (PacksStorage.CMSIS_PACK_TYPE.equals(type)) {
					// collect all pdsc references
					readCmsisIndex(indexUrl, pdscList);
				} else {
					m_out.println(type + " not recognised.");
				}
				monitor.worked(1);
				worked++;
			}

			TreeNode tree = new TreeNode("root");

			int workedFrom = worked;
			if (worked > workUnits) {
				workedFrom = workUnits;
			}

			PdscParser parser = new PdscParser();
			parser.setIsBrief(true);

			int i = 0;
			for (String[] pdsc : pdscList) {

				if (monitor.isCanceled()) {
					break;
				}

				// Make url always end in '/'
				String url = Utils.cosmetiseUrl(pdsc[0]);
				String name = pdsc[1];
				String version = pdsc[2];

				monitor.subTask(name);
				parser.parsePdscBrief(url, name, version, tree);

				++i;
				int newWorked = workedFrom + (workUnits - workedFrom) * i
						/ (pdscList.size() - 1);
				if (newWorked > worked) {
					monitor.worked(newWorked - worked);
					worked = newWorked;
				}
			}

			if (monitor.isCanceled()) {
				m_out.println("Job cancelled.");
				m_running = false;
				return Status.CANCEL_STATUS;
			}

			// Write the tree to the cache.xml file in the packages folder
			PacksStorage.putCache(tree);
			m_out.println("Tree written.");

			PacksStorage.updateInstalled();
			m_out.println("Mark installed packs.");

		} catch (FileNotFoundException e) {
			m_out.println("Error: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Activator.getPacksView().forceRefresh();
				Activator.getDevicesView().forceRefresh();
				Activator.getBoardsView().forceRefresh();
				Activator.getKeywordsView().forceRefresh();
			}
		});

		m_out.println("Refresh packs completed.");
		m_out.println();
		m_running = false;
		return Status.OK_STATUS;
	}

	private void readCmsisIndex(String indexUrl, List<String[]> pdscList) {

		m_out.println("Parsing \"" + indexUrl + "\"...");

		try {

			int count = PacksStorage.readCmsisIndex(indexUrl, pdscList);
			
			m_out.println("Contributed " + count + " pack(s).");

			return;
			
		} catch (FileNotFoundException e) {
			m_out.println("Failed: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		return;

	}
}
