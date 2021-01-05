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
import java.util.Collection;

/**
 * Base class for all nodes in a JS tree. It keeps track of parent nodes. When
 * used as a JsObject property, remember the property name.
 *
 * The parent reference is automatically set when adding object properties or
 * array elements.
 *
 * @author ilg
 */

public abstract class JsNode {

	// The back link to the parent, or null for the root node.
	private JsNode fParent;

	// The property name (map key), or null if not a property value.
	private String fPropertyName;

	protected JsNode() {
		fParent = null;
		fPropertyName = null;
	}

	public JsNode getParent() {
		return fParent;
	}

	/**
	 * Set the node parent; available only in derived classes.
	 *
	 * @param parent
	 *            a reference to an existing node; cannot be null.
	 */
	protected void setParent(JsNode parent) {
		fParent = parent;
	}

	public String getPropertyName() {
		return fPropertyName;
	}

	/**
	 * Set the property name used by the parent to refer to this object. Array
	 * elements do not have this value.
	 *
	 * @param propertyName
	 *            a string.
	 */
	protected void setPropertyName(String propertyName) {
		assert propertyName != null;
		fPropertyName = propertyName;
	}

	/**
	 * Check if the parent object has the desired property name.
	 *
	 * @param propertyName
	 *            a string with the desired key; cannot be null.
	 * @return true if the object has a parent and it has the key.
	 */
	public boolean isChildOf(String propertyName) {
		assert propertyName != null;
		if (fParent == null) {
			return false;
		}
		if (propertyName.equals(fParent.fPropertyName)) {
			return true;
		}
		return false;
	}

	public abstract boolean hasChildren();

	public abstract Collection<JsNode> getChildren();

	public abstract JsNode getFirstChild();

	public JsNode getNextSibling() {
		return fParent.getNextSibling(this);
	}

	protected abstract JsNode getNextSibling(JsNode node);

	public abstract void serialize(OutputStream o) throws IOException;
}
