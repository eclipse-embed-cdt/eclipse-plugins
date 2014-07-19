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

	private String m_type;
	private String m_value;

	// If more are needed, better use a map
	private String m_vendor;
	private String m_vendorId;

	public Selector(String type) {
		m_type = type.trim();
		m_value = "";

		m_vendor = null;
		m_vendorId = null;
	}

	public String getType() {
		return m_type;
	}

	public String getValue() {
		return m_value;
	}

	public void setValue(String value) {
		if (value != null) {
			m_value = value.trim();
		} else {
			m_value = "";
		}
	}

	public boolean hasVendor() {
		return m_vendor != null;
	}

	public String getVendor() {
		return m_vendor;
	}

	public void setVendor(String vendor) {
		if (vendor != null) {
			m_vendor = vendor.trim();
		} else {
			m_vendor = null;
		}
	}

	public boolean hasVendorId() {
		return m_vendorId != null;
	}

	public String getVendorId() {
		return m_vendorId;
	}

	public void setVendorId(String vendorId) {
		if (vendorId != null) {
			m_vendorId = vendorId.trim();
		} else {
			m_vendorId = null;
		}
	}

	public String toString() {
		return m_type + ":" + m_value;
	}

	public boolean equals(Object obj) {

		if (!(obj instanceof Selector))
			return false;

		Selector s = (Selector) obj;

		if (!m_type.equals(s.m_type))
			return false;

		if (!m_value.equals(s.m_value))
			return false;

		if (m_vendor != null && !m_vendor.equals(m_vendor))
			return false;

		if (m_vendorId != null && !m_vendorId.equals(m_vendorId))
			return false;

		return true;
	}

}