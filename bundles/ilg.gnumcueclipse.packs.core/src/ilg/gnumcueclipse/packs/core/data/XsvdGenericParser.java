/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *******************************************************************************/

package ilg.gnumcueclipse.packs.core.data;

public class XsvdGenericParser extends JsonSimpleParser {

	public XsvdGenericParser() {
		;
	}

	/**
	 * Identify properties that are collections.
	 * 
	 * @return the type of the children nodes or null if not collection.
	 */
	public String isCollection(String name) {
		if ("device".equals(name)) {
			// Return the same name, this means no intermediate node.
			return "device"; 
		}
		if ("peripherals".equals(name)) {
			return "peripheral";
		}
		if ("clusters".equals(name)) {
			return "cluster";
		}
		if ("registers".equals(name)) {
			return "register";
		}
		if ("fields".equals(name)) {
			return "field";
		}
		if ("enumerations".equals(name)) {
			return "enumeration";
		}
		if ("values".equals(name)) {
			return "value";
		}
		return null;
	}

}
