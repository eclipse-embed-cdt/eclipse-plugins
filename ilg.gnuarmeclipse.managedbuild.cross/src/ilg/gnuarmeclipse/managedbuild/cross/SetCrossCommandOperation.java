/*******************************************************************************
 * Copyright (c) 2009, 2013 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Doug Schaefer - initial API and implementation
 *     Marc-Andre Laperle - Moved to an operation for a custom wizard page
 *     Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.build.core.scannerconfig.ScannerConfigBuilder;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * An operation that runs when the new project wizard finishes for the Cross GCC
 * toolchain. It reuses the information from {@link SetCrossCommandWizardPage}
 * to store options (index and path) in persistent storage.
 * 
 */
public class SetCrossCommandOperation implements IRunnableWithProgress {

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		// System.out.println("SetCrossCommandOperation.run() begin");

		// get local properties
		String projectName = (String) MBSCustomPageManager.getPageProperty(
				SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_PROJECT_NAME);

		String toolchainName = (String) MBSCustomPageManager.getPageProperty(
				SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME);
		String path = (String) MBSCustomPageManager.getPageProperty(
				SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_PATH);

		// store them on the permanent storage in
		// workspace/.plugins/org.eclipse.cdt.core/shareddefaults.xml

		SharedStorage.putToolchainPath(toolchainName, path);
		SharedStorage.putToolchainName(toolchainName);

		SharedStorage.update();
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		if (!project.exists())
			return;

		IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
		if (buildInfo == null)
			return;

		IConfiguration[] configs = buildInfo.getManagedProject()
				.getConfigurations();
		for (IConfiguration config : configs) {

			try {
				updateOptions(config);
			} catch (BuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ManagedBuildManager.saveBuildInfo(project, true);

		if (true) {
			for (IConfiguration config : configs) {
				ScannerConfigBuilder.build(config,
						ScannerConfigBuilder.PERFORM_CORE_UPDATE,
						new NullProgressMonitor());
			}
		}

		// System.out.println("SetCrossCommandOperation.run() end");

	}

	private void updateOptions(IConfiguration config) throws BuildException {

		String sToolchainName = (String) MBSCustomPageManager.getPageProperty(
				SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME);

		int toolchainIndex;
		try {
			toolchainIndex = ToolchainDefinition
					.findToolchainByName(sToolchainName);
		} catch (IndexOutOfBoundsException e) {
			toolchainIndex = ToolchainDefinition.getDefault();
		}

		IOption option;
		IToolChain toolchain = config.getToolChain();

		updateOptions(config, toolchainIndex);

		String path = (String) MBSCustomPageManager.getPageProperty(
				SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_PATH);
		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_PATH); //$NON-NLS-1$
		// Do not use config.setOption() to DO NOT save it on .cproject...
		option.setValue(path);

		// ... instead save it to the workspace project storage
		ProjectStorage.putPath(config, path);
	}

	public static void updateOptions(IConfiguration config, int toolchainIndex)
			throws BuildException {

		boolean m_isExecutable;
		boolean m_isStaticLibrary;

		IBuildPropertyValue propertyValue = config.getBuildArtefactType();
		if (Utils.BUILD_ARTEFACT_TYPE_EXE.equals(propertyValue.getId()))
			m_isExecutable = true;
		else
			m_isExecutable = false;

		if (Utils.BUILD_ARTEFACT_TYPE_STATICLIB.equals(propertyValue.getId()))
			m_isStaticLibrary = true;
		else
			m_isStaticLibrary = false;

		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		ToolchainDefinition td = ToolchainDefinition
				.getToolchain(toolchainIndex);

		// Do NOT use ManagedBuildManager.setOption() to avoid sending
		// events to the option. Also do not use option.setValue()
		// since this does not propagate notifications and the
		// values are not saved to .cproject.
		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getName());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); //$NON-NLS-1$
		// compose the architecture ID
		String sArchitecture = td.getArchitecture();
		val = Option.OPTION_ARCHITECTURE + "." + sArchitecture;
		Utils.setOptionForced(config, toolchain, option, val);

		if ("arm".equals(sArchitecture)) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_FAMILY);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_ARM_MCPU_CORTEXM3);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_INSTRUCTIONSET);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_ARM_INSTRUCTIONSET_THUMB);
		} else if ("aarch64".equals(sArchitecture)) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_AARCH64_TARGET_FAMILY);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_AARCH64_MCPU_GENERIC);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_AARCH64_FEATURE_SIMD);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_AARCH64_FEATURE_SIMD_ENABLED);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_AARCH64_CMODEL);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_AARCH64_CMODEL_SMALL);
		}

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getPrefix());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getSuffix());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdC());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdCpp());

		if (m_isStaticLibrary) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdAr());
		}

		if (m_isExecutable) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdObjcopy());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdObjdump());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdSize());
		}

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdMake());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdRm());

		if (m_isExecutable) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); //$NON-NLS-1$
			config.setOption(toolchain, option, true);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); //$NON-NLS-1$
			config.setOption(toolchain, option, true);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); //$NON-NLS-1$
			config.setOption(toolchain, option, true);
		}
	}

}
