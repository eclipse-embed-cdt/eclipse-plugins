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

import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralGroupVMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterFieldVMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterVMNode;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;

public class PeripheralEditingSupport extends EditingSupport {

	// ------------------------------------------------------------------------

	private Tree fEditorParent;
	private CellEditor fTextCellEditor;
	private PeripheralEnumerationCellEditor fEnumerationCellEditor;

	// ------------------------------------------------------------------------

	public PeripheralEditingSupport(TreeViewer viewer) {
		super(viewer);

		fEditorParent = viewer.getTree();
		fTextCellEditor = new TextCellEditor(fEditorParent);

		fEnumerationCellEditor = null;
	}

	// ------------------------------------------------------------------------

	@Override
	protected CellEditor getCellEditor(Object element) {

		if ((element instanceof PeripheralGroupVMNode)) {
			// Peripherals and clusters should not get an editor.
			return null;
		}

		// Be sure it is before checking instance of PeripheralRegisterVMNode
		if ((element instanceof PeripheralRegisterFieldVMNode)) {
			// Fields might get an editor.
			PeripheralRegisterFieldVMNode peripheralRegisterField = (PeripheralRegisterFieldVMNode) element;
			if (peripheralRegisterField.supportsValueModification()) {
				if (peripheralRegisterField.isEnumeration()) {
					fEnumerationCellEditor = new PeripheralEnumerationCellEditor(fEditorParent,
							peripheralRegisterField);
					return fEnumerationCellEditor;
				}
				return fTextCellEditor;
			}
		}

		if ((element instanceof PeripheralRegisterVMNode)) {
			// Registers might get an editor.
			PeripheralRegisterVMNode peripheralRegister = (PeripheralRegisterVMNode) element;
			if (peripheralRegister.supportsValueModification()) {
				return fTextCellEditor;
			}
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {

		if ((element instanceof PeripheralGroupVMNode)) {
			// Peripherals and clusters are not editable.
			return false;
		}

		if ((element instanceof PeripheralRegisterVMNode)) {
			// Registers and fields might be editable.
			PeripheralRegisterVMNode peripheralRegister = (PeripheralRegisterVMNode) element;
			if (peripheralRegister.supportsValueModification()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {

		if ((element instanceof PeripheralGroupVMNode)) {
			// Peripherals and clusters have no values.
			return null;
		}

		if ((element instanceof PeripheralRegisterVMNode)) {
			// Registers and fields have values, get it.
			PeripheralRegisterVMNode peripheralRegister = (PeripheralRegisterVMNode) element;
			return getValueForCellEditor(peripheralRegister);
		}
		return null;
	}

	/**
	 * Get the initial value to be used for the edit control.
	 * 
	 * @param peripheralRegister
	 *            the view node where to get the value from.
	 * @return a string with the value, an Integer, or null if none.
	 */
	private Object getValueForCellEditor(PeripheralRegisterVMNode peripheralRegister) {

		String value = peripheralRegister.getValueString();
		if (peripheralRegister instanceof PeripheralRegisterFieldVMNode) {
			PeripheralRegisterFieldVMNode field = (PeripheralRegisterFieldVMNode) peripheralRegister;
			if (field.isEnumeration()) {

				Integer index = field.getEnumerationComboIndex();
				if (index != null) {
					return index;
				}
				return new Integer(0);
			}
		}
		return value;
	}

	@Override
	protected void setValue(Object element, Object value) {

		boolean doNeedRefresh = false;
		if ((element instanceof PeripheralRegisterFieldVMNode)) {
			PeripheralRegisterFieldVMNode peripheralRegisterField = (PeripheralRegisterFieldVMNode) element;
			// This editor sends either a numeric value as string, or an
			// Integer object, with enumeration index.
			if (value instanceof String) {
				doNeedRefresh = peripheralRegisterField.setNumericValue((String) value);
			} else if (value instanceof Integer) {
				// Value is either the selection or -1 if the string
				// was entered manually. Since the selection text includes
				// the value, there is no need to search the enumeration list.
				Control control = fEnumerationCellEditor.getControl();
				// Get actual combo value.
				String str = ((CCombo) control).getText();
				if (((Integer) value).intValue() != -1 || str.indexOf(":") >= 0) {
					// If selection, retrieve value
					String as[] = str.split(":");
					str = as[0];
				}
				doNeedRefresh = peripheralRegisterField.setNumericValue(str);
			}
		} else if ((element instanceof PeripheralRegisterVMNode)) {
			PeripheralRegisterVMNode peripheralRegister = (PeripheralRegisterVMNode) element;
			// This editor always send numeric values
			doNeedRefresh = peripheralRegister.setNumericValue((String) value);
		}
		if (doNeedRefresh) {
			getViewer().refresh();
		} else {
			getViewer().update(element, null);
		}

		// De-select the current line
		getViewer().setSelection(null);
		// new StructuredSelection(new Object[] { element }));
	}

	// ------------------------------------------------------------------------
}
