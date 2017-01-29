/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ningareddy Modase - initial implementation.
 *     Liviu Ionescu - ARM version.
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.restart.action;

import ilg.gnuarmeclipse.debug.gdbjtag.restart.Activator;

import org.eclipse.debug.core.commands.IRestartHandler;
import org.eclipse.debug.ui.actions.DebugCommandAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RestartCommandAction extends DebugCommandAction {

	// ------------------------------------------------------------------------

	public RestartCommandAction() {
		setActionDefinitionId(Activator.PLUGIN_ID + ".commands.Restart");
	}

	@Override
	public String getText() {
		return "Restart";
	}

	@Override
	public String getHelpContextId() {
		return "org.eclipse.debug.ui.disconnect_action_context";
	}

	@Override
	public String getId() {
		return "org.eclipse.debug.ui.debugview.toolbar.disconnect";
	}

	@Override
	public String getToolTipText() {
		return "Reset target and restart debugging";
	}

	@Override
	public ImageDescriptor getDisabledImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/dlcl16/restart_co.gif");
	}

	@Override
	public ImageDescriptor getHoverImageDescriptor() {
		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/elcl16/restart_co.gif");
	}

	@Override
	protected Class<IRestartHandler> getCommandType() {
		return IRestartHandler.class;
	}

	// ------------------------------------------------------------------------
}