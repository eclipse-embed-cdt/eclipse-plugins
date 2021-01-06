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

package org.eclipse.embedcdt.packs.core.data.cmsis;

import org.eclipse.embedcdt.packs.core.data.XmlGenericParser;
import org.eclipse.embedcdt.packs.core.tree.Leaf;

/**
 * Specific implementation of the generic parser used to convert the messy PDSC
 * files into a more regular and compact representation.
 */
public class PdscGenericParser extends XmlGenericParser {

	public PdscGenericParser() {

	}

	/**
	 * Configure all elements that generate properties, mainly <description> for a
	 * list of nodes, and a few ones for <package> nodes, are automatically
	 * identified by the generic parser, no need to define them explicitly.
	 */
	@Override
	public boolean isProperty(String name, Leaf node) {
		return false;
	}
}
