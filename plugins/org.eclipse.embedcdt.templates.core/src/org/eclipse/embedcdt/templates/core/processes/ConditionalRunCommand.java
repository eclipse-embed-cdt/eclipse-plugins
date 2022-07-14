/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.templates.core.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.internal.core.Activator;
import org.eclipse.embedcdt.templates.core.Utils;

public class ConditionalRunCommand extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		IProject projectHandle = null;
		String condition = null;
		String cwd = null;
		String command = null;
		String cmdArgs[] = {};

		for (ProcessArgument arg : args) {
			String argName = arg.getName();
			if (argName.equals("projectName")) { //$NON-NLS-1$
				projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(arg.getSimpleValue());
			} else if (argName.equals("condition")) { //$NON-NLS-1$
				condition = arg.getSimpleValue();
			} else if (argName.equals("cwd")) { //$NON-NLS-1$
				cwd = arg.getSimpleValue();
			} else if (argName.equals("command")) { //$NON-NLS-1$
				command = arg.getSimpleValue();
			} else if (argName.equals("args")) { //$NON-NLS-1$
				cmdArgs = arg.getSimpleArrayValue();
			}
		}

		if (projectHandle == null) {
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "projectName not specified")); //$NON-NLS-1$
		}

		if (cwd == null || cwd.isEmpty()) {
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "path not specified")); //$NON-NLS-1$
		}

		if (command == null || command.isEmpty()) {
			throw new ProcessFailureException(getProcessMessage(processId, IStatus.ERROR, "path not specified")); //$NON-NLS-1$
		}

		if (!Utils.isConditionSatisfied(condition)) {
			return;
		}

		String cmdArray[] = new String[cmdArgs.length + 1];
		cmdArray[0] = command;
		for (int i = 0; i < cmdArgs.length; i++) {
			cmdArray[i + 1] = cmdArgs[i];
		}

		String originalCommand = StringUtils.join(cmdArray, " ");

		// cmdArray[0] = EclipseUtils.makePathAbsolute(command);

		if (Activator.getInstance().isDebugging()) {
			Map<String, String> env = System.getenv();
			for (String envName : env.keySet()) {
				System.out.format("%s=%s%n", envName, env.get(envName));
			}
		}

		// Inherit from parent process.
		String envp[] = null;
		if (!EclipseUtils.isWindows()) {
			envp = EclipseUtils.getShellEnvironment();
		}

		String cwdSubstituted = EclipseUtils.performStringSubstitution(cwd);
		if (cwdSubstituted == null) {
			// If substitution fails, revert to the original string, to alert
			// the user.
			cwdSubstituted = cwd;
		}

		File dir = new File(cwdSubstituted);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("cwd: " + dir);
		}
		System.out.println("> " + originalCommand);

		try {
			BufferedReader reader = null;
			Process process = ProcessFactory.getFactory().exec(cmdArray, envp, dir);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			List<String> outputLines = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				outputLines.add(line);
			}

			for (String l : outputLines) {
				System.out.println(l);
			}

			reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			List<String> errorLines = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				errorLines.add(line);
			}

			process.destroy();
			if (process.exitValue() != 0) {
				for (String l : errorLines) {
					System.out.println(l);
				}
				System.out.println("exit (" + process.exitValue() + ")");
			}

		} catch (IOException e) {
			// e.printStackTrace();

			throw new ProcessFailureException(e.getMessage()); //$NON-NLS-1$
		}

		// Refresh project. This is important, without it the
		// final action to select the main.* file fails.
		try {
			projectHandle.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}
}
