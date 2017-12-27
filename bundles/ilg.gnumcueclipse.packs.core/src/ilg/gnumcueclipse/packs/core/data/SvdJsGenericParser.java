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

package ilg.gnumcueclipse.packs.core.data;

import org.w3c.dom.Element;

/**
 * Specific implementation of the generic parser used to convert the SVD files
 * into a more regular and compact representation.
 */
public class SvdJsGenericParser extends XmlJsGenericParser {

	public SvdJsGenericParser() {
		;
	}

	/**
	 * Define the elements that should generate collections.
	 */
	@Override
	public String isCollection(String name, Element el) {
		if ("peripherals".equals(name)) {
			return "peripheral|name";
		} else if ("registers".equals(name)) {
			return "register|name";
		} else if ("fields".equals(name)) {
			return "field|name";
		}
		return null;
	}
	
	/**
	 * Define the elements that should generate collections.
	 */
	@Override
	public String isMalformedCollectionMember(String name, Element el) {
		if ("interrupt".equals(name)) {
			return "interrupts|name";
		}
		return null;
	}
	
}
