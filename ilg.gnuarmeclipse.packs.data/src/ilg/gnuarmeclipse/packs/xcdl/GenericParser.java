package ilg.gnuarmeclipse.packs.xcdl;

import java.util.List;
import java.util.Map;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DocumentParseException;
import ilg.gnuarmeclipse.packs.data.Xml;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenericParser {

	protected MessageConsoleStream fOut;
	protected Document fDocument;

	public GenericParser(Document document) {

		fOut = ConsoleStream.getConsoleOut();
		fDocument = document;
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

	public Map<String, String> getReplacements(){
		return null;
	}
	
	public Node parse() throws DocumentParseException {

		Node node = new Node(Type.ROOT);

		Element firstElement = fDocument.getDocumentElement();
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

		node.putProperty(Property.SCHEMA_VERSION, schemaVersion);
		
		List<Element> childElements = Xml.getChildrenElementsList(firstElement);
		for (Element childElement : childElements) {

			// String elementName = childElement.getNodeName();
			processDefaults(childElement, node);
		}

		return node;
	}

	private void processDefaults(Element el, Leaf parent) {

		String elementName = el.getNodeName();
		if ("description".equals(elementName)) {
			processDescription(el, parent);
		} else if ("properties".equals(elementName)) {
			processProperties(el, parent);
		} else if ("property".equals(elementName)) {
			processProperty(el, parent);
		} else if ("nodes".equals(elementName) && parent instanceof Node) {
			processNodes(el, (Node) parent);
		} else if ("node".equals(elementName) && parent instanceof Node) {
			processGenericNode(el, (Node) parent, getTypeAttribute(el));
		} else if (parent instanceof Node) {
			boolean processed = false;
			String[] groupsToIgnore = getGroupsToIgnore();
			if (groupsToIgnore != null) {
				for (int i = 0; i < groupsToIgnore.length; ++i) {
					if (groupsToIgnore[i].equals(elementName)) {
						processNodes(el, (Node) parent);
						processed = true;
						break;
					}
				}
			}
			if (!processed) {
				// All other nodes go here
				processGenericNode(el, (Node) parent, elementName);
			}
		} else {
			notProcessed(el);
		}
	}

	private void processDescription(Element el, Leaf node) {

		String description = Xml.getElementContent(el);
		node.setDescription(description);
	}

	private void processProperties(Element el, Leaf node) {

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("property".equals(elementName)) {
				processProperty(childElement, node);
			} else {
				// All other nodes go here
				notProcessed(childElement);
			}
		}
	}

	private void processProperty(Element el, Leaf node) {

		String name = getNameAttribute(el);
		String value = Xml.getElementContent(el);

		node.putProperty(name, value);
	}

	private String getNameAttribute(Element el) {
		return el.getAttribute("name").trim();
	}

	private String getTypeAttribute(Element el) {
		return el.getAttribute("type").trim();
	}

	private void processNodes(Element el, Node node) {

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
		if (replacements != null && replacements.containsKey(type)){
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

			processDefaults(childElement, node);
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
