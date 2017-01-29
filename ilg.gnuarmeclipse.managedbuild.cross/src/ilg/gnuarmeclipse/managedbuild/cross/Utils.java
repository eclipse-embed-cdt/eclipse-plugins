/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import ilg.gnuarmeclipse.core.EclipseUtils;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFileInfo;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.IToolChain;

public class Utils {

	// ------------------------------------------------------------------------

	public static String BUILD_ARTEFACT_TYPE = "org.eclipse.cdt.build.core.buildArtefactType";
	public static String BUILD_ARTEFACT_TYPE_EXE = BUILD_ARTEFACT_TYPE + ".exe";
	public static String BUILD_ARTEFACT_TYPE_STATICLIB = BUILD_ARTEFACT_TYPE + ".staticLib";

	/**
	 * Extracts a resource info from a build object. If no resource info can be
	 * found, it returns null.
	 * 
	 * @param configuration
	 * @return
	 */
	public static IResourceInfo getResourceInfo(IBuildObject configuration) {
		if (configuration instanceof IFolderInfo)
			return (IFolderInfo) configuration;
		if (configuration instanceof IFileInfo)
			return (IFileInfo) configuration;
		if (configuration instanceof IConfiguration)
			return ((IConfiguration) configuration).getRootFolderInfo();
		return null;
	}

	public static IConfiguration getConfiguration(IBuildObject configuration) {
		if (configuration instanceof IFolderInfo)
			return ((IFolderInfo) configuration).getParent();
		if (configuration instanceof IFileInfo)
			return ((IFileInfo) configuration).getParent();
		if (configuration instanceof IConfiguration)
			return (IConfiguration) configuration;
		return null;
	}

	public static IConfiguration getConfiguration(IHoldsOptions holder) {
		if (holder instanceof IToolChain)
			return ((IToolChain) holder).getParent();
		return null;
	}

	public static IOption setOptionForced(IConfiguration config, IToolChain toolchain, IOption option, String value)
			throws BuildException {

		// System.out.println("setOptionForced(" + config.getName() + ", "
		// + toolchain.getName() + ", " + option.getName() + ", " + value
		// + ") was " + option.getStringValue());
		// setOption() does nothing if the new value is identical to the
		// previous one. this is generally ok, except the initial settings
		// when we do not want to depend on defaults, so we do this in
		// two steps, we first set an impossible value, than the actual
		// one
		IOption newOption = config.setOption(toolchain, option, "?!");
		return config.setOption(toolchain, newOption, value);
	}

	public static IOption forceOptionRewrite(IConfiguration config, IToolChain toolchain, IOption option)
			throws BuildException {

		String value = option.getStringValue();
		// System.out.println("setOptionForced(" + config.getName() + ", "
		// + toolchain.getName() + ", " + option.getName() + ") was "
		// + option.getStringValue());
		// setOption() does nothing if the new value is identical to the
		// previous one. this is generally ok, except the initial settings
		// when we do not want to depend on defaults, so we do this in
		// two steps, we first set an impossible value, than the actual
		// one
		IOption newOption = config.setOption(toolchain, option, "?!");
		return config.setOption(toolchain, newOption, value);
	}

	static public String escapeWhitespaces(String path) {
		path = path.trim();
		// Escape the spaces in the path/filename if it has any
		String[] segments = path.split("\\s"); //$NON-NLS-1$
		if (segments.length > 1) {
			if (EclipseUtils.isWindows()) {
				if (path.startsWith("\"") || path.startsWith("'"))
					return path;

				return "\"" + path + "\"";
			} else {
				StringBuffer escapedPath = new StringBuffer();
				for (int index = 0; index < segments.length; ++index) {
					escapedPath.append(segments[index]);
					if (index + 1 < segments.length) {
						escapedPath.append("\\ "); //$NON-NLS-1$
					}
				}
				return escapedPath.toString().trim();
			}
		} else {
			return path;
		}
	}

	static public String quoteWhitespaces(String path) {
		path = path.trim();
		// Escape the spaces in the path/filename if it has any
		String[] segments = path.split("\\s"); //$NON-NLS-1$
		if (segments.length > 1) {
			if (path.startsWith("\"") || path.startsWith("'"))
				return path;

			return "\"" + path + "\"";
		} else {
			return path;
		}
	}

	// ------------------------------------------------------------------------
}
