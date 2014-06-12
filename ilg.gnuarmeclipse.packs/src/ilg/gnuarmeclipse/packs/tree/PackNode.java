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

package ilg.gnuarmeclipse.packs.tree;

import java.util.LinkedList;
import java.util.List;

public class PackNode extends Node {

	protected List<Selector> m_selectors;
	protected Node m_outline;

	public PackNode(String type) {

		super(type);

		m_selectors = null;
		m_outline = null;
	}

	public PackNode(Leaf node) {

		super(node);

		m_selectors = null;
		m_outline = null;
	}

	public static PackNode addUniqueChild(Node parent, String type, String name) {

		PackNode packNode = (PackNode) parent.getChild(type, name);
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
		return (m_selectors != null && !m_selectors.isEmpty());
	}

	public List<Selector> getSelectors() {
		return m_selectors;
	}

	public List<Selector> getSelectorsByType(String type) {
		List<Selector> list = new LinkedList<Selector>();
		if (m_selectors != null) {
			for (Selector condition : m_selectors) {
				if (condition.getType().equals(type)) {
					list.add(condition);
				}
			}
		}

		return list;
	}

	public void addSelector(Selector selector) {

		assert (selector != null);

		if (m_selectors == null) {
			m_selectors = new LinkedList<Selector>();
		} else {

			// Check if not already in
			for (Selector sel : m_selectors) {
				if (sel.equals(selector)) {
					return;
				}
			}
		}
		m_selectors.add(selector);
	}

	public List<Selector> copySelectorsRef(PackNode node) {

		m_selectors = node.m_selectors;
		return m_selectors;
	}

	public boolean hasOutline() {
		return (m_outline != null);
	}

	public Node getOutline() {
		return m_outline;
	}

	public void setOutline(Node node) {
		m_outline = node;
	}

}
