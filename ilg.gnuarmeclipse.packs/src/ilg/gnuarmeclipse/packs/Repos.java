package ilg.gnuarmeclipse.packs;

import ilg.gnuarmeclipse.packs.ui.preferences.FolderConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Repos {

	public static final String SITES_FILE_NAME = "sites.xml";

	public static final String CMSIS_PACK_TYPE = "CMSIS Pack";
	public static final String XCDL_CMSIS_PACK_TYPE = "XCDL/CMSIS Pack";

	private static Repos ms_repos;

	public static Repos getInstance() {
		if (ms_repos == null) {
			ms_repos = new Repos();
		}

		return ms_repos;
	}

	// ------------------------------------------------------------------------

	private List<Map<String, Object>> m_list;
	private IPath m_folderPath;

	public Repos() {
		m_list = null;
		m_folderPath = null;
	}

	// ------------------------------------------------------------------------

	public File getFileObject(String name) throws IOException {

		IPath path = getFolderPath().append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null.");
		}
		return file; // Cannot return null
	}

	public IPath getFolderPath() throws IOException {

		if (m_folderPath == null) {

			m_folderPath = new Path(getFolderPathString());
		}

		return m_folderPath;
	}

	// Return the absolute full path of the folder used to store packages
	public String getFolderPathString() throws IOException {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String sitesFolderPath = store.getString(FolderConstants.P_FOLDER_PATH)
				.trim();

		// Remove the terminating separator
		if (sitesFolderPath.endsWith(String.valueOf(IPath.SEPARATOR))) {
			sitesFolderPath = sitesFolderPath.substring(0,
					sitesFolderPath.length() - 1);
		}

		if ((sitesFolderPath == null) || (sitesFolderPath.length() == 0)) {
			throw new IOException("Missing folder path.");
		}
		return sitesFolderPath;
	}

	public void updateFolderPath() {

		m_folderPath = null;

		try {
			getFolderPath();
		} catch (IOException e) {
			;
		}
	}

	// ------------------------------------------------------------------------

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

		m_list = list;

		return list;
	}

	public void updateList() {
		m_list = null;

		getList();
	}

	// Return a list of urls where repositories are stored
	public List<Map<String, Object>> getList() {

		if (m_list != null) {
			return m_list;
		}

		List<Map<String, Object>> sites;
		boolean useDefaults = false;
		try {
			sites = parseFile();
			return sites;
		} catch (UsingDefaultFileException e) {
			Activator.log(e.getMessage());
			useDefaults = true;
		} catch (Exception e) {
			Activator.log(e);
		}
		List<Map<String, Object>> list = getDefaultList();
		if (useDefaults) {
			try {
				putList(list);
			} catch (IOException e) {
				;
			}
		}
		return list;
	}

	private List<Map<String, Object>> parseFile() throws IOException,
			ParserConfigurationException, SAXException {

		File file = getFileObject(SITES_FILE_NAME);
		if (!file.exists()) {
			throw new UsingDefaultFileException("File " + SITES_FILE_NAME
					+ " does not exist, using defaults.");
		}

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element el = document.getDocumentElement();
		if (!"sites".equals(el.getNodeName())) {
			throw new IOException("Missing <sites>.");
		}

		List<Map<String, Object>> sitesList;
		sitesList = new ArrayList<Map<String, Object>>();

		NodeList siteList = el.getElementsByTagName("site");
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

	public void putList(List<Map<String, Object>> sitesList) throws IOException {

		File file = getFileObject(SITES_FILE_NAME);

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<sites>");

			for (Map<String, Object> site : sitesList) {
				writer.println("\t<site>");

				writer.println("\t\t<type>"
						+ Utils.xmlEscape((String) site.get("type"))
						+ "</type>");
				writer.println("\t\t<name>"
						+ Utils.xmlEscape((String) site.get("name"))
						+ "</name>");
				writer.println("\t\t<url>"
						+ Utils.xmlEscape((String) site.get("url")) + "</url>");
				writer.println("\t</site>");
			}
			writer.println("</sites>");
			writer.close();
		}
	}

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
	public String getFileNameFromUrl(String url) {

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

}
