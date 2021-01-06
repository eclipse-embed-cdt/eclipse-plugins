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
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral;

import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.SvdDMNode;

public class PeripheralClusterVMNode extends PeripheralGroupVMNode {

	// ------------------------------------------------------------------------

	public PeripheralClusterVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode) {

		super(parent, dmNode);

	}

	// ------------------------------------------------------------------------

	@Override
	public String getDisplayNodeType() {
		return "Cluster";
	}

	@Override
	public String getImageName() {
		return "registergroup_obj";
	}

	@Override
	public String getDisplaySize() {
		return null;
	}

	// ------------------------------------------------------------------------
}
