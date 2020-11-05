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
 *     Liviu Ionescu - initial version 
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.data;

public class XsvdGenericParser extends JsonGenericParser {

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
