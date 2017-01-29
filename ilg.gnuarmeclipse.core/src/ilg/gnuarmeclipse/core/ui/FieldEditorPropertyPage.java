/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.core.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;

public abstract class FieldEditorPropertyPage extends FieldEditorPreferencePage implements IWorkbenchPropertyPage {

	/**
	 * The element.
	 */
	private IAdaptable element;

	/**
	 * Creates a new property page.
	 */
	public FieldEditorPropertyPage() {
		super();
	}

	protected FieldEditorPropertyPage(int style) {
		super(style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
	 */
	@Override
	public IAdaptable getElement() {
		return element;
	}

	/**
	 * Sets the element that owns properties shown on this page.
	 * 
	 * @param element
	 *            the element
	 */
	@Override
	public void setElement(IAdaptable element) {
		this.element = element;
	}
}
