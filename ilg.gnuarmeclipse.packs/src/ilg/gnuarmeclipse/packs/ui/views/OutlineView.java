package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.TreeNode;
import ilg.gnuarmeclipse.packs.jobs.ParseOutlineJob;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

public class OutlineView extends ViewPart {

	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.OutlineView";

	private TreeViewer m_viewer;
	private ISelectionListener m_pageSelectionListener;
	private ViewContentProvider m_contentProvider;

	private Action m_expandAll;
	private Action m_collapseAll;
	private Action m_doubleClickAction;
	private Path m_packagePath;

	public static final int AUTOEXPAND_LEVEL = 0;

	// Embedded classes
	class ViewContentProvider extends AbstractViewContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {

			if ((inputElement == null) || !(inputElement instanceof TreeNode)) {
				m_tree = null; // new TreeNode(TreeNode.NONE_TYPE);
				m_packagePath = null;
				return new Object[] { new TreeNode(TreeNode.NONE_TYPE) };
			}

			TreeNode node = (TreeNode) inputElement;
			String type = node.getType();
			if (m_tree == null) {
				m_packagePath = null;
				if (TreeNode.PACKAGE_TYPE.equals(type)
						|| TreeNode.VERSION_TYPE.equals(type)
						|| TreeNode.NONE_TYPE.equals(type)) {
					m_tree = node;
				}
			}

			if (m_tree == null) {
				m_packagePath = null;
				return new Object[] { new TreeNode(TreeNode.NONE_TYPE) };
			} else if (node == m_tree) {
				m_packagePath = null;
				TreeNode outline = m_tree.getOutline();
				if (outline != null) {
					String folder = outline
							.getProperty(TreeNode.FOLDER_PROPERTY);
					if (folder != null) {
						m_packagePath = new Path(folder);
					}
					return outline.getChildrenArray();
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

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
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
					// System.out.println("Outline: " + part + " selection="
					// + selection);
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

	private void makeActions() {

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

		m_doubleClickAction = new Action() {
			public void run() {
				ISelection selection = m_viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof TreeNode) {
					try {
						doubleClick((TreeNode) obj);
					} catch (PartInitException e) {
					} catch (MalformedURLException e) {
					}
				}
			}
		};

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				OutlineView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(m_viewer.getControl());
		m_viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, m_viewer);
	}

	private void hookDoubleClickAction() {
		m_viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				m_doubleClickAction.run();
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
	}

	private void doubleClick(TreeNode node) throws PartInitException,
			MalformedURLException {
		String type = node.getType();
		if (m_packagePath == null) {
			// System.out.println("null m_packagePath");
			return;
		}

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		if (TreeNode.FILE_TYPE.equals(type)) {

			String category = node.getProperty(TreeNode.CATEGORY_PROPERTY);
			String relativeFile = node.getProperty(TreeNode.FILE_PROPERTY);
			if ("header".equals(category) || "source".equals(category)) {

				// System.out.println("Edit " + relativeFile);
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						m_packagePath.append(relativeFile));

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only
				IDE.openInternalEditorOnFileStore(page, fileStore);

			} else if ("doc".equals(category)) {

				// System.out.println("Document " + node);
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						m_packagePath.append(relativeFile));
				// System.out.println(fileStore);

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only
				IDE.openEditorOnFileStore(page, fileStore);

			} else if ("include".equals(category) || "library".equals(category)) {
				; // ignore folders
			} else {
				System.out.println("File " + node + "  " + category);
			}

		} else if (TreeNode.BOOK_TYPE.equals(type)) {

			String url = node.getProperty(TreeNode.URL_PROPERTY, "");
			String relativeFile = node.getProperty(TreeNode.FILE_PROPERTY, "");
			if (url.length() > 0) {

				// System.out.println("Open " + url);
				PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser().openURL(new URL(url));

			} else if (relativeFile.length() > 0) {

				// System.out.println("Path " + relativeFile);
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						m_packagePath.append(relativeFile));

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only
				IDE.openEditorOnFileStore(page, fileStore);

			} else {
				System.out.println("Book " + node);
			}

		} else if (TreeNode.HEADER_TYPE.equals(type)) {

			// System.out.println("Header " + node );

			String relativeFile = node.getProperty(TreeNode.FILE_PROPERTY);
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					m_packagePath.append(relativeFile));

			// Open external file in Eclipse editor (as read only, since the
			// packages were marked as read only
			IDE.openInternalEditorOnFileStore(page, fileStore);

		} else if (TreeNode.DEBUG_TYPE.equals(type)) {

			// System.out.println("Debug " + node );

			String relativeFile = node.getProperty(TreeNode.FILE_PROPERTY);
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					m_packagePath.append(relativeFile));

			// Open external file in Eclipse editor (as read only, since the
			// packages were marked as read only
			IDE.openInternalEditorOnFileStore(page, fileStore);

		} else {
			// System.out.println("Double-click detected on " + node + " " + type);
		}
	}
}
