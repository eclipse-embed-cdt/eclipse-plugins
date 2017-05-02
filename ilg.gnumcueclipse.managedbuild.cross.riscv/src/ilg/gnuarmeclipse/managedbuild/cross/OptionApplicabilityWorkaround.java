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

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IFileInfo;
import org.eclipse.cdt.managedbuilder.core.IFolderInfo;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;
import org.eclipse.cdt.managedbuilder.core.IResourceInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.internal.core.BooleanExpressionApplicabilityCalculator;
import org.eclipse.cdt.managedbuilder.internal.core.Option;

/**
 * A workaround for MBS bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=273822
 * <br>
 * <br>
 * When attached to an option as its applicabilityCalculator, this class will
 * evaluate the option's enablements for visibility, command line enablement and
 * value.<br>
 * <br>
 * The workaround is that this class will also update the option value according
 * to CONTAINER_ATTRIBUTE enablements, referring to the most recent options.
 * Somehow, this class is called in a way that does not override the option
 * values, thereby circumventing the bug.<br>
 * <br>
 * This workaround does not support extension adjustment options.
 * 
 * 
 * @author mariopi
 * 
 */
@SuppressWarnings("restriction")
public class OptionApplicabilityWorkaround implements IOptionApplicability {

	// ------------------------------------------------------------------------

	@Override
	public boolean isOptionEnabled(IBuildObject configuration, IHoldsOptions holder, IOption option) {

		// Invoke the options enablement expressions
		if (!(option instanceof Option))
			throw new AssertionError();
		Option opt = (Option) option;
		BooleanExpressionApplicabilityCalculator app = opt.getBooleanExpressionCalculator(false);

		IResourceInfo rcInfo = getResourceInfo(configuration);

		// Update CONTAINER_ATTRIBUTES
		app.adjustOption(rcInfo, holder, option, false); // OptionEnablementExpression.FLAG_CONTAINER_ATTRIBUTE

		// Update UI_ENABLEMENT
		return app.isOptionEnabled(configuration, holder, option);
	}

	@Override
	public boolean isOptionUsedInCommandLine(IBuildObject configuration, IHoldsOptions holder, IOption option) {

		if (!(option instanceof Option))
			throw new AssertionError();
		Option opt = (Option) option;
		BooleanExpressionApplicabilityCalculator app = opt.getBooleanExpressionCalculator(false);

		IResourceInfo rcInfo = getResourceInfo(configuration);

		// Update CONTAINER_ATTRIBUTES
		app.adjustOption(rcInfo, holder, option, false); // OptionEnablementExpression.FLAG_CONTAINER_ATTRIBUTE

		// Update UI_CMD_USAGE
		return app.isOptionUsedInCommandLine(configuration, holder, option);
	}

	@Override
	public boolean isOptionVisible(IBuildObject configuration, IHoldsOptions holder, IOption option) {

		if (!(option instanceof Option))
			throw new AssertionError();
		Option opt = (Option) option;
		BooleanExpressionApplicabilityCalculator app = opt.getBooleanExpressionCalculator(false);

		IResourceInfo rcInfo = getResourceInfo(configuration);

		// Update CONTAINER_ATTRIBUTES
		app.adjustOption(rcInfo, holder, option, false); // OptionEnablementExpression.FLAG_CONTAINER_ATTRIBUTE

		// Update UI_VISIBILITY
		// Once an option is determined as invisible, it is not updated anymore.
		// Options deemed as invisible at UI creation will thus remain
		// invisible.
		return app.isOptionVisible(configuration, holder, option);
	}

	/**
	 * Searches for an option in an IConfiguration. The toolchain options are
	 * scanned first, then the individual tools.
	 * 
	 * @param config
	 *            the configuration in which the option must be searched.
	 * @param optionId
	 *            id of the option
	 * @return an IOption instance, or null if no option is found with the given
	 *         id
	 */
	public IOption searchOption(IBuildObject configuration, String optionId) {
		IConfiguration config;

		if (configuration instanceof IConfiguration) {
			config = (IConfiguration) configuration;
		} else if (configuration instanceof IResourceInfo) {
			config = ((IResourceInfo) configuration).getParent();
		} else
			throw new AssertionError();

		IOption opt = config.getToolChain().getOptionBySuperClassId(optionId);
		if (opt == null) {
			// Scan for option in the tools
			ITool[] tools = config.getTools();
			for (ITool t : tools) {
				opt = t.getOptionBySuperClassId(optionId);
				if (opt != null)
					break;
			}
		}
		return opt;
	}

	/**
	 * Extracts a resource info from a build object. If no resource info can be
	 * found, it returns null.
	 * 
	 * @param configuration
	 * @return
	 */
	private IResourceInfo getResourceInfo(IBuildObject configuration) {
		if (configuration instanceof IFolderInfo)
			return (IFolderInfo) configuration;
		if (configuration instanceof IFileInfo)
			return (IFileInfo) configuration;
		if (configuration instanceof IConfiguration)
			return ((IConfiguration) configuration).getRootFolderInfo();
		return null;
	}

	// ------------------------------------------------------------------------
}