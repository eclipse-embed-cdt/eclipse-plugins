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

package ilg.gnuarmeclipse.packs.cmsis;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.data.Repos;

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

	// Parse the Keil index. The original index is available with
	// curl "http://www.keil.com/pack/index.idx"

	// The syntax is erroneous, the root element is missing, so we need
	// to dynamically add it.

	// <?xml version="1.0" encoding="UTF-8" ?>
	// <pdsc url="http://www.keil.com/pack/" name="ARM.CMSIS.pdsc"
	// version="3.20.4"/>
	// <pdsc url="http://media.infineon.com/mdk/"
	// name="Infineon.XMC1000_DFP.pdsc" version="1.5.0"/>
	// ...

	// The new index fixed the broken syntax and is available with
	// $ curl -L www.keil.com/pack/index.pidx

	// <?xml version="1.0" encoding="UTF-8" ?>
	// <index schemaVersion="1.1.0" xs:noNamespaceSchemaLocation="PackIndex.xsd"
	// xmlns:xs="http://www.w3.org/2001/XMLSchema-instance">
	// <vendor>Keil</vendor>
	// <url>http://www.keil.com/pack/</url>
	// <timestamp>2017-04-21T10:00:36.7154574+00:00</timestamp>
	// <pindex>
	// <pdsc url="http://www.keil.com/pack" vendor="ARM"
	// name="CMSIS-Driver_Validation" version="1.1.0" />
	// <pdsc url="http://www.keil.com/pack/" vendor="ARM" name="minar"
	// version="1.0.0" />
	// <pdsc url="http://www.keil.com/pack/" vendor="ARM" name="mbedClient"
	// version="1.1.0" />
	// <pdsc url="http://www.keil.com/pack" vendor="ARM"
	// name="CMSIS-RTOS_Validation" version="1.1.0" />
	// ...
	// <pdsc url="http://gd32mcu.21ic.com/data/documents/yingyongruanjian/"
	// vendor="GigaDevice" name="GD32F1x0_DFP" version="3.0.2" />
	// </pindex>

	// Append string arrays to the given list
	// new String[] { url, name, version }

	public static int readIndex(String indexUrl, List<String[]> pdscList)
			throws ParserConfigurationException, SAXException, IOException {

		URL u = new URL(indexUrl);

		// Read from url to local buffer
		BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
		String line = null;

		// Insert missing root element
		StringBuilder buffer = new StringBuilder();
		if (indexUrl.endsWith("/index.idx")) {
			buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
			buffer.append("<index schemaVersion=\"1.1.0\" xs:noNamespaceSchemaLocation=\"PackIndex.xsd\" "
					+ "xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
			String vendorName = StringUtils.capitalizeFirst(Repos.getDomaninNameFromUrl(indexUrl));
			buffer.append("<vendor>" + vendorName + "</vendor>\n");
			buffer.append("<url>" + indexUrl.substring(0, indexUrl.length() - "index.idx".length()) + "</url>\n");
			// The <timestamp> element... not really.
			buffer.append("<pindex>\n");

			while ((line = in.readLine()) != null) {
				if (line.startsWith("<pdsc")) {
					String arr[] = line.split(" ");
					buffer.append("  <pdsc ");
					for (int i = 1; i < arr.length; ++i) {
						if (arr[i].startsWith("url=\"") || arr[i].startsWith("version=\"")) {
							buffer.append(arr[i]);
							buffer.append(" ");
						} else if (arr[i].startsWith("name=\"") && arr[i].endsWith(".pdsc\"")) {
							String tmp = arr[i].substring(6, arr[i].length() - ".pdsc\n".length());
							String tmpArr[] = tmp.split("[.]", 2);
							buffer.append("vendor=\"");
							buffer.append(tmpArr[0]);
							buffer.append("\" name=\"");
							buffer.append(tmpArr[1]);
							buffer.append("\" ");
						}
					}
					buffer.append("/>\n");
				}
			}
			buffer.append("</pindex>\n");
			buffer.append("</index>\n");
		} else {
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		}
		// Parse from local buffer
		InputSource inputSource = new InputSource(new StringReader(buffer.toString()));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element el = document.getDocumentElement();
		if (!"index".equals(el.getNodeName())) {
			return 0;
		}

		int count = 0;
		Element pindex = (Xml.getChildrenElementsList(el, "pindex")).get(0);
		List<Element> pdscElements = Xml.getChildrenElementsList(pindex, "pdsc");
		for (Element pdscElement : pdscElements) {

			String url = pdscElement.getAttribute("url").trim();
			String vendor = pdscElement.getAttribute("vendor").trim();
			String name = pdscElement.getAttribute("name").trim();
			String version = pdscElement.getAttribute("version").trim();
			String deprecated = pdscElement.getAttribute("deprecated").trim();
			String replacement = pdscElement.getAttribute("replacement").trim();

			pdscList.add(new String[] { url, vendor + "." + name + ".pdsc", version, vendor, name, deprecated,
					replacement });
			++count;
		}

		return count;
	}

}
