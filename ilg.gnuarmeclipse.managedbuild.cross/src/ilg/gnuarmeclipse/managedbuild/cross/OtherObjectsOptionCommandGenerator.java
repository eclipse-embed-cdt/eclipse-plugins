/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.CdtVariableResolver;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;

/**
 * Custom option command generator to quote spaces in other object names.
 */
public class OtherObjectsOptionCommandGenerator implements
		IOptionCommandGenerator {

	@Override
	public String generateCommand(IOption option,
			IVariableSubstitutor macroSubstitutor) {

		StringBuffer command = new StringBuffer();
		try {
			int valueType = option.getValueType();

			if (valueType == IOption.OBJECTS) {
				for (String value : option.getUserObjects()) {

					if (value != null) {
						value = value.trim();
					}

					if (!value.isEmpty()) {

						value = CdtVariableResolver.resolveToString(value,
								macroSubstitutor);
						command.append(Utils.quoteWhitespaces(value));
						command.append(" ");
					}
				}
			}
		} catch (BuildException e) {
			e.printStackTrace();
		} catch (CdtVariableException e) {
			e.printStackTrace();
		}

		return command.toString().trim();
	}

}
