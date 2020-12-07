/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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
 *     		(based on Ningareddy Modase contribution)
 *******************************************************************************/

package org.eclipse.embedcdt.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.eclipse.embedcdt.internal.core.Activator;

/**
 * A thread collecting lines containing a key from a reader into a string.
 */
public class AltWindowsRegistryStreamReader extends Thread {

	// ------------------------------------------------------------------------

	private BufferedReader fReader;
	private StringWriter fWriter = new StringWriter();
	private String fKey;

	// ------------------------------------------------------------------------

	public AltWindowsRegistryStreamReader(InputStream is, String key) {
		fReader = new BufferedReader(new InputStreamReader(is));
		fKey = key;
	}

	// ------------------------------------------------------------------------

	public void run() {
		try {
			String line = fReader.readLine();
			while (line != null) {
				line = fReader.readLine();
				if (line != null && line.contains(fKey)) {
					fWriter.append(line.trim());
				}
			}
			fReader.close();
		} catch (IOException e) {
			Activator.log(e);
		}
	}

	public String getResult() {
		return fWriter.toString();
	}

	// ------------------------------------------------------------------------
}
