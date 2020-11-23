/*******************************************************************************
 * Copyright (c) 2009, 2013 Wind River Systems, Inc. and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Doug Schaefer - initial API and implementation
 *     Marc-Andre Laperle - Moved to an operation for a custom wizard page
 *     Liviu Ionescu - Arm version
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.arm.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.cdt.build.core.scannerconfig.ScannerConfigBuilder;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.embedcdt.managedbuild.cross.arm.Activator;
import org.eclipse.embedcdt.managedbuild.cross.arm.ToolchainDefinition;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;

/**
 * An operation that runs when the new project wizard finishes for the Cross GCC
 * toolchain. It reuses the information from {@link SetCrossCommandWizardPage}
 * to store options (index and path) in persistent storage.
 */
public class SetCrossCommandWizardOperation implements IRunnableWithProgress {

	// ------------------------------------------------------------------------

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("arm.SetCrossCommandWizardOperation.run() begin");
		}

		// get local properties
		String projectName = (String) MBSCustomPageManager.getPageProperty(SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_PROJECT_NAME);
		if (projectName == null) {
			// This is to avoid bug 512550
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=512550
			IWizard wizard = (IWizard) MBSCustomPageManager.getPageProperty(SetCrossCommandWizardPage.PAGE_ID,
					SetCrossCommandWizardPage.CROSS_WIZARD);

			projectName = SetCrossCommandWizardPage.getProjectName(wizard);
		}

		assert projectName != null;

		String toolchainName = (String) MBSCustomPageManager.getPageProperty(SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME);
		String path = (String) MBSCustomPageManager.getPageProperty(SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_PATH);

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"arm.SetCrossCommandWizardOperation.run() name=\"" + toolchainName + "\", path=\"" + path + "\")");
		}

		assert toolchainName != null;

		if (!toolchainName.isEmpty() && !path.isEmpty()) {
			// Store persistent values in Eclipse scope
			PersistentPreferences persistentPreferences = Activator.getInstance().getPersistentPreferences();
			persistentPreferences.putToolchainPath(toolchainName, path);
			persistentPreferences.putToolchainName(toolchainName);
			persistentPreferences.flush();
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		if (!project.exists())
			return;

		IManagedBuildInfo buildInfo = ManagedBuildManager.getBuildInfo(project);
		if (buildInfo == null)
			return;

		IConfiguration[] configs = buildInfo.getManagedProject().getConfigurations();
		for (IConfiguration config : configs) {

			try {
				updateOptions(config);
			} catch (BuildException e) {
				Activator.log(e);
			}
		}

		ManagedBuildManager.saveBuildInfo(project, true);

		if (true) {
			for (IConfiguration config : configs) {
				ScannerConfigBuilder.build(config, ScannerConfigBuilder.PERFORM_CORE_UPDATE, new NullProgressMonitor());
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("arm.SetCrossCommandWizardOperation.run() end");
		}
	}

	private void updateOptions(IConfiguration config) throws BuildException {

		String sToolchainName = (String) MBSCustomPageManager.getPageProperty(SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("arm.SetCrossCommandWizardOperation.updateOptions(" + config.getName() + ") name=\""
					+ sToolchainName + "\"");
		}

		int toolchainIndex;
		try {
			toolchainIndex = ToolchainDefinition.findToolchainByName(sToolchainName);
		} catch (IndexOutOfBoundsException e) {
			toolchainIndex = ToolchainDefinition.getDefault();
		}

		TabToolchains.setOptionsForToolchain(config, toolchainIndex);
	}

	// ------------------------------------------------------------------------
}
