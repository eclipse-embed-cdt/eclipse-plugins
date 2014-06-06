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
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;

public class BoardsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.BoardsView";

	// ------------------------------------------------------------------------

	class ViewContentProvider extends AbstractViewContentProvider implements
			IPacksStorageListener {

		public Object[] getElements(Object parent) {

			if (parent.equals(getViewSite())) {
				m_tree = getBoardsTree();
				return getChildren(m_tree);
			}
			return getChildren(parent);
		}

		@Override
		public void packsChanged(PacksStorageEvent event) {

			String type = event.getType();
			System.out.println("BoardsView.packsChanged(), type=\"" + type
					+ "\".");

			if (PacksStorageEvent.Type.REFRESH.equals(type)) {
				m_viewer.refresh();
			}
		}
	}

	// ------------------------------------------------------------------------

	class ViewLabelProvider extends CellLabelProvider {

		public String getText(Object obj) {
			return " " + ((Leaf) obj).getName();
		}

		public Image getImage(Object obj) {

			Leaf node = ((Leaf) obj);
			String type = node.getType();

			if (Type.VENDOR.equals(type)) {
				String imageKey = ISharedImages.IMG_OBJ_FOLDER;
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(imageKey);
			} else if (Type.BOARD.equals(type)) {
				if (node.isBooleanProperty(Property.ENABLED)) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/board.png")
							.createImage();
				} else {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/board_grey.png")
							.createImage();
				}
			} else {
				return null;
			}
		}

		@Override
		public String getToolTipText(Object obj) {

			Leaf node = ((Leaf) obj);
			String type = node.getType();

			if (Type.VENDOR.equals(type)) {
				return "Vendor";
			} else if (Type.BOARD.equals(type)) {
				String description = node.getDescription();
				if (description != null && description.length() > 0) {
					return description;
				}
			}
			return null;
		}

		@Override
		public void update(ViewerCell cell) {
			cell.setText(getText(cell.getElement()));
			cell.setImage(getImage(cell.getElement()));
		}
	}

	// ------------------------------------------------------------------------

	class NameSorter extends ViewerSorter {
	}

	// ------------------------------------------------------------------------

	private TreeViewer m_viewer;
	private Action m_removeFilters;
	private Action m_expandAll;
	private Action m_collapseAll;

	private ViewContentProvider m_contentProvider;

	private PacksStorage m_storage;
	private MessageConsoleStream m_out;

	public BoardsView() {

		Activator.setBoardsView(this);

		m_out = Activator.getConsoleOut();

		m_storage = PacksStorage.getInstance();
		System.out.println("BoardsView()");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		System.out.println("BoardsView.createPartControl()");

		m_viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);

		ColumnViewerToolTipSupport.enableFor(m_viewer);

		m_contentProvider = new ViewContentProvider();

		// Register this content provider to the packs storage notifications
		m_storage.addListener(m_contentProvider);

		m_viewer.setContentProvider(m_contentProvider);
		m_viewer.setLabelProvider(new ViewLabelProvider());
		m_viewer.setSorter(new NameSorter());

		m_viewer.setInput(getViewSite());

		addProviders();
		addListners();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void dispose() {

		super.dispose();
		System.out.println("BoardsView.dispose()");
	}

	private void addProviders() {

		// Register this viewer as the selection provider
		getSite().setSelectionProvider(m_viewer);
	}

	private void addListners() {
		// None
	}

	private void hookContextMenu() {

		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				BoardsView.this.fillContextMenu(manager);
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

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
		manager.add(new Separator());
		manager.add(m_removeFilters);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(m_removeFilters);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
		manager.add(new Separator());
		manager.add(m_removeFilters);
	}

	private void makeActions() {

		m_removeFilters = new Action() {
			public void run() {
				// Empty selection
				m_viewer.setSelection(null);
			}
		};

		m_removeFilters.setText("Remove filters");
		m_removeFilters
				.setToolTipText("Remove all filters based on selections");
		m_removeFilters.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/removeall.png"));

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
		// None
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		m_viewer.getControl().setFocus();
	}

	public void forceRefresh() {

		m_contentProvider.forceRefresh();

		// m_viewer.refresh();
		m_viewer.setInput(getViewSite());

		System.out.println("BoardsView.forceRefresh()");
	}

	public void update(Object obj) {

		if (obj instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<Node> list = (List<Node>) obj;
			for (Object node : list) {
				m_viewer.update(node, null);
			}
		} else {
			m_viewer.update(obj, null);
		}
		System.out.println("BoardsView.updated()");
	}

	public String toString() {
		return "BoardsView";
	}

	// ------------------------------------------------------------------------

	// Get view data from storage.
	// Return a two level hierarchy of vendor and device nodes.
	private Node getBoardsTree() {

		Node packsTree = m_storage.getPacksTree();
		Node boardsRoot = new Node(Type.ROOT);

		if (packsTree.hasChildren()) {

			m_out.println();
			m_out.println(Utils.getCurrentDateTime());
			m_out.println("Collecting boards...");

			int count = getBoardsRecursive(packsTree, boardsRoot, false);

			m_out.println("Found " + count + " board(s), from "
					+ boardsRoot.getChildren().size() + " vendor(s).");
		}

		return boardsRoot;
	}

	// Identify outline & external nodes and collect boards from inside
	private int getBoardsRecursive(Leaf node, Node root, boolean isInstalled) {

		int count = 0;
		String type = node.getType();
		if (Type.OUTLINE.equals(type) || Type.EXTERNAL.equals(type)) {
			for (Leaf child : node.getChildrenArray()) {
				String childType = child.getType();
				if (Type.BOARD.equals(childType)) {

					// Collect unique keywords
					count += addBoard(child, root, isInstalled);
				}
			}
		} else if (node instanceof Node && node.hasChildren()) {

			boolean isVersionInstalled = isInstalled;
			if (Type.VERSION.equals(type)
					&& node.isBooleanProperty(Property.INSTALLED)) {
				isVersionInstalled = true;
			}
			for (Leaf child : node.getChildren()) {

				// Recurse down
				count += getBoardsRecursive(child, root, isVersionInstalled);
			}
		}

		return count;
	}

	private int addBoard(Leaf node, Node tree, boolean isInstalled) {

		int count = 0;
		String vendorName = node.getProperty(Property.VENDOR_NAME);

		Node vendorNode = (Node) tree.addUniqueChild(Type.VENDOR, vendorName);

		String boardName = node.getName();
		String description = node.getDescription();

		Leaf boardNode = vendorNode.getChild(Type.BOARD, boardName);
		if (boardNode == null) {

			boardNode = new Leaf(Type.BOARD);
			vendorNode.addChild(boardNode);

			boardNode.setName(boardName);
			boardNode.setDescription(description);
			boardNode.putProperty(Property.VENDOR_NAME, vendorName);

			if (isInstalled) {
				boardNode.setBooleanProperty(Property.ENABLED, true);
			}

			count++;
		}

		return count;
	}

	// ------------------------------------------------------------------------
}