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
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.Activator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

public class GenericSerialiser {

	protected class ElementOptions {

		public String fNodeElementName;
		public String fNodesElementName;
		public boolean fDoOutputNodes;
		public boolean fDoOutputName;
		public boolean fIsNameOptional;
		public boolean fDoOutputProperties;
		public boolean fHasNoChildrenElements;
		public boolean doIgnoreChildren;

		public ElementOptions() {

			fNodeElementName = "";
			fNodesElementName = "";
			fDoOutputNodes = false;
			fDoOutputName = true;
			fDoOutputProperties = false;
			fIsNameOptional = true;
			fHasNoChildrenElements = false;
			doIgnoreChildren = false;
		}
	}

	PrintWriter fWriter;
	ElementOptions fDefaultOptions;

	public GenericSerialiser() {

		fWriter = null;
		fDefaultOptions = new ElementOptions();
	}

	// To be overwritten by derived classes
	public String getSchemaVersion() {
		return "1.1";
	}

	// To be overwritten by derived classes
	public ElementOptions getElementOptions(String type) {
		return null; // use default
	}

	// To be overwritten by derived classes
	public Set<String> getPropertyNames() {
		return null;
	}

	public void serialise(Node tree, File file) throws IOException {

		// System.out.println("Serialise to " + file);

		file.getParentFile().mkdir();

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			// writer = new PrintWriter(new BufferedWriter(new
			// FileWriter(file)));
			serialise(tree, new PrintWriter(file, "UTF-8"));
		}
	}

	public void serialise(Node tree, PrintWriter writer) throws IOException {

		if (writer != null) {

			fWriter = writer;

			fWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			fWriter.println();
			fWriter.println("<root version=\"" + getSchemaVersion() + "\">");

			if (tree.isType(Type.ROOT) && tree.hasChildren()) {
				// Skip the initial root node
				for (Leaf child : tree.getChildren()) {
					serialiseRecursive(child, 0);
				}
			} else {
				serialiseRecursive(tree, 0);
			}

			fWriter.println("</root>");
			fWriter.close();
		}
	}

	private void serialiseRecursive(Leaf node, int depth) {

		putIndentation(depth);

		String nodeType = node.getType();

		ElementOptions eo = getElementOptions(nodeType);
		if (eo == null) {
			eo = fDefaultOptions;
		}

		if (eo.fNodeElementName.length() > 0) {
			fWriter.print("<" + eo.fNodeElementName);
		} else {
			fWriter.print("<node type=\"" + nodeType + "\"");
		}
		if (eo.fDoOutputName) {
			String nodeName = node.getName();
			if (nodeName.length() > 0 || !eo.fIsNameOptional) {
				fWriter.print(" name=\"" + node.getName() + "\"");
			}
		}
		if (eo.fHasNoChildrenElements) {
			fWriter.println(" />");
		} else {
			fWriter.println(">");

			String description = node.getDescription();
			if (description != null && description.length() > 0) {
				putIndentation(depth + 1);
				fWriter.println("<description>" + Xml.xmlEscape(node.getDescription()) + "</description>");
			}

			if (node.hasRelevantProperties()) {

				if (eo.fDoOutputProperties) {
					putIndentation(depth + 1);
					fWriter.println("<properties>");
				}

				int newDepth;
				if (eo.fDoOutputProperties) {
					newDepth = depth + 2;
				} else {
					newDepth = depth + 1;
				}

				Map<String, String> properties = node.getProperties();
				for (Object key : properties.keySet()) {
					if (Property.NAME.equals(key)) {
						// Skip special name property
						continue;
					}
					if (Property.DESCRIPTION.equals(key)) {
						// Skip special description property
						continue;
					}
					putIndentation(newDepth);

					String propertyName = key.toString();
					Set<String> propertyNames = getPropertyNames();
					if (propertyNames != null && propertyNames.contains(propertyName)) {
						fWriter.println("<" + propertyName + ">" + Xml.xmlEscape(properties.get(key).toString()) + "</"
								+ propertyName + ">");
					} else {
						fWriter.println("<property name=\"" + propertyName + "\">"
								+ Xml.xmlEscape(properties.get(key).toString()) + "</property>");
					}
				}

				if (eo.fDoOutputProperties) {
					putIndentation(depth + 1);
					fWriter.println("</properties>");
				}
			}

			if (node.hasChildren()) {

				if (eo.doIgnoreChildren) {
					Activator.log("Ignoring children of " + node);
				} else {
					if (eo.fDoOutputNodes) {
						putIndentation(depth + 1);
						if (eo.fNodesElementName.length() > 0) {
							fWriter.println("<" + eo.fNodesElementName + ">");
						} else {
							fWriter.println("<nodes>");
						}
					}

					int newDepth;
					if (eo.fDoOutputNodes) {
						newDepth = depth + 2;
					} else {
						newDepth = depth + 1;
					}

					for (Leaf child : ((Node) node).getChildren()) {
						serialiseRecursive(child, newDepth);
					}

					if (eo.fDoOutputNodes) {
						putIndentation(depth + 1);
						if (eo.fNodesElementName.length() > 0) {
							fWriter.println("</" + eo.fNodesElementName + ">");
						} else {
							fWriter.println("</nodes>");
						}
					}
				}
			}

			putIndentation(depth);
			if (eo.fNodeElementName.length() > 0) {
				fWriter.println("</" + eo.fNodeElementName + ">");
			} else {
				fWriter.println("</node>");
			}
		}
	}

	// Add a consistent, small (2 spaces) indentation
	private void putIndentation(int depth) {

		depth++;
		for (int i = 0; i < depth; ++i) {
			fWriter.print("  ");
		}
	}
}
