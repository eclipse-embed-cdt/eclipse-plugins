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

package org.eclipse.embedcdt.packs.core.data.xcdl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class XcdlUtils {

	public static BigInteger convertUnits(String str) throws NumberFormatException {
		String arr[] = str.split("[ ]");
		BigInteger value = new BigInteger(arr[0]);
		if (arr.length > 1) {
			if ("MHz".equalsIgnoreCase(arr[1])) {
				value = value.multiply(new BigInteger("1000000"));
			} else if ("kHz".equalsIgnoreCase(arr[1])) {
				value = value.multiply(new BigInteger("1000"));
			} else if ("Hz".equalsIgnoreCase(arr[1])) {
				;
			}
		}
		return value;
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 * @apiNote This method returns a non-API type, {@link JSONObject}, the concrete
	 *          version of this type may change in a future release, however the
	 *          method itself is API. Consumers of this API should ensure their
	 *          {@link JSONObject} is loaded from the same bundle as this bundle.
	 *          See https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/453
	 *          for the current status of this issue.
	 */
	public static JSONObject getPackageJson(IProject project) throws IOException, ParseException {

		assert project != null;
		IPath projectPath = project.getLocation();
		File packageFile = projectPath.append("package.json").toFile();
		if (packageFile.exists() && !packageFile.isDirectory()) {
			JSONParser parser = new JSONParser();
			FileReader reader;
			reader = new FileReader(packageFile);
			JSONObject packageJson = (JSONObject) parser.parse(reader);
			return packageJson;
		}
		throw new FileNotFoundException("package.json not found.");
	}

}
