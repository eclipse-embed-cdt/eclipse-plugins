/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.jstree;

import java.util.Iterator;
import java.util.NoSuchElementException;

// This is a generic tree iterator, allowing a convenient
// tree traversal in pre-order (root first, then children).
//
// Internally it iterates all nodes, but externally it presents
// only nodes that pass the user isIterable() test.
//
// It is more efficient then building a list and iterating it, and less
// efficient than manually recursing the tree.
//
// One possible optimisation may be a method to define leaf nodes,
// that are not entered for children inspection.

public abstract class AbstractJsTreePreOrderIterator implements IJsTreeIterator {

	private JsNode fTopNode;
	private JsNode fCurrentNode;
	private JsNode fNextNode;

	public AbstractJsTreePreOrderIterator() {
		fTopNode = null;
		fCurrentNode = null;
		fNextNode = null;
	}

	@Override
	public void setTreeNode(JsNode node) {
		fTopNode = node;
		fCurrentNode = null;
		fNextNode = null;
	}

	@Override
	public Iterator<JsNode> iterator() {
		return this;
	}

	public abstract boolean isIterable(JsNode node);

	public abstract boolean isLeaf(JsNode node);

	@Override
	public boolean hasNext() {

		JsNode node = tryNext(fCurrentNode);
		if (node != null) {
			fNextNode = node;
			return true;
		}
		return false;
	}

	@Override
	public JsNode next() {

		JsNode node;
		if (fNextNode != null) {
			node = fNextNode;
		} else {
			node = tryNext(fCurrentNode);
		}

		if (node != null) {
			fCurrentNode = node;
			fNextNode = null;
			return node;
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		// Not used
	}

	// Depth-first, pre-order (root first, then children)
	private JsNode tryNext(JsNode node) {

		do {
			if (node == null) {
				node = fTopNode;
			} else {
				if (!isLeaf(node) && node.hasChildren()) {
					// First children
					node = node.getFirstChild();
				} else {
					for (;;) {
						// Next sibling
						JsNode sibling = node.getNextSibling();
						if (sibling != null) {
							node = sibling;
							break;
						}
						// No sibling found, try parent
						if (node.getParent() != null) {
							// Go up
							node = node.getParent();
						} else {
							// Neither sibling nor parent, no hope
							node = null;
							break;
						}
					}
				}
			}
		} while (node != null && !isIterable(node));

		return node; // null if no more nodes
	}
}
