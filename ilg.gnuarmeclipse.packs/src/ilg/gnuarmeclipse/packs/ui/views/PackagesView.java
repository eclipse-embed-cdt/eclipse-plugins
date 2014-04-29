package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.*;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IAdaptable;

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

public class PackagesView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.PackagesView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action refreshAction;
	// private Action action1;
	// private Action action2;
	private Action doubleClickAction;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;

		public TreeObject(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public TreeParent getParent() {
			return parent;
		}

		public String toString() {
			return getName();
		}

		public Object getAdapter(Class key) {
			return null;
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList children;

		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}

		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}

		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children
					.size()]);
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}
	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {
		private TreeParent invisibleRoot;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null)
					initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			}
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			}
			return new Object[0];
		}

		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent) parent).hasChildren();
			return false;
		}

		/*
		 * We will set up a dummy model to initialize tree heararchy. In a real
		 * code, you will connect to a real model and expose its hierarchy.
		 */
		private void initialize() {
			TreeObject to1 = new TreeObject("1.0.1");
			TreeObject to2 = new TreeObject("1.0.2");
			TreeObject to3 = new TreeObject("1.0.3");
			TreeParent p1 = new TreeParent("STM32F3_DFP");
			p1.addChild(to1);
			p1.addChild(to2);
			p1.addChild(to3);

			TreeObject to4 = new TreeObject("1.0.6");
			TreeParent p2 = new TreeParent("STM32F4_DFP");
			p2.addChild(to4);

			TreeParent root = new TreeParent("Keil");
			root.addChild(p1);
			root.addChild(p2);

			invisibleRoot = new TreeParent("");
			invisibleRoot.addChild(root);
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
			if (obj instanceof TreeParent)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages()
					.getImage(imageKey);
		}
	}

	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				String imageKey;
				if (element instanceof TreeParent) {
					imageKey = ISharedImages.IMG_OBJ_FOLDER;
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(imageKey);
				} else {
					imageKey = ISharedImages.IMG_OBJ_ELEMENT;
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/package_obj.png").createImage();
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return element.toString();
			case 2:
				return element.toString() + " description";
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public PackagesView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		Tree tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeColumn column1 = new TreeColumn(tree, SWT.NONE);	
		//column1.setAlignment(SWT.CENTER);
		column1.setText("  Name");
		column1.setWidth(160);
		
		TreeColumn column3 = new TreeColumn(tree, SWT.NONE);
		column3.setAlignment(SWT.CENTER);
		column3.setText("Status");
		column3.setWidth(50);
		
		TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Description");
		column2.setWidth(400);

		// viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL |
		// SWT.V_SCROLL);
		viewer = new TreeViewer(tree);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		// viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(viewer.getControl(), "ilg.gnuarmeclipse.packs.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PackagesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);

		// manager.add(action1);
		// manager.add(new Separator());
		// manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);

		// manager.add(action1);
		// manager.add(action2);
		// manager.add(new Separator());
		// drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		// manager.add(action2);
		// manager.add(new Separator());
		// drillDownAdapter.addNavigationActions(manager);
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

		// refreshAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
		// getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		// ControlFlowGraphPlugin.getDefault().getImageDescriptor("icons/refresh_view.gif")

		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Packages", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}