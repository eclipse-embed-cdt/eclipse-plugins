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

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.core.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.ToolchainDefinition;

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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * An operation that runs when the new project wizard finishes for the Cross GCC
 * toolchain. It reuses the information from {@link SetCrossCommandWizardPage}
 * to store options (index and path) in persistent storage.
 */
public class SetCrossCommandWizardOperation implements IRunnableWithProgress {

	// ------------------------------------------------------------------------

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		System.out.println("SetCrossCommandOperation.run() begin");

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

		assert toolchainName != null;

		if (!toolchainName.isEmpty() && !path.isEmpty()) {
			// Store persistent values in Eclipse scope
			PersistentPreferences.putToolchainPath(toolchainName, path);
			PersistentPreferences.putToolchainName(toolchainName);
			PersistentPreferences.flush();
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

		System.out.println("SetCrossCommandOperation.run() end");
	}

	private void updateOptions(IConfiguration config) throws BuildException {

		String sToolchainName = (String) MBSCustomPageManager.getPageProperty(SetCrossCommandWizardPage.PAGE_ID,
				SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME);

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
