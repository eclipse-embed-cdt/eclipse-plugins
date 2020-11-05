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
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.data;

import java.util.EventObject;

public class DataManagerEvent extends EventObject {

	private static final long serialVersionUID = 7130648407827013912L;

	public class Type {
		public static final String NEW_INPUT = "new.input";
		// public static final String REFRESH_ALL = "refresh.all";
		public static final String UPDATE_PACKS = "update.packs";
		// public static final String UPDATE_DEVICES = "update.devices";
		// public static final String UPDATE_BOARDS = "update.boards";
		public static final String UPDATE_VERSIONS = "update.versions";

	}

	private String fType;
	private Object fPayload;

	public DataManagerEvent(Object source, String type) {

		super(source);

		fType = type;
		fPayload = null;
	}

	public DataManagerEvent(Object source, String type, Object payload) {

		super(source);

		fType = type;
		fPayload = payload;
	}

	public String getType() {
		return fType;
	}

	public Object getPayload() {
		return fPayload;
	}
}
