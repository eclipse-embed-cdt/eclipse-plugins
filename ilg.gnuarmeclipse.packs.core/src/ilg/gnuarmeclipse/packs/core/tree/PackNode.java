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

package ilg.gnuarmeclipse.packs.core.tree;

import java.util.LinkedList;
import java.util.List;

public class PackNode extends Node {

	protected List<Selector> fSelectors;
	protected Node fOutline;

	public PackNode(String type) {

		super(type);

		fSelectors = null;
		fOutline = null;
	}

	public PackNode(Leaf node) {

		super(node);

		fSelectors = null;
		fOutline = null;
	}

	public static PackNode addUniqueChild(Node parent, String type, String name) {

		PackNode packNode = (PackNode) parent.findChild(type, name);
		if (packNode == null) {

			packNode = new PackNode(type);
			parent.addChild(packNode);

			packNode.setName(name);
		}

		return packNode;
	}

	public static PackNode addNewChild(Node parent, String type) {

		PackNode node = new PackNode(type);
		parent.addChild(node);

		return node;
	}

	public static PackNode addNewChild(Node parent, Leaf from) {

		assert (parent != null);

		PackNode node = new PackNode(from);
		parent.addChild(node);
		return node;
	}

	public boolean hasSelectors() {
		return (fSelectors != null && !fSelectors.isEmpty());
	}

	public List<Selector> getSelectors() {
		return fSelectors;
	}

	public List<Selector> getSelectorsByType(String type) {
		List<Selector> list = new LinkedList<Selector>();
		if (fSelectors != null) {
			for (Selector condition : fSelectors) {
				if (condition.getType().equals(type)) {
					list.add(condition);
				}
			}
		}

		return list;
	}

	public void addSelector(Selector selector) {

		assert (selector != null);

		if (fSelectors == null) {
			fSelectors = new LinkedList<Selector>();
		} else {

			// Check if not already in
			for (Selector sel : fSelectors) {
				if (sel.equals(selector)) {
					return;
				}
			}
		}
		fSelectors.add(selector);
	}

	public List<Selector> copySelectorsRef(PackNode node) {

		fSelectors = node.fSelectors;
		return fSelectors;
	}

	public boolean hasOutline() {
		return (fOutline != null);
	}

	public Node getOutline() {
		return fOutline;
	}

	public void setOutline(Node node) {
		fOutline = node;
	}

}
