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
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.storage.PacksStorage;
import ilg.gnuarmeclipse.packs.storage.PacksStorageEvent;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Type;

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

public class DevicesView extends ViewPart implements IPacksStorageListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.DevicesView";

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
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(imageKey);
			} else if (Type.FAMILY.equals(type)) {
				if (node.isBooleanProperty(Property.ENABLED)) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/hardware_chip.png")
							.createImage();
				} else {
					return Activator
							.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
									"icons/hardware_chip_grey.png")
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
			} else if (Type.FAMILY.equals(type)) {
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

	public DevicesView() {

		m_out = Activator.getConsoleOut();

		m_storage = PacksStorage.getInstance();
		// System.out.println("DevicesView()");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		// System.out.println("DevicesView.createPartControl()");

		m_viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);

		ColumnViewerToolTipSupport.enableFor(m_viewer);

		m_contentProvider = new ViewContentProvider();

		// Register this content provider to the packs storage notifications
		// m_storage.addListener(m_contentProvider);

		// Register this view to the packs storage notifications
		m_storage.addListener(this);

		m_viewer.setContentProvider(m_contentProvider);
		m_viewer.setLabelProvider(new ViewLabelProvider());
		m_viewer.setSorter(new NameSorter());

		m_viewer.setInput(getDevicesTree());

		addProviders();
		addListners();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void dispose() {

		super.dispose();
		m_storage.removeListener(this);

		System.out.println("DevicesView.dispose()");
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
				DevicesView.this.fillContextMenu(manager);
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

		// System.out.println("DevicesView.refresh() " + obj);
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

		// System.out.println("DevicesView.updated()");
	}

	public String toString() {
		return "DevicesView";
	}

	// ------------------------------------------------------------------------

	@Override
	public void packsChanged(PacksStorageEvent event) {

		String type = event.getType();
		// System.out.println("DevicesView.packsChanged(), type=\"" + type +
		// "\".");

		if (PacksStorageEvent.Type.NEW_INPUT.equals(type)) {

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("DevicesView NEW_INPUT");

					m_viewer.setInput(getDevicesTree());
				}
			});

		} else if (PacksStorageEvent.Type.REFRESH_ALL.equals(type)) {

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("DevicesView REFRESH_ALL");

					m_viewer.refresh();
				}
			});

		} else if (PacksStorageEvent.Type.UPDATE_VERSIONS.equals(type)) {

			final Map<String, Leaf> updatedMap = new HashMap<String, Leaf>();
			updateDevicesTree(updatedMap);

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
	private Node getDevicesTree() {

		Node packsTree = m_storage.getPacksTree();
		Node devicesRoot = new Node(Type.ROOT);
		devicesRoot.setName("Devices");

		if (packsTree.hasChildren()) {

			m_out.println();
			m_out.println(Utils.getCurrentDateTime());
			m_out.println("Collecting devices...");

			int count = getDevicesRecursive(packsTree, devicesRoot, false);

			m_out.println("Found " + count + " device(s), from "
					+ devicesRoot.getChildren().size() + " vendor(s).");

		} else {

			Node empty = Node.addNewChild(devicesRoot, Type.NONE);
			empty.setName("(none)");
		}

		return devicesRoot;
	}

	// Identify outline & external nodes and collect devices from inside
	private int getDevicesRecursive(Leaf node, Node root, boolean isInstalled) {

		int count = 0;
		String type = node.getType();
		if (node.hasChildren()) {

			if (Type.OUTLINE.equals(type) || Type.EXTERNAL.equals(type)) {

				for (Leaf child : ((Node) node).getChildren()) {
					String childType = child.getType();
					if (Type.FAMILY.equals(childType)) {

						// Collect unique keywords
						count += addDevice(child, root, isInstalled);
					}
				}

			} else {
				boolean isVersionInstalled = isInstalled;
				if (Type.VERSION.equals(type)
						&& node.isBooleanProperty(Property.INSTALLED)) {
					isVersionInstalled = true;
				}
				for (Leaf child : ((Node) node).getChildren()) {

					// Recurse down
					count += getDevicesRecursive(child, root,
							isVersionInstalled);
				}
			}
		}

		return count;
	}

	private int addDevice(Leaf node, Node tree, boolean isInstalled) {

		int count = 0;
		String vendorName = node.getProperty(Property.VENDOR_NAME);
		String vendorId = node.getProperty(Property.VENDOR_ID);

		Node vendorNode = Node.addUniqueChild(tree, Type.VENDOR, vendorName);
		vendorNode.putProperty(Property.VENDOR_ID, vendorId);

		String deviceName = node.getName();
		String description = node.getDescription();

		Leaf deviceNode = vendorNode.getChild(Type.FAMILY, deviceName);
		if (deviceNode == null) {

			deviceNode = Leaf.addNewChild(vendorNode, Type.FAMILY);

			deviceNode.setName(deviceName);
			deviceNode.setDescription(description);
			deviceNode.putProperty(Property.VENDOR_NAME, vendorName);
			deviceNode.putProperty(Property.VENDOR_ID, vendorId);

			if (isInstalled) {
				deviceNode.setBooleanProperty(Property.ENABLED, true);
			}

			count++;
		}

		return count;
	}

	// ------------------------------------------------------------------------

	private void updateDevicesTree(Map<String, Leaf> updatedList) {

		Node modelTree = m_storage.getPacksTree();
		Node viewTree = (Node) m_viewer.getInput();

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
			updateDevicesRecursive(modelTree, viewTree, false, updatedList);
		}
	}

	// Identify outline & external nodes and update devices from inside
	private void updateDevicesRecursive(Leaf modelNode, Node viewTree,
			boolean isInstalled, Map<String, Leaf> updatedMap) {

		String type = modelNode.getType();
		if (modelNode.hasChildren()) {

			if (Type.OUTLINE.equals(type) || Type.EXTERNAL.equals(type)) {
				for (Leaf child : ((Node) modelNode).getChildren()) {
					String childType = child.getType();
					if (Type.FAMILY.equals(childType)) {

						// Collect unique keywords
						updateDevice(child, viewTree, isInstalled, updatedMap);
					}
				}
			} else {
				boolean isVersionInstalled = isInstalled;
				if (Type.VERSION.equals(type)
						&& modelNode.isBooleanProperty(Property.INSTALLED)) {
					isVersionInstalled = true;
				}
				for (Leaf child : ((Node) modelNode).getChildren()) {

					// Recurse down
					updateDevicesRecursive(child, viewTree, isVersionInstalled,
							updatedMap);
				}
			}
		}
	}

	private void updateDevice(Leaf modelFamilyNode, Node viewTree,
			boolean isInstalled, Map<String, Leaf> updatedList) {

		String deviceName = modelFamilyNode.getName();
		String vendorName = modelFamilyNode.getProperty(Property.VENDOR_NAME);
		String vendorId = modelFamilyNode.getProperty(Property.VENDOR_ID);

		Node vendorNode = (Node) viewTree.getChild(Type.VENDOR, vendorName);
		if (vendorNode == null
				|| !vendorId.equals(vendorNode.getProperty(Property.VENDOR_ID))) {

			// Make new vendor node
			vendorNode = Node.addNewChild(viewTree, Type.VENDOR);
			vendorNode.setName(vendorName);
			vendorNode.putProperty(Property.VENDOR_ID, vendorId);
		}

		Leaf deviceNode = vendorNode.getChild(Type.FAMILY, deviceName);
		if (deviceNode == null) {

			// Make new device node
			deviceNode = Leaf.addNewChild(vendorNode, Type.FAMILY);
			deviceNode.setName(deviceName);

			String description = modelFamilyNode.getDescription();
			deviceNode.setDescription(description);

			deviceNode.putProperty(Property.VENDOR_NAME, vendorName);
			deviceNode.putProperty(Property.VENDOR_ID, vendorId);
		}

		if (isInstalled) {
			deviceNode.setBooleanProperty(Property.ENABLED, true);

			updatedList.put(vendorId, vendorNode);
		}

	}
	// ------------------------------------------------------------------------

}