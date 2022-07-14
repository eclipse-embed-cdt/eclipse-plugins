/*******************************************************************************
 * Copyright (c) 2007, 2009 Symbian Software Limited and others.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IPathEntry;
import org.eclipse.cdt.core.settings.model.CSourceEntry;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICSourceEntry;
import org.eclipse.cdt.core.settings.model.WriteAccessException;
import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.cdt.core.templateengine.process.processes.Messages;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.embedcdt.templates.core.Utils;

/**
 * Creates a include Folder to the project.
 */
public class ConditionalAddSourceFolders extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		String projectName = null;
		IProject projectHandle = null;
		String targetPaths[] = null;
		String condition = null;

		for (ProcessArgument arg : args) {
			String argName = arg.getName();
			if (argName.equals("projectName")) { //$NON-NLS-1$
				projectName = arg.getSimpleValue();
				projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			} else if (argName.equals("paths")) { //$NON-NLS-1$
				targetPaths = arg.getSimpleArrayValue();
			} else if (argName.equals("condition")) { //$NON-NLS-1$
				condition = arg.getSimpleValue();
			}
		}

		if (projectHandle == null) {
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "projectName not specified")); //$NON-NLS-1$
		}

		if (!projectHandle.exists()) {
			throw new ProcessFailureException(Messages.getString("CreateSourceFolder.0") + projectName); //$NON-NLS-1$
		}

		if (targetPaths == null) {
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "No paths")); //$NON-NLS-1$Â
		}

		if (!Utils.isConditionSatisfied(condition)) {
			return;
		}

		for (String targetPath : targetPaths) {
			createSourceFolder(projectHandle, targetPath, monitor);
		}
	}

	protected void createSourceFolder(IProject projectHandle, String targetPath, IProgressMonitor monitor)
			throws ProcessFailureException {

		// If the targetPath is an empty string, there will be no source folder to create.
		// Also this is not an error. So just return gracefully.
		if (targetPath == null || targetPath.length() == 0) {
			return;
		}

		// String projectName = projectHandle.getName();
		// CreateFolder.createFolder(projectName, targetPath, monitor);

		IPath projPath = projectHandle.getFullPath();
		IFolder folder = projectHandle.getFolder(targetPath);

		try {
			ICProject cProject = CoreModel.getDefault().create(projectHandle);
			if (cProject != null) {
				if (CCorePlugin.getDefault().isNewStyleProject(cProject.getProject())) {
					//create source folder for new style project
					createNewStyleProjectFolder(monitor, projectHandle, folder);
				} else {
					//create source folder for all other projects
					createFolder(targetPath, monitor, projPath, cProject);
				}
			}
		} catch (WriteAccessException e) {
			throw new ProcessFailureException(Messages.getString("CreateSourceFolder.2") + e.getMessage(), e); //$NON-NLS-1$
		} catch (CoreException e) {
			throw new ProcessFailureException(Messages.getString("CreateSourceFolder.2") + e.getMessage(), e); //$NON-NLS-1$
		}
	}

	/**
	 * @param monitor
	 * @param projectHandle
	 * @param folder
	 * @throws CoreException
	 * @throws WriteAccessException
	 */
	private void createNewStyleProjectFolder(IProgressMonitor monitor, IProject projectHandle, IFolder folder)
			throws CoreException, WriteAccessException {
		ICSourceEntry newEntry = new CSourceEntry(folder, null, 0);
		ICProjectDescription description = CCorePlugin.getDefault().getProjectDescription(projectHandle);

		ICConfigurationDescription configs[] = description.getConfigurations();
		for (int i = 0; i < configs.length; i++) {
			ICConfigurationDescription config = configs[i];
			ICSourceEntry[] entries = config.getSourceEntries();
			Set<ICSourceEntry> set = new HashSet<>();
			for (int j = 0; j < entries.length; j++) {
				if (new Path(entries[j].getValue()).segmentCount() == 1)
					continue;
				set.add(entries[j]);
			}
			set.add(newEntry);
			config.setSourceEntries(set.toArray(new ICSourceEntry[set.size()]));
		}

		CCorePlugin.getDefault().setProjectDescription(projectHandle, description, false, monitor);
	}

	/**
	 * @param targetPath
	 * @param monitor
	 * @param projPath
	 * @param cProject
	 * @throws CModelException
	 */
	private void createFolder(String targetPath, IProgressMonitor monitor, IPath projPath, ICProject cProject)
			throws CModelException {
		IPathEntry[] entries = cProject.getRawPathEntries();
		List<IPathEntry> newEntries = new ArrayList<>(entries.length + 1);

		int projectEntryIndex = -1;
		IPath path = projPath.append(targetPath);

		for (int i = 0; i < entries.length; i++) {
			IPathEntry curr = entries[i];
			if (path.equals(curr.getPath())) {
				// just return if this folder exists already
				return;
			}
			if (projPath.equals(curr.getPath())) {
				projectEntryIndex = i;
			}
			newEntries.add(curr);
		}

		IPathEntry newEntry = CoreModel.newSourceEntry(path);

		if (projectEntryIndex != -1) {
			newEntries.set(projectEntryIndex, newEntry);
		} else {
			newEntries.add(CoreModel.newSourceEntry(path));
		}

		cProject.setRawPathEntries(newEntries.toArray(new IPathEntry[newEntries.size()]), monitor);
	}

}
