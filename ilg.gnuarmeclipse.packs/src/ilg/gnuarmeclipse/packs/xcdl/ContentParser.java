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

package ilg.gnuarmeclipse.packs.xcdl;

import java.util.List;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.DocumentParseException;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.PackNode;
import ilg.gnuarmeclipse.packs.tree.Type;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ContentParser {

	private MessageConsoleStream m_out;
	private Document m_document;

	public ContentParser(Document document) {

		m_out = Activator.getConsoleOut();
		m_document = document;
	}

	public Node parseDocument() throws DocumentParseException {

		Node node = new Node(Type.ROOT);

		Element firstElement = m_document.getDocumentElement();
		String firstElementName = firstElement.getNodeName();
		if (!"root".equals(firstElementName)) {
			throw new DocumentParseException("Missing <root>, <"
					+ firstElementName + "> encountered");
		}

		String schemaVersion = firstElement.getAttribute("version").trim();
		if ("1.1".equals(schemaVersion)) {
			;
		} else {
			throw new DocumentParseException("Unrecognised schema version "
					+ schemaVersion);
		}

		List<Element> childElements = Utils.getChildElementsList(firstElement);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("repository".equals(elementName)) {
				processRepository(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}

		return node;
	}

	private String getNameAttribute(Element el) {
		return el.getAttribute("name").trim();
	}

	private void notProcessed(Element el) {

		Element parentElement = Utils.getParentElement(el);

		m_out.print("Element <" + el.getNodeName() + "> ");
		if (parentElement != null) {
			m_out.print(" below <" + parentElement.getNodeName() + "> ");
		}
		m_out.println("not processed.");
	}

	private void processDescription(Element el, Node node) {

		String description = Utils.getElementContent(el);
		node.setDescription(description);
	}

	private void processRepository(Element el, Node parent) {

		Node node = Node.addNewChild(parent, Type.REPOSITORY);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {
				processDescription(childElement, node);
			} else if ("properties".equals(elementName)) {
				processProperties(childElement, node);
			} else if ("packages".equals(elementName)) {
				processPackages(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processProperties(Element el, Node node) {

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("property".equals(elementName)) {
				processProperty(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processProperty(Element el, Node node) {

		String name = getNameAttribute(el);
		String value = Utils.getElementContent(el);

		node.putProperty(name, value);
	}

	private void processPackages(Element el, Node node) {

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("package".equals(elementName)) {
				processPackage(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processPackage(Element el, Node parent) {

		PackNode node = PackNode.addNewChild(parent, Type.PACKAGE);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {
				processDescription(childElement, node);
			} else if ("versions".equals(elementName)) {
				processVersions(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processVersions(Element el, Node node) {

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("version".equals(elementName)) {
				processVersion(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processVersion(Element el, Node parent) {

		PackNode node = PackNode.addNewChild(parent, Type.VERSION);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {
				processDescription(childElement, node);
			} else if ("properties".equals(elementName)) {
				processProperties(childElement, node);
			} else if ("outline".equals(elementName)) {
				processOutline(childElement, node);
			} else if ("external".equals(elementName)) {
				processExternal(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processOutline(Element el, Node parent) {

		Node node = Node.addNewChild(parent, Type.OUTLINE);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("keyword".equals(elementName)) {
				processKeyword(childElement, node);
			} else if ("devicefamily".equals(elementName)) {
				processLeaf(childElement, node, Type.FAMILY);
			} else if ("board".equals(elementName)) {
				processLeaf(childElement, node, Type.BOARD);
			} else if ("component".equals(elementName)) {
				processLeaf(childElement, node, Type.COMPONENT);
			} else if ("bundle".equals(elementName)) {
				processLeaf(childElement, node, Type.BUNDLE);
			} else if ("example".equals(elementName)) {
				processLeaf(childElement, node, Type.EXAMPLE);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processExternal(Element el, Node parent) {

		Node node = Node.addNewChild(parent, Type.EXTERNAL);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("devicefamily".equals(elementName)) {
				processLeaf(childElement, node, Type.FAMILY);
			} else if ("board".equals(elementName)) {
				processLeaf(childElement, node, Type.BOARD);
			} else {
				notProcessed(childElement);
			}
		}
	}

	private void processKeyword(Element el, Node parent) {

		Node node = Node.addNewChild(parent, Type.KEYWORD);

		String name = getNameAttribute(el);

		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			notProcessed(childElement);
		}
	}

	private void processLeaf(Element el, Node parent, String type) {

		Node node;
		node = Node.addNewChild(parent, type);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {
				processDescription(childElement, node);
			} else if ("property".equals(elementName)) {
				processProperty(childElement, node);
			} else {
				notProcessed(childElement);
			}
		}
	}
}
