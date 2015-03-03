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
	public void process(TemplateCore template, ProcessArgument[] args,
			String processId, IProgressMonitor monitor)
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
