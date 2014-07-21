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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Xml {

	public static Element getChildElement(Element el, String name) {

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

	public static List<Element> getChildElementsList(Element el) {

		return getChildElementsList(el, null);
	}

	public static List<Element> getChildElementsList(Element el, String name) {

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

}
