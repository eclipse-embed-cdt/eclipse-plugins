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

//		ManagedOptionValueHandlerDebug.dump(configuration, holder, option,
//				extraArgument, event);

		if (event == EVENT_APPLY) {
			String val;
			try {
				val = option.getSelectedEnum();
				// System.out.println("SelectedEnum="+val);

				int pos = val.lastIndexOf(".");
				String sToolchainIndex = val.substring(pos + 1);
				System.out.println("ToolchainIndex="+sToolchainIndex);

				ToolchainDefinition td = ToolchainDefinition
						.getToolchain(sToolchainIndex);

				// maybe configuration?
				//IConfiguration cfg = ((FolderInfo) configuration).getParent();
				//IToolChain toolchain = cfg.getToolChain();

				IOption selOption;

				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".family");
				String sFamily = Activator.getOptionPrefix() + ".family."
						+ td.getFamily();

				selOption.setValue(sFamily);

				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.prefix");
				selOption.setValue(td.getPrefix());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.suffix");
				selOption.setValue(td.getSuffix());

				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.c");
				selOption.setValue(td.getCmdC());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.cpp");
				selOption.setValue(td.getCmdCpp());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.ar");
				selOption.setValue(td.getCmdAr());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.objcopy");
				selOption.setValue(td.getCmdObjcopy());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.objdump");
				selOption.setValue(td.getCmdObjdump());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.size");
				selOption.setValue(td.getCmdSize());

				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.make");
				selOption.setValue(td.getCmdMake());
				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".command.rm");
				selOption.setValue(td.getCmdRm());

				String pathKey = SetCrossCommandWizardPage.SHARED_CROSS_COMMAND_PATH
						+ "." + sToolchainIndex;
				String path = SharedDefaults.getInstance().getSharedDefaultsMap().get(pathKey);
				if (path == null)
					path = "";

				selOption = holder.getOptionBySuperClassId(Activator
						.getOptionPrefix() + ".path");
				selOption.setValue(path);

				// Clear discovered includes and macros, to make room for
				// new ones
				SpecsProvider.clear();
				
				// the event was handled
				return true;
				
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Toolchain index out of range");

			} catch (BuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// The event was not handled, thus return false
		return false;

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
		
		// All are appropriate
		return true;
	}

	/**
	 * Extracts a resource info from a build object. If no resource info can be
	 * found, it returns null.
	 * 
	 * @param configuration
	 * @return
	 */
	private IResourceInfo getResourceInfo(IBuildObject configuration) {
		if (configuration instanceof IFolderInfo)
			return (IFolderInfo) configuration;
		if (configuration instanceof IFileInfo)
			return (IFileInfo) configuration;
		if (configuration instanceof IConfiguration)
			return ((IConfiguration) configuration).getRootFolderInfo();
		return null;
	}

}
