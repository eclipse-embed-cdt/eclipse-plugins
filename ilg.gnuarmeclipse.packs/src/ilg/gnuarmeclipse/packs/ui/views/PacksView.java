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

package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.IPacksStorageListener;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.PacksStorageEvent;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.jobs.CopyExampleJob;
import ilg.gnuarmeclipse.packs.jobs.InstallJob;
import ilg.gnuarmeclipse.packs.jobs.RemoveJob;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.PackNode;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Selector;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

public class PacksView extends ViewPart implements IPacksStorageListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.PackagesView";

	private static final int AUTOEXPAND_LEVEL = 2;

	// ------------------------------------------------------------------------

	class ViewContentProvider extends AbstractViewContentProvider {

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

	}

	// ------------------------------------------------------------------------

	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object obj, int columnIndex) {

			switch (columnIndex) {
			case 0:
				// String imageKey;
				Leaf node = ((Leaf) obj);
				String type = node.getType();

				if (Type.VENDOR.equals(type)) {
					// imageKey = ISharedImages.IMG_OBJ_FOLDER;
					// return PlatformUI.getWorkbench().getSharedImages()
					// .getImage(imageKey);
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/pack_folder.png")
							.createImage();
				} else if (Type.PACKAGE.equals(type)) {
					if (node.isBooleanProperty(Property.INSTALLED)) {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID, "icons/package_obj.png")
								.createImage();
					} else {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID,
								"icons/package_obj_grey.png").createImage();
					}
				} else if (Type.VERSION.equals(type)) {
					if (node.isBooleanProperty(Property.INSTALLED)) {
						return Activator
								.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
										"icons/jtypeassist_co.png")
								.createImage();
					} else {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID,
								"icons/jtypeassist_co_grey.png").createImage();
					}
				} else if (Type.EXAMPLE.equals(type)) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/binaries_obj.gif")
							.createImage();
				}
			}
			return null;
		}

		public String getColumnText(Object obj, int columnIndex) {

			Leaf node = ((Leaf) obj);

			switch (columnIndex) {
			case 0:
				String name = node.getName();
				if (node.isBooleanProperty(Property.INSTALLED)) {
					name += " (installed)";
				} else {
					if (node.isType(Type.VERSION)) {
						String size = node.getProperty(Property.ARCHIVE_SIZE);
						if (size != null) {
							try {
								int n = Integer.parseInt(size);
								if (n <= 0) {
									name += " (n/a)";
								} else {
									name += " (" + Utils.convertSizeToString(n)
											+ ")";
								}
							} catch (NumberFormatException e) {
								;
							}
						}
					}
				}
				return " " + name;
			case 1:
				return " " + node.getDescription();
			}
			return null;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}

	// ------------------------------------------------------------------------

	class NameSorter extends ViewerSorter {

		@SuppressWarnings("unchecked")
		public int compare(Viewer viewer, Object e1, Object e2) {

			Leaf n1 = (Leaf) e1;
			String name1 = n1.getName();
			String name2 = ((Leaf) e2).getName();

			if (n1.isType(Type.VERSION)) {
				// Reverse the order for versions
				return getComparator().compare(name2, name1);
			} else {
				return getComparator().compare(name1, name2);
			}
		}
	}

	// ------------------------------------------------------------------------

	private TreeViewer m_viewer;
	private ISelectionListener m_pageSelectionListener;
	private ViewContentProvider m_contentProvider;

	private Action m_refreshAction;
	private Action m_installAction;
	private Action m_removeAction;
	private Action m_copyExampleAction;
	private Action m_expandAll;
	private Action m_collapseAll;

	private PacksFilter m_packsFilter;
	private ViewerFilter[] m_packsFilters;
	private boolean m_isInstallEnabled;
	private boolean m_isRemoveEnabled;
	private boolean m_isCopyExampleEnabled;

	private PacksStorage m_storage;
	private MessageConsoleStream m_out;

	public PacksView() {

		m_out = Activator.getConsoleOut();

		m_storage = PacksStorage.getInstance();
		System.out.println("PacksView()");
	}

	public TreeViewer getTreeViewer() {
		return m_viewer;
	}

	public void createPartControl(Composite parent) {

		System.out.println("PacksView.createPartControl()");

		m_packsFilter = new PacksFilter();
		m_packsFilters = new PacksFilter[] { m_packsFilter };

		Tree tree = new Tree(parent, SWT.BORDER | SWT.MULTI
				| SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// You can only add the Layout to a container whose only child is the
		// Tree control you want the Layout applied to. Don't assign the layout
		// directly the Tree

		TreeColumnLayout layout = new TreeColumnLayout();
		parent.setLayout(layout);

		TreeColumn nameColumn = new TreeColumn(tree, SWT.NONE);
		nameColumn.setText("  Name");
		// nameColumn.setWidth(200);
		layout.setColumnData(nameColumn, new ColumnPixelData(200));

		TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
		descriptionColumn.setAlignment(SWT.LEFT);
		descriptionColumn.setText(" Description");
		// descriptionColumn.setWidth(450);
		layout.setColumnData(descriptionColumn, new ColumnPixelData(450));

		m_viewer = new TreeViewer(tree);

		m_contentProvider = new ViewContentProvider();

		// Register this view to the packs storage notifications
		m_storage.addListener(this);

		m_viewer.setContentProvider(m_contentProvider);
		m_viewer.setLabelProvider(new TableLabelProvider());
		m_viewer.setSorter(new NameSorter());

		m_viewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
		m_viewer.setInput(getPacksTree());

		addProviders();
		addListners();
		hookPageSelection();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void dispose() {

		super.dispose();

		if (m_pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(
					m_pageSelectionListener);
		}

		System.out.println("PacksView.dispose()");
	}

	private void addProviders() {

		// Register this viewer as a selection provider
		getSite().setSelectionProvider(m_viewer);
	}

	private void addListners() {

		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();

				updateButtonsEnableStatus(selection);

				// System.out.println("Packs Selected: " + selection.toList());
			}
		});
	}

	private void hookPageSelection() {

		m_pageSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {

				if ((part instanceof DevicesView)
						|| (part instanceof BoardsView)
						|| (part instanceof KeywordsView)) {
					friendViewSelectionChanged(part, selection);
				}
			}
		};
		getSite().getPage().addPostSelectionListener(m_pageSelectionListener);
	}

	// Called when selection in the _part_View change
	protected void friendViewSelectionChanged(IWorkbenchPart part,
			ISelection selection) {

		if (selection.isEmpty()) {

			// System.out.println("Packs: resetFilters()");
			m_viewer.resetFilters();

			return;
		}

		System.out.println("Packs: " + part + " selection=" + selection);

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		String conditionType = "";
		if (part instanceof DevicesView) {
			conditionType = Selector.DEVICEFAMILY_TYPE;
		} else if (part instanceof BoardsView) {
			conditionType = Selector.BOARD_TYPE;
		} else if (part instanceof KeywordsView) {
			conditionType = Selector.KEYWORD_TYPE;
		}

		m_packsFilter.setSelection(conditionType, structuredSelection);

		m_viewer.setFilters(m_packsFilters);

		m_viewer.setSelection(null);
	}

	public void updateButtonsEnableStatus(IStructuredSelection selection) {

		if (selection == null || selection.isEmpty()) {
			// System.out.println("Empty Selection");
			return;
		}

		if (((Leaf) selection.getFirstElement()).isType(Type.NONE)) {
			return;
		}

		m_isInstallEnabled = false;
		m_isRemoveEnabled = false;
		m_isCopyExampleEnabled = false;

		for (Object obj : selection.toArray()) {
			Leaf node = (Leaf) obj;
			String type = node.getType();

			boolean isInstalled = false;
			if (node.isBooleanProperty(Property.INSTALLED)) {
				isInstalled = true;
			}

			// Check if the selection contain any package or
			// version not installed
			if (Type.PACKAGE.equals(type)) {
				if (!isInstalled) {
					m_isInstallEnabled = true;
				}
			}
			if (Type.VERSION.equals(type)) {
				int size = 0;
				try {
					size = Integer.valueOf(node.getProperty(
							Property.ARCHIVE_SIZE, "0"));
				} catch (NumberFormatException e) {
					;
				}
				if (!isInstalled && size > 0) {
					m_isInstallEnabled = true;
				}
				if (isInstalled) {
					m_isRemoveEnabled = true;
				}
			}
			if ((Type.EXAMPLE.equals(type))) {
				m_isCopyExampleEnabled = true;
			}
		}
		m_installAction.setEnabled(m_isInstallEnabled);
		m_removeAction.setEnabled(m_isRemoveEnabled);
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
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
		manager.add(new Separator());
		manager.add(m_installAction);
		manager.add(m_removeAction);
		manager.add(new Separator());
		manager.add(m_refreshAction);

		// manager.add(action1);
		// manager.add(new Separator());
		// manager.add(action2);
	}

	// Right click actions
	private void fillContextMenu(IMenuManager manager) {

		if (m_isInstallEnabled) {
			manager.add(m_installAction);
		}

		if (m_isRemoveEnabled) {
			manager.add(m_removeAction);
		}

		if (m_isCopyExampleEnabled) {
			manager.add(m_copyExampleAction);
		}

		// manager.add(new Separator());

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	// Top tool bar buttons
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
		manager.add(new Separator());
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

		// -----
		m_copyExampleAction = new Action() {
			public void run() {
				System.out.println("m_copyAction.run();");

				TreeSelection selection = (TreeSelection) m_viewer
						.getSelection();
				System.out.println(selection);

				CopyExampleJob job = new CopyExampleJob("Copy example",
						selection);
				job.schedule();
			}
		};
		m_copyExampleAction.setText("Copy to folder");

		// -----
		m_expandAll = new Action() {
			public void run() {
				m_viewer.expandAll();
			}
		};

		m_expandAll.setText("Expand all");
		m_expandAll.setToolTipText("Expand all children nodes");
		m_expandAll.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/expandall.png"));

		m_collapseAll = new Action() {
			public void run() {
				m_viewer.collapseAll();
			}
		};

		m_collapseAll.setText("Collapse all");
		m_collapseAll.setToolTipText("Collapse all children nodes");
		m_collapseAll.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/collapseall.png"));

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

	public void refresh() {
		m_viewer.refresh();
	}

	public void refresh(Object obj) {

		if (obj instanceof Collection<?>) {
			for (Object node : (Collection<?>) obj) {
				m_viewer.refresh(node);
			}
		} else {
			m_viewer.refresh(obj);
		}

		// Setting the selection will force the outline update
		m_viewer.setSelection(m_viewer.getSelection());

		// Return focus to this view
		setFocus();

		System.out.println("PacksView.refresh() " + obj);
	}

	public void update(Object obj) {

		if (obj instanceof Collection<?>) {
			for (Object node : (Collection<?>) obj) {
				m_viewer.update(node, null);
			}
		} else {
			m_viewer.update(obj, null);
		}
		System.out.println("PacksView.updated() " + obj);
	}

	public String toString() {
		return "PacksView";
	}

	// ------------------------------------------------------------------------

	// Warning, this code runs on the notifier thread, hopefully will not
	// interfere with GUI actions.

	@Override
	public void packsChanged(PacksStorageEvent event) {

		String type = event.getType();
		System.out.println("PacksView.packsChanged(), type=\"" + type + "\".");

		if (PacksStorageEvent.Type.REFRESH.equals(type)) {

			// Run the refresh on the GUI thread
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// ((TreeViewer) m_viewer)
					// .setAutoExpandLevel(AUTOEXPAND_LEVEL);
					// m_viewer.setInput(getPacksTree());
					m_viewer.refresh();
				}
			});

		} else if (PacksStorageEvent.Type.UPDATE_VERSIONS.equals(type)) {

			@SuppressWarnings("unchecked")
			final List<PackNode> updatedList = (List<PackNode>) event
					.getPayload();

			final Map<String, Node> parentsMap = new HashMap<String, Node>();
			for (PackNode versionNode : updatedList) {
				String vendorName = versionNode
						.getProperty(Property.VENDOR_NAME);
				String packName = versionNode.getProperty(Property.PACK_NAME);
				String versionName = versionNode
						.getProperty(Property.VERSION_NAME);

				Node modelNode = m_storage.getPackVersion(vendorName, packName,
						versionName);
				updateVersioNode(versionNode, modelNode);

				String key = m_storage.makeMapKey(vendorName, packName);

				Node parent = versionNode.getParent();
				if (!parentsMap.containsKey(key)) {
					parentsMap.put(key, parent);
				}
			}

			for (Node packNode : parentsMap.values()) {

				// Compute if the parent has any installed child
				boolean hasInstalledChildren = false;
				for (Leaf child : packNode.getChildren()) {
					if (child.isBooleanProperty(Property.INSTALLED)) {
						hasInstalledChildren = true;
						break;
					}
				}

				packNode.setBooleanProperty(Property.INSTALLED,
						hasInstalledChildren);
			}

			// Run the refresh on the GUI thread
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// Refresh pack node, this will update all version
					// and examples below them
					refresh(parentsMap.values());
				}
			});
		}
	}

	// ------------------------------------------------------------------------

	// Get view data from storage.
	// Return a hierarchy of vendor/packages/versions/examples nodes.
	private Node getPacksTree() {

		Node packsTree = m_storage.getPacksTree();
		Node packsRoot = new Node(Type.ROOT);
		packsRoot.setName("Packs");

		if (packsTree.hasChildren()) {

			m_out.println();
			m_out.println(Utils.getCurrentDateTime());
			m_out.println("Collecting packs...");

			int count = getPacksRecursive(packsTree, null, packsRoot);

			m_out.println("Found " + count + " package version(s), from "
					+ packsRoot.getChildren().size() + " vendor(s).");
		}

		return packsRoot;
	}

	// Identify outline & external nodes and collect devices from inside.
	private int getPacksRecursive(Leaf modelNode, PackNode parentPackNode,
			Node root) {

		int count = 0;

		if (modelNode.isType(Type.PACKAGE)) {
			parentPackNode = (PackNode) modelNode;
		}

		if (modelNode.isType(Type.VERSION)) {

			count += addVersion((PackNode) modelNode, parentPackNode, root);

		} else if (modelNode instanceof Node && modelNode.hasChildren()) {

			for (Leaf child : ((Node) modelNode).getChildren()) {

				// Recurse down.
				count += getPacksRecursive(child, parentPackNode, root);
			}
		}

		return count;
	}

	private int addVersion(PackNode modelNode, PackNode parentPackNode,
			Node tree) {

		int count = 0;
		String vendorName = modelNode.getProperty(Property.VENDOR_NAME);
		String packName = modelNode.getProperty(Property.PACK_NAME);
		String versionName = modelNode.getProperty(Property.VERSION_NAME);
		String description = modelNode.getDescription();

		Node vendorNode = Node.addUniqueChild(tree, Type.VENDOR, vendorName);

		PackNode packNode = PackNode.addUniqueChild(vendorNode, Type.PACKAGE,
				packName);
		// Copy properties like INSTALLED.
		packNode.copyPropertiesRef(parentPackNode);
		packNode.putProperty(Property.VENDOR_NAME, vendorName);

		// Copy selectors.
		packNode.copySelectorsRef(parentPackNode);

		if (parentPackNode != null) {
			packNode.setDescription(parentPackNode.getDescription());
		}

		PackNode versionNode = PackNode.addUniqueChild(packNode, Type.VERSION,
				versionName);

		versionNode.setDescription(description);

		// Pass a reference to original model properties to the view node.
		versionNode.copyPropertiesRef(modelNode);

		updateVersioNode(versionNode, modelNode);

		// To save space, the brief or full outlines are not prepared
		// for all nodes, but only for nodes needed by selections.

		// If there will be error conditions preventing this to work,
		// the count should not be incremented.
		count++;

		return count;
	}

	private void updateVersioNode(PackNode versionNode, Node modelNode) {

		if (versionNode.isBooleanProperty(Property.INSTALLED)) {

			assert (modelNode != null);

			// For the installed nodes, add examples as children.
			Node outlineNode = (Node) modelNode.getChild(Type.OUTLINE);
			if (outlineNode != null && outlineNode.hasChildren()) {

				for (Leaf child : outlineNode.getChildren()) {

					if (child.isType(Type.EXAMPLE)) {

						// New node with child personality
						// (must be PackNode to accommodate outline).
						PackNode.addNewChild(versionNode, child);
					}
				}
			}
		} else {

			// For the removed nodes, remove all children.
			versionNode.removeChildren();
		}

		// Clear outline.
		versionNode.setOutline(null);
	}

	// ------------------------------------------------------------------------

}