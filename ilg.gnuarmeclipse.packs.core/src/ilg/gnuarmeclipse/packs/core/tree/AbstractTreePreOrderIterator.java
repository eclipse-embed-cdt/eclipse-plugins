package ilg.gnuarmeclipse.packs.core.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractTreePreOrderIterator implements Iterator<Leaf>,
		Iterable<Leaf> {

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

	protected abstract boolean isIterable(Leaf node);

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
				if (node.hasChildren()) {
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
