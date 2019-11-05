/*******************************************************************************
 * Copyright (c) 2019 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.packs.core.data;

import java.io.IOException;

public class FileNotFoundException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileNotFoundException(String message) {
		super(message);
	}
}
