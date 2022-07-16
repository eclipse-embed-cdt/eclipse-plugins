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

		String substitutedCommand = EclipseUtils.performStringSubstitution(command);
		if (substitutedCommand == null) {
			substitutedCommand = command;
		}

		// TODO: quote spaces
		String joinedArgs = StringUtils.join(cmdArgs, " ");
		String substitutedArgs = EclipseUtils.performStringSubstitution(joinedArgs);
		if (substitutedArgs == null) {
			substitutedArgs = joinedArgs;
		}

		String cmdArray[];

		if (EclipseUtils.isWindows()) {
			// ComSpec=C:\Windows\system32\cmd.exe
			String shell = System.getenv("ComSpec");
			if (shell == null) {
				shell = "cmd.exe";
			}

			cmdArray = new String[5];
			cmdArray[0] = shell;
			cmdArray[1] = "/d"; // Disable execution of AutoRun commands from registry
			cmdArray[2] = "/s"; // Modifies the treatment of string after /C or /K
			cmdArray[3] = "/c"; // Carries out the command specified by string and then terminates
			cmdArray[4] = substitutedCommand + " " + substitutedArgs;
		} else {
			String shell = System.getenv("SHELL");
			if (shell == null) {
				shell = "/bin/sh";
			}

			cmdArray = new String[4];
			cmdArray[0] = shell;
			cmdArray[1] = "-i"; // To read .zshrc, where nvm is configured.
			cmdArray[2] = "-c";
			cmdArray[3] = substitutedCommand + " " + substitutedArgs;
		}

		if (Activator.getInstance().isDebugging()) {
			Map<String, String> env = System.getenv();
			for (String envName : env.keySet()) {
				System.out.format("%s=%s%n", envName, env.get(envName));
			}
		}

		// Inherit from parent process.
		String envp[] = null;

		String substitutedCwd = EclipseUtils.performStringSubstitution(cwd);
		if (substitutedCwd == null) {
			// If substitution fails, revert to the original string, to alert
			// the user.
			substitutedCwd = cwd;
		}

		File dir = new File(substitutedCwd);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("cwd: " + dir);
		}
		System.out.println("> " + StringUtils.join(cmdArray, " "));

		Activator.log("cwd: " + dir);
		Activator.log("> " + StringUtils.join(cmdArray, " "));

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
			if (!errorLines.isEmpty()) {
				Activator.log(errorLines.toString());
			}

			process.destroy();
			if (process.exitValue() != 0) {
				for (String l : errorLines) {
					System.out.println(l);
				}
				System.out.println("exit (" + process.exitValue() + ")");
				Activator.log("exit (" + process.exitValue() + ")");

				throw new ProcessFailureException(errorLines.toString()); //$NON-NLS-1$
			}

		} catch (IOException e) {
			// e.printStackTrace();

			// Do not pass a message and a cause, they are messed due to a bug.
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
