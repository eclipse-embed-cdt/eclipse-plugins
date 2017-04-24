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

package ilg.gnuarmeclipse.packs.data;

import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.ContentParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Repos {

	public static final String REPOS_FILE_NAME = ".repos.xml";

	public static final String CMSIS_PACK_TYPE = "CMSIS Pack";
	public static final String XCDL_CMSIS_PACK_TYPE = "XCDL/CMSIS Pack";
	public static final String UNUSED_PACK_TYPE = "Unused";

	// Used in NewSiteDialog.java.
	public static final String[] TYPES = { CMSIS_PACK_TYPE, XCDL_CMSIS_PACK_TYPE, UNUSED_PACK_TYPE };

	private static final Repos fgInstance;

	static {
		fgInstance = new Repos();
	}

	public static Repos getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	private List<Map<String, Object>> fList;
	private MessageConsoleStream fOut;

	public Repos() {

		fList = null;
		fOut = ConsoleStream.getConsoleOut();
	}

	// ------------------------------------------------------------------------

	// Return the default list of repositories
	public List<Map<String, Object>> getDefaultList() {

		List<Map<String, Object>> list;
		list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map;
		map = new HashMap<String, Object>();
		map.put("type", CMSIS_PACK_TYPE);
		map.put("name", "Keil");
		map.put("url", "http://www.keil.com/pack/index.pidx");
		list.add(map);

		fList = list;

		return list;
	}

	public String[] convertToArray(Map<String, Object> map) {

		String sa[] = new String[map.size()];
		sa[0] = (String) map.get("type");
		sa[1] = (String) map.get("name");
		sa[2] = (String) map.get("url");

		return sa;
	}

	public Map<String, Object> convertToMap(String[] sa) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", sa[0]);
		map.put("name", sa[1]);
		map.put("url", sa[2]);

		return map;
	}

	public void updateList() {

		fList = null;
		getList();
	}

	// Return a list of urls where repositories are stored
	public List<Map<String, Object>> getList() {

		if (fList != null) {
			return fList;
		}

		List<Map<String, Object>> list;
		boolean useDefaults = false;
		try {
			list = parseFile();
			fList = list;
			return list;
		} catch (UsingDefaultFileException e) {
			Activator.log(e.getMessage());
			useDefaults = true;
		} catch (Exception e) {
			Activator.log(e);
		}
		list = getDefaultList();
		if (useDefaults) {
			try {
				putList(list);
			} catch (IOException e) {
				;
			}
		}

		fList = list;
		return list;
	}

	// Parse the file containing the list of repositories
	private List<Map<String, Object>> parseFile() throws IOException, ParserConfigurationException, SAXException {

		File file = PacksStorage.getFileObject(REPOS_FILE_NAME);
		if (!file.exists()) {
			throw new UsingDefaultFileException("File " + REPOS_FILE_NAME + " does not exist, using defaults.");
		}

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element repositoriesElement = document.getDocumentElement();
		if (!"repositories".equals(repositoriesElement.getNodeName())) {
			throw new IOException("Missing <repositories>.");
		}

		List<Map<String, Object>> sitesList;
		sitesList = new ArrayList<Map<String, Object>>();

		List<Element> repositoryElements = Xml.getChildrenElementsList(repositoriesElement, "repository");
		for (Element repositoryElement : repositoryElements) {

			String type = "(unknown)";
			String url = "(unknown)";
			String name = "";

			List<Element> childElements = Xml.getChildrenElementsList(repositoryElement);
			for (Element childElement : childElements) {

				String elementName = childElement.getNodeName();
				String elementContent = Xml.getElementContent(childElement);
				if ("type".equals(elementName)) {
					type = elementContent;
				} else if ("name".equals(elementName)) {
					name = elementContent;
				} else if ("url".equals(elementName)) {
					url = elementContent;
				}
			}

			// Pack all attributes as a map.
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", type);
			if (name.length() == 0) {
				name = getDomaninNameFromUrl(url);
			}
			map.put("name", name);
			map.put("url", url);

			// Add this map to the repos list.
			sitesList.add(map);
		}

		return sitesList;
	}

	// Write the list of repositories to a local file
	public void putList(List<Map<String, Object>> sitesList) throws IOException {

		File file = PacksStorage.getFileObject(REPOS_FILE_NAME);

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			// PrintWriter writer = new PrintWriter(new BufferedWriter(new
			// FileWriter(file)));
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<repositories>");

			for (Map<String, Object> site : sitesList) {
				writer.println("  <repository>");

				writer.println("    <type>" + Xml.xmlEscape((String) site.get("type")) + "</type>");
				writer.println("    <name>" + Xml.xmlEscape((String) site.get("name")) + "</name>");
				writer.println("    <url>" + Xml.xmlEscape((String) site.get("url")) + "</url>");
				writer.println("  </repository>");
			}
			writer.println("</repositories>");
			writer.close();
		}
	}

	// Do some magic to obtain a domain name from the url
	static public String getDomaninNameFromUrl(String url) {

		String s = url;
		if (s.startsWith("http://")) {
			s = s.substring(7); // skip first part
		} else if (s.startsWith("file://")) {
			s = s.substring(7); // skip first part
		}

		// Keep only domain name (cut everything past first slash)
		int ix = s.indexOf('/');
		if (ix > 0) {
			s = s.substring(0, ix);
		}

		// Cut trailing top level domain
		ix = s.lastIndexOf('.');
		if (ix > 0) {
			s = s.substring(0, ix);
		}

		// Cut leading site names
		ix = s.lastIndexOf('.');
		if (ix > 0) {
			s = s.substring(ix + 1);
		}

		return s;
	}

	// Try to get a unique name for the site specific content.xml
	// based on site url
	public String getFileNamePrefixFromUrl(String url) {

		String s = url;
		if (s.startsWith("http://")) { //$NON-NLS-1$
			s = s.substring(7); // skip first part
		} else if (s.startsWith("file:///")) { //$NON-NLS-1$
			s = s.substring(8); // skip first part
		}

		// Convert chars that are not allowed in a file name
		s = s.replace('/', '_');
		s = s.replace('?', '_');
		s = s.replace(':', '_');
		s = s.replace('|', '_');
		// Also convert space
		s = s.replace(' ', '_');

		// Cut suffixes
		for (String suffix : new String[] { "content.xml", "index.idx", "index.xml", ".xml", ".idx" }) {

			int ix = s.lastIndexOf(suffix);
			if (ix > 0) {
				s = s.substring(0, ix);
			}
		}

		s = s.replace('.', '_');
		if (s.endsWith("_")) { //$NON-NLS-1$
			return s.substring(0, s.length() - 1); // Cut trailing '_'
		} else {
			return s;
		}
	}

	public String getRepoContentXmlFromUrl(String url) {

		String fileName = PacksStorage.CACHE_FOLDER + "/" + PacksStorage.CONTENT_FILE_NAME_PREFIX
				+ getFileNamePrefixFromUrl(url) + PacksStorage.CONTENT_FILE_NAME_SUFFIX;

		return fileName;
	}

	public List<PackNode> loadCachedReposContent(Node parent) {

		fOut.println("Loading repos summaries...");

		List<Map<String, Object>> reposList;
		reposList = getList();

		List<PackNode> packsVersionsList = new LinkedList<PackNode>();

		for (Map<String, Object> map : reposList) {
			String type = (String) map.get("type");
			// String name = (String) map.get("name");
			String url = (String) map.get("url");

			if (Repos.CMSIS_PACK_TYPE.equals(type) || Repos.XCDL_CMSIS_PACK_TYPE.equals(type)) {

				String fileName = getRepoContentXmlFromUrl(url);

				try {
					File file = PacksStorage.getFileObject(fileName);
					Node node = parseContentFile(file);

					if (node.hasChildren()) {

						// It must be only one child, the repository node
						assert node.getChildren().size() == 1;

						Leaf repositoryNode = node.getFirstChild();
						// Link the content tree to the repository
						map.put("content", repositoryNode);

						// and to the list of packs versions
						getVersionsRecursive(repositoryNode, packsVersionsList);

						// and move from the ROOT node to the tree of
						// repositories
						if (parent != null) {
							repositoryNode.moveTo(parent);
						}
					}
				} catch (IOException e) {
					fOut.println(e.getMessage());
				} catch (ParserConfigurationException e) {
					fOut.println(e.toString());
				} catch (SAXParseException e) {
					String msg = e.getMessage() + ", file: " + fileName + ", line: " + e.getLineNumber() + ", column: "
							+ e.getColumnNumber();
					fOut.println("Error: " + msg);
				} catch (SAXException e) {
					fOut.println(e.toString());
				}
			}
		}
		return packsVersionsList;
	}

	private void getVersionsRecursive(Leaf node, List<PackNode> list) {

		if (node.isType(Type.VERSION)) {
			list.add((PackNode) node);
			// Stop recursion here
		} else if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				getVersionsRecursive(child, list);
			}
		}
	}

	private Node parseContentFile(File file) throws IOException, ParserConfigurationException, SAXException {

		long beginTime = System.currentTimeMillis();

		fOut.println("Parsing cached content file \"" + file.getPath() + "\"...");

		if (!file.exists()) {
			throw new IOException("File does not exist, ignored.");
		}

		Document document = Xml.parseFile(file);
		ContentParser parser = new ContentParser();
		Node node = parser.parse(document);

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		fOut.println("File parsed in " + duration + "ms.");

		return node;
	}

	// ------------------------------------------------------------------------
}
