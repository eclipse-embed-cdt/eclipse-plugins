package ilg.gnuarmeclipse.packs.core.tree;

public class TreePreOrderIterator extends AbstractTreePreOrderIterator {

	public TreePreOrderIterator() {
		super();
	}

	@Override
	protected boolean isIterable(Leaf node) {
		return true;
	}
}
