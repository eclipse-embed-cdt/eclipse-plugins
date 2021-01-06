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
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral;

import org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral.PeripheralRegisterFieldVMNode;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class PeripheralEnumerationCellEditor extends ComboBoxCellEditor {

	// ------------------------------------------------------------------------

	public PeripheralEnumerationCellEditor(Composite editorParent,
			PeripheralRegisterFieldVMNode peripheralRegisterField) {

		super(editorParent, peripheralRegisterField.getEnumerationComboItems(), SWT.BORDER);
	}

	@Override
	protected Control createControl(Composite composite) {

		CCombo combo = (CCombo) super.createControl(composite);

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				PeripheralEnumerationCellEditor.this.focusLost();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				PeripheralEnumerationCellEditor.this.focusLost();
			}
		});
		return combo;
	}

	// ------------------------------------------------------------------------
}
