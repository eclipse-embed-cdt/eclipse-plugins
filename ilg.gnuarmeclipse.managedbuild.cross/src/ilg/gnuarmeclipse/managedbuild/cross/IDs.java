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

package ilg.gnuarmeclipse.managedbuild.cross;

public class IDs {

	// ------------------------------------------------------------------------

	public static String getIdPrefix() {

		// keep it explicitly defined, since it must not be changed, even if the
		// plug-in id is changed
		return "ilg.gnuarmeclipse.managedbuild.cross";
	}

	public static final String TOOLCHAIN_ID = getIdPrefix() + ".toolchain";

	// ------------------------------------------------------------------------

}
