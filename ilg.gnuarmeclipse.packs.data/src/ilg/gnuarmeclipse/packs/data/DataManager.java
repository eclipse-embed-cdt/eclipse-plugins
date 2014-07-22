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

import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.tree.Node;

public class DataManager implements IDataManager {

	private static DataManager sfInstance;

	public static synchronized DataManager getInstance() {

		if (sfInstance == null) {
			sfInstance = new DataManager();
		}

		return sfInstance;
	}

	// ------------------------------------------------------------------------

	public DataManager() {
		;
	}

	// Called from TabDevice in managedbuild.cross
	@Override
	public Node getInstalledDevicesForBuild() {

		// Forward call to initial old packs storage
		return PacksStorage.getInstance().getInstalledDevicesForBuild();
	}

	// Will be called from template code
	@Override
	public Node getCmsisCoreFiles(String deviceName, String compiler) {
		// TODO Auto-generated method stub
		return null;
	}

	// Will be called from debugger
	@Override
	public Node getRegisterDetailsForDebug(String deviceName) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Will be called from 
	@Override
	public Node getBooks(String deviceName, String boardName) {

		return null;
	}

}
