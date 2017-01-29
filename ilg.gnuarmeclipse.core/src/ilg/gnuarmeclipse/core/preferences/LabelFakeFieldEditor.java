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

package ilg.gnuarmeclipse.core.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A field editor for a string type preference.
 * <p>
 * This class may be used as is, or subclassed as required.
 * </p>
 */
public class LabelFakeFieldEditor extends FieldEditor {

	/**
	 * Old text value.
	 * 
	 * @since 3.4 this field is protected.
	 */
	protected String oldValue;

	/**
	 * The text field, or <code>null</code> if none.
	 */
	Label labelField;

	String labelValue;

	/**
	 * The error message, or <code>null</code> if none.
	 */
	private String errorMessage;

	/**
	 * Creates a new string field editor
	 */
	protected LabelFakeFieldEditor() {
	}

	/**
	 * Creates a label fake field editor.
	 * 
	 * @param value
	 *            the content of the label field
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @since 2.0
	 */
	public LabelFakeFieldEditor(String value, String labelText, Composite parent) {

		init("no_name", labelText);
		labelValue = value;
		errorMessage = JFaceResources.getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData) labelField.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 * <p>
	 * The string field implementation of this <code>FieldEditor</code>
	 * framework method contributes the text field. Subclasses may override but
	 * must call <code>super.doFillIntoGrid</code>.
	 * </p>
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		getLabelControl(parent);

		labelField = getTextControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;

		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		labelField.setLayoutData(gd);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doLoad() {
		setStringValue(labelValue);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doLoadDefault() {
		setStringValue(labelValue);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doStore() {
		;
	}

	/**
	 * Returns the error message that will be displayed when and if an error
	 * occurs.
	 *
	 * @return the error message, or <code>null</code> if none
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns the field editor's value.
	 *
	 * @return the current value
	 */
	public String getStringValue() {

		if (labelField != null) {
			return labelField.getText();
		}

		return "";
	}

	/**
	 * Returns this field editor's text control.
	 *
	 * @return the text control, or <code>null</code> if no text field is
	 *         created yet
	 */
	protected Label getTextControl() {
		return labelField;
	}

	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 *
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	public Label getTextControl(Composite parent) {

		if (labelField == null) {
			labelField = new Label(parent, SWT.SINGLE | SWT.BORDER);
			labelField.setFont(parent.getFont());
			labelField.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent event) {
					labelField = null;
				}
			});
		} else {
			checkParent(labelField, parent);
		}
		return labelField;
	}

	/**
	 * Sets the error message that will be displayed when and if an error
	 * occurs.
	 *
	 * @param message
	 *            the error message
	 */
	public void setErrorMessage(String message) {
		errorMessage = message;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	public void setFocus() {
		if (labelField != null) {
			labelField.setFocus();
		}
	}

	/**
	 * Sets this field editor's value.
	 *
	 * @param value
	 *            the new value, or <code>null</code> meaning the empty string
	 */
	public void setStringValue(String value) {

		if (labelField != null) {
			if (value == null) {
				value = "";//$NON-NLS-1$
			}
			oldValue = labelField.getText();
			if (!oldValue.equals(value)) {
				labelField.setText(value);
				valueChanged();
			}
		}
	}

	/**
	 * Shows the error message set via <code>setErrorMessage</code>.
	 */
	public void showErrorMessage() {
		showErrorMessage(errorMessage);
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change to
	 * the value (<code>VALUE</code> property) provided that the old and new
	 * values are different.
	 * <p>
	 * This hook is <em>not</em> called when the text is initialized (or reset
	 * to the default value) from the preference store.
	 * </p>
	 */
	protected void valueChanged() {

		setPresentsDefaultValue(false);
		refreshValidState();

		String newValue = labelField.getText();
		if (!newValue.equals(oldValue)) {
			fireValueChanged(VALUE, oldValue, newValue);
			oldValue = newValue;
		}
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getTextControl(parent).setEnabled(enabled);
	}
}
