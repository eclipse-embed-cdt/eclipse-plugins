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

package ilg.gnuarmeclipse.packs.data;

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
