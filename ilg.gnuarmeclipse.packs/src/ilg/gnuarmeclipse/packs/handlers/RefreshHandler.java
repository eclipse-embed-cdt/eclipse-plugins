package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.TreeNode.Condition;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
		m_out.println("Refresh packs started.");

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

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				Activator.getPacksView().forceRefresh();
			}
		});

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
			List<Element> pdscElements = Utils.getChildElementList(el, "pdsc");
			for (Element pdscElement : pdscElements) {

				String url = pdscElement.getAttribute("url").trim();
				String name = pdscElement.getAttribute("name").trim();
				String version = pdscElement.getAttribute("version").trim();

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

			Element packageElement = document.getDocumentElement();
			if (!"package".equals(packageElement.getNodeName())) {
				return;
			}

			// Packages
			TreeNode packNode;
			{
				TreeNode packagesNode = tree.addUniqueChild("packages", null);

				Element vendorElement = Utils.getChildElement(packageElement,
						"vendor");
				if (vendorElement == null) {
					m_out.println("Missing <vendor>");
					return;
				}
				String packVendor = vendorElement.getTextContent().trim();

				Element nameElement = Utils.getChildElement(packageElement,
						"name");
				if (nameElement == null) {
					m_out.println("Missing <name>");
					return;
				}
				String packName = nameElement.getTextContent().trim();

				Element descriptionElement = Utils.getChildElement(
						packageElement, "description");
				if (descriptionElement == null) {
					m_out.println("Missing <description>");
					return;
				}
				String packDescription = descriptionElement.getTextContent()
						.trim();

				TreeNode vendorNode = packagesNode.addUniqueChild("vendor",
						packVendor);

				packNode = vendorNode.getChild("package", packName);
				if (packNode != null) {
					m_out.println("Duplicate package name \"" + packName
							+ "\", ignored.");
					return;
				}

				packNode = new TreeNode("package");
				packNode.setName(packName);
				packNode.setDescription(packDescription);

				String shortUrl = url;
				if (shortUrl.endsWith("/")) {
					shortUrl = shortUrl.substring(0, shortUrl.length() - 1);
				}
				packNode.putProperty(TreeNode.URL_PROPERTY, shortUrl);

				packNode.putProperty(TreeNode.VENDOR_PROPERTY, packVendor);

				// Attach package right below vendor node
				vendorNode.addChild(packNode);

				m_out.println("Package node \"" + packName + "\" added.");
			}

			// Kludge: use URL to detect empty package
			// TODO: use a better condition
			Element urlElement = Utils.getChildElement(packageElement, "url");
			if (urlElement == null
					|| urlElement.getTextContent().trim().length() == 0) {

				TreeNode.Condition condition = packNode.new Condition(
						Condition.DEPRECATED_TYPE);
				packNode.addCondition(condition);
			}

			// Releases
			Element releasesElement = Utils.getChildElement(packageElement,
					"releases");
			if (releasesElement != null) {

				List<Element> releaseElements = Utils.getChildElementList(
						releasesElement, "release");
				for (Element releaseElement : releaseElements) {
					String releaseVersion = releaseElement.getAttribute(
							"version").trim();
					String description = releaseElement.getTextContent();

					if (description != null) {
						description = description.trim();
					} else {
						description = "";
					}

					TreeNode versionNode = packNode.addUniqueChild("version",
							releaseVersion);
					versionNode.setDescription(description);
					m_out.println("Version node \"" + releaseVersion
							+ "\" added.");
				}
			} else {
				packNode.addUniqueChild("version", version);
				m_out.println("Version node \"" + version
						+ "\" added. (value from index)");
			}

			// Devices
			Element devicesElement = Utils.getChildElement(packageElement,
					"devices");
			if (devicesElement != null) {

				TreeNode devicesNode = tree.addUniqueChild("devices", null);

				List<Element> familyElements = Utils.getChildElementList(
						devicesElement, "family");
				for (Element familyElement : familyElements) {
					String family = familyElement.getAttribute("Dfamily")
							.trim();
					String vendor = familyElement.getAttribute("Dvendor")
							.trim();

					String va[] = vendor.split("[:]");
					TreeNode vendorNode = devicesNode.addUniqueChild("vendor",
							va[0]);
					if (va.length < 2) {
						// Vendor not enumeration
						continue;
					}
					vendorNode.putProperty("vendorid", va[1]);

					TreeNode familyNode = vendorNode.addUniqueChild("family",
							family);

					// Add package conditions
					TreeNode.Condition condition = packNode.new Condition(
							Condition.DEVICEFAMILY_TYPE);
					condition.setVendor(va[1]);
					condition.setValue(family);
					packNode.addCondition(condition);

					Element processorElement = Utils.getChildElement(
							familyElement, "processor");
					String core = "";
					if (processorElement != null) {
						core = processorElement.getAttribute("Dcore").trim();
					}

					List<Element> subFamilyElements = Utils
							.getChildElementList(familyElement, "subFamily");
					for (Element subFamilyElement : subFamilyElements) {
						String subFamily = subFamilyElement.getAttribute(
								"DsubFamily").trim();

						TreeNode subFamilyNode = familyNode.addUniqueChild(
								"subfamily", subFamily);

						// Process devices below subFamily
						processDevice(subFamilyElement, subFamilyNode, core,
								packNode, va[1]);
					}

					// Process devices below family
					processDevice(familyElement, familyNode, core, packNode,
							va[1]);
				}
			}

			// Boards
			Element boardsElement = Utils.getChildElement(packageElement,
					"boards");
			if (boardsElement != null) {

				TreeNode boardsNode = tree.addUniqueChild("boards", null);

				List<Element> boardElements = Utils.getChildElementList(
						boardsElement, "board");
				for (Element boardElement : boardElements) {
					String vendor = boardElement.getAttribute("vendor").trim();
					String boardName = boardElement.getAttribute("name").trim();
					String revision = boardElement.getAttribute("revision")
							.trim();

					Element descriptionElement = Utils.getChildElement(
							boardElement, "description");
					String description = "";
					if (descriptionElement != null) {
						description = descriptionElement.getTextContent()
								.trim();
					}
					TreeNode vendorNode = boardsNode.addUniqueChild("vendor",
							vendor);

					TreeNode boardNode = vendorNode.addUniqueChild("board",
							boardName);

					if (revision.length() > 0) {
						description += " (" + revision + ")";
					}
					boardNode.setDescription(description);

					// Add package conditions
					TreeNode.Condition condition = packNode.new Condition(
							Condition.BOARD_TYPE);
					condition.setVendor(vendor);
					condition.setValue(boardName);
					packNode.addCondition(condition);
				}
			}

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

	void processDevice(Element parentElement, TreeNode parentNode, String core,
			TreeNode packNode, String vendorid) {

		List<Element> deviceElements = Utils.getChildElementList(parentElement,
				"device");
		for (Element deviceElement : deviceElements) {
			String device = deviceElement.getAttribute("Dname").trim();

			String description = "";
			if (core.length() > 0) {
				description += core;
			}

			Element processorElement = Utils.getChildElement(deviceElement,
					"processor");
			if (processorElement != null) {
				String clock = processorElement.getAttribute("Dclock").trim();
				if (clock.length() > 0) {
					int clockMHz = Integer.valueOf(clock) / 1000000;

					if (description.length() > 0) {
						description += ", ";
					}

					description += String.valueOf(clockMHz) + " MHz";
				}
			}

			List<Element> memoryElements = Utils.getChildElementList(
					deviceElement, "memory");

			int ramKB = 0;
			int romKB = 0;

			for (Element memoryElement : memoryElements) {

				String size = memoryElement.getAttribute("size").trim();
				if (size.length() > 0) {
					int sizeKB = Utils.convertHexInt(size) / 1024;

					String id = memoryElement.getAttribute("id").trim();
					if (id.startsWith("IRAM") || id.startsWith("RAM")) {
						ramKB += sizeKB;
					} else if (id.startsWith("IROM") || id.startsWith("ROM")) {
						romKB += sizeKB;
					}
				}
			}

			if (ramKB > 0) {
				if (description.length() > 0) {
					description += ", ";
				}

				description += String.valueOf(ramKB) + " kB RAM";
			}

			if (romKB > 0) {
				if (description.length() > 0) {
					description += ", ";
				}

				description += String.valueOf(romKB) + " kB ROM";
			}

			TreeNode deviceNode = parentNode.addUniqueChild("device", device);
			if (description.length() > 0) {
				deviceNode.setDescription(description);
			}
		}

	}
}
