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
import org.eclipse.embedcdt.packs.core.xml.GenericSerialiser;

/**
 * @since 3.1
 */
public class InstalledDevicesSerialiser extends GenericSerialiser {

	public InstalledDevicesSerialiser() {

		super();
	}

	@Override
	public String getSchemaVersion() {
		return PacksStorage.INSTALLED_DEVICES_XML_VERSION;
	}

}
