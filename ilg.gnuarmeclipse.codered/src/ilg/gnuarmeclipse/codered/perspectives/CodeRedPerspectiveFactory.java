/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.codered.perspectives;

import ilg.gnuarmeclipse.codered.Activator;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.osgi.framework.Bundle;

/**
 * The CodeRed debug perspective is a homage to the team that created the Code
 * Red suite (now defunct, followed by NXP LPCXpresso), and a small help to
 * ensure continuity to its users.
 */
public class CodeRedPerspectiveFactory implements IPerspectiveFactory {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnuarmeclipse.packs.ui.perspectives.CodeRedPerspective";

	// ------------------------------------------------------------------------

	public CodeRedPerspectiveFactory() {

		super();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("CodeRedPerspectiveFactory()");
		}
	}

	// ------------------------------------------------------------------------

	public void createInitialLayout(IPageLayout layout) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("CodeRedPerspectiveFactory.createInitialLayout()");
		}

		createLayout(layout);

		// Action sets are defined in plugin.xml.

		// ShowView menus are defined in plugin.xml.

		// Currently do not associate launchers.
		// setDebugPerspective();

		if (isDebugViewVisble()) {
			showView(IDebugUIConstants.ID_DEBUG_VIEW);
		}
	}

	@SuppressWarnings("unused")
	private void createLayout(IPageLayout layout) {

		Bundle bundle;

		String editorId = layout.getEditorArea();
		IFolderLayout topLeftLayout = layout.createFolder("topLeft", IPageLayout.LEFT, 0.33F, editorId);
		topLeftLayout.addView(IPageLayout.ID_PROJECT_EXPLORER);
		topLeftLayout.addPlaceholder("org.eclipse.cdt.ui.CView");
		topLeftLayout.addPlaceholder(IPageLayout.ID_BOOKMARKS);

		topLeftLayout.addView(IDebugUIConstants.ID_REGISTER_VIEW);
		bundle = Platform.getBundle("ilg.gnuarmeclipse.debug.gdbjtag");
		if (bundle != null) {
			topLeftLayout.addView("ilg.gnuarmeclipse.debug.gdbjtag.ui.views.PeripheralsView");
		}

		IFolderLayout bottomLeftLayout = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.5F, "topLeft");

		// Here the QuickstartView was placed.
		bottomLeftLayout.addView(IDebugUIConstants.ID_VARIABLE_VIEW);
		bottomLeftLayout.addView(IDebugUIConstants.ID_BREAKPOINT_VIEW);
		bottomLeftLayout.addView(IPageLayout.ID_OUTLINE);
		bottomLeftLayout.addPlaceholder(IDebugUIConstants.ID_EXPRESSION_VIEW);

		IPlaceholderFolderLayout topLayout = layout.createPlaceholderFolder("top", 3, 0.175F, editorId);
		topLayout.addPlaceholder(IDebugUIConstants.ID_DEBUG_VIEW);

		IFolderLayout consoleLayout = layout.createFolder("consoleEtc", IPageLayout.BOTTOM, 0.8F, editorId);
		consoleLayout.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		consoleLayout.addView(IPageLayout.ID_PROBLEM_VIEW);
		consoleLayout.addPlaceholder(IPageLayout.ID_PROGRESS_VIEW);
		consoleLayout.addView(IDebugUIConstants.ID_MEMORY_VIEW);

		IFolderLayout bottomLayout = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75F, editorId);

		// TODO: add placeholder for SWV & other tracing, when available.

		IPlaceholderFolderLayout sideRightLayout = layout.createPlaceholderFolder("sideRight", IPageLayout.RIGHT, 0.66F,
				editorId);
		sideRightLayout.addPlaceholder("org.eclipse.cdt.dsf.debug.ui.disassembly.view");

	}

	/**
	 * Check if the Eclipse debug view is visible. Iterate all pages and all
	 * view references until the DebugView is identified.
	 * 
	 * @return true if DebugView is visible.
	 */
	private boolean isDebugViewVisble() {

		boolean bool = false;
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IViewReference[] refs = pages[i].getViewReferences();
			for (int j = 0; j < refs.length; j++) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println(refs[j].getId());
				}
				if ("org.eclipse.debug.ui.DebugView".equals(refs[j].getId())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}

	/**
	 * Make the view identified by ID visible.
	 * 
	 * @param id
	 *            a String with view id.
	 */
	private void showView(final String id) {

		final IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					pages[0].showView(id);
				} catch (Exception e) {
					Activator.log(e);
				}
			}
		});
	}

	private static final String[] launchIds = { "ilg.gnuarmeclipse.debug.gdbjtag.jlink.launchConfigurationType",
			"ilg.gnuarmeclipse.debug.gdbjtag.qemu.launchConfigurationType",
			"ilg.gnuarmeclipse.debug.gdbjtag.openocd.launchConfigurationType" };

	/**
	 * Associate some launchers with this perspective. Currently not used.
	 */
	@SuppressWarnings("unused")
	private void setLaunchPerspective() {

		String[] ids = launchIds;
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType[] types = launchManager.getLaunchConfigurationTypes();
		ArrayList<ILaunchConfigurationType> list = new ArrayList<ILaunchConfigurationType>();
		for (int i = 0; i < types.length; i++) {
			String id = types[i].getIdentifier();
			// System.out.println(id);
			for (int j = 0; j < ids.length; j++)
				if (id.equals(ids[j]))
					list.add(types[i]);
		}
		Iterator<ILaunchConfigurationType> it = list.iterator();
		while (it.hasNext()) {
			ILaunchConfigurationType type = it.next();
			DebugUITools.setLaunchPerspective(type, "debug", ID);
		}
	}

	// ------------------------------------------------------------------------
}
