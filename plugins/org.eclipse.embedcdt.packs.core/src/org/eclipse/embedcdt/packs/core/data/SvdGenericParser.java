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

import org.eclipse.embedcdt.packs.core.tree.Leaf;

/**
 * Specific implementation of the generic parser used to convert the SVD files
 * into a more regular and compact representation.
 */
public class SvdGenericParser extends XmlGenericParser {

	public SvdGenericParser() {

	}

	/**
	 * Configure the elements that generate properties.
	 */
	@Override
	public boolean isProperty(String name, Leaf node) {

		return false;
	}
}
