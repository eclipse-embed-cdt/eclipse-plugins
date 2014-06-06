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
import ilg.gnuarmeclipse.packs.tree.Type;

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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;

public class KeywordsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.KeywordsView";

	// ------------------------------------------------------------------------

	class ViewContentProvider extends AbstractViewContentProvider implements
			IPacksStorageListener {

		public Object[] getElements(Object parent) {

			if (parent.equals(getViewSite())) {
				m_tree = getKeywordsTree();
				return getChildren(m_tree);
			}
			return getChildren(parent);
		}

		@Override
		public void packsChanged(PacksStorageEvent event) {

			String type = event.getType();
			System.out.println("KeywordsView.packsChanged(), type=\"" + type
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

	private TreeViewer m_viewer;
	private Action m_removeFilters;

	private ViewContentProvider m_contentProvider;

	private PacksStorage m_storage;
	private MessageConsoleStream m_out;

	public KeywordsView() {

		// Store reference to this view
		Activator.setKeywordsView(this);

		m_out = Activator.getConsoleOut();

		m_storage = PacksStorage.getInstance();
		System.out.println("KeywordsView()");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		System.out.println("KeywordsView.createPartControl()");

		m_viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);

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
		m_storage.removeListener(m_contentProvider);

		System.out.println("KeywordsView.dispose()");
	}

	private void addProviders() {

		// Register this viewer as a selection provider
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
				KeywordsView.this.fillContextMenu(manager);
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
		manager.add(m_removeFilters);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(m_removeFilters);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_removeFilters);
	}

	private void makeActions() {

		m_removeFilters = new Action() {
			public void run() {
				// Empty selection
				m_viewer.setSelection(null);// new TreeSelection());
			}
		};

		m_removeFilters.setText("Remove filters");
		m_removeFilters
				.setToolTipText("Remove all filters based on selections");
		m_removeFilters.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/removeall.png"));

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

	// TODO: remove it after migration to storage listeners
	public void forceRefresh() {

		m_contentProvider.forceRefresh();

		m_viewer.setInput(getViewSite());

		System.out.println("KeywordsView.forceRefresh()");
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
		System.out.println("KeywordsView.updated()");
	}

	public String toString() {
		return "KeywordsView";
	}

	// ------------------------------------------------------------------------

	// Get view data from storage.
	// Return a one level hierarchy of keyword nodes.
	private Node getKeywordsTree() {

		Node packsTree = m_storage.getPacksTree();
		Node keywordsRoot = new Node(Type.ROOT);

		if (packsTree.hasChildren()) {

			m_out.println();
			m_out.println(Utils.getCurrentDateTime());
			m_out.println("Collecting keywords...");

			Set<String> set = new HashSet<String>();
			// Collect keywords
			getKeywordsRecursive(packsTree, set);

			// Add keyword nodes to the hierarchy
			for (String keywordName : set) {
				Leaf keywordNode = new Leaf(Leaf.Type.KEYWORD);
				keywordNode.setName(keywordName);
				keywordsRoot.addChild(keywordNode);
			}

			m_out.println("Found " + set.size() + " keyword(s).");
		}

		return keywordsRoot;
	}

	// Identify outline nodes and collect keywords from inside
	private void getKeywordsRecursive(Leaf node, Set<String> set) {

		String type = node.getType();
		if (Type.OUTLINE.equals(type)) {
			for (Leaf child : node.getChildrenArray()) {
				String childType = child.getType();
				if (Type.KEYWORD.equals(childType)) {

					// Collect unique keywords
					set.add(child.getName());
				}
			}
		} else if (Type.EXTERNAL.equals(type)) {
			; // no keywords inside externals, avoid recursion
		} else if (node instanceof Node && node.hasChildren()) {
			for (Leaf child : node.getChildren()) {

				// Recurse down
				getKeywordsRecursive(child, set);
			}
		}
	}

	// ------------------------------------------------------------------------

}