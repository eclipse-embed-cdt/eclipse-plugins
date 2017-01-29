/*******************************************************************************
 * Copyright (c) 2007, 2010 Symbian Software Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Bala Torati (Symbian) - Initial API and implementation
 *******************************************************************************/
package ilg.gnuarmeclipse.templates.core.processes;

import java.io.File;

import ilg.gnuarmeclipse.templates.core.Utils;

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
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class Appends contents to Managed Build System String Option Value.
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ConditionalAppendToMBSStringOptionValue extends ProcessRunner {

	/**
	 * This method Appends contents to Managed Build System StringList Option
	 * Values.
	 */
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
		} catch (CoreException e) {
			// ignore
		}

		String condition = args[1].getSimpleValue();
		if (!Utils.isConditionSatisfied(condition))
			return;

		ProcessArgument[][] resourcePathObjects = args[2].getComplexArrayValue();
		boolean modified = false;
		for (ProcessArgument[] resourcePathObject : resourcePathObjects) {
			String id = resourcePathObject[0].getSimpleValue();
			String value = resourcePathObject[1].getSimpleValue();
			String path = resourcePathObject[2].getSimpleValue();
			String buildType = resourcePathObject[3].getSimpleValue();
			try {
				modified |= setOptionValue(projectHandle, id, value, path, buildType);
			} catch (BuildException e) {
				throw new ProcessFailureException(Messages.getString("AppendToMBSStringOptionValue.0") + e.getMessage(), //$NON-NLS-1$
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
			IResourceInfo resourceInfo = null;
			IResourceConfiguration resourceConfig = null;
			if (resource) {
				resourceConfig = config.getResourceConfiguration(path);
				if (resourceConfig == null) {
					IFile file = projectHandle.getFile(path);
					if (file == null) {
						throw new ProcessFailureException(Messages.getString("AppendToMBSStringOptionValue.3") + path); //$NON-NLS-1$
					}
					IPath absolutePath = file.getLocation();
					File f = absolutePath.toFile();

					if (f.isFile()) {
						resourceConfig = config.createResourceConfiguration(file);

						ITool[] tools = resourceConfig.getTools();
						for (ITool tool : tools) {
							modified |= setOptionForResourceConfig(id, value, resourceConfig, tool.getOptions(), tool);
						}
					} else if (f.isDirectory()) {

						// This required a bit of tweaking, to make the folder
						// resource info similar to those created by hand
						// in the settings page. The only difference is the
						// folder id, which is not numeric, but a string,
						// like for file resources created before.
						String fid = ManagedBuildManager.calculateChildId(config.getId(), path);
						resourceInfo = config.createFolderInfo(file.getProjectRelativePath(), fid, "/");

						ITool[] tools = resourceInfo.getTools();
						for (ITool tool : tools) {
							modified |= setOptionForResourceInfo(id, value, resourceInfo, tool.getOptions(), tool);
						}
					} else {
						throw new ProcessFailureException("Unsupported type for " + path); //$NON-NLS-1$
					}
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
				if (option.getValueType() == IOption.STRING) {
					String oldValue = option.getStringValue();

					// Check if value is not already in.
					if (oldValue.indexOf(value) < 0) {
						String newValue = oldValue + " " + value;
						ManagedBuildManager.setOption(resourceConfig, optionHolder, option, newValue);
						modified = true;
					}
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
				if (option.getValueType() == IOption.STRING) {
					String oldValue = option.getStringValue();
					String newValue = oldValue + " " + value;
					ManagedBuildManager.setOption(config, optionHolder, option, newValue);
					modified = true;
				}
			}
		}
		return modified;
	}

	private boolean setOptionForResourceInfo(String id, String value, IResourceInfo config, IOption[] options,
			IHoldsOptions optionHolder) throws BuildException {
		boolean modified = false;
		String lowerId = id.toLowerCase();
		for (IOption option : options) {
			if (option.getBaseId().toLowerCase().matches(lowerId)) {
				if (option.getValueType() == IOption.STRING) {
					String oldValue = option.getStringValue();
					String newValue = oldValue + " " + value;
					ManagedBuildManager.setOption(config, optionHolder, option, newValue);
					modified = true;
				}
			}
		}
		return modified;
	}
}
