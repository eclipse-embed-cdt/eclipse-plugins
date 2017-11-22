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

	public String isCollection(String name) {
		if ("device".equals(name)) {
			return "device";
		}
		if ("peripherals".equals(name)) {
			return "peripheral";
		}
		if ("registers".equals(name)) {
			return "register";
		}
		if ("fields".equals(name)) {
			return "field";
		}
		return null;
	}

}
