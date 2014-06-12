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
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.Repos;
import ilg.gnuarmeclipse.packs.jobs.ParsePdscJob;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.PackNode;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

	public static final int AUTOEXPAND_LEVEL = 0;

	// ------------------------------------------------------------------------

	// Embedded classes
	class ViewContentProvider extends AbstractViewContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {

			return getChildren(inputElement);
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			// System.out.println(viewer + " " + oldInput + " " + newInput);

			super.inputChanged(viewer, oldInput, newInput);

			// Always clear previous path
			m_packageAbsolutePath = null;

			if (newInput instanceof Node) {
				// newInput is an outline node
				String folder = ((Node) newInput)
						.getProperty(Property.DEST_FOLDER);
				if (folder != null) {
					try {
						m_packageAbsolutePath = Repos.getInstance()
								.getFolderPath().append(folder);
					} catch (IOException e) {
						;
					}
				}
			}
		}

	};

	// ------------------------------------------------------------------------

	class ViewLabelProvider extends CellLabelProvider {

		public String getText(Object obj) {

			Leaf node = ((Leaf) obj);
			String name = node.getName();

			return " " + name;
		}

		public Image getImage(Object obj) {

			Leaf node = ((Leaf) obj);
			String type = node.getType();

			if (Type.NONE.equals(type)) {
				return null;
			}

			String name = "";
			try {
				name = node.getName().toLowerCase();
			} catch (Exception e) {
				;
			}

			if (Type.FAMILY.equals(type) || Type.SUBFAMILY.equals(type)
					|| Type.DEVICE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/hardware_chip.png").createImage();
			} else if (Type.COMPATIBLEDEVICE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/hardware_chip_grey.png").createImage();
			} else if (Type.BOARD.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/board.png").createImage();
			} else if (Type.VERSION.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/jtypeassist_co.png").createImage();
			} else if (Type.EXAMPLE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/binaries_obj.gif" /* "icons/exec_obj.gif" */)
						.createImage();
			} else if (Type.COMPONENT.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/component.png" /* "icons/codeassist_co.gif" */)
						.createImage();
			} else if (Type.BUNDLE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/bundle.png" /* "icons/javaassist_co.png" */)
						.createImage();
			} else if (Type.CATEGORY.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/label_obj.gif").createImage();
			} else if (Type.KEYWORD.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/info_obj.png").createImage();
			} else if (Type.DEBUGINTERFACE.equals(type)
					|| Type.DEBUG.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/exec_dbg_obj.gif").createImage();
			} else if (Type.FEATURE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/genericvariable_obj.png").createImage();
			} else if (Type.BOOK.equals(type)) {
				String url = node.getProperty(Node.URL_PROPERTY, "");
				if (url.length() > 0) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/external_browser.png")
							.createImage();
				}
				String path = node.getProperty(Node.FILE_PROPERTY, "")
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
			} else if (Type.PROCESSOR.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/methpro_obj.png").createImage();
			} else if (Type.FILE.equals(type)) {
				String category = node.getProperty(Node.CATEGORY_PROPERTY, "");
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
			} else if (Type.HEADER.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/include_obj.gif").createImage();
			} else if (Type.DEFINE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/define_obj.gif").createImage();
			} else if (Type.MEMORY.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/memory_view.png").createImage();
			} else if (Type.TAXONOMY.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/jdoc_tag_obj.png").createImage();
			} else if (Type.CONDITION.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/smartmode_co.png").createImage();
			} else if (Type.REQUIRE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/default_co.png").createImage();
			} else if (Type.ACCEPT.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/method_public_obj.gif").createImage();
			} else if (Type.DENY.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/method_private_obj.gif").createImage();
			} else if (Type.PACKAGE.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/package_obj.png").createImage();
			} else if (Type.API.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/int_obj.gif").createImage();
			} else if (Type.ENVIRONMENT.equals(type)) {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/config-profile.gif").createImage();
			} else {
				return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
						"icons/unknown_obj.gif").createImage();
			}
		}

		@Override
		public String getToolTipText(Object obj) {

			Leaf node = ((Leaf) obj);

			String description = node.getDescription();
			if (description != null && description.length() > 0) {
				return description;
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

		public int compare(Viewer viewer, Object e1, Object e2) {
			// Always in order
			return 1;
		}
	}

	// ------------------------------------------------------------------------

	private TreeViewer m_viewer;
	private ISelectionListener m_pageSelectionListener;
	private ViewContentProvider m_contentProvider;

	private Action m_expandAll;
	private Action m_collapseAll;
	private Action m_doubleClickAction;
	private Action m_openWithText;
	private Action m_openWithSystem;

	// Needed to construct absolute path for double click actions
	private IPath m_packageAbsolutePath;

	private Node m_noOutlineNode;

	private PacksStorage m_storage;

	// private MessageConsoleStream m_out;

	public OutlineView() {
		// Activator.setDevicesView(this);

		m_noOutlineNode = new Node(Type.OUTLINE);
		m_noOutlineNode.setName("No outline");
		Node.addNewChild(m_noOutlineNode, Type.NONE).setName(
				"(Outline not available)");

		// m_out = Activator.getConsoleOut();

		m_storage = PacksStorage.getInstance();
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

		m_viewer.setInput(m_noOutlineNode);

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

		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// Called when the local view selection changes

				m_openWithText.setEnabled(false);
				m_openWithSystem.setEnabled(false);

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}

				Leaf node = (Leaf) selection.getFirstElement();
				String type = node.getType();

				// Enable 'Open With Text Editor'
				if (Type.FILE.equals(type)) {

					String category = node.getProperty(Node.CATEGORY_PROPERTY);
					if ("header".equals(category) || "source".equals(category)) {
						m_openWithText.setEnabled(true);
					}
				} else if (Type.HEADER.equals(type)) {
					m_openWithText.setEnabled(true);
				} else if (Type.DEBUG.equals(type)) {
					m_openWithText.setEnabled(true);
				}

				// Enable 'Open With System Editor'
				if (Type.FILE.equals(type)) {

					String category = node.getProperty(Node.CATEGORY_PROPERTY);
					if ("doc".equals(category)) {
						m_openWithSystem.setEnabled(true);
					}
				} else if (Type.DEBUG.equals(type)) {
					m_openWithSystem.setEnabled(true);
				} else if (Type.BOOK.equals(type)) {

					String relativeFile = node.getProperty(Node.FILE_PROPERTY,
							"");
					if (relativeFile.length() > 0) {
						m_openWithSystem.setEnabled(true);
					}

					String url = node.getProperty(Node.URL_PROPERTY, "");
					if (url.length() > 0) {
						m_openWithSystem.setEnabled(true);
					}
				}
			}
		});

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

	// Called when selection in other views change
	protected void packsViewSelectionChanged(IWorkbenchPart part,
			ISelection selection) {

		// System.out.println("Outline: packs selection=" + selection);

		Node outline = m_noOutlineNode;

		if ((!selection.isEmpty()) && selection instanceof TreeSelection) {

			// Limit to a single selection.
			Leaf node = (Leaf) ((TreeSelection) selection).getFirstElement();

			// Only PackNode can have an outline.
			if (node instanceof PackNode) {

				if (((PackNode) node).hasOutline()) {

					// If the node already has outline, show it
					m_viewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
					outline = ((PackNode) node).getOutline();

				} else if (node.isType(Type.VERSION)) {

					if (node.isBooleanProperty(Property.INSTALLED)) {
						// If the version node is installed, get outline
						ParsePdscJob job = new ParsePdscJob("Parse Outline",
								(PackNode) node, (PackNode) node, m_viewer);
						job.schedule();
					} else {

						// Get brief outline
						outline = getBriefOutline((Node) node);
						((PackNode) node).setOutline(outline);
					}
				} else if (node.isType(Type.PACKAGE)) {

					// Get brief outline
					outline = getBriefOutline((Node) node);
					((PackNode) node).setOutline(outline);

				} else if (node.isType(Type.EXAMPLE)) {

					Node versionNode = (Node) node.getParent();

					if (versionNode.isBooleanProperty(Property.INSTALLED)) {
						// If the version node is installed, get outline
						ParsePdscJob job = new ParsePdscJob("Parse Outline",
								(PackNode) versionNode, (PackNode) node,
								m_viewer);
						job.schedule();
					}
				}
			}
		}

		m_viewer.setInput(outline);

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

		m_openWithText = new Action() {
			public void run() {
				System.out.println("openWithText");

				ISelection selection = m_viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof Leaf) {
					try {
						openWithTextAction((Node) obj);
					} catch (PartInitException e) {
					}
				}
			}
		};

		m_openWithText.setText("Open With Text Viewer");
		m_openWithText.setEnabled(false);

		m_openWithSystem = new Action() {
			public void run() {
				System.out.println("openWithSystem");

				ISelection selection = m_viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof Node) {
					try {
						openWithSystemAction((Node) obj);
					} catch (PartInitException e) {
					} catch (MalformedURLException e) {
					}
				}
			}
		};

		m_openWithSystem.setText("Open With System Viewer");
		m_openWithSystem.setEnabled(false);

		m_doubleClickAction = new Action() {
			public void run() {
				ISelection selection = m_viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof Node) {
					try {
						doubleClickAction((Node) obj);
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
		manager.add(m_openWithText);
		manager.add(m_openWithSystem);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(m_expandAll);
		manager.add(m_collapseAll);
	}

	private void doubleClickAction(Node node) throws PartInitException,
			MalformedURLException {

		String type = node.getType();
		if (m_packageAbsolutePath == null) {
			System.out.println("null m_packagePath");
			return;
		}

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		if (Type.FILE.equals(type)) {

			String category = node.getProperty(Node.CATEGORY_PROPERTY);
			String relativeFile = node.getProperty(Node.FILE_PROPERTY);
			if ("header".equals(category) || "source".equals(category)) {

				// System.out.println("Edit " + relativeFile);
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						m_packageAbsolutePath.append(relativeFile));

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only
				IDE.openInternalEditorOnFileStore(page, fileStore);

			} else if ("doc".equals(category)) {

				// System.out.println("Document " + node);
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						m_packageAbsolutePath.append(relativeFile));
				// System.out.println(fileStore);

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only
				IDE.openEditorOnFileStore(page, fileStore);

			} else if ("include".equals(category) || "library".equals(category)) {
				; // ignore folders
			} else {
				System.out.println("File " + node + "  " + category);
			}

		} else if (Type.BOOK.equals(type)) {

			String url = node.getProperty(Node.URL_PROPERTY, "");
			String relativeFile = node.getProperty(Node.FILE_PROPERTY, "");
			if (url.length() > 0) {

				// System.out.println("Open " + url);
				PlatformUI.getWorkbench().getBrowserSupport()
						.getExternalBrowser().openURL(new URL(url));

			} else if (relativeFile.length() > 0) {

				// System.out.println("Path " + relativeFile);
				IFileStore fileStore = EFS.getLocalFileSystem().getStore(
						m_packageAbsolutePath.append(relativeFile));

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only
				IDE.openEditorOnFileStore(page, fileStore);

			} else {
				System.out.println("Book " + node);
			}

		} else if (Type.HEADER.equals(type)) {

			// System.out.println("Header " + node );

			String relativeFile = node.getProperty(Node.FILE_PROPERTY);
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					m_packageAbsolutePath.append(relativeFile));

			// Open external file in Eclipse editor (as read only, since the
			// packages were marked as read only
			IDE.openInternalEditorOnFileStore(page, fileStore);

		} else if (Type.DEBUG.equals(type)) {

			// System.out.println("Debug " + node );

			String relativeFile = node.getProperty(Node.FILE_PROPERTY);
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					m_packageAbsolutePath.append(relativeFile));

			// Open external file in Eclipse editor (as read only, since the
			// packages were marked as read only
			IDE.openInternalEditorOnFileStore(page, fileStore);

		} else {
			// System.out.println("Double-click detected on " + node + " " +
			// type);
		}
	}

	private void openWithTextAction(Leaf node) throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		String relativeFile = node.getProperty(Node.FILE_PROPERTY, "");

		assert (m_packageAbsolutePath != null);

		if (relativeFile.length() > 0) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					m_packageAbsolutePath.append(relativeFile));
			IDE.openInternalEditorOnFileStore(page, fileStore);
		}
	}

	private void openWithSystemAction(Leaf node) throws PartInitException,
			MalformedURLException {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		String relativeFile = node.getProperty(Node.FILE_PROPERTY, "");
		if (relativeFile.length() > 0) {
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(
					m_packageAbsolutePath.append(relativeFile));
			IDE.openEditorOnFileStore(page, fileStore);
			return;
		}

		String url = node.getProperty(Node.URL_PROPERTY, "");
		if (url.length() > 0) {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL(url));
			return;
		}
	}

	// ------------------------------------------------------------------------

	private Node getBriefOutline(Node node) {

		Node outlineNode = m_noOutlineNode;

		Node input = null;
		// Identify original node
		if (node.isType(Type.PACKAGE)) {

			String vendorName = node.getProperty(Property.VENDOR_NAME);
			String packName = node.getName();

			input = (Node) m_storage.getPackLatest(vendorName, packName);

		} else if (node.isType(Type.VERSION)) {

			String vendorName = node.getProperty(Property.VENDOR_NAME);
			String packName = node.getProperty(Property.PACK_NAME);
			String versionName = node.getName();

			input = (Node) m_storage.getPackVersion(vendorName, packName,
					versionName);
		}

		assert (input != null);

		if (input.isType(Type.VERSION)) {

			Node oNode = (Node) input.getChild(Type.OUTLINE);
			if (oNode != null && oNode.hasChildren()) {
				outlineNode = new Node(Type.OUTLINE);
				outlineNode.setName("Brief Outline");

				Leaf leaf = Leaf.addNewChild(outlineNode, Type.PACKAGE);
				leaf.setName(input.getProperty(Property.PACK_NAME));

				leaf = Leaf.addNewChild(outlineNode, Type.VERSION);
				leaf.setName(input.getName());

				for (Leaf child : oNode.getChildren()) {

					if (child.isType(Type.KEYWORD)) {
						// Ignore keywords inside outline
						continue;
					}

					// Create copies of outline nodes
					leaf = Leaf.addNewChild(outlineNode, child);
				}
			}
		}

		return outlineNode;
	}

	// ------------------------------------------------------------------------
}
