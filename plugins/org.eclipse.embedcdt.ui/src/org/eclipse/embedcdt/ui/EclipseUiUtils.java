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
 *    Liviu Ionescu - initial version
 *    Alexander Fedorov (ArSysOp) - extract UI part
 *******************************************************************************/

package org.eclipse.embedcdt.ui;

import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.embedcdt.internal.ui.Activator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

/**
 * Various utilities that use Eclipse global classes, grouped together more like
 * a reference.
 * <ul>
 * <li>PlatformUI</li>
 * </ul>
 * 
 * Other interesting places to search for utility functions are:
 * <ul>
 * <li>Plugin</li>
 * </ul>
 *
 * For debugging, use
 * 
 * <pre>
 * private static final boolean DEBUG_TWO =
 *     ExamplesPlugin.getDefault().isDebugging() &&
 *        "true".equalsIgnoreCase(Platform.getDebugOption(
 *        "org.eclipse.faq.examples/debug/option2"));
 *  ...
 *  if (DEBUG_TWO)
 *     System.out.println("Debug statement two.");
 * </pre>
 * 
 * This will test two properties like
 * <ul>
 * <li>org.eclipse.faq.examples/debug=true</li>
 * <li>org.eclipse.faq.examples/debug/option2=true</li>
 * </ul>
 * These properties should be stored in a .option file in the plug-in root, or
 * in a custom file whose name is passed to the Eclipse -debug option.
 * <p>
 * See also the <a href=
 * "https://wiki.eclipse.org/FAQ_How_do_I_use_the_platform_debug_tracing_facility"
 * >Eclipse Wiki</a>.
 * 
 */
@SuppressWarnings("restriction")
public class EclipseUiUtils {

	// ------------------------------------------------------------------------

	public static void openExternalBrowser(URL url) throws PartInitException {

		// System.out.println("Open " + url);
		PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);

	}

	public static void openExternalFile(IPath path) throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// System.out.println("Path " + relativeFile);
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);

		// Open external file. If the file is text, it will be opened
		// in the Eclipse editor, otherwise a system viewer is selected.
		IDE.openEditorOnFileStore(page, fileStore);
	}

	public static void openFileWithInternalEditor(IPath path) throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);

		// Open a non-resource regular file with the Eclipse internal text
		// editor
		IDE.openInternalEditorOnFileStore(page, fileStore);
	}

	// ------------------------------------------------------------------------

	/**
	 * Find the project selected in the Project viewer. This works only if the
	 * project is really selected (i.e. the Project Explorer has the focus, and the
	 * project name is coloured in blue); if the focus is lost, the function returns
	 * null.
	 *
	 * @return the project or null, if not found
	 */
	public static IProject getSelectedProject() {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {

			Object selection = window.getSelectionService().getSelection();
			if ((selection != null) && (selection instanceof IStructuredSelection)
					&& (((IStructuredSelection) selection).size() == 1)) {
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof IAdaptable) {
					IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
					return project;
				}
			}
		}

		return null;
	}

	/**
	 * Find the active page. Used, for example, to check if a part (like a view) is
	 * visible (page.ispartVisible(part)).
	 * <p>
	 * Preferably use getSite().getPage().
	 * 
	 * @return the active page.
	 */
	public static IWorkbenchPage getActivePage() {

		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public static IWorkbenchPage getActivePage(IWorkbenchPart part) {
		return part.getSite().getWorkbenchWindow().getActivePage();
	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	// ------------------------------------------------------------------------

	/**
	 * Helper function to open an error dialog.
	 * 
	 * @param title
	 * @param message
	 * @param e
	 */
	static public void openError(final String title, final String message, final Exception e) {
		UIJob uiJob = new SystemUIJob("open error") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// open error for the exception
				String msg = message;
				if (e != null) {
					msg = "\n" + e.getMessage();
				}

				MessageDialog.openError(getShell(), title, msg); // $NON-NLS-1$
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	public static void clearStatusMessage() {

		// Display.getDefault().syncExec(new Runnable() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					WorkbenchWindow w = (WorkbenchWindow) window;
					w.getStatusLineManager().setErrorMessage("");
				}
			}
		});
	}

	/**
	 * Shows status message in RCP
	 * 
	 * @param message
	 *            message to be displayed
	 * @param isError
	 *            if its an error message or normal message
	 */
	public static void showStatusMessage(final String message) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(message);
		}
		// Display.getDefault().syncExec(new Runnable() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					WorkbenchWindow w = (WorkbenchWindow) window;
					w.getStatusLineManager().setMessage(message);
				}
			}
		});
	}

	public static void showStatusErrorMessage(final String message) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(message);
		}
		Activator.log(message);
		// Display.getDefault().syncExec(new Runnable() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					WorkbenchWindow w = (WorkbenchWindow) window;
					w.getStatusLineManager().setErrorMessage("  " + message);
				}
			}
		});
	}

	// ------------------------------------------------------------------------
}
