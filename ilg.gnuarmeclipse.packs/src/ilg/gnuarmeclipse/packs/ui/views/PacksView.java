package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.UsingDefaultFileException;
import ilg.gnuarmeclipse.packs.jobs.InstallJob;
import ilg.gnuarmeclipse.packs.jobs.RemoveJob;

import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

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

public class PacksView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.PackagesView";

	private TreeViewer m_viewer;

	private Action m_refreshAction;
	private Action m_installAction;
	private Action m_removeAction;
	private ViewContentProvider m_contentProvider;

	private static final int AUTOEXPAND_LEVEL = 2;
	
	public TreeViewer getTreeViewer() {
		return m_viewer;
	}

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {

		private TreeNode m_tree;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (m_tree == null) {
					System.out.println("getCachedSubTree(packages)");
					try {
						m_tree = PacksStorage.getCachedSubTree("packages");
					} catch (UsingDefaultFileException e) {
						Activator.log(e.getMessage());
					} catch (Exception e) {
						Activator.log(e);
					}
				}

				if (m_tree == null) {
					m_tree = new TreeNode(TreeNode.NONE_TYPE);
					return new Object[] { m_tree };
				}
				return getChildren(m_tree);
			}
			return getChildren(parent);
		}

		public Object getParent(Object child) {
			return ((TreeNode) child).getParent();
		}

		public Object[] getChildren(Object parent) {
			return ((TreeNode) parent).getChildrenArray();
		}

		public boolean hasChildren(Object parent) {
			return ((TreeNode) parent).hasChildren();
		}

		public void forceRefresh() {
			// System.out.println("forceRefresh()");
			m_tree = null;
		}
	}

	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object obj, int columnIndex) {

			switch (columnIndex) {
			case 0:
				String imageKey;
				TreeNode node = ((TreeNode) obj);
				String type = node.getType();

				if (TreeNode.VENDOR_TYPE.equals(type)) {
					imageKey = ISharedImages.IMG_OBJ_FOLDER;
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(imageKey);
				} else if ("package".equals(type)) {
					if (node.isInstalled()) {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID, "icons/package_obj.png")
								.createImage();
					} else {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID,
								"icons/package_obj_grey.png").createImage();
					}
				} else if ("version".equals(type)) {
					if (node.isInstalled()) {
						return Activator
								.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
										"icons/jtypeassist_co.png")
								.createImage();
					} else {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID,
								"icons/jtypeassist_co_grey.png").createImage();
					}
				}
			}
			return null;
		}

		public String getColumnText(Object obj, int columnIndex) {

			TreeNode node = ((TreeNode) obj);

			switch (columnIndex) {
			case 0:
				String name = node.getName();
				if (node.isInstalled()) {
					name += " (installed)";
				}
				return " " + name;
			case 1:
				return " " + node.getDescription();
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

		@SuppressWarnings("unchecked")
		public int compare(Viewer viewer, Object e1, Object e2) {

			TreeNode n1 = (TreeNode) e1;
			String name1 = n1.getName();
			String name2 = ((TreeNode) e2).getName();

			if ("version".equals(n1.getType())) {
				// Reverse the order for versions
				return getComparator().compare(name2, name1);
			} else {
				return getComparator().compare(name1, name2);
			}
		}

	}

	/**
	 * The constructor.
	 */
	public PacksView() {
		Activator.setPacksView(this);

		System.out.println("PacksView()");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		System.out.println("PacksView.createPartControl()");

		Tree tree = new Tree(parent, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeColumn nameColumn = new TreeColumn(tree, SWT.NONE);
		nameColumn.setText("  Name");
		nameColumn.setWidth(200);

		TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
		descriptionColumn.setAlignment(SWT.LEFT);
		descriptionColumn.setText(" Description");
		descriptionColumn.setWidth(450);

		m_viewer = new TreeViewer(tree);

		m_contentProvider = new ViewContentProvider();
		m_viewer.setContentProvider(m_contentProvider);
		m_viewer.setLabelProvider(new TableLabelProvider());
		m_viewer.setSorter(new NameSorter());
		m_viewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
		m_viewer.setInput(getViewSite());

		addProviders();
		addListners();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		// Cleared by the parent dispose()
		// addPropertyListener(propertyListener);
	}

	public void dispose() {
		super.dispose();
		System.out.println("PacksView.dispose()");
	}

	private void addProviders() {
		// Register this viewer as the selection provider
		getSite().setSelectionProvider(m_viewer);
	}

	private void addListners() {

		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				updateButtonsEnableStatus(selection);

				System.out.println("Packs Selected: " + selection.toList());
			}
		});
	}

	public void updateButtonsEnableStatus(IStructuredSelection selection) {

		if (selection == null || selection.isEmpty()) {
			System.out.println("Empty Selection");
			// Activator.getPacksView().getTreeViewer().resetFilters();
			return;
		}

		if (TreeNode.NONE_TYPE.equals(((TreeNode) selection.getFirstElement())
				.getType())) {
			return;
		}

		boolean doEnableInstall = false;
		boolean doEnableRemove = false;
		for (Object obj : selection.toArray()) {
			TreeNode node = (TreeNode) obj;
			String type = node.getType();

			// Check if the selection contain any package or
			// version not installed
			if (("package".equals(type) || "version".equals(type))
					&& !node.isInstalled()) {
				doEnableInstall = true;
			}
			if (("version".equals(type)) && node.isInstalled()) {
				doEnableRemove = true;
			}
		}
		m_installAction.setEnabled(doEnableInstall);
		m_removeAction.setEnabled(doEnableRemove);

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PacksView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(m_viewer.getControl());
		m_viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, m_viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	// Top down arrow
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(m_refreshAction);
		manager.add(new Separator());
		manager.add(m_installAction);
		manager.add(m_removeAction);

		// manager.add(action1);
		// manager.add(new Separator());
		// manager.add(action2);
	}

	// Right click actions
	private void fillContextMenu(IMenuManager manager) {
		manager.add(m_installAction);
		manager.add(m_removeAction);

		// manager.add(new Separator());

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	// Top tool bar buttons
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_installAction);
		manager.add(m_removeAction);
		manager.add(new Separator());
		manager.add(m_refreshAction);
	}

	private void makeActions() {

		m_refreshAction = new Action() {
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
		m_refreshAction.setText("Refresh");
		m_refreshAction
				.setToolTipText("Read packages descriptions from all sites");
		m_refreshAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/refresh_nav.gif"));

		// -----
		m_installAction = new Action() {
			public void run() {
				System.out.println("m_installAction.run();");

				TreeSelection selection = (TreeSelection) m_viewer
						.getSelection();
				System.out.println(selection);

				InstallJob job = new InstallJob("Install Packs", selection);
				job.schedule();
			}
		};
		m_installAction.setText("Install");
		m_installAction
				.setToolTipText("Install a local copy of the selected package(s)");
		m_installAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/package_mode.png"));
		m_installAction.setEnabled(false);

		// -----
		m_removeAction = new Action() {
			public void run() {
				System.out.println("m_removeAction.run();");

				TreeSelection selection = (TreeSelection) m_viewer
						.getSelection();
				System.out.println(selection);

				RemoveJob job = new RemoveJob("Remove Packs", selection);
				job.schedule();
			}
		};
		m_removeAction.setText("Remove");
		m_removeAction
				.setToolTipText("Remove the local copy of the selected package version(s)");
		m_removeAction.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/removeall.png"));
		m_removeAction.setEnabled(false);
	}

	private void hookDoubleClickAction() {
		// m_viewer.addDoubleClickListener(new IDoubleClickListener() {
		// public void doubleClick(DoubleClickEvent event) {
		// doubleClickAction.run();
		// }
		// });
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		m_viewer.getControl().setFocus();
	}

	public void forceRefresh() {
		m_contentProvider.forceRefresh();

		m_viewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
		m_viewer.setInput(getViewSite());
		System.out.println("PacksView.forceRefresh()");
	}

	public void update(Object obj) {

		if (obj instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<TreeNode> list = (List<TreeNode>) obj;
			for (Object node : list) {
				m_viewer.update(node, null);
			}
		} else {
			m_viewer.update(obj, null);
		}
		System.out.println("PacksView.updated()");
	}

}