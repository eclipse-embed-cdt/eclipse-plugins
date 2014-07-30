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

package ilg.gnuarmeclipse.packs.cmsis;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;

/**
 * Specific implementation of the generic parser used to convert the messy PDSC
 * files into a more regular and compact representation.
 */
public class PdscGenericParser extends GenericParser {

	public PdscGenericParser() {
		;
	}

	/**
	 * Configure the elements that generate properties, mainly <description> for
	 * a list of nodes, and a few ones for <package> nodes.
	 */
	@Override
	public boolean isProperty(String name, Leaf node) {

		String type = node.getType();
		if ("description".equals(name)) {
			if ("package".equals(type) || "condition".equals(type)
					|| "example".equals(type) || "component".equals(type)
					|| "bundle".equals(type) || "family".equals(type)
					|| "subFamily".equals(type) || "device".equals(type)
					|| "variant".equals(type) || "board".equals(type)
					|| "api".equals(type)) {
				return true;
			}
		} else if ("name".equals(name) || "vendor".equals(name)
				|| "url".equals(name)) {
			if ("package".equals(type)) {
				return true;
			}
		}
		return false;
	}
}
