/*******************************************************************************
 * Copyright (c) 2007, 2010 Symbian Software Limited and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Bala Torati (Symbian) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.embedcdt.templates.core.processes;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.cdt.core.templateengine.process.processes.Messages;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceConfiguration;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.embedcdt.templates.core.Utils;

/**
 * This class Sets (overwrites) contents of Managed Build System StringList
 * Option Values.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class SetMBSStringListOptionValues extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {
		String projectName = args[0].getSimpleValue();
		IProject projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription workspaceDesc = workspace.getDescription();
		boolean autoBuilding = workspaceDesc.isAutoBuilding();
		workspaceDesc.setAutoBuilding(false);
		try {
			workspace.setDescription(workspaceDesc);
		} catch (CoreException e) {// ignore
		}

		ProcessArgument[][] resourcePathObjects = args[1].getComplexArrayValue();
		boolean modified = false;
		for (ProcessArgument[] resourcePathObject : resourcePathObjects) {
			String id = resourcePathObject[0].getSimpleValue();
			String[] values = resourcePathObject[1].getSimpleArrayValue();
			String path = resourcePathObject[2].getSimpleValue();
			String buildType = resourcePathObject[3].getSimpleValue();
			try {
				modified |= setOptionValue(projectHandle, id, values, path, buildType);
			} catch (BuildException e) {
				throw new ProcessFailureException(Messages.getString("SetMBSStringListOptionValues.0") + e.getMessage(), //$NON-NLS-1$
						e);
			}
		}
		if (modified) {
			ManagedBuildManager.saveBuildInfo(projectHandle, true);
		}

		workspaceDesc.setAutoBuilding(autoBuilding);
		try {
			workspace.setDescription(workspaceDesc);
		} catch (CoreException e) {// ignore
		}
	}

	private boolean setOptionValue(IProject projectHandle, String id, String[] value, String path, String buildType)
			throws BuildException, ProcessFailureException {
		IConfiguration[] projectConfigs = ManagedBuildManager.getBuildInfo(projectHandle).getManagedProject()
				.getConfigurations();

		boolean resource = !(path == null || path.equals("") || path.equals("/")); //$NON-NLS-1$ //$NON-NLS-2$
		boolean modified = false;

		for (IConfiguration config : projectConfigs) {
			if (!Utils.isBuildType(config, buildType)) {
				continue;
			}
			IResourceConfiguration resourceConfig = null;
			if (resource) {
				resourceConfig = config.getResourceConfiguration(path);
				if (resourceConfig == null) {
					IFile file = projectHandle.getFile(path);
					if (file == null) {
						throw new ProcessFailureException(Messages.getString("SetMBSStringListOptionValues.3") + path); //$NON-NLS-1$
					}
					resourceConfig = config.createResourceConfiguration(file);
				}
				ITool[] tools = resourceConfig.getTools();
				for (ITool tool : tools) {
					modified |= setOptionForResourceConfig(id, value, resourceConfig, tool.getOptions(), tool);
				}
			} else {
				IToolChain toolChain = config.getToolChain();
				modified |= setOptionForConfig(id, value, config, toolChain.getOptions(), toolChain);

				ITool[] tools = config.getTools();
				for (ITool tool : tools) {
					modified |= setOptionForConfig(id, value, config, tool.getOptions(), tool);
				}
			}
		}

		return modified;
	}

	private boolean setOptionForResourceConfig(String id, String[] value, IResourceConfiguration resourceConfig,
			IOption[] options, IHoldsOptions optionHolder) throws BuildException {
		boolean modified = false;
		String lowerId = id.toLowerCase();
		for (IOption option : options) {
			if (option.getBaseId().toLowerCase().matches(lowerId)) {
				switch (option.getValueType()) {
				case IOption.STRING_LIST:
				case IOption.INCLUDE_PATH:
				case IOption.PREPROCESSOR_SYMBOLS:
				case IOption.LIBRARIES:
				case IOption.OBJECTS:
				case IOption.INCLUDE_FILES:
				case IOption.LIBRARY_PATHS:
				case IOption.LIBRARY_FILES:
				case IOption.MACRO_FILES:
					ManagedBuildManager.setOption(resourceConfig, optionHolder, option, value);
					break;
				default:
					continue;
				}
				modified = true;
			}
		}
		return modified;
	}

	private boolean setOptionForConfig(String id, String[] value, IConfiguration config, IOption[] options,
			IHoldsOptions optionHolder) throws BuildException {
		boolean modified = false;
		String lowerId = id.toLowerCase();
		for (IOption option : options) {
			if (option.getBaseId().toLowerCase().matches(lowerId)) {
				switch (option.getValueType()) {
				case IOption.STRING_LIST:
				case IOption.INCLUDE_PATH:
				case IOption.PREPROCESSOR_SYMBOLS:
				case IOption.LIBRARIES:
				case IOption.OBJECTS:
				case IOption.INCLUDE_FILES:
				case IOption.LIBRARY_PATHS:
				case IOption.LIBRARY_FILES:
				case IOption.MACRO_FILES:
					ManagedBuildManager.setOption(config, optionHolder, option, value);
					break;
				default:
					continue;
				}
				modified = true;
			}
		}
		return modified;
	}
}
