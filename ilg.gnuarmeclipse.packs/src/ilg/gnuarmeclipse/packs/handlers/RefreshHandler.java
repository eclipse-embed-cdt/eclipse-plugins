package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
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

		// TODO: prevent re-entry
		job.schedule();
		return null;
	}

	// ------------------------------------------------------------------------

	private IStatus myRun(IProgressMonitor monitor) {

		if (m_running)
			return Status.CANCEL_STATUS;

		m_running = true;
		m_out.println("Refresh packs");

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
				m_out.println(type + " not recognised");
			}
			monitor.worked(1);
			worked++;
		}

		TreeNode tree = new TreeNode("root");

		int workedFrom = worked;
		if (worked > workUnits) {
			workedFrom = workUnits;
		}

		int i = 0;
		for (String[] pdsc : pdscList) {
			
			if (monitor.isCanceled()) {
				break;
			}

			String url = pdsc[0];
			String name = pdsc[1];
			String version = pdsc[2];

			monitor.subTask(name);
			parsePdsc(url, name, version, tree);

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
		m_out.println("Tree written.");
		PacksStorage.putCache(tree);

		m_out.println("Refresh packs completed.");
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

	private void parsePdsc(String url, String name, String version,
			TreeNode tree) {

		String fullUrl;
		if (url.endsWith("/")) {
			fullUrl = url + name;
		} else {
			fullUrl = url + "/" + name;
		}

		m_out.println("Parsing \"" + fullUrl + "\" v" + version + "...");

		try {

			URL u = new URL(fullUrl);
			InputSource inputSource = new InputSource(new InputStreamReader(
					u.openStream()));

			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document document = parser.parse(inputSource);

			Element el = document.getDocumentElement();
			if (!"package".equals(el.getNodeName())) {
				return;
			}

			NodeList elemList;

			elemList = el.getElementsByTagName("vendor");
			if (elemList.getLength() == 0) {
				m_out.println("Missing <vendor>");
				return;
			}
			String packVendor = elemList.item(0).getTextContent().trim();

			elemList = el.getElementsByTagName("name");
			if (elemList.getLength() == 0) {
				m_out.println("Missing <name>");
				return;
			}
			String packName = elemList.item(0).getTextContent().trim();

			elemList = el.getElementsByTagName("description");
			if (elemList.getLength() == 0) {
				m_out.println("Missing <description>");
				return;
			}
			String packDescription = elemList.item(0).getTextContent().trim();

			TreeNode vendorNode = tree.getChild(packVendor);
			if (vendorNode == null) {
				vendorNode = new TreeNode("vendor");
				vendorNode.setName(packVendor);

				tree.addChild(vendorNode);
				m_out.println("Vendor node \"" + packVendor + "\" added.");
			}

			TreeNode packNode = vendorNode.getChild(packName);
			if (packNode != null) {
				m_out.println("Duplicate package name \"" + packName
						+ "\", ignored.");
				return;
			}

			packNode = new TreeNode("package");
			packNode.setName(packName);
			packNode.setDescription(packDescription);

			vendorNode.addChild(packNode);
			m_out.println("Package node \"" + packName + "\" added.");

			TreeNode versionNode = new TreeNode("version");
			versionNode.setName(version);
			packNode.addChild(versionNode);

		} catch (MalformedURLException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		} catch (FileNotFoundException e) {
			m_out.println("Failed: " + e.toString());
		} catch (IOException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		} catch (ParserConfigurationException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		} catch (SAXException e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

	}

}
