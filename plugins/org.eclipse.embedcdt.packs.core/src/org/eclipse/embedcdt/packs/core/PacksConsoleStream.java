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
 * Used to write messages to a message console. A message console may have more
 * than one stream connected to it. Each stream may be displayed in a different
 * color.
 * 
 * @since 3.0
 */
public interface PacksConsoleStream {
	
	/**
	 * Appends the specified message to this stream.
	 *
	 * @param message message to append
	 */
	public void print(String message);


	/**
	 * Appends a line separator string to this stream.
	 */
	public void println();

	/**
	 * Appends the specified message to this stream, followed by a line
	 * separator string.
	 *
	 * @param message message to print
	 */
	public void println(String message);

}
