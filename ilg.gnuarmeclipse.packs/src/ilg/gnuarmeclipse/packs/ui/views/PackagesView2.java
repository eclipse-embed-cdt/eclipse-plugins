package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.*;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class PackagesView2 extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.PackagesView2";

	private Action refreshAction;
	private Tree tree;

	/**
	 * The constructor.
	 */
	public PackagesView2() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		tree = new Tree(parent, SWT.CHECK);
		tree.setHeaderVisible(true);

		TreeColumn column1 = new TreeColumn(tree, SWT.NONE);
		tree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("");
		column1.setWidth(160);
		TreeColumn column3 = new TreeColumn(tree, SWT.NONE);
		column3.setAlignment(SWT.LEFT);
		column3.setText("Status");
		column3.setWidth(50);
		TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Description");
		column2.setWidth(400);

		for (int i = 1; i < 4; i++) {
			TreeItem grandParent = new TreeItem(tree, 0);
			grandParent.setText("Grand Parent - " + i);
			for (int j = 1; j < 4; j++) {
				TreeItem parentNode = new TreeItem(grandParent, 0);
				parentNode.setText("Parent - " + j);
				for (int k = 1; k < 5; k++) {
					TreeItem child = new TreeItem(parentNode, 0);
					child.setText("Child - " + k);
				}
			}
		}
		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] selected = tree.getSelection();
				if (selected.length > 0) {
					System.out.println("Selected: " + selected[0].getText());
				}
			}
		});

		makeActions();
		// hookContextMenu();
		contributeToActionBars();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	// Below the top right down arrow
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
	}

	// The top right button
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
	}

	private void makeActions() {

		refreshAction = new Action() {
			public void run() {
				// Obtain IServiceLocator implementer, e.g. from
				// PlatformUI.getWorkbench():
				// IServiceLocator serviceLocator = PlatformUI.getWorkbench();
				// or a site from within a editor or view:
				IServiceLocator serviceLocator = getSite();

				ICommandService commandService = (ICommandService) serviceLocator
						.getService(ICommandService.class);

				try {
					// Lookup commmand with its ID
					Command command = commandService
							.getCommand("ilg.gnuarmeclipse.packs.commands.refreshCommand");

					// Optionally pass a ExecutionEvent instance, default
					// no-param arg creates blank event
					command.executeWithChecks(new ExecutionEvent());

				} catch (Exception e) {

					Activator.log(e);
				}
			}
		};
		refreshAction.setText("Refresh");
		refreshAction
				.setToolTipText("Read packages descriptions from all sites.");
		refreshAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/refresh_nav.gif"));

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// viewer.getControl().setFocus();
	}

}