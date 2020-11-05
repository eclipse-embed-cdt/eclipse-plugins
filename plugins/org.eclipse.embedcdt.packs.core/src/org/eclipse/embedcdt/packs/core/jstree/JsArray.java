/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.jstree;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * A class to manage an array of objects. Elements can be only strings or
 * JsNodes.
 * 
 * DO NOT add or remove elements via direct access via getElements(), to avoid
 * dangling references to parent objects.
 * 
 * @author ilg
 */

public class JsArray extends JsNode {

	private ArrayList<Object> fElements;

	// Redundant list, to keep track of children (elements with object values)
	private ArrayList<JsNode> fChildren;

	public JsArray() {
		// Linked list, to preserve the order.
		fElements = new ArrayList<Object>();

		fChildren = new ArrayList<JsNode>();
	}

	public JsArray(int initialSize) {
		// Linked list, to preserve the order.
		fElements = new ArrayList<Object>(initialSize);

		fChildren = new ArrayList<JsNode>();
	}

	public ArrayList<Object> getElements() {
		return fElements;
	}

	@Override
	public boolean hasChildren() {
		return !fChildren.isEmpty();
	}

	@Override
	public Collection<JsNode> getChildren() {
		return fChildren;
	}

	@Override
	public JsNode getFirstChild() {
		assert !fChildren.isEmpty();
		return fChildren.get(0);
	}

	@Override
	protected JsNode getNextSibling(JsNode node) {
		int index = fChildren.indexOf(node);
		assert index >= 0;
		index++;

		if (index >= fChildren.size()) {
			// No more siblings.
			return null;
		}
		return fChildren.get(index);
	}

	public int size() {
		return fElements.size();
	}

	public boolean isEmpty() {
		return fElements.isEmpty();
	}

	public boolean add(String element) {
		return fElements.add(element);
	}

	public boolean add(JsNode element) {
		element.setParent(this);

		fChildren.add(element);
		return fElements.add(element);
	}

	public boolean add(Object element) {
		if (element instanceof String) {
			return add((String) element);
		} else if (element instanceof JsNode) {
			return add((JsNode) element);
		} else {
			assert false : "Unsupported element type " + element.getClass().getName();
		}
		return false;
	}

	public void add(int index, String element) {
		fElements.add(index, element);
	}

	public void add(int index, JsNode element) {
		element.setParent(this);
		fChildren.add(element);
		fElements.add(index, element);
	}

	public Object get(int index) {
		return fElements.get(index);
	}

	public Object remove(int index) {
		Object element = fElements.remove(index);
		if ((element != null) && (element instanceof JsNode)) {
			// Unlink from parent.
			((JsNode) element).setParent(null);

			fChildren.remove(element);
		}

		return element;
	}

	public Object remove(Object o) {
		Object element = fElements.remove(o);
		if ((element != null) && (element instanceof JsNode)) {
			// Unlink from parent.
			((JsNode) element).setParent(null);

			fChildren.remove(element);
		}
		return element;
	}

	public void clear() {
		for (int i = 0; i < fElements.size(); ++i) {
			remove(i);
		}
	}

	public String toString() {
		String str = "[ ";
		boolean first = true;
		String pName = getPropertyName();
		if (pName != null) {
			str += "\"$this\":\"" + pName + "\"";
			first = false;
		}
		for (Object element : fElements) {
			if (first) {
				first = false;
			} else {
				str += ", ";
			}
			if (element instanceof String) {
				str += "\"" + element + "\"";
			} else if (element instanceof JsNode) {
				str += element.toString();
			} else {
				// Normally should not happen.
				str += "<" + element.getClass().getSimpleName() + ">";
			}
		}
		str += " ]";
		return str;
	}

	@Override
	public void serialize(OutputStream o) throws IOException {
		String str = "[";
		boolean first = true;
		for (Object element : fElements) {
			if (first) {
				first = false;
			} else {
				str += ",";
			}
			if (element instanceof String) {
				str += "\"" + element + "\"";
				o.write(str.getBytes());
				str = "";
			} else if (element instanceof JsNode) {
				o.write(str.getBytes());
				str = "";
				((JsNode) element).serialize(o);
			}
		}
		str += " ]";
		o.write(str.getBytes());
	}

}
