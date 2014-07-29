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

public abstract class AbstractTreePreOrderIterator implements ITreeIterator {

	private Leaf fTopNode;
	private Leaf fCurrentNode;
	private Leaf fNextNode;

	public AbstractTreePreOrderIterator() {
		fTopNode = null;
		fCurrentNode = null;
		fNextNode = null;
	}

	public void setTreeNode(Leaf node) {
		fTopNode = node;
		fCurrentNode = null;
		fNextNode = null;
	}

	public Iterator<Leaf> iterator() {
		return this;
	}

	public abstract boolean isIterable(Leaf node);

	public abstract boolean isLeaf(Leaf node);

	@Override
	public boolean hasNext() {

		Leaf node = tryNext(fCurrentNode);
		if (node != null) {
			fNextNode = node;
			return true;
		}
		return false;
	}

	@Override
	public Leaf next() {

		Leaf node;
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
		; // Not used
	}

	// Depth-first, pre-order (root first, then children)
	private Leaf tryNext(Leaf node) {

		do {
			if (node == null) {
				node = fTopNode;
			} else {
				if (!isLeaf(node) && node.hasChildren()) {
					// First children
					node = ((Node) node).getFirstChild();
				} else {
					for (;;) {
						// Next sibling
						Leaf sibling = node.getNextSibling();
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
