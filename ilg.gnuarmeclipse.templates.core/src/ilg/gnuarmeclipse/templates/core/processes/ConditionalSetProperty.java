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
import ilg.gnuarmeclipse.templates.core.Utils;

import java.util.Map;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.core.runtime.IProgressMonitor;

public class ConditionalSetProperty extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		String condition = args[1].getSimpleValue();
		if (!Utils.isConditionSatisfied(condition))
			return;

		String propertyName = args[2].getSimpleValue();
		String propertyValue = args[3].getSimpleValue();

		Map<String, String> values = template.getValueStore();
		if (values.containsKey(propertyName)) {
			values.put(propertyName, propertyValue);
		} else {
			Activator.log("Property " + propertyName + " not defined.");
		}
	}
}
