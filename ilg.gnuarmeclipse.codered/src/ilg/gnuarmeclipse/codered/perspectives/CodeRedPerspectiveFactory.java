/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.codered.perspectives;

import ilg.gnuarmeclipse.codered.Activator;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class CodeRedPerspectiveFactory implements IPerspectiveFactory {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnuarmeclipse.packs.ui.perspectives.CodeRedPerspective";

	// ------------------------------------------------------------------------

	private ILaunchesListener2 fLaunchesListener;

	@SuppressWarnings("unused")
	private IPageLayout fLayout;

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
			System.out
					.println("CodeRedPerspectiveFactory.createInitialLayout()");
		}

		fLayout = layout;

		createLayout(layout);

		// setDebugPerspective();
		// addLaunchesListener();
	}

	@SuppressWarnings("unused")
	private void createLayout(IPageLayout layout) {

		boolean bool = isDebugViewVisble();
		Bundle bundle;

		String editorId = layout.getEditorArea();
		IFolderLayout topLeftLayout = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.33F, editorId);
		topLeftLayout.addView("org.eclipse.ui.navigator.ProjectExplorer");
		topLeftLayout.addPlaceholder("org.eclipse.cdt.ui.CView");
		topLeftLayout.addPlaceholder("org.eclipse.ui.views.BookmarkView");

		topLeftLayout.addView("org.eclipse.debug.ui.RegisterView");
		bundle = Platform.getBundle("ilg.gnuarmeclipse.debug.gdbjtag");
		if (bundle != null) {
			topLeftLayout
					.addView("ilg.gnuarmeclipse.debug.gdbjtag.ui.views.PeripheralsView");
		}

		// bundle = Platform.getBundle("com.crt.trueview");
		// if (bundle != null) {
		// topLeftLayout.addView("com.crt.trueview.views.CRTRegisterViewer");
		// topLeftLayout.addView("com.crt.trueview.views.PeripheralsViewer");
		// }
		// bundle = Platform.getBundle("com.crt.dsfdebug");
		// if (bundle != null) {
		// topLeftLayout.addView("com.crt.peripheral.PeripheralView");
		// topLeftLayout.addView("org.eclipse.debug.ui.RegisterView");
		// }

		IFolderLayout bottomLeftLayout = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.5F, "topLeft");
		// localIFolderLayout2.addView("com.crt.quickstart.views.QuickstartView");
		bottomLeftLayout.addView("org.eclipse.debug.ui.VariableView");
		bottomLeftLayout.addView("org.eclipse.debug.ui.BreakpointView");
		bottomLeftLayout.addView("org.eclipse.ui.views.ContentOutline");
		bottomLeftLayout.addPlaceholder("org.eclipse.debug.ui.ExpressionView");

		IPlaceholderFolderLayout topLayout = layout.createPlaceholderFolder(
				"top", 3, 0.175F, editorId);
		topLayout.addPlaceholder("org.eclipse.debug.ui.DebugView");

		IFolderLayout consoleLayout = layout.createFolder("consoleEtc",
				IPageLayout.BOTTOM, 0.8F, editorId);
		consoleLayout.addView("org.eclipse.ui.console.ConsoleView");
		consoleLayout.addView("org.eclipse.ui.views.ProblemView");
		consoleLayout.addPlaceholder("org.eclipse.ui.views.ProgressView");
		consoleLayout.addView("org.eclipse.debug.ui.MemoryView");

		IFolderLayout bottomLayout = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.75F, editorId);
		// bundle = Platform.getBundle("com.crt.trueview");
		// if (bundle != null) {
		// bottomLayout
		// .addPlaceholder("com.crt.trueview.views.DisassemblyViewer");
		// }
		// bundle = Platform.getBundle("com.crt.dsfswv");
		// if (bundle != null)
		// bottomLayout.addPlaceholder("com.crt.dsfswv.views.SwvViews");
		// bundle = Platform.getBundle("com.crt.redtracepreview");
		// if (bundle != null)
		// bottomLayout
		// .addPlaceholder("com.crt.redtracepreview.views.Preview");

		layout.addActionSet("org.eclipse.cdt.ui.buildConfigActionSet");

		if (bool) {
			displayView("org.eclipse.debug.ui.DebugView");
		}
		layout.addActionSet("org.eclipse.debug.ui.debugActionSet");

		IPlaceholderFolderLayout sideRightLayout = layout
				.createPlaceholderFolder("sideRight", IPageLayout.RIGHT, 0.66F,
						editorId);
		sideRightLayout
				.addPlaceholder("org.eclipse.cdt.dsf.debug.ui.disassembly.view");
	}

	private boolean isDebugViewVisble() {

		boolean bool = false;
		IWorkbenchPage[] pages = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();
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

	private void displayView(final String id) {

		final IWorkbenchPage[] pages = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();

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

	private static final String[] launchIds = {
			"ilg.gnuarmeclipse.debug.gdbjtag.jlink.launchConfigurationType",
			"ilg.gnuarmeclipse.debug.gdbjtag.qemu.launchConfigurationType",
			"ilg.gnuarmeclipse.debug.gdbjtag.openocd.launchConfigurationType" };

	/**
	 * Associate some launcher with this perspective. Currently not used.
	 */
	@SuppressWarnings("unused")
	private void setDebugPerspective() {

		String[] ids = launchIds;
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType[] types = launchManager
				.getLaunchConfigurationTypes();
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

	/**
	 * Currently not used.
	 */
	@SuppressWarnings("unused")
	private void addLaunchesListener() {

		int i = 1;
		if (i != 0) {
			this.fLaunchesListener = new ILaunchesListener2() {
				public void launchesAdded(ILaunch[] launches) {
					traceIt("added", launches);
				}

				public void launchesChanged(ILaunch[] launches) {
					traceIt("changed", launches);
				}

				public void launchesRemoved(ILaunch[] launches) {
					launchesTerminated(launches);
				}

				public void launchesTerminated(ILaunch[] launches) {
					traceIt("terminated", launches);
				}

				private void traceIt(String actionName, ILaunch[] launches) {
					for (int i = 0; i < launches.length; i++) {
						IDebugTarget[] targets = launches[i].getDebugTargets();
						for (int j = 0; j < targets.length; j++)
							try {
								System.out.println(actionName + ">>"
										+ targets[j].getName());
							} catch (DebugException e) {
								;
							}
					}
				}
			};
			DebugPlugin.getDefault().getLaunchManager()
					.addLaunchListener(fLaunchesListener);
		}
	}

	// ------------------------------------------------------------------------
}
