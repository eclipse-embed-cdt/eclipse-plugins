package ilg.gnuarmeclipse.packs.cmsis;

import ilg.gnuarmeclipse.packs.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Index {

	public static int readIndex(String indexUrl, List<String[]> pdscList)
			throws ParserConfigurationException, SAXException, IOException {

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
			return 0;
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

		return count;

	}

}
