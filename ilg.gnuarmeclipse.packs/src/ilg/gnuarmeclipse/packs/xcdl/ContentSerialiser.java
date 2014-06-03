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

import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.Repos;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class ContentSerialiser {

	private Repos m_repos;
	PrintWriter writer;

	public ContentSerialiser() {

		m_repos = Repos.getInstance();
	}

	public void serialise(TreeNode tree, String fileName) throws IOException {

		File file = m_repos.getFileObject(fileName);

		file.getParentFile().mkdir();

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println();
			writer.println("<root version=\""
					+ PacksStorage.CONTENT_XML_VERSION + "\">");

			serialiseRecursive(tree, 0);

			writer.println("</root>");
			writer.close();

			// System.out.println(SITES_FILE_NAME+" saved");
		}
	}

	private void serialiseRecursive(TreeNode node, int depth) {

		putIndentation(depth);

		String nodeType = node.getType();

		String nodeElementName = "";
		String nodesElementName = "";
		boolean doOutputNodes = true;
		boolean doOutputName = true;
		boolean doOutputProperties = true;
		boolean hasNoChildrenElements = false;
		if (TreeNode.REPOSITORY_TYPE.equals(nodeType)) {
			nodeElementName = "repository";
			nodesElementName = "packages";
		} else if (TreeNode.PACKAGE_TYPE.equals(nodeType)) {
			nodeElementName = "packages";
			nodesElementName = "versions";
		} else if (TreeNode.VERSION_TYPE.equals(nodeType)) {
			nodeElementName = "version";
			doOutputNodes = false;
		} else if (TreeNode.OUTLINE_TYPE.equals(nodeType)) {
			nodeElementName = "outline";
			doOutputNodes = false;
			doOutputName = false;
		} else if (TreeNode.EXTERNAL_TYPE.equals(nodeType)) {
			nodeElementName = "external";
			doOutputNodes = false;
			doOutputName = false;
		} else if (TreeNode.FAMILY_TYPE.equals(nodeType)) {
			nodeElementName = "devicefamily";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.BOARD_TYPE.equals(nodeType)) {
			nodeElementName = "board";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.KEYWORD_TYPE.equals(nodeType)) {
			nodeElementName = "keyword";
			doOutputNodes = false;
			doOutputProperties = false;
			hasNoChildrenElements = true;
		} else if (TreeNode.COMPONENT_TYPE.equals(nodeType)) {
			nodeElementName = "component";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.BUNDLE_TYPE.equals(nodeType)) {
			nodeElementName = "bundle";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.EXAMPLE_TYPE.equals(nodeType)) {
			nodeElementName = "example";
			doOutputNodes = false;
			doOutputProperties = false;
		}

		if (nodeElementName.length() > 0) {
			writer.print("<" + nodeElementName);
		} else {
			writer.print("<node type=\"" + nodeType);
		}
		if (doOutputName) {
			writer.print(" name=\"" + node.getName() + "\"");
		}
		if (hasNoChildrenElements) {
			writer.println(" />");
		} else {
			writer.println(">");

			String description = node.getDescription();
			if (description != null && description.length() > 0) {
				putIndentation(depth + 1);
				writer.println("<description>"
						+ Utils.xmlEscape(node.getDescription())
						+ "</description>");
			}

			if (node.hasProperties()) {

				if (doOutputProperties) {
					putIndentation(depth + 1);
					writer.println("<properties>");
				}

				int newDepth;
				if (doOutputProperties) {
					newDepth = depth + 2;
				} else {
					newDepth = depth + 1;
				}

				Map<String, String> properties = node.getProperties();
				for (Object key : properties.keySet()) {
					putIndentation(newDepth);
					writer.println("<property name=\"" + key.toString()
							+ "\" value=\"" + properties.get(key).toString()
							+ "\" />");
				}

				if (doOutputProperties) {
					putIndentation(depth + 1);
					writer.println("</properties>");
				}
			}

			List<TreeNode> children = node.getChildren();
			if (children != null && !children.isEmpty()) {
				if (doOutputNodes) {
					putIndentation(depth + 1);
					if (nodesElementName.length() > 0) {
						writer.println("<" + nodesElementName + ">");
					} else {
						writer.println("<nodes>");
					}
				}

				int newDepth;
				if (doOutputNodes) {
					newDepth = depth + 2;
				} else {
					newDepth = depth + 1;
				}

				for (TreeNode child : children) {
					serialiseRecursive(child, newDepth);
				}

				if (doOutputNodes) {
					putIndentation(depth + 1);
					if (nodesElementName.length() > 0) {
						writer.println("</" + nodesElementName + ">");
					} else {
						writer.println("</nodes>");
					}
				}
			}

			putIndentation(depth);
			if (nodeElementName.length() > 0) {
				writer.println("</" + nodeElementName + ">");
			} else {
				writer.println("</node>");
			}
		}
	}

	private void putIndentation(int depth) {
		depth++;
		for (int i = 0; i < depth; ++i) {
			writer.print("  ");
		}
	}
}
