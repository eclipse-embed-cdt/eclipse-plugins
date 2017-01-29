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

package ilg.gnuarmeclipse.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Add some ease to the usual complicated xml parser interface.
public class Xml {

	public static Document parseFile(File file) throws ParserConfigurationException, SAXException, IOException {

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = xml.parse(inputSource);

		return document;
	}

	// Return the first child element.
	public static Element getFirstChildElement(Element el) {

		return getFirstChildElement(el, null);
	}

	// Return the first child element with the given name.
	public static Element getFirstChildElement(Element el, String name) {

		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals(name)) {

					// Return the first element with the given name
					return (Element) node;
				}
			}
		}
		return null;
	}

	// Return the list of all direct children elements
	public static List<Element> getChildrenElementsList(Element el) {

		return getChildrenElementsList(el, null);
	}

	// Return the list of direct children elements with the given name
	public static List<Element> getChildrenElementsList(Element el, String name) {

		NodeList nodeList = el.getChildNodes();

		// Allocate exactly the number of children
		List<Element> list = new ArrayList<Element>(nodeList.getLength());

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if ((name == null) || node.getNodeName().equals(name)) {
					list.add((Element) node);
				}
			}
		}
		return list;
	}

	// Return a string, even an empty one.
	public static String getElementContent(Element el) {

		String content = "";

		if (el != null && el.getNodeType() == Node.ELEMENT_NODE) {
			content = el.getTextContent();
			if (content != null) {
				content = content.trim();
			}
		}
		return content;
	}

	public static String getElementMultiLineContent(Element el) {

		String s = getElementContent(el);

		String sa[] = s.split("\\r?\\n");
		if (sa.length > 1) {
			s = "";
			for (int i = 0; i < sa.length; ++i) {
				if (i > 0) {
					s += '\n';
				}
				s += sa[i].trim();
			}
		}

		return s;
	}

	public static Element getParentElement(Element el) {

		do {
			el = (Element) el.getParentNode();
		} while (el != null && el.getNodeType() != Node.ELEMENT_NODE);

		return el;
	}

	public static List<String> getAttributesNames(Element el) {

		List<String> list = new LinkedList<String>();

		NamedNodeMap attribs = el.getAttributes();
		for (int i = 0; i < attribs.getLength(); ++i) {
			String name = attribs.item(i).getNodeName();
			list.add(name);
		}

		return list;
	}

	public static List<String> getAttributesNames(Element el, String sa[]) {

		List<String> list = new LinkedList<String>();

		NamedNodeMap attribs = el.getAttributes();
		for (int i = 0; i < attribs.getLength(); ++i) {
			String name = attribs.item(i).getNodeName();
			list.add(name);
		}

		List<String> listOut = new LinkedList<String>();
		for (String s : sa) {
			if (list.contains(s)) {
				list.remove(s);
				listOut.add(s);
			}
		}
		listOut.addAll(list);
		return listOut;
	}

	/**
	 * Escape characters that are not allowed in XML.
	 * 
	 * @param value
	 *            a string to be escaped
	 * @return the escaped string
	 */
	public static String xmlEscape(String value) {

		value = value.replaceAll("\\&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$
		value = value.replaceAll("\\\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
		value = value.replaceAll("\\\'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
		value = value.replaceAll("\\<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$
		value = value.replaceAll("\\>", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$

		return value;
	}

	/**
	 * If the string contains line separators, split the string in lines, trim
	 * each line and then join everything back to a single string.
	 * 
	 * @param str
	 *            a string that might span multiple lines.
	 * @return a string with lines joined, or the original string.
	 */
	public static String joinMultiLine(String str) {

		assert str != null;
		String sa[] = str.split("\\r?\\n");
		if (sa.length == 1) {
			return str; // If no multi line, return original string
		}

		return StringUtils.join(sa, " ");
	}

}
