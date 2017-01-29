/*******************************************************************************
 * Copyright (c) 2007, 2013 Symbian Software Limited and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Bala Torati (Symbian) - Initial API and implementation
 * Doug Schaefer (QNX) - Added overridable start and end patterns
 *******************************************************************************/
package ilg.gnuarmeclipse.templates.core.processes;

import ilg.gnuarmeclipse.templates.core.Activator;
import ilg.gnuarmeclipse.templates.core.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.TemplateEngineHelper;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessHelper;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.cdt.core.templateengine.process.processes.Messages;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Adds Files to the Project
 */
public class ConditionalCopyFolders extends ProcessRunner {

	/**
	 * This method Adds the list of Files to the corresponding Project.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {
		IProject projectHandle = null;
		ProcessArgument[][] folders = null;
		String startPattern = null;
		String endPattern = null;
		String condition = null;

		for (ProcessArgument arg : args) {
			String argName = arg.getName();
			if (argName.equals("projectName")) { //$NON-NLS-1$
				projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(arg.getSimpleValue());
			} else if (argName.equals("folders")) { //$NON-NLS-1$
				folders = arg.getComplexArrayValue();
			} else if (argName.equals("condition")) { //$NON-NLS-1$
				condition = arg.getSimpleValue();
			}
		}

		if (projectHandle == null)
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "projectName not specified")); //$NON-NLS-1$

		if (folders == null)
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "No folders")); //$NON-NLS-1$

		if (!Utils.isConditionSatisfied(condition))
			return;

		for (int i = 0; i < folders.length; i++) {
			ProcessArgument[] folder = folders[i];
			String folderSourcePath = folder[0].getSimpleValue();
			String folderTargetPath = folder[1].getSimpleValue();
			String pattern = folder[2].getSimpleValue().trim();
			boolean replaceable = folder[3].getSimpleValue().equals("true"); //$NON-NLS-1$

			// System.out.println(folderSourcePath);
			// System.out.println(folderTargetPath);
			// System.out.println(pattern);
			File dir = new File(folderSourcePath);
			try {
				if (!dir.isAbsolute()) {
					URL folderURL;
					folderURL = TemplateEngineHelper.getTemplateResourceURLRelativeToTemplate(template,
							folderSourcePath);
					if (folderURL == null) {
						throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR,
								Messages.getString("AddFiles.1") + folderSourcePath)); //$NON-NLS-1$
					}

					// System.out.println(folderURL.getFile());
					dir = new File(folderURL.getFile());
				}
			} catch (IOException e) {
				Activator.log(e);
			}
			if (dir.isDirectory()) {
				for (File child : dir.listFiles()) {
					// System.out.println(child);
					// System.out.println(child.getName());

					String fileName = child.getName();
					if (pattern.length() > 0) {
						if (!fileName.matches(pattern)) {
							if (Activator.getInstance().isDebugging()) {
								System.out.println(fileName + " skipped");
							}
							continue;
						}
					}

					if (child.isDirectory()) {
						if (Activator.getInstance().isDebugging()) {
							System.out.println(fileName + " skipped");
						}
						continue;
					}

					URL sourceURL;
					try {
						sourceURL = child.toURL(); // using .toURI().toURL()
													// fails,
													// due to
													// spaces substitution

					} catch (MalformedURLException e2) {
						throw new ProcessFailureException(Messages.getString("AddFiles.2") + folderSourcePath); //$NON-NLS-1$
					}

					InputStream contents = null;
					if (replaceable) {
						String fileContents;
						try {
							fileContents = ProcessHelper.readFromFile(sourceURL);
						} catch (IOException e) {
							throw new ProcessFailureException(Messages.getString("AddFiles.3") + folderSourcePath); //$NON-NLS-1$
						}
						if (startPattern != null && endPattern != null)
							fileContents = ProcessHelper.getValueAfterExpandingMacros(fileContents,
									ProcessHelper.getReplaceKeys(fileContents, startPattern, endPattern),
									template.getValueStore(), startPattern, endPattern);
						else
							fileContents = ProcessHelper.getValueAfterExpandingMacros(fileContents,
									ProcessHelper.getReplaceKeys(fileContents), template.getValueStore());

						contents = new ByteArrayInputStream(fileContents.getBytes());
					} else {
						try {
							contents = sourceURL.openStream();
						} catch (IOException e) {
							throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR,
									Messages.getString("AddFiles.4") + folderSourcePath)); //$NON-NLS-1$
						}
					}

					try {
						IFolder iFolder = projectHandle.getFolder(folderTargetPath);
						if (!iFolder.exists()) {
							ProcessHelper.mkdirs(projectHandle,
									projectHandle.getFolder(iFolder.getProjectRelativePath()));
						}

						// Should be OK on Windows too
						File concat = new File(folderTargetPath, fileName);
						IFile iFile = projectHandle.getFile(concat.getPath());

						if (iFile.exists()) {
							// honor the replaceable flag and replace the
							// file
							// contents
							// if the file already exists.
							if (replaceable) {
								iFile.setContents(contents, true, true, null);
							} else {
								throw new ProcessFailureException(Messages.getString("AddFiles.5")); //$NON-NLS-1$
							}

						} else {
							iFile.create(contents, true, null);
							iFile.refreshLocal(IResource.DEPTH_ONE, null);
						}
					} catch (CoreException e) {
						throw new ProcessFailureException(Messages.getString("AddFiles.6") + e.getMessage(), e); //$NON-NLS-1$
					}

				}
			}

		}
		try {
			projectHandle.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			throw new ProcessFailureException(Messages.getString("AddFiles.7") + e.getMessage(), e); //$NON-NLS-1$
		}
	}
}
