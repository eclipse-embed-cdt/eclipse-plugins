/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *     Alexander Fedorov (ArSysOp) - extract UI part
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.embedcdt.packs.core.IConsoleStream;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Utils {

	/**
	 * 
	 * @param sourceUrl
	 * @param destinationFile
	 * @param out
	 * @param monitor
	 * @param shell
	 * @return 0 = Ok, 1 = Retry, 2 = Ignore, 3 = Ignore All, 4 = Abort
	 * @throws IOException
	 */
	public static int copyFileWithShell(final URL sourceUrl, File destinationFile, IConsoleStream out,
			IProgressMonitor monitor, final Shell shell, final boolean ignoreError) throws IOException {

		while (true) {
			try {
				org.eclipse.embedcdt.packs.core.data.DataUtils.copyFile(sourceUrl, destinationFile, out, monitor);
				return 0;
			} catch (final IOException e) {

				if (ignoreError) {
					return 3; // Ignore All
				}

				class ErrorMessageDialog implements Runnable {

					public int retCode = 0;

					@Override
					public void run() {
						String[] buttons = new String[] { "Retry", "Ignore", "Ignore All", "Abort" };
						MessageDialog dialog = new MessageDialog(shell, "Read error", null,
								sourceUrl.toString() + "\n" + e.getMessage(), MessageDialog.ERROR, buttons, 0);
						retCode = dialog.open();
					}
				}

				ErrorMessageDialog messageDialog = new ErrorMessageDialog();
				Display.getDefault().syncExec(messageDialog);

				if (messageDialog.retCode == 3) {
					throw e; // Abort
				} else if (messageDialog.retCode == 1 || messageDialog.retCode == 2) {
					return messageDialog.retCode + 1; // Ignore & Ignore All
				}

				// Else try again
			}
		}

		// HandlerUtil.getActiveShell(event)
	}
}
