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

package ilg.gnumcueclipse.packs.core.jstree;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class JsNodeViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

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

	/**
	 * @return an array of Objects, or null if there are no children.
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof JsNode) {
			JsNode node = (JsNode) parentElement;
			Collection<JsNode> children = node.getChildren();
			return children.toArray(new JsNode[children.size()]);
		} else {
			return null;
		}
	}

	/**
	 * @return a JsNode or null, if the element is the root node.
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof JsNode) {
			JsNode node = (JsNode) element;
			return node.getParent();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof JsNode) {
			JsNode node = (JsNode) element;
			return node.hasChildren();
		}
		return false;
	}

}
