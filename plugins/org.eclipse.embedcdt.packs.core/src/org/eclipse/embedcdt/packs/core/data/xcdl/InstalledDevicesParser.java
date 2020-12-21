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

package org.eclipse.embedcdt.packs.core.data.xcdl;

import org.eclipse.embedcdt.packs.core.data.PacksStorage;
import org.eclipse.embedcdt.packs.core.xml.DocumentParseException;
import org.eclipse.embedcdt.packs.core.xml.GenericParser;

public class InstalledDevicesParser extends GenericParser {

	public InstalledDevicesParser() {

		super();

	}
	
	@Override
	public void checkSchemaVersion(String schemaVersion) throws DocumentParseException {

		if (PacksStorage.INSTALLED_DEVICES_XML_VERSION.equals(schemaVersion)) {
			return;
		} else {
			throw new DocumentParseException("Unrecognised schema version " + schemaVersion + ", refresh");
		}
	}

}
