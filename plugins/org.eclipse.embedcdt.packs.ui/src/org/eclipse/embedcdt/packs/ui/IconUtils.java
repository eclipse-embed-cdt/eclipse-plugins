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
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.ui;

import org.eclipse.embedcdt.internal.packs.ui.Activator;
import org.eclipse.embedcdt.packs.core.tree.Leaf;
import org.eclipse.embedcdt.packs.core.tree.Node;
import org.eclipse.swt.graphics.Image;

public class IconUtils {

	public static Image getBookIcon(Leaf node) {

		String url = node.getProperty(Node.URL_PROPERTY);
		if (url.length() > 0) {
			return Activator.getInstance().getImage("external_browser");
		}
		String path = node.getProperty(Node.FILE_PROPERTY).toLowerCase();

		if (path.endsWith(".pdf")) {
			return Activator.getInstance().getImage("pdficon_small");
		} else if (path.endsWith(".chm")) {
			return Activator.getInstance().getImage("chm");
		} else if (path.endsWith(".zip")) {
			return Activator.getInstance().getImage("zip");
		} else {
			return Activator.getInstance().getImage("library_obj");
		}
	}
}
