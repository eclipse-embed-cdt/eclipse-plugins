package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.jobs.ParseOutlineJob;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class OutlineView extends ViewPart {

	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.OutlineView";

	private TreeViewer m_viewer;
	private ISelectionListener m_pageSelectionListener;
	private ViewContentProvider m_contentProvider;

	public static final int AUTOEXPAND_LEVEL = 0;

	// Embedded classes
	class ViewContentProvider extends AbstractViewContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {

			if ((inputElement == null) || !(inputElement instanceof TreeNode)) {
				m_tree = null; // new TreeNode(TreeNode.NONE_TYPE);
				return new Object[] { new TreeNode(TreeNode.NONE_TYPE) };
			}

			TreeNode node = (TreeNode) inputElement;
			String type = node.getType();
			if (m_tree == null) {
				if (TreeNode.PACKAGE_TYPE.equals(type)
						|| TreeNode.VERSION_TYPE.equals(type)
						|| TreeNode.NONE_TYPE.equals(type)) {
					m_tree = node;
				}
			}

			if (m_tree == null) {
				return new Object[] { new TreeNode(TreeNode.NONE_TYPE) };
			} else if (node == m_tree) {
				if (m_tree.getOutline() != null) {
					return m_tree.getOutline().getChildrenArray();
				} else {
					return new Object[] { m_tree };
				}
			} else {
				return getChildren(inputElement);
			}
		}
	};

	class ViewLabelProvider extends CellLabelProvider {

		public String getText(Object obj) {
			TreeNode node = ((TreeNode) obj);
			String name = node.getName();

			return " " + name;
		}

		public Image getImage(Object obj) {

			TreeNode node = ((TreeNode) obj);
			String type = node.getType();

			if (TreeNode.NONE_TYPE.equals(type)) {
				return null;
			}

			String name = "";
			try {
				name = node.getName().toLowerCase();
			} catch (Exception e) {
			}

			if (TreeNode.FAMILY_TYPE.equals(type)
					|| TreeNode.SUBFAMILY_TYPE.equals(type)
					|| TreeNode.DEVICE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/hardware_chip.png").createImage();
			} else if (TreeNode.BOARD_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/board.png").createImage();
			} else if (TreeNode.VERSION_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/jtypeassist_co.png").createImage();
			} else if (TreeNode.EXAMPLE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/exec_obj.gif").createImage();
			} else if (TreeNode.COMPONENT_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/codeassist_co.gif").createImage();
			} else if (TreeNode.BUNDLE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/javaassist_co.png").createImage();
			} else if (TreeNode.CATEGORY_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/label_obj.gif").createImage();
			} else if (TreeNode.KEYWORD_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/info_obj.png").createImage();
			} else if (TreeNode.DEBUGINTERFACE_TYPE.equals(type)
					|| TreeNode.DEBUG_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/exec_dbg_obj.gif").createImage();
			} else if (TreeNode.FEATURE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/genericvariable_obj.png").createImage();
			} else if (TreeNode.BOOK_TYPE.equals(type)) {
				String url = node.getProperty(TreeNode.URL_PROPERTY, "");
				if (url.length() > 0) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/external_browser.png")
							.createImage();
				}
				String path = node.getProperty(TreeNode.FILE_PROPERTY, "")
						.toLowerCase();

				if (path.endsWith(".pdf")) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/pdficon_small.png")
							.createImage();
				} else if (path.endsWith(".chm")) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/chm.png").createImage();
				} else if (path.endsWith(".zip")) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/jar_obj.png")
							.createImage();
				} else {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/library_obj.png")
							.createImage();
				}
			} else if (TreeNode.PROCESSOR_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/methpro_obj.png").createImage();
			} else if (TreeNode.FILE_TYPE.equals(type)) {
				String category = node.getProperty(TreeNode.CATEGORY_PROPERTY,
						"");
				if ("source".equals(category)) {
					if (name.endsWith(".s")) {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID, "icons/s_file_obj.gif")
								.createImage();
					} else {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID, "icons/c_file_obj.gif")
								.createImage();
					}
				} else if ("header".equals(category)) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/h_file_obj.gif")
							.createImage();
				} else if ("include".equals(category)) {
					return Activator
							.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
									"icons/includes_container.gif")
							.createImage();
				} else if ("library".equals(category)) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/ar_obj.gif")
							.createImage();
				} else if ("doc".equals(category)) {
					if (name.endsWith(".pdf")) {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID, "icons/pdficon_small.png")
								.createImage();
					} else if (name.endsWith(".html")) {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID,
								"icons/external_browser.png").createImage();
					} else {
						return Activator.imageDescriptorFromPlugin(
								Activator.PLUGIN_ID, "icons/file_obj.png")
								.createImage();
					}
				} else {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/file_obj.png")
							.createImage();
				}
			} else if (TreeNode.HEADER_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/include_obj.gif").createImage();
			} else if (TreeNode.DEFINE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/define_obj.gif").createImage();
			} else if (TreeNode.MEMORY_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/memory_view.png").createImage();
			} else if (TreeNode.TAXONOMY_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/jdoc_tag_obj.png").createImage();
			} else if (TreeNode.CONDITION_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/smartmode_co.png").createImage();
			} else if (TreeNode.REQUIRE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/default_co.png").createImage();
			} else if (TreeNode.ACCEPT_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/method_public_obj.gif").createImage();
			} else if (TreeNode.DENY_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/method_private_obj.gif").createImage();
			} else if (TreeNode.PACKAGE_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/package_obj.png").createImage();
			} else if (TreeNode.API_TYPE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/int_obj.gif").createImage();
			} else {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/unknown_obj.gif").createImage();
			}
		}

		@Override
		public String getToolTipText(Object obj) {

			TreeNode node = ((TreeNode) obj);

			String description = node.getDescription();
			if (description != null && description.length() > 0) {
				return description;
			}
			String type = node.getType();
			if (TreeNode.VERSION_TYPE.equals(type)) {
				return "Version";
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

		public int compare(Viewer viewer, Object e1, Object e2) {
			// Always in order
			return 1;
		}
	}

	public OutlineView() {
		// Activator.setDevicesView(this);

		System.out.println("OutlineView()");
	}

	@Override
	public void createPartControl(Composite parent) {

		System.out.println("OutlineView.createPartControl()");

		m_viewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);

		ColumnViewerToolTipSupport.enableFor(m_viewer);

		m_contentProvider = new ViewContentProvider();
		m_viewer.setContentProvider(m_contentProvider);
		m_viewer.setLabelProvider(new ViewLabelProvider());
		m_viewer.setSorter(new NameSorter());
		m_viewer.setInput(null);

		addProviders();
		addListners();
		hookPageSelection();
	}

	@Override
	public void setFocus() {
		m_viewer.getControl().setFocus();
	}

	private void addProviders() {
		// Register this viewer as the selection provider
		getSite().setSelectionProvider(m_viewer);
	}

	private void addListners() {

	}

	private void hookPageSelection() {

		m_pageSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part,
					ISelection selection) {

				if (part instanceof PacksView) {
					packsViewSelectionChanged(part, selection);
				} else {
					System.out.println("Outline: " + part + " selection="
							+ selection);
				}
			}
		};
		getSite().getPage().addPostSelectionListener(m_pageSelectionListener);
	}

	public void dispose() {
		super.dispose();

		if (m_pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(
					m_pageSelectionListener);
		}

		System.out.println("OutlineView.dispose()");
	}

	protected void packsViewSelectionChanged(IWorkbenchPart part,
			ISelection selection) {

		System.out.println("Outline: packs selection=" + selection);
		if (selection.isEmpty()) {
			m_viewer.setInput(null);
		} else if (selection instanceof TreeSelection) {
			TreeNode node = (TreeNode) ((TreeSelection) selection)
					.getFirstElement();

			if (node.hasOutline()) {
				// If the node already has outline, show it
				m_viewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
				m_viewer.setInput(node);
			} else if (TreeNode.VERSION_TYPE.equals(node.getType())
					&& node.isInstalled()) {
				// If the version node is installed, get outline
				ParseOutlineJob job = new ParseOutlineJob("Parse Outline",
						node, m_viewer);
				job.schedule();
			} else if (!node.isInstalled()) {
				TreeNode none = new TreeNode(TreeNode.NONE_TYPE);
				none.setName("(not installed)");
				m_viewer.setInput(none);
			} else {
				// For all other nodes, show nothing
				m_viewer.setInput(null);
			}
		}
	}
}
