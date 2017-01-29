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

package ilg.gnuarmeclipse.packs.core.data;

import ilg.gnuarmeclipse.packs.core.Preferences;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;

public class PacksStorage {

	public static final String CACHE_FOLDER = ".cache";

	public static final String CONTENT_FILE_NAME_PREFIX = ".content_";
	public static final String CONTENT_FILE_NAME_SUFFIX = ".xml";
	public static final String CONTENT_XML_VERSION = "1.1";

	public static final String INSTALLED_DEVICES_FILE_NAME = ".installed_devices_boards_books.xml";

	private static IPath fgFolderPath = null;

	// ------------------------------------------------------------------------

	// Return a file object in Packages
	public static File getFileObject(String name) throws IOException {

		IPath path = getFolderPath().append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null.");
		}
		return file; // Cannot return null
	}

	// Return a file object in Packages/.cache
	public static File getCachedFileObject(String name) throws IOException {

		IPath path = getFolderPath().append(CACHE_FOLDER).append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null.");
		}
		return file; // Cannot return null
	}

	public static File getPackageFileObject(String vendor, String packageName, String version, String name)
			throws IOException {

		IPath path = getFolderPath().append(vendor).append(packageName).append(version).append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null.");
		}
		return file; // Cannot return null
	}

	// Return the absolute 'Packages' path.
	public static synchronized IPath getFolderPath() throws IOException {

		if (fgFolderPath == null) {

			fgFolderPath = new Path(getFolderPathString());
		}

		return fgFolderPath;
	}

	// Return a string with the absolute full path of the folder used
	// to store packages
	public static String getFolderPathString() throws IOException {

		IPreferenceStore store = Preferences.getPreferenceStore();
		String folderPath = store.getString(Preferences.PACKS_FOLDER_PATH).trim();

		if (folderPath == null) {
			throw new IOException("Missing folder path.");
		}

		// Remove the terminating separator
		if (folderPath.endsWith(String.valueOf(IPath.SEPARATOR))) {
			folderPath = folderPath.substring(0, folderPath.length() - 1);
		}

		if (folderPath.length() == 0) {
			throw new IOException("Missing folder path.");
		}
		return folderPath;
	}

	public static String makeCachedPdscName(String pdscName, String version) {

		String s;

		s = pdscName;
		int ix = s.lastIndexOf('.');
		if (ix > 0) {
			// Insert .version before extension
			s = s.substring(0, ix) + "." + version + s.substring(ix);
		}
		return s;
	}

	// ------------------------------------------------------------------------
}
