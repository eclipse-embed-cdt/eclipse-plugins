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

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.DurationMonitor;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.data.IDataManagerListener;
import ilg.gnuarmeclipse.packs.ui.Activator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;

public class BoardsView extends ViewPart implements IDataManagerListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.BoardsView";

	// ------------------------------------------------------------------------

	class ViewContentProvider extends NodeViewContentProvider {

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
				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			} else if (Type.BOARD.equals(type)) {
				if (node.isBooleanProperty(Property.ENABLED)) {
					return Activator.getInstance().getImage("board");
				} else {
					return Activator.getInstance().getImage("board_grey");
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

	private TreeViewer fViewer;
	private Action fRemoveFilters;
	private Action fExpandAll;
	private Action fCollapseAll;

	private ViewContentProvider fContentProvider;

	private DataManager fDataManager;
	private MessageConsoleStream fOut;

	public BoardsView() {

		fOut = ConsoleStream.getConsoleOut();

		fDataManager = DataManager.getInstance();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		// System.out.println("BoardsView.createPartControl()");

		fViewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		ColumnViewerToolTipSupport.enableFor(fViewer);

		fContentProvider = new ViewContentProvider();

		// Register this content provider to the packs storage notifications
		// m_storage.addListener(m_contentProvider);

		// Register this view to the packs storage notifications
		fDataManager.addListener(this);

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(new ViewLabelProvider());
		fViewer.setSorter(new NameSorter());

		fViewer.setInput(getBoardsTree());

		addProviders();
		addListners();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void dispose() {

		super.dispose();
		fDataManager.removeListener(this);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("BoardsView.dispose()");
		}
	}

	private void addProviders() {

		// Register this viewer as the selection provider
		getSite().setSelectionProvider(fViewer);
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
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(fExpandAll);
		manager.add(fCollapseAll);
		manager.add(new Separator());
		manager.add(fRemoveFilters);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(fRemoveFilters);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fExpandAll);
		manager.add(fCollapseAll);
		manager.add(new Separator());
		manager.add(fRemoveFilters);
	}

	private void makeActions() {

		fRemoveFilters = new Action() {

			public void run() {
				// Empty selection
				fViewer.setSelection(null);
			}
		};

		fRemoveFilters.setText("Remove filters");
		fRemoveFilters.setToolTipText("Remove all filters based on selections");
		fRemoveFilters
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/removeall.png"));

		// -----
		fExpandAll = new Action() {

			public void run() {
				fViewer.expandAll();
			}
		};

		fExpandAll.setText("Expand all");
		fExpandAll.setToolTipText("Expand all children nodes");
		fExpandAll.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/expandall.png"));

		fCollapseAll = new Action() {

			public void run() {
				fViewer.collapseAll();
			}
		};

		fCollapseAll.setText("Collapse all");
		fCollapseAll.setToolTipText("Collapse all children nodes");
		fCollapseAll
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/collapseall.png"));
	}

	private void hookDoubleClickAction() {
		// None
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	public void refresh(Object obj) {

		if (obj instanceof Collection<?>) {
			for (Object node : (Collection<?>) obj) {
				fViewer.refresh(node);
			}
		} else {
			fViewer.refresh(obj);
		}

		// Setting the selection will force the outline update
		fViewer.setSelection(fViewer.getSelection());

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DevicesView.refresh() " + obj);
		}
	}

	public void update(Object obj) {

		if (obj instanceof List<?>) {
			@SuppressWarnings("unchecked")
			List<Node> list = (List<Node>) obj;
			for (Object node : list) {
				fViewer.update(node, null);
			}
		} else {
			fViewer.update(obj, null);
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("BoardsView.updated()");
		}
	}

	public String toString() {
		return "BoardsView";
	}

	// ------------------------------------------------------------------------

	@Override
	public void packsChanged(DataManagerEvent event) {

		String type = event.getType();
		// System.out
		// .println("BoardsView.ViewContentProvider.packsChanged(), type=\""
		// + type + "\".");

		if (DataManagerEvent.Type.NEW_INPUT.equals(type)) {

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("BoardsView NEW_INPUT");

					fViewer.setInput(getBoardsTree());
				}
			});

			// } else if (DataManagerEvent.Type.REFRESH_ALL.equals(type)) {
			//
			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// // m_out.println("BoardsView REFRESH_ALL");
			//
			// fViewer.refresh();
			// }
			// });
			//
		} else if (DataManagerEvent.Type.UPDATE_VERSIONS.equals(type)) {

			final Map<String, Leaf> updatedMap = new HashMap<String, Leaf>();
			updateBoardsTree(updatedMap);

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					refresh(updatedMap.values());
				}
			});
		}
	}

	// ------------------------------------------------------------------------

	// Get view data from storage.
	// Return a two level hierarchy of vendor and device nodes.
	private Node getBoardsTree() {

		final Node packsTree = fDataManager.getRepositoriesTree();

		final Node boardsRoot = new Node(Type.ROOT);
		boardsRoot.setName("Boards");

		if (packsTree.hasChildren()) {

			(new DurationMonitor()).displayTimeAndRun(new Runnable() {

				public void run() {
					fOut.println("Collecting boards...");

					int count = 0;
					try {
						count = getBoardsRecursive(packsTree, boardsRoot, false);
					} catch (Exception e) {
						Activator.log(e);
					}
					if (boardsRoot.hasChildren()) {
						fOut.println("Found " + count + " board(s), from " + boardsRoot.getChildren().size()
								+ " vendor(s).");
					} else {
						fOut.println("Found none.");
					}
				}
			});
		}

		if (!boardsRoot.hasChildren()) {

			Node empty = Node.addNewChild(boardsRoot, Type.NONE);
			empty.setName("(none)");
		}

		return boardsRoot;
	}

	// Identify outline & external nodes and collect boards from inside
	private int getBoardsRecursive(Leaf node, Node root, boolean isInstalled) {

		int count = 0;
		String type = node.getType();
		if (node.hasChildren()) {

			if (Type.OUTLINE.equals(type) || Type.EXTERNAL.equals(type)) {

				for (Leaf child : ((Node) node).getChildren()) {
					String childType = child.getType();
					if (Type.BOARD.equals(childType)) {

						// Collect unique keywords
						count += addBoard(child, root, isInstalled);
					}
				}
			} else {
				boolean isVersionInstalled = isInstalled;
				if (Type.VERSION.equals(type) && node.isBooleanProperty(Property.INSTALLED)) {
					isVersionInstalled = true;
				}
				for (Leaf child : ((Node) node).getChildren()) {

					// Recurse down
					count += getBoardsRecursive(child, root, isVersionInstalled);
				}
			}
		}

		return count;
	}

	private int addBoard(Leaf node, Node tree, boolean isInstalled) {

		int count = 0;
		String vendorName = node.getProperty(Property.VENDOR_NAME);

		Node vendorNode = Node.addUniqueChild(tree, Type.VENDOR, vendorName);

		String boardName = node.getName();
		String description = node.getDescription();

		Leaf boardNode = vendorNode.findChild(Type.BOARD, boardName);
		if (boardNode == null) {

			boardNode = Leaf.addNewChild(vendorNode, Type.BOARD);

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

	private void updateBoardsTree(Map<String, Leaf> updatedList) {

		Node modelTree = fDataManager.getRepositoriesTree();
		Node viewTree = (Node) fViewer.getInput();

		if (modelTree.hasChildren() && viewTree != null) {

			// Disable all devices
			if (viewTree.hasChildren()) {
				for (Leaf vendor : viewTree.getChildren()) {
					if (vendor.hasChildren()) {
						for (Leaf device : ((Node) vendor).getChildren()) {
							device.setBooleanProperty(Property.ENABLED, false);
						}
					}
				}
			}
			updateBoardsRecursive(modelTree, viewTree, false, updatedList);
		}
	}

	// Identify outline & external nodes and update devices from inside
	private void updateBoardsRecursive(Leaf modelNode, Node viewTree, boolean isInstalled,
			Map<String, Leaf> updatedMap) {

		String type = modelNode.getType();
		if (modelNode.hasChildren()) {

			if (Type.OUTLINE.equals(type) || Type.EXTERNAL.equals(type)) {
				for (Leaf child : ((Node) modelNode).getChildren()) {
					String childType = child.getType();
					if (Type.BOARD.equals(childType)) {

						// Collect unique keywords
						updateBoard(child, viewTree, isInstalled, updatedMap);
					}
				}
			} else {
				boolean isVersionInstalled = isInstalled;
				if (Type.VERSION.equals(type) && modelNode.isBooleanProperty(Property.INSTALLED)) {
					isVersionInstalled = true;
				}
				for (Leaf child : ((Node) modelNode).getChildren()) {

					// Recurse down
					updateBoardsRecursive(child, viewTree, isVersionInstalled, updatedMap);
				}
			}
		}
	}

	private void updateBoard(Leaf modelFamilyNode, Node viewTree, boolean isInstalled, Map<String, Leaf> updatedList) {

		String boardName = modelFamilyNode.getName();
		String vendorName = modelFamilyNode.getProperty(Property.VENDOR_NAME);

		Node vendorNode = (Node) viewTree.findChild(Type.VENDOR, vendorName);
		if (vendorNode == null || !vendorName.equals(vendorNode.getName())) {

			// Make new vendor node
			vendorNode = Node.addNewChild(viewTree, Type.VENDOR);
			vendorNode.setName(vendorName);
		}

		Leaf boardNode = vendorNode.findChild(Type.BOARD, boardName);
		if (boardNode == null) {

			// Make new board node
			boardNode = Leaf.addNewChild(vendorNode, Type.BOARD);
			boardNode.setName(boardName);

			String description = modelFamilyNode.getDescription();
			boardNode.setDescription(description);

			boardNode.putProperty(Property.VENDOR_NAME, vendorName);
		}

		if (isInstalled) {
			boardNode.setBooleanProperty(Property.ENABLED, true);

			updatedList.put(vendorName, vendorNode);
		}
	}

	// ------------------------------------------------------------------------
}