/*******************************************************************************
 * Copyright (c) 2023 John Dallaway and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    John Dallaway - initial implementation (#608)
 *    Liviu Ionescu - adjust for ECDT
 *******************************************************************************/
package org.eclipse.embedcdt.managedbuild.cross.arm.core;

import org.eclipse.embedcdt.managedbuild.cross.core.LibrariesCommandGenerator;

/**
 * A libraries command generator for GNU C projects.
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @since 8.6
 */
public class LibrariesCommandGeneratorCpp extends LibrariesCommandGenerator {

	public LibrariesCommandGeneratorCpp() {
		super("ilg.gnuarmeclipse.managedbuild.cross.option.cpp.linker.grouplibs"); //$NON-NLS-1$
	}

}
