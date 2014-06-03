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

import ilg.gnuarmeclipse.packs.TreeNode;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class AbstractViewContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {

	protected TreeNode m_tree;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		m_tree = null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return ((TreeNode) parentElement).getChildrenArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((TreeNode) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((TreeNode) element).hasChildren();
	}

	@Override
	public abstract Object[] getElements(Object inputElement);

	public void forceRefresh() {
		m_tree = null;
	}

}
