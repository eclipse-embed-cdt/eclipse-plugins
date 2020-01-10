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
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross.riscv;

public class IDs {

	// ------------------------------------------------------------------------

	public static String getIdPrefix() {

		// keep it explicitly defined, since it must not be changed, even if the
		// plug-in id is changed
		return "ilg.gnumcueclipse.managedbuild.cross.riscv";
	}

	public static final String TOOLCHAIN_ID = getIdPrefix() + ".toolchain";

	// ------------------------------------------------------------------------

}
