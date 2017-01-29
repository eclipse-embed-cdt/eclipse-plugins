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
import java.util.Map;
import java.util.Set;

import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DocumentParseException;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The hierarchy is simple, nodes have properties and children nodes. Properties
 * can have custom names: <something>value</something>. Nodes can be grouped,
 * with no other implications, group children are in fact added to the parent
 * group node. (<nodes> is the default group, but, as all groups, it is
 * optional).
 * <p>
 * The only processed attributes are type="..." and name="...". The type is
 * mandatory for <node> otherwise it is ignored for other custom nodes or
 * properties. The name is mandatory for <property> and optional for nodes.
 */
public class GenericParser {

	protected MessageConsoleStream fOut;

	public GenericParser() {

		fOut = ConsoleStream.getConsoleOut();
	}

	// Override this in derived class
	public String[] getGroupsToIgnore() {
		return null;
	}

	// Override this in derived class
	public Leaf addNewChild(Node parent, String type) {

		Leaf node = Node.addNewChild(parent, type);
		return node;
	}

	// Override this in derived class
	public Map<String, String> getReplacements() {
		return null;
	}

	// Override this in derived class
	public Set<String> getPropertyNames() {
		return null;
	}

	// Override this in derived class
	public void checkSchemaVersion(String schemaVersion) throws DocumentParseException {

		if ("1.1".equals(schemaVersion)) {
			;
		} else {
			throw new DocumentParseException("Unrecognised schema version " + schemaVersion);
		}
	}

	public Node parse(Document document) throws DocumentParseException {

		Node node = new Node(Type.ROOT);

		Element firstElement = document.getDocumentElement();
		String firstElementName = firstElement.getNodeName();
		if (!"root".equals(firstElementName)) {
			throw new DocumentParseException("Missing <root>, <" + firstElementName + "> encountered");
		}

		String schemaVersion = firstElement.getAttribute("version").trim();
		checkSchemaVersion(schemaVersion);
		node.putProperty(Property.SCHEMA_VERSION, schemaVersion);

		List<Element> childElements = Xml.getChildrenElementsList(firstElement);
		for (Element childElement : childElements) {

			// String elementName = childElement.getNodeName();
			processChildrenElements(childElement, node);
		}

		return node;
	}

	private void processChildrenElements(Element el, Leaf parent) {

		String elementName = el.getNodeName();
		if ("description".equals(elementName)) {
			processDescriptionElement(el, parent);
		} else if ("properties".equals(elementName)) {
			processPropertiesElement(el, parent);
		} else if ("property".equals(elementName)) {
			processPropertyElement(el, parent);
		} else if ("nodes".equals(elementName) && parent instanceof Node) {
			processNodesElement(el, (Node) parent);
		} else if ("node".equals(elementName) && parent instanceof Node) {
			processGenericNode(el, (Node) parent, getTypeAttribute(el));
		} else if (parent instanceof Node) {

			// The unknown element may be:
			// - a custom group,
			// - a custom property or
			// - a new node

			boolean processed = false;
			String[] groupsToIgnore = getGroupsToIgnore();
			if (groupsToIgnore != null) {
				for (int i = 0; i < groupsToIgnore.length; ++i) {
					if (groupsToIgnore[i].equals(elementName)) {

						// If group, treat it similar to <nodes>, i.e.
						// get children
						processNodesElement(el, (Node) parent);
						processed = true;
						break;
					}
				}
			}

			Set<String> propertyNames = getPropertyNames();
			if (propertyNames != null && propertyNames.contains(elementName)) {

				// If property, add it to the current node
				processPropertyElement(el, parent);
				processed = true;
			}

			if (!processed) {
				// Otherwise create a new node
				processGenericNode(el, (Node) parent, elementName);
			}

		} else {

			// For leaf nodes, the unknown element may be only
			// - a custom property.
			Set<String> propertyNames = getPropertyNames();
			if (propertyNames != null && propertyNames.contains(elementName)) {
				processPropertyElement(el, parent);
			} else {
				// All other nodes go here
				notProcessed(el);
			}
		}
	}

	private void processDescriptionElement(Element el, Leaf node) {

		String description = Xml.getElementContent(el);
		node.setDescription(description);
	}

	private void processPropertiesElement(Element el, Leaf node) {

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("property".equals(elementName)) {
				processPropertyElement(childElement, node);
			} else {
				Set<String> propertyNames = getPropertyNames();
				if (propertyNames != null && propertyNames.contains(elementName)) {
					processPropertyElement(childElement, node);
				} else {
					// All other nodes go here
					notProcessed(childElement);
				}
			}
		}
	}

	private void processPropertyElement(Element el, Leaf node) {

		String name;
		if ("property".equals(el.getNodeName())) {
			name = getNameAttribute(el);
		} else {
			name = el.getNodeName();
		}
		String value = Xml.getElementContent(el);

		node.putProperty(name, value);
	}

	private String getNameAttribute(Element el) {
		return el.getAttribute("name").trim();
	}

	private String getTypeAttribute(Element el) {
		return el.getAttribute("type").trim();
	}

	private void processNodesElement(Element el, Node node) {

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("node".equals(elementName)) {
				processGenericNode(childElement, node, getTypeAttribute(el));
			} else {
				processGenericNode(childElement, node, elementName);
			}
		}
	}

	private void processGenericNode(Element el, Node parent, String type) {

		String actualType = type;
		Map<String, String> replacements = getReplacements();
		if (replacements != null && replacements.containsKey(type)) {
			actualType = replacements.get(type);
		}
		Leaf node = addNewChild(parent, actualType);
		assert node != null;

		String name = getNameAttribute(el);
		if (name.length() > 0) {
			node.setName(name);
		}

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			processChildrenElements(childElement, node);
		}

	}

	private void notProcessed(Element el) {

		Element parentElement = Xml.getParentElement(el);

		fOut.print("Element <" + el.getNodeName() + "> ");
		if (parentElement != null) {
			fOut.print(" below <" + parentElement.getNodeName() + "> ");
		}
		fOut.println("not processed.");
	}

}
