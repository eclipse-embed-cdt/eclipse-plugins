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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Repos {

	public static final String REPOS_FILE_NAME = ".repos.xml";

	public static final String CMSIS_PACK_TYPE = "CMSIS Pack";
	public static final String XCDL_CMSIS_PACK_TYPE = "XCDL/CMSIS Pack";

	// Used in NewSiteDialog.java.
	public static final String[] TYPES = { CMSIS_PACK_TYPE,
			XCDL_CMSIS_PACK_TYPE };

	private static Repos sfRepos;

	public static synchronized Repos getInstance() {

		if (sfRepos == null) {
			sfRepos = new Repos();
		}

		return sfRepos;
	}

	// ------------------------------------------------------------------------

	private List<Map<String, Object>> fList;

	public Repos() {
		fList = null;
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
		map.put("url", "http://www.keil.com/pack/index.idx");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("type", XCDL_CMSIS_PACK_TYPE);
		map.put("name", "GNU ARM Eclipse");
		map.put("url",
				"http://gnuarmeclipse.sourceforge.net/packages/content.xml");
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
	private List<Map<String, Object>> parseFile() throws IOException,
			ParserConfigurationException, SAXException {

		File file = PacksStorage.getFileObject(REPOS_FILE_NAME);
		if (!file.exists()) {
			throw new UsingDefaultFileException("File " + REPOS_FILE_NAME
					+ " does not exist, using defaults.");
		}

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element el = document.getDocumentElement();
		if (!"repositories".equals(el.getNodeName())) {
			throw new IOException("Missing <repositories>.");
		}

		List<Map<String, Object>> sitesList;
		sitesList = new ArrayList<Map<String, Object>>();

		NodeList siteList = el.getElementsByTagName("repository");
		for (int i = 0; i < siteList.getLength(); ++i) {
			Node siteNode = siteList.item(i);

			String type = "(unknown)";
			String url = "(unknown)";
			String name = "";

			NodeList siteNodeChildren = siteNode.getChildNodes();
			for (int j = 0; j < siteNodeChildren.getLength(); ++j) {
				Node child = siteNodeChildren.item(j);
				short nodeType = child.getNodeType();
				if (nodeType != Node.ELEMENT_NODE) {
					continue;
				}
				String childName = child.getNodeName();
				if ("type".equals(childName)) {
					type = child.getTextContent().trim();
				} else if ("name".equals(childName)) {
					name = child.getTextContent().trim();
				} else if ("url".equals(childName)) {
					url = child.getTextContent().trim();
				}
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("type", type);
			if (name.length() == 0) {
				name = getDomaninNameFromUrl(url);
			}
			map.put("name", name);
			map.put("url", url);
			sitesList.add(map);
		}
		// System.out.println(SITES_FILE_NAME+" parsed");
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

				writer.println("    <type>"
						+ Utils.xmlEscape((String) site.get("type"))
						+ "</type>");
				writer.println("    <name>"
						+ Utils.xmlEscape((String) site.get("name"))
						+ "</name>");
				writer.println("    <url>"
						+ Utils.xmlEscape((String) site.get("url")) + "</url>");
				writer.println("  </repository>");
			}
			writer.println("</repositories>");
			writer.close();
		}
	}

	// Do some magic to obtain a domain name from the url
	public String getDomaninNameFromUrl(String url) {

		String s = url;
		if (s.startsWith("http://")) {
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
		if (s.startsWith("http://")) {
			s = s.substring(7); // skip first part
		}

		// Convert chars that are not allowed in a file name
		s = s.replace('/', '_');
		s = s.replace('?', '_');

		// Cut suffixes
		for (String suffix : new String[] { "content.xml", "index.idx",
				"index.xml", ".xml", ".idx" }) {

			int ix = s.lastIndexOf(suffix);
			if (ix > 0) {
				s = s.substring(0, ix);
			}
		}

		s = s.replace('.', '_');
		return s;
	}

	public String getRepoContentXmlFromUrl(String url) {

		String fileName = PacksStorage.CACHE_FOLDER + "/"
				+ getFileNamePrefixFromUrl(url)
				+ PacksStorage.CONTENT_FILE_NAME_SUFFIX;

		return fileName;
	}
}
