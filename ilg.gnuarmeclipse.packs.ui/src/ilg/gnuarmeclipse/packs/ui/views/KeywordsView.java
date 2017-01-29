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
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.IDataManagerListener;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.ui.Activator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;

public class KeywordsView extends ViewPart implements IDataManagerListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.KeywordsView";

	// ------------------------------------------------------------------------

	class ViewContentProvider extends NodeViewContentProvider {

	}

	// ------------------------------------------------------------------------

	class ViewLabelProvider extends CellLabelProvider {

		public String getText(Object obj) {
			return " " + ((Leaf) obj).getName();
		}

		public Image getImage(Object obj) {

			return null;
		}

		@Override
		public String getToolTipText(Object obj) {

			return null;
		}

		@Override
		public void update(ViewerCell cell) {
			cell.setText(getText(cell.getElement()));
		}
	}

	// ------------------------------------------------------------------------

	class NameSorter extends ViewerSorter {
		// Default ascending sorter
	}

	// ------------------------------------------------------------------------

	private TreeViewer fViewer;
	private Action fRemoveFilters;

	private ViewContentProvider fContentProvider;

	private DataManager fDataManager;
	private MessageConsoleStream fOut;

	public KeywordsView() {

		fOut = ConsoleStream.getConsoleOut();

		fDataManager = DataManager.getInstance();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		// System.out.println("KeywordsView.createPartControl()");

		fViewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		fContentProvider = new ViewContentProvider();

		// Register this content provider to the packs storage notifications
		fDataManager.addListener(this);

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(new ViewLabelProvider());
		fViewer.setSorter(new NameSorter());

		fViewer.setInput(getKeywordsTree());

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
			System.out.println("KeywordsView.dispose()");
		}
	}

	private void addProviders() {

		// Register this viewer as a selection provider
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
				KeywordsView.this.fillContextMenu(manager);
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
		manager.add(fRemoveFilters);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(fRemoveFilters);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fRemoveFilters);
	}

	private void makeActions() {

		fRemoveFilters = new Action() {

			public void run() {
				// Empty selection
				fViewer.setSelection(null);// new TreeSelection());
			}
		};

		fRemoveFilters.setText("Remove filters");
		fRemoveFilters.setToolTipText("Remove all filters based on selections");
		fRemoveFilters
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/removeall.png"));

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
			System.out.println("KeywordsView.updated()");
		}
	}

	public String toString() {
		return "KeywordsView";
	}

	// ------------------------------------------------------------------------

	@Override
	public void packsChanged(DataManagerEvent event) {

		String type = event.getType();
		// System.out.println("KeywordsView.packsChanged(), type=\"" + type
		// + "\".");

		if (DataManagerEvent.Type.NEW_INPUT.equals(type)) {

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("KeywordsView NEW_INPUT");

					fViewer.setInput(getKeywordsTree());
				}
			});

			// } else if (DataManagerEvent.Type.REFRESH_ALL.equals(type)) {
			//
			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// // m_out.println("KeywordsView REFRESH_ALL");
			//
			// fViewer.refresh();
			// }
			// });
			//
		} else if (DataManagerEvent.Type.UPDATE_VERSIONS.equals(type)) {

			// Nothing to do

		}
	}

	// ------------------------------------------------------------------------

	// Get view data from storage.
	// Return a one level hierarchy of keyword nodes.
	private Node getKeywordsTree() {

		final Node packsTree = fDataManager.getRepositoriesTree();

		final Node keywordsRoot = new Node(Type.ROOT);

		if (packsTree.hasChildren()) {

			(new DurationMonitor()).displayTimeAndRun(new Runnable() {

				public void run() {

					fOut.println("Collecting keywords...");

					Set<String> set = new HashSet<String>();

					try {

						// Collect keywords
						getKeywordsRecursive(packsTree, set);

						// Add keyword nodes to the hierarchy
						for (String keywordName : set) {
							Leaf keywordNode = Leaf.addNewChild(keywordsRoot, Type.KEYWORD);
							keywordNode.setName(keywordName);
						}

					} catch (Exception e) {
						Activator.log(e);
					}

					if (set.size() > 0) {
						fOut.println("Found " + set.size() + " keyword(s).");
					} else {
						fOut.println("Found none.");
					}
				}
			});

		}

		if (!keywordsRoot.hasChildren()) {

			Node empty = Node.addNewChild(keywordsRoot, Type.NONE);
			empty.setName("(none)");
		}

		return keywordsRoot;
	}

	// Identify outline nodes and collect keywords from inside
	private void getKeywordsRecursive(Leaf node, Set<String> set) {

		String type = node.getType();
		if (Type.OUTLINE.equals(type)) {

			if (node.hasChildren()) {
				for (Leaf child : ((Node) node).getChildren()) {
					String childType = child.getType();
					if (Type.KEYWORD.equals(childType)) {

						// Collect unique keywords
						set.add(child.getName());
					}
				}
			}

		} else if (Type.EXTERNAL.equals(type)) {

			; // no keywords inside externals, avoid recursion

		} else if (node instanceof Node && node.hasChildren()) {

			for (Leaf child : ((Node) node).getChildren()) {

				// Recurse down
				getKeywordsRecursive(child, set);
			}
		}
	}

	// ------------------------------------------------------------------------

}