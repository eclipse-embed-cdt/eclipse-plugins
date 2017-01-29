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

import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DocumentParseException;

import java.util.List;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ContentParser1 {

	private MessageConsoleStream fOut;
	private Document fDocument;

	public ContentParser1(Document document) {

		fOut = ConsoleStream.getConsoleOut();
		fDocument = document;
	}

	public Node parse() throws DocumentParseException {

		Node node = new Node(Type.ROOT);

		Element firstElement = fDocument.getDocumentElement();
		String firstElementName = firstElement.getNodeName();
		if (!"root".equals(firstElementName)) {
			throw new DocumentParseException("Missing <root>, <" + firstElementName + "> encountered");
		}

		String schemaVersion = firstElement.getAttribute("version").trim();
		if ("1.1".equals(schemaVersion)) {
			;
		} else {
			throw new DocumentParseException("Unrecognised schema version " + schemaVersion);
		}

		List<Element> childElements = Xml.getChildrenElementsList(firstElement);
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

		Element parentElement = Xml.getParentElement(el);

		fOut.print("Element <" + el.getNodeName() + "> ");
		if (parentElement != null) {
			fOut.print(" below <" + parentElement.getNodeName() + "> ");
		}
		fOut.println("not processed.");
	}

	private void processDescription(Element el, Node node) {

		String description = Xml.getElementContent(el);
		node.setDescription(description);
	}

	private void processRepository(Element el, Node parent) {

		Node node = Node.addNewChild(parent, Type.REPOSITORY);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("packages".equals(elementName)) {
				processPackages(childElement, node);
			} else {
				processDefaults(childElement, node);
			}
		}
	}

	private void processProperties(Element el, Node node) {

		List<Element> childElements = Xml.getChildrenElementsList(el);
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
		String value = Xml.getElementContent(el);

		node.putProperty(name, value);
	}

	private void processPackages(Element el, Node node) {

		List<Element> childElements = Xml.getChildrenElementsList(el);
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

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("versions".equals(elementName)) {
				processVersions(childElement, node);
			} else {
				processDefaults(childElement, node);
			}
		}
	}

	private void processVersions(Element el, Node node) {

		List<Element> childElements = Xml.getChildrenElementsList(el);
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

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("outline".equals(elementName)) {
				processOutline(childElement, node);
			} else if ("external".equals(elementName)) {
				processExternal(childElement, node);
			} else {
				processDefaults(childElement, node);
			}
		}
	}

	private void processOutline(Element el, Node parent) {

		Node node = Node.addNewChild(parent, Type.OUTLINE);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Xml.getChildrenElementsList(el);
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

		List<Element> childElements = Xml.getChildrenElementsList(el);
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

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			notProcessed(childElement);
		}
	}

	private void processLeaf(Element el, Node parent, String type) {

		Node node;
		node = Node.addNewChild(parent, type);

		String name = getNameAttribute(el);
		node.setName(name);

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			processDefaults(childElement, node);
		}
	}

	private void processDefaults(Element el, Node parent) {

		String elementName = el.getNodeName();
		if ("description".equals(elementName)) {
			processDescription(el, parent);
		} else if ("properties".equals(elementName)) {
			processProperties(el, parent);
		} else if ("property".equals(elementName)) {
			processProperty(el, parent);
		} else {
			notProcessed(el);
		}
	}
}
