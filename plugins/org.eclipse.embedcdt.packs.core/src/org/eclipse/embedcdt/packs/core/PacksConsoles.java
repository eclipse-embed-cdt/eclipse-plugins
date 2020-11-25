/*******************************************************************************
 * Copyright (c) 2020 ArSysOp and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Alexander Fedorov (ArSysOp) - initial API and implementation.
 *******************************************************************************/
package org.eclipse.embedcdt.packs.core;

/**
 * 
 * Provides access to console streams.
 * 
 * @since 3.0
 */
public interface PacksConsoles {
	
	/**
	 * 
	 * @return an output stream
	 */
	PacksConsoleStream output();
	
}
