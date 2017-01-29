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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class NodeViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	protected Viewer fViewer;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		fViewer = viewer;
	}

	// @Override
	// public abstract Object[] getElements(Object inputElement);

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Node) {
			Node node = (Node) parentElement;
			if (node.hasChildren()) {
				List<Leaf> children = node.getChildren();
				return children.toArray(new Leaf[children.size()]);
			} else {
				return new Leaf[0];
			}
		} else if (parentElement instanceof Leaf) {
			return new Leaf[0];
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		return ((Leaf) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		if (children != null && children.length > 0) {
			return true;
		} else {
			return false;
		}
	}

}
