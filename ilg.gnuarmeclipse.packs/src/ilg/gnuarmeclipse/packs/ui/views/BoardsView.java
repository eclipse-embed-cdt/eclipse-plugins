package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.TreeNode;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
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
import org.eclipse.ui.part.ViewPart;

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

public class BoardsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.BoardsView";

	private TreeViewer m_viewer;
	private Action m_removeFilters;

	// private PacksFilter m_packsFilter;
	// private ViewerFilter[] m_packsFilters;

	// private DrillDownAdapter drillDownAdapter;

	private ViewContentProvider m_contentProvider;

	// private Action doubleClickAction;

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
					try {
						m_tree = PacksStorage.getCachedSubTree("boards");
					} catch (Exception e) {
						// TODO Auto-generated catch block
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

	class ViewLabelProvider extends CellLabelProvider {

		public String getText(Object obj) {
			return " " + ((TreeNode) obj).getName();
		}

		public Image getImage(Object obj) {
			TreeNode node = ((TreeNode) obj);
			String type = node.getType();

			if (TreeNode.NONE_TYPE.equals(type)) {
				return null;
			}

			if (!TreeNode.BOARD_TYPE.equals(type)) {
				String imageKey = ISharedImages.IMG_OBJ_FOLDER;
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(imageKey);
			} else {
				if (node.isInstalled()) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/board.png")
							.createImage();
				} else {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/board_grey.png")
							.createImage();
				}
			}
		}

		@Override
		public String getToolTipText(Object obj) {

			TreeNode node = ((TreeNode) obj);
			String type = node.getType();

			if (TreeNode.BOARD_TYPE.equals(type)) {
				String description = node.getDescription();
				if (description != null && description.length() > 0) {
					return description;
				}
			} else if (TreeNode.VENDOR_TYPE.equals(type)) {
				return "Vendor";
			}
			return null;
		}

		@Override
		public void update(ViewerCell cell) {
			cell.setText(getText(cell.getElement()));
			cell.setImage(getImage(cell.getElement()));
		}
	}

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public BoardsView() {
		Activator.setBoardsView(this);

		System.out.println("BoardsView()");
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		System.out.println("BoardsView.createPartControl()");

		// m_packsFilter = new PacksFilter();
		// m_packsFilters = new PacksFilter[] { m_packsFilter };

		m_viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL);

		// drillDownAdapter = new DrillDownAdapter(m_viewer);

		ColumnViewerToolTipSupport.enableFor(m_viewer);

		m_contentProvider = new ViewContentProvider();
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
				m_viewer.setSelection(null);
			}
		};

		m_removeFilters.setText("Remove filters");
		m_removeFilters
				.setToolTipText("Remove all filters based on selections");
		m_removeFilters.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/removeall.png"));

	}

	private void hookDoubleClickAction() {
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
			List<TreeNode> list = (List<TreeNode>) obj;
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
}