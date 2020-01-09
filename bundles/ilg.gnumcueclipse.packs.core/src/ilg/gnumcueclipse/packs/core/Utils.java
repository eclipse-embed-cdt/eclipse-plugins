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

package ilg.gnumcueclipse.packs.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {

	public static String getCurrentDateTime() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

}
