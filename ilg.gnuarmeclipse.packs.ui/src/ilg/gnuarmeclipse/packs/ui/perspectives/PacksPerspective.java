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

package ilg.gnuarmeclipse.packs.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

//import org.eclipse.jdt.ui.JavaUI;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class PacksPerspective implements IPerspectiveFactory {

	public static final String ID = "ilg.gnuarmeclipse.packs.ui.perspectives.PacksPerspective";

	private IPageLayout fFactory;

	public PacksPerspective() {

		super();

		// System.out.println("PacksPerspective()");
	}

	public void createInitialLayout(IPageLayout factory) {

		// System.out.println("PacksPerspective.createInitialLayout()");

		fFactory = factory;

		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
	}

	private void addViews() {

		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining
		// EditorArea.

		IFolderLayout bottom = fFactory.createFolder("packsBottom", // NON-NLS-1
				IPageLayout.BOTTOM, 0.75f, fFactory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		// bottom.addView("org.eclipse.team.ui.GenericHistoryView"); //NON-NLS-1
		// bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		IFolderLayout topLeft = fFactory.createFolder("packsNav", // NON-NLS-1
				IPageLayout.LEFT, 0.20f, fFactory.getEditorArea());
		// topLeft.addView(IPageLayout.ID_RES_NAV);
		topLeft.addView("ilg.gnuarmeclipse.packs.ui.views.DevicesView");
		topLeft.addView("ilg.gnuarmeclipse.packs.ui.views.BoardsView");
		topLeft.addView("ilg.gnuarmeclipse.packs.ui.views.KeywordsView");

		IFolderLayout topRight = fFactory.createFolder("packsOutline", // NON-NLS-1
				IPageLayout.RIGHT, 0.66f, fFactory.getEditorArea());
		topRight.addView("ilg.gnuarmeclipse.packs.ui.views.OutlineView");
		// topRight.addView("ilg.gnuarmeclipse.packs.ui.views.PackagesView");

		// Leave 20% for the editor
		fFactory.addView("ilg.gnuarmeclipse.packs.ui.views.PackagesView", IPageLayout.TOP, 0.8f,
				fFactory.getEditorArea());

		// factory.addFastView("org.eclipse.team.ccvs.ui.RepositoriesView",0.50f);
		// //NON-NLS-1
		// factory.addFastView("org.eclipse.team.sync.views.SynchronizeView",
		// 0.50f); //NON-NLS-1
	}

	private void addActionSets() {
		// factory.addActionSet("org.eclipse.debug.ui.launchActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.debugActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.profileActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.team.ui.actionSet"); //NON-NLS-1
		// factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation");
		// //NON-NLS-1
		// factory.addActionSet(JavaUI.ID_ACTION_SET);
		// factory.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		// factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); //NON-NLS-1
	}

	private void addPerspectiveShortcuts() {
		// factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective");
		// //NON-NLS-1
		// factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		// //NON-NLS-1
		// factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
		// //NON-NLS-1
	}

	private void addNewWizardShortcuts() {
		// factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");//NON-NLS-1
		// factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//NON-NLS-1
		// factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//NON-NLS-1
	}

	@SuppressWarnings("deprecation")
	private void addViewShortcuts() {

		fFactory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		// fFactory.addShowViewShortcut(JavaUI.ID_PACKAGES);
		fFactory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		fFactory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		fFactory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}

}
