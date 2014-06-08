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

package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class AbstractViewContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {

	protected Leaf m_tree;
	protected Viewer m_viewer;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		m_tree = null;
		m_viewer = viewer;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Node) {
			return ((Node) parentElement).getChildrenArray();
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
		if (element instanceof Node) {
			return ((Node) element).hasChildren();
		} else if (element instanceof Leaf) {
			return false;
		} else {
			return false;
		}
	}

	@Override
	public abstract Object[] getElements(Object inputElement);

	public void forceRefresh() {
		m_tree = null;
	}

}
