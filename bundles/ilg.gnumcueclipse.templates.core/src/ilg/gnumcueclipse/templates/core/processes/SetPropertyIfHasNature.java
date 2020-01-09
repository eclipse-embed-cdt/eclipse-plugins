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

package ilg.gnumcueclipse.templates.core.processes;

import java.util.Map;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ilg.gnumcueclipse.templates.core.Activator;

public class SetPropertyIfHasNature extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		String projectName = args[0].getSimpleValue();
		String natureString = args[1].getSimpleValue();
		String propertyName = args[2].getSimpleValue();
		String propertyValue = args[3].getSimpleValue();

		IProject projectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		try {
			if (projectHandle.hasNature(natureString)) {
				// System.out.println("is " + natureString + " set "
				// + propertyName + "=" + propertyValue);

				Map<String, String> values = template.getValueStore();
				if (values.containsKey(propertyName)) {
					values.put(propertyName, propertyValue);
				} else {
					Activator.log("Property " + propertyName + " not defined.");
				}
			}
		} catch (CoreException e1) {
			Activator.log(e1.getStatus());
		}

	}

}
