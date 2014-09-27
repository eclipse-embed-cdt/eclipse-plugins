/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ilg.gnuarmeclipse.core.AbstractUIActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnuarmeclipse.debug.core"; //$NON-NLS-1$

	@Override
	public String getBundleId() {
		return PLUGIN_ID;
	}

	// ------------------------------------------------------------------------

	// The shared instance
	private static Activator fgInstance;

	public static Activator getInstance() {
		return fgInstance;
	}

	public Activator() {

		super();
		fgInstance = this;
	}

	// ------------------------------------------------------------------------

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	// ------------------------------------------------------------------------

	public ImageDescriptor getImageDescriptor(String name) {

		String str = name.toLowerCase();
		ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(str);
		if (imageDescriptor == null)
			imageDescriptor = declareImage(str);
		return imageDescriptor;
	}

	protected ImageDescriptor declareImage(String name) {

		Object path = new Path("icons/", name);
		String extension = ((IPath) path).getFileExtension();
		if ((extension == null) || (extension.isEmpty()))
			path = ((IPath) path).addFileExtension("png");
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(PLUGIN_ID, ((IPath) path).toString());
		try {
			String key = getKey(name);
			if (getImageRegistry().getDescriptor(key) == null)
				getImageRegistry().put(key, imageDescriptor);
		} catch (Exception e) {
		}
		return imageDescriptor;
	}

	private String getKey(String name) {
		return name.toLowerCase();
	}

	// ------------------------------------------------------------------------

	public static void statusMessage(String msg, final boolean flag) {
		System.out.println(msg);
	}

	// ------------------------------------------------------------------------
}
