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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.embedcdt.templates.core.Utils;

/**
 * This class sets the Managed Build System Option Values. Note that this class
 * handles both string options and enumerated options.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ConditionalSetMBSStringOptionValue extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		String projectName = null;
		IProject projectHandle = null;
		String condition = null;
		ProcessArgument[][] resourcePathObjects = null;

		for (ProcessArgument arg : args) {
			String argName = arg.getName();
			if (argName.equals("projectName")) { //$NON-NLS-1$
				projectName = arg.getSimpleValue();
				projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			} else if (argName.equals("resourcePaths")) { //$NON-NLS-1$
				resourcePathObjects = arg.getComplexArrayValue();
			} else if (argName.equals("condition")) { //$NON-NLS-1$
				condition = arg.getSimpleValue();
			}
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceDescription workspaceDesc = workspace.getDescription();
		boolean autoBuilding = workspaceDesc.isAutoBuilding();
		workspaceDesc.setAutoBuilding(false);
		try {
			workspace.setDescription(workspaceDesc);
		} catch (CoreException e) {// ignore
		}

		if (!Utils.isConditionSatisfied(condition)) {
			return;
		}

		if (resourcePathObjects == null) {
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "No resourcePaths")); //$NON-NLS-1$
		}

		boolean modified = false;
		for (ProcessArgument[] resourcePathObject : resourcePathObjects) {
			String id = null;
			String value = null;
			String path = null;
			String buildType = null;

			for (ProcessArgument arg : resourcePathObject) {
				String argName = arg.getName();
				if (argName.equals("id")) {
					id = arg.getSimpleValue();
				} else if (argName.equals("value")) {
					value = arg.getSimpleValue();
				} else if (argName.equals("path")) {
					path = arg.getSimpleValue().trim();
				} else if (argName.equals("buildType")) {
					buildType = arg.getSimpleValue().trim();
				}
			}

			try {
				modified |= setOptionValue(projectHandle, id, value, path, buildType);
			} catch (BuildException e) {
				throw new ProcessFailureException(Messages.getString("SetMBSStringOptionValue.0") + e.getMessage(), e); //$NON-NLS-1$
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

	private boolean setOptionValue(IProject projectHandle, String id, String value, String path, String buildType)
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
						throw new ProcessFailureException(Messages.getString("SetMBSStringOptionValue.3") + path); //$NON-NLS-1$
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

	private boolean setOptionForResourceConfig(String id, String value, IResourceConfiguration resourceConfig,
			IOption[] options, IHoldsOptions optionHolder) throws BuildException {
		boolean modified = false;
		String lowerId = id.toLowerCase();
		for (IOption option : options) {
			if (option.getBaseId().toLowerCase().matches(lowerId)) {
				int optionType = option.getValueType();
				if ((optionType == IOption.STRING) || (optionType == IOption.ENUMERATED)
						|| (optionType == IOption.TREE)) {
					ManagedBuildManager.setOption(resourceConfig, optionHolder, option, value);
					modified = true;
				}
			}
		}
		return modified;
	}

	private boolean setOptionForConfig(String id, String value, IConfiguration config, IOption[] options,
			IHoldsOptions optionHolder) throws BuildException {
		boolean modified = false;
		String lowerId = id.toLowerCase();
		for (IOption option : options) {
			if (option.getBaseId().toLowerCase().matches(lowerId)) {
				int optionType = option.getValueType();
				if ((optionType == IOption.STRING) || (optionType == IOption.ENUMERATED)
						|| (optionType == IOption.TREE)) {
					ManagedBuildManager.setOption(config, optionHolder, option, value);
					modified = true;
				}
			}
		}
		return modified;
	}
}
