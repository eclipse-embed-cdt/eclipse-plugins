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

package ilg.gnuarmeclipse.managedbuild.packs.ui.views;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.data.IDataManagerListener;
import ilg.gnuarmeclipse.packs.ui.IconUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.ui.part.ViewPart;

public class DocsView extends ViewPart implements IDataManagerListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.managedbuild.packs.ui.views.DocsView";

	public static final int AUTOEXPAND_LEVEL = 2;

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

			if (Type.FOLDER.equals(type)) {
				String imageKey = ISharedImages.IMG_OBJ_FOLDER;
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(imageKey);
			} else if (Type.BOOK.equals(type)) {
				return IconUtils.getBookIcon(node);
			} else {
				return null;
			}
		}

		@Override
		public String getToolTipText(Object obj) {

			// Leaf node = ((Leaf) obj);
			// String type = node.getType();

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

	private ViewContentProvider fContentProvider;

	// private PacksStorage fStorage;
	private DataManager fDataManager;

	// private MessageConsoleStream fOut;

	public DocsView() {

		// fOut = ConsoleStream.getConsoleOut();

		// fStorage = PacksStorage.getInstance();
		fDataManager = DataManager.getInstance();
		// System.out.println("DevicesView()");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		// System.out.println("DevicesView.createPartControl()");

		fViewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);

		ColumnViewerToolTipSupport.enableFor(fViewer);

		fContentProvider = new ViewContentProvider();

		// Register this content provider to the packs storage notifications
		// m_storage.addListener(m_contentProvider);

		// Register this view to the data manager notifications
		fDataManager.addListener(this);

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(new ViewLabelProvider());
		fViewer.setSorter(new NameSorter());

		fViewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
		fViewer.setInput(getDocsTree());

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

		System.out.println("DevicesView.dispose()");
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
				DocsView.this.fillContextMenu(manager);
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
	}

	private void fillContextMenu(IMenuManager manager) {

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void makeActions() {

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

		// System.out.println("DevicesView.refresh() " + obj);
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

		// System.out.println("DevicesView.updated()");
	}

	public String toString() {
		return "DocsView";
	}

	// ------------------------------------------------------------------------

	@Override
	public void packsChanged(DataManagerEvent event) {

		String type = event.getType();
		// System.out.println("DevicesView.packsChanged(), type=\"" + type +
		// "\".");

		if (DataManagerEvent.Type.NEW_INPUT.equals(type)) {

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("DevicesView NEW_INPUT");

					fViewer.setInput(getDocsTree());
				}
			});

			// } else if (DataManagerEvent.Type.REFRESH_ALL.equals(type)) {
			//
			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// // m_out.println("DevicesView REFRESH_ALL");
			//
			// fViewer.refresh();
			// }
			// });
			//
		} else if (DataManagerEvent.Type.UPDATE_VERSIONS.equals(type)) {

			final Map<String, Leaf> updatedMap = new HashMap<String, Leaf>();
			// updateDevicesTree(updatedMap);

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
	private Node getDocsTree() {

		// TODO: get these from current project
		String deviceVendorId = "13";
		String deviceName = "STM32F407VG";
		String boardVendorName = "STMicroelectronics";
		String boardName = "STM32F4-Discovery";

		Node devicesRoot = DataManager.getInstance().getDocsTree(
				deviceVendorId, deviceName, boardVendorName, boardName);
		return devicesRoot;
	}

	// ------------------------------------------------------------------------
}