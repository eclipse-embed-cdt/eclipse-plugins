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

package ilg.gnumcueclipse.packs.core.data;

public class XcdlGenericParser extends JsonGenericParser {

	public XcdlGenericParser() {
		;
	}
	
	/**
	 * Identify properties that are collections.
	 * 
	 * @return the type of the children nodes or null if not collection.
	 */
	public String isCollection(String name) {
		if ("boards".equals(name)) {
			// Return the same name, this means no intermediate node.
			return "board"; 
		}
		if ("families".equals(name)) {
			return "family";
		}
		if ("devices".equals(name)) {
			return "device";
		}
		if ("memoryRegions".equals(name)) {
			return "memoryRegion";
		}
		return null;
	}

}
