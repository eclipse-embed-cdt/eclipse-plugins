/*******************************************************************************
 * Copyright (c) 2011, 2012 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.build.crossgcc;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.cdt.internal.build.crossgcc.messages"; //$NON-NLS-1$
	public static String SetCrossCommandWizardPage_browse;
	public static String SetCrossCommandWizardPage_description;
	public static String SetCrossCommandWizardPage_name;
	public static String SetCrossCommandWizardPage_path;
	public static String SetCrossCommandWizardPage_prefix;
	public static String SetCrossCommandWizardPage_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
