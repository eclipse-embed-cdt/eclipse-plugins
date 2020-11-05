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

package org.eclipse.embedcdt.debug.gdbjtag.render.peripheral;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

public class LinkToolTip extends ColumnViewerToolTipSupport {

	// ------------------------------------------------------------------------

	public static final void enableFor(ColumnViewer viewer, int style, ILinkToolTipListener listener) {

		new LinkToolTip(viewer, style, false, listener);
	}

	// ------------------------------------------------------------------------

	private ILinkToolTipListener fListener;

	// ------------------------------------------------------------------------

	protected LinkToolTip(ColumnViewer viewer, int style, boolean manualActivation, ILinkToolTipListener listener) {
		super(viewer, style, manualActivation);

		fListener = listener;
	}

	// ------------------------------------------------------------------------

	protected Composite createToolTipContentArea(Event event, Composite parent) {

		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);

		Color fgColor = getForegroundColor(event);
		Color bgColor = getBackgroundColor(event);
		Font font = getFont(event);
		Link link = new Link(composite, 0);
		link.setFont(font);
		link.setBackground(bgColor);
		link.setForeground(fgColor);

		link.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (fListener != null)
					fListener.linkSelected(event.text);
			}
		});
		link.setText(getText(event));
		return composite;
	}

	public boolean isHideOnMouseDown() {
		return false;
	}

	// ------------------------------------------------------------------------
}
