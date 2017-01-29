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

package ilg.gnuarmeclipse.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public abstract class AbstractUIActivator extends AbstractUIPlugin {

	// ------------------------------------------------------------------------

	private static AbstractUIActivator fgInstance;

	public static AbstractUIActivator getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	public abstract String getBundleId();

	// ------------------------------------------------------------------------

	protected boolean fIsDebugging;

	// ------------------------------------------------------------------------

	public AbstractUIActivator() {

		super();
		fgInstance = this;

		fIsDebugging = "true".equalsIgnoreCase(Platform.getDebugOption(getBundleId() + "/debug"));
	}

	// ------------------------------------------------------------------------

	public void start(BundleContext context) throws Exception {

		if (isDebugging()) {
			System.out.println(getBundleId() + ".start()");
		}
		super.start(context);

	}

	public void stop(BundleContext context) throws Exception {

		super.stop(context);
		if (isDebugging()) {
			System.out.println(getBundleId() + ".stop()");
		}
	}

	// ------------------------------------------------------------------------

	public boolean isDebugging() {

		return fIsDebugging;
	}

	// ------------------------------------------------------------------------

	/**
	 * For a given name, get the 'icons/name.png' description from the registry.
	 * If not there, register it.
	 * 
	 * @param name
	 *            a String with the file name (default .png).
	 * @return an ImageDescriptor or null.
	 */
	public ImageDescriptor getImageDescriptor(String name) {

		String str = name.toLowerCase();
		ImageDescriptor imageDescriptor = getImageRegistry().getDescriptor(str);
		if (imageDescriptor == null) {
			imageDescriptor = declareImage(str);
		}
		return imageDescriptor;
	}

	/**
	 * For a given name, get the 'icons/name.png' image from the registry. If
	 * not there, register it.
	 * 
	 * @param name
	 *            a String with the file name (default .png).
	 * @return an Image, possibly a default one.
	 */
	public Image getImage(String name) {

		String str = name.toLowerCase();

		Image image = getImageRegistry().get(str);
		if (image != null) {
			// The common case, when the image exist; return it.
			return image;
		}

		// Image not known, register it.
		ImageDescriptor imageDescriptor = declareImage(str);
		if (imageDescriptor == null) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
		}
		image = getImageRegistry().get(str);
		return image;
	}

	/**
	 * For a given 'name', register 'icons/name.png' in the local image
	 * registry.
	 * 
	 * @param name
	 *            a String with the file name (default .png).
	 * @return an ImageDescriptor or null.
	 */
	protected ImageDescriptor declareImage(String name) {

		Object path = new Path("icons/", name);
		Object pathx = path;
		String extension = ((IPath) path).getFileExtension();
		if ((extension == null) || (extension.isEmpty())) {
			// If missing, try default extension '.png'.
			pathx = ((IPath) path).addFileExtension("png");
		}

		// Check path in the plug-in name space.
		ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(getBundleId(),
				((IPath) pathx).toString());
		if (imageDescriptor == null) {
			if ((extension == null) || (extension.isEmpty())) {
				// If missing, second default extension is '.gif'.
				pathx = ((IPath) path).addFileExtension("gif");

				// Check gif path in the plug-in name space.
				imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(getBundleId(), ((IPath) pathx).toString());
			}
			if (imageDescriptor == null) {
				return null;
			}
		}
		try {
			String key = getKey(name);
			if (getImageRegistry().getDescriptor(key) == null) {
				getImageRegistry().put(key, imageDescriptor);
			}
		} catch (Exception e) {
			log(e);
		}
		return imageDescriptor;
	}

	/**
	 * The key is based on the image name.
	 * 
	 * @param name
	 * @return
	 */
	private String getKey(String name) {
		return name.toLowerCase();
	}

	// ------------------------------------------------------------------------

	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getInstance().getBundleId(), 1, "Internal Error", e)); //$NON-NLS-1$
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, getInstance().getBundleId(), 1, message, null)); // $NON-NLS-1$
	}

	// ------------------------------------------------------------------------
}
