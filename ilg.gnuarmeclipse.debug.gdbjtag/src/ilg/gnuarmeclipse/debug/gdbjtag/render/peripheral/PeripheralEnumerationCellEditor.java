/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral;

import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterFieldVMNode;

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

	protected Control createControl(Composite composite) {

		CCombo combo = (CCombo) super.createControl(composite);

		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent event) {
				PeripheralEnumerationCellEditor.this.focusLost();
			}

			public void widgetSelected(SelectionEvent event) {
				PeripheralEnumerationCellEditor.this.focusLost();
			}
		});
		return combo;
	}

	// ------------------------------------------------------------------------
}
