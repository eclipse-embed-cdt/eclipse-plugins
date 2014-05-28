package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.PdscParser;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

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

		MessageConsole myConsole = Utils.findConsole();
		m_out = myConsole.newMessageStream();

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

				String url = pdsc[0];
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
			URL u = new URL(indexUrl);

			// Read from url to local buffer
			BufferedReader in = new BufferedReader(new InputStreamReader(
					u.openStream()));
			String line = null;

			// Insert missing root element
			StringBuilder buffer = new StringBuilder();
			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			buffer.append("<root>\n");

			while ((line = in.readLine()) != null) {
				if (line.startsWith("<pdsc")) {
					buffer.append(line);
				}
			}
			buffer.append("</root>\n");

			// Parse from local buffer
			InputSource inputSource = new InputSource(new StringReader(
					buffer.toString()));

			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = parser.parse(inputSource);

			Element el = document.getDocumentElement();
			if (!"root".equals(el.getNodeName())) {
				return;
			}

			int count = 0;
			List<Element> pdscElements = Utils.getChildElementsList(el, "pdsc");
			for (Element pdscElement : pdscElements) {

				String url = pdscElement.getAttribute("url").trim();
				String name = pdscElement.getAttribute("name").trim();
				String version = pdscElement.getAttribute("version").trim();

				pdscList.add(new String[] { url, name, version });
				++count;
			}

			m_out.println("Contributed " + count + " packs.");

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
