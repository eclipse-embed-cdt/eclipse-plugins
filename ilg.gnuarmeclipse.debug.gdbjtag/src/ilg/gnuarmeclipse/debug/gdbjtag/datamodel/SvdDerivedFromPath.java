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

package ilg.gnuarmeclipse.debug.gdbjtag.datamodel;

public class SvdDerivedFromPath {

	// ------------------------------------------------------------------------

	public String peripheralName;
	public String registerName;
	public String fieldName;
	public String enumerationName;

	// ------------------------------------------------------------------------

	public SvdDerivedFromPath() {
		peripheralName = null;
		registerName = null;
		fieldName = null;
		enumerationName = null;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return String.format("[P=%s,R=%s,F=%s,E=%s]", peripheralName, registerName, fieldName, enumerationName);
	}

	// ------------------------------------------------------------------------

	public static SvdDerivedFromPath createPeripheralPath(String str) {

		if (str == null) {
			return null;
		}

		String as[] = str.split(".");

		SvdDerivedFromPath path = new SvdDerivedFromPath();
		if (as.length == 0) {
			path.peripheralName = str;
		} else {
			path.peripheralName = as[0];
		}

		return path;
	}

	public static SvdDerivedFromPath createRegisterPath(String str) {

		if (str == null) {
			return null;
		}

		String as[] = str.split(".");

		SvdDerivedFromPath path = new SvdDerivedFromPath();
		if (as.length == 0) {
			path.registerName = str;
		} else if (as.length == 1) {
			path.registerName = as[0];
		} else {
			path.peripheralName = as[0];
			path.registerName = as[1];
		}

		return path;
	}

	public static SvdDerivedFromPath createFieldPath(String str) {

		if (str == null) {
			return null;
		}

		String as[] = str.split(".");

		SvdDerivedFromPath path = new SvdDerivedFromPath();
		if (as.length == 0) {
			path.fieldName = str;
		} else if (as.length == 1) {
			path.fieldName = as[0];
		} else if (as.length == 1) {
			path.registerName = as[0];
			path.fieldName = as[1];
		} else {
			path.peripheralName = as[0];
			path.registerName = as[1];
			path.fieldName = as[2];
		}

		return path;
	}

	public static SvdDerivedFromPath createEnumerationPath(String str) {

		if (str == null) {
			return null;
		}

		String as[] = str.split(".");

		SvdDerivedFromPath path = new SvdDerivedFromPath();
		if (as.length == 0) {
			path.enumerationName = str;
		} else if (as.length == 1) {
			path.enumerationName = as[0];
		} else if (as.length == 2) {
			path.fieldName = as[0];
			path.enumerationName = as[1];
		} else if (as.length == 3) {
			path.registerName = as[0];
			path.fieldName = as[1];
			path.enumerationName = as[2];
		} else {
			path.peripheralName = as[0];
			path.registerName = as[1];
			path.fieldName = as[2];
			path.enumerationName = as[3];
		}

		return path;
	}

	// ------------------------------------------------------------------------
}
