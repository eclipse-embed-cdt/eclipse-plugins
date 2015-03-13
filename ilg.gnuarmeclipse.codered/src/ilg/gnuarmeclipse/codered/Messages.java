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

package ilg.gnuarmeclipse.codered;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	// public static String MyMessage_text;

	static {
		// Initialise resource bundle.
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}

	private Messages() {
	}

}
