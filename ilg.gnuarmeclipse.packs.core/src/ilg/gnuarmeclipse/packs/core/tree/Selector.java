/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.core.tree;

public class Selector {

	public class Type {
		public static final String DEVICEFAMILY = "devicefamily";
		public static final String BOARD = "board";
		public static final String KEYWORD = "keyword";
	};

	public static final String DEVICEFAMILY_TYPE = "devicefamily";
	public static final String BOARD_TYPE = "board";
	public static final String KEYWORD_TYPE = "keyword";

	public static final String DEPRECATED_TYPE = "deprecated";

	private String fType;
	private String fValue;

	// If more are needed, better use a map
	private String fVendor;
	private String fVendorId;

	public Selector(String type) {
		fType = type.trim();
		fValue = "";

		fVendor = null;
		fVendorId = null;
	}

	public String getType() {
		return fType;
	}

	public String getValue() {
		return fValue;
	}

	public void setValue(String value) {
		if (value != null) {
			fValue = value.trim();
		} else {
			fValue = "";
		}
	}

	public boolean hasVendor() {
		return fVendor != null;
	}

	public String getVendor() {
		return fVendor;
	}

	public void setVendor(String vendor) {
		if (vendor != null) {
			fVendor = vendor.trim();
		} else {
			fVendor = null;
		}
	}

	public boolean hasVendorId() {
		return fVendorId != null;
	}

	public String getVendorId() {
		return fVendorId;
	}

	public void setVendorId(String vendorId) {
		if (vendorId != null) {
			fVendorId = vendorId.trim();
		} else {
			fVendorId = null;
		}
	}

	public String toString() {
		return fType + ":" + fValue;
	}

	public boolean equals(Object obj) {

		if (!(obj instanceof Selector))
			return false;

		Selector s = (Selector) obj;

		if (!fType.equals(s.fType))
			return false;

		if (!fValue.equals(s.fValue))
			return false;

		if (fVendor != null && !fVendor.equals(fVendor))
			return false;

		if (fVendorId != null && !fVendorId.equals(fVendorId))
			return false;

		return true;
	}

}