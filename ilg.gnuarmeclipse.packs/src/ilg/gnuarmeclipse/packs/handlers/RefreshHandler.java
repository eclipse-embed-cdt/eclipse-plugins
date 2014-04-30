package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.SitesStorage;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.MessageDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshHandler extends AbstractHandler {

	private MessageConsoleStream m_out;

	/**
	 * The constructor.
	 */
	public RefreshHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (false) {
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindowChecked(event);
			MessageDialog.openInformation(window.getShell(),
					"GNU ARM C/C++ Packages Support",
					"Prepare for the 10 sec job");
		}

		MessageConsole myConsole = Utils.findConsole();
		m_out = myConsole.newMessageStream();

		Job job = new Job("My Job") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return myRun(monitor);
			}
		};

		// TODO: prevent re-entry
		job.schedule();
		return null;
	}

	// ------------------------------------------------------------------------

	private IStatus myRun(IProgressMonitor monitor) {

		m_out.println("Refresh packs");

		// set total number of work units
		monitor.beginTask("Refresh packs", 100);

		List<String[]> sitesList = SitesStorage.getSites();

		for (String[] site : sitesList) {
			String type = site[0];
			String indexUrl = site[1];
			if (SitesStorage.CMSIS_PACK_TYPE.equals(type)) {
				List<String[]> pdscList = new ArrayList<String[]>();
				readCmsisIndex(indexUrl, pdscList);
			} else {
				m_out.println(type + " not recognised");
			}
		}

		if (false) {
			for (int i = 0; i < 10; i++) {
				try {
					// sleep a second
					TimeUnit.SECONDS.sleep(1);
					if (monitor.isCanceled()) {
						break;
					}

					monitor.subTask("I'm doing something here " + i);
					m_out.println("I'm doing something here " + i);

					// report that 10 additional units are done
					monitor.worked(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					System.out.println("Job cancelled");
					return Status.CANCEL_STATUS;
				}
			}
		}
		if (monitor.isCanceled()) {
			m_out.println("Job cancelled");
			return Status.CANCEL_STATUS;
		}
		m_out.println("Refresh packs completed");
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
			NodeList elemList = el.getElementsByTagName("pdsc");
			for (int i = 0; i < elemList.getLength(); ++i) {
				Node siteNode = elemList.item(i);

				NamedNodeMap attrs = siteNode.getAttributes();
				String url = attrs.getNamedItem("url").getNodeValue();
				String name = attrs.getNamedItem("name").getNodeValue();
				String version = attrs.getNamedItem("version").getNodeValue();

				pdscList.add(new String[] { url, name, version });
				++count;
			}

			m_out.println("Contributed " + count + " packs.");

			return;
		} catch (ParserConfigurationException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		} catch (SAXException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		} catch (FileNotFoundException e) {
			m_out.println("Failed: " + e.toString());
		} catch (IOException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		return;

	}
}
