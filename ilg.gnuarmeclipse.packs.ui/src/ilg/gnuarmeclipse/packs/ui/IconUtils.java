/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.ui;

import org.eclipse.swt.graphics.Image;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

public class IconUtils {

	public static final String ICONS_PLUGIN_ID = "ilg.gnuarmeclipse.packs.ui";

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
