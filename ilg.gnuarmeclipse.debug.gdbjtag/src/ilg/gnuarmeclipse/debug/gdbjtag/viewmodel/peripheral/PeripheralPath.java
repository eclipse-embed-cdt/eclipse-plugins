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

package ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral;

public class PeripheralPath {

	// ------------------------------------------------------------------------

	private String[] fSegments = null;

	// ------------------------------------------------------------------------

	public PeripheralPath() {
		fSegments = new String[0];
	}

	public PeripheralPath(PeripheralPath parentPath, PeripheralPath namePath) {
		String[] parentSegments = parentPath.getSegments();
		String[] nameSegments = namePath.getSegments();
		fSegments = new String[parentSegments.length + nameSegments.length];
		System.arraycopy(parentSegments, 0, fSegments, 0, parentSegments.length);
		System.arraycopy(nameSegments, 0, fSegments, parentSegments.length, nameSegments.length);
	}

	public PeripheralPath(String name) {
		fSegments = new String[] { name };
	}

	public PeripheralPath(String parent, String name) {
		fSegments = new String[] { parent, name };
	}

	// ------------------------------------------------------------------------

	private String[] getSegments() {
		return fSegments;
	}

	public String toPath() {
		return toStringBuilder().toString();
	}

	public String toPath(boolean addSlash) {

		StringBuilder sb = toStringBuilder();
		if (addSlash) {
			sb.append('/');
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toPath();
	}

	private StringBuilder toStringBuilder() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fSegments.length; i++)
			if (fSegments[i] != null) {
				if (sb.length() != 0) {
					sb.append('.');
				}
				sb.append(fSegments[i]);
			}
		return sb;
	}

	// ------------------------------------------------------------------------
}
