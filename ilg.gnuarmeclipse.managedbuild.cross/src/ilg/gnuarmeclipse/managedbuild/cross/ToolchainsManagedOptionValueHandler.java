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

import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsManager;
import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFileInfo;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceConfiguration;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.BooleanExpressionApplicabilityCalculator;
import org.eclipse.cdt.managedbuilder.internal.core.FolderInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ResourceConfiguration;
import org.eclipse.cdt.managedbuilder.language.settings.providers.AbstractBuiltinSpecsDetector;

public class ToolchainsManagedOptionValueHandler implements
		IManagedOptionValueHandler {
	
	@Override
	public boolean handleValue(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument,
			int event) {

		ManagedOptionValueHandlerDebug.dump(configuration, holder, option,
				extraArgument, event);

		if (event == EVENT_APPLY) {
			try {

				updateOptions(holder, option);
				
				// Clear discovered includes and macros, to make room for
				// new ones
				SpecsProvider.clear();
				
				// the event was handled
				return true;
				
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Toolchain index out of range");
			} catch (NumberFormatException e) {
				System.out.println("Toolchain index not a number");

			} catch (BuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// The event was not handled, thus return false
		return false;

	}

	private void updateOptions(IHoldsOptions holder, IOption option) throws BuildException
	{
		String val;
		val = option.getSelectedEnum();
		// System.out.println("SelectedEnum="+val);

		int pos = val.lastIndexOf(".");
		String sToolchainIndex = val.substring(pos + 1);
		
		System.out.println("ToolchainIndex="+sToolchainIndex);

		IConfiguration config = Utils.getConfiguration(holder);
		Utils.updateOptions(config, sToolchainIndex);

		IOption selOption;

		// get the path from the shared storage
		String pathKey = SetCrossCommandWizardPage.SHARED_CROSS_COMMAND_PATH
				+ "." + sToolchainIndex;
		String path = SharedDefaults.getInstance().getSharedDefaultsMap().get(pathKey);
		if (path == null)
			path = "";

		selOption = holder.getOptionBySuperClassId(Activator
				.getOptionPrefix() + ".path");
		// Do not use config.setOption() to DO NOT save it on .cproject...
		selOption.setValue(path);
		
		// save path to the project workspace storage
		PathManagedOptionValueHandler.putPersistent(config, path);

	}
	@Override
	public boolean isDefaultValue(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument) {
		
		// No explicit default
		return false;
	}

	@Override
	public boolean isEnumValueAppropriate(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument,
			String enumValue) {
		
		if ("none".equals(enumValue))
			return false;
		
		// All other are appropriate
		return true;
	}

}
