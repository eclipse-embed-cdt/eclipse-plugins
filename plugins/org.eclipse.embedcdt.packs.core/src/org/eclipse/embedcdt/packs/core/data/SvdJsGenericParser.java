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

package org.eclipse.embedcdt.packs.core.data;

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
