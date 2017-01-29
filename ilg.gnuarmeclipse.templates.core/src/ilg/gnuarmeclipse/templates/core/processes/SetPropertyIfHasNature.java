/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.templates.core.processes;

import ilg.gnuarmeclipse.templates.core.Activator;

import java.util.Map;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
