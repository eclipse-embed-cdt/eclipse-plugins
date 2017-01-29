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

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.jobs.ParsePdscRunnable;
import ilg.gnuarmeclipse.packs.ui.Activator;
import ilg.gnuarmeclipse.packs.ui.IconUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;

public class OutlineView extends ViewPart {

	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.OutlineView";

	public static final int AUTOEXPAND_LEVEL = 0;

	// ------------------------------------------------------------------------

	// Embedded classes
	class ViewContentProvider extends NodeViewContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {

			return getChildren(inputElement);
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

			// System.out.println(viewer + " " + oldInput + " " + newInput);

			super.inputChanged(viewer, oldInput, newInput);

			// Always clear previous path
			fPackageAbsolutePath = null;

			if (newInput instanceof Node) {
				// newInput is an outline node
				String folder = ((Node) newInput).getProperty(Property.DEST_FOLDER);
				if (folder.length() > 0) {
					try {
						fPackageAbsolutePath = PacksStorage.getFolderPath().append(folder);
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

			if (Type.FAMILY.equals(type) || Type.SUBFAMILY.equals(type) || Type.DEVICE.equals(type)
					|| Type.VARIANT.equals(type)) {
				return Activator.getInstance().getImage("hardware_chip");
			} else if (Type.COMPATIBLEDEVICE.equals(type)) {
				return Activator.getInstance().getImage("hardware_chip_grey");
			} else if (Type.BOARD.equals(type)) {
				return Activator.getInstance().getImage("board");
			} else if (Type.VERSION.equals(type)) {
				return Activator.getInstance().getImage("jtypeassist_co");
			} else if (Type.EXAMPLE.equals(type)) {
				return Activator.getInstance().getImage("binaries_obj" /* "icons/exec_obj.gif" */);
			} else if (Type.COMPONENT.equals(type)) {
				return Activator.getInstance().getImage("component" /* "icons/codeassist_co.gif" */);
			} else if (Type.BUNDLE.equals(type)) {
				return Activator.getInstance().getImage("bundle" /* "icons/javaassist_co.png" */);
			} else if (Type.CATEGORY.equals(type)) {
				return Activator.getInstance().getImage("label_obj");
			} else if (Type.KEYWORD.equals(type)) {
				return Activator.getInstance().getImage("info_obj");
			} else if (Type.DEBUGINTERFACE.equals(type) || Type.DEBUG.equals(type)) {
				return Activator.getInstance().getImage("exec_dbg_obj");
			} else if (Type.FEATURE.equals(type)) {
				return Activator.getInstance().getImage("genericvariable_obj");
			} else if (Type.BOOK.equals(type)) {
				return IconUtils.getBookIcon(node);
			} else if (Type.PROCESSOR.equals(type)) {
				return Activator.getInstance().getImage("methpro_obj");
			} else if (Type.FILE.equals(type)) {
				String category = node.getProperty(Property.CATEGORY);
				if ("source".equals(category)) {
					if (name.endsWith(".s")) {
						Activator.getInstance().getImage("s_file_obj");
					} else {
						return Activator.getInstance().getImage("c_file_obj");
					}
				} else if ("header".equals(category)) {
					return Activator.getInstance().getImage("h_file_obj");
				} else if ("include".equals(category)) {
					return Activator.getInstance().getImage("includes_container");
				} else if ("library".equals(category)) {
					return Activator.getInstance().getImage("ar_obj");
				} else if ("doc".equals(category)) {
					if (name.endsWith(".pdf")) {
						return Activator.getInstance().getImage("pdficon_small");
					} else if (name.endsWith(".html")) {
						return Activator.getInstance().getImage("external_browser");
					} else {
						return Activator.getInstance().getImage("file_obj");
					}
				} else {
					return Activator.getInstance().getImage("file_obj");
				}
			} else if (Type.HEADER.equals(type)) {
				return Activator.getInstance().getImage("include_obj");
			} else if (Type.DEFINE.equals(type)) {
				return Activator.getInstance().getImage("define_obj");
			} else if (Type.MEMORY.equals(type)) {
				return Activator.getInstance().getImage("memory_view");
			} else if (Type.TAXONOMY.equals(type)) {
				return Activator.getInstance().getImage("jdoc_tag_obj");
			} else if (Type.CONDITION.equals(type)) {
				return Activator.getInstance().getImage("smartmode_co");
			} else if (Type.REQUIRE.equals(type)) {
				return Activator.getInstance().getImage("default_co");
			} else if (Type.ACCEPT.equals(type)) {
				return Activator.getInstance().getImage("method_public_obj");
			} else if (Type.DENY.equals(type)) {
				return Activator.getInstance().getImage("method_private_obj");
			} else if (Type.PACKAGE.equals(type)) {
				return Activator.getInstance().getImage("package_obj");
			} else if (Type.API.equals(type)) {
				return Activator.getInstance().getImage("int_obj");
			} else if (Type.ENVIRONMENT.equals(type)) {
				return Activator.getInstance().getImage("config-profile");
			} else {
				return Activator.getInstance().getImage("unknown_obj");
			}
			return null;
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

	private TreeViewer fViewer;
	private ISelectionListener fPageSelectionListener;
	private ViewContentProvider fContentProvider;

	private Action fExpandAll;
	private Action fCollapseAll;
	private Action fDoubleClickAction;
	private Action fOpenWithText;
	private Action fOpenWithSystem;

	// Needed to construct absolute path for double click actions
	private IPath fPackageAbsolutePath;

	private Node fNoOutlineNode;

	private DataManager fDataManager;

	// private MessageConsoleStream m_out;

	public OutlineView() {
		// Activator.setDevicesView(this);

		fNoOutlineNode = new Node(Type.OUTLINE);
		fNoOutlineNode.setName("No outline");
		Node.addNewChild(fNoOutlineNode, Type.NONE).setName("An outline is not available.");

		fDataManager = DataManager.getInstance();
	}

	@Override
	public void createPartControl(Composite parent) {

		// System.out.println("OutlineView.createPartControl()");

		fViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

		ColumnViewerToolTipSupport.enableFor(fViewer);

		fContentProvider = new ViewContentProvider();
		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(new ViewLabelProvider());
		fViewer.setSorter(new NameSorter());

		fViewer.setInput(fNoOutlineNode);

		addProviders();
		addListners();
		hookPostSelectionListener();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	@Override
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	/**
	 * Register this viewer as source of events.
	 */
	private void addProviders() {
		// Register this viewer as the selection provider
		getSite().setSelectionProvider(fViewer);
	}

	/**
	 * Register listeners for local events.
	 */
	private void addListners() {

		// Register listener for local selection changes
		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				// Called when the local view selection changes, to update the
				// two right click actions.

				fOpenWithText.setEnabled(false);
				fOpenWithSystem.setEnabled(false);

				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}

				Leaf node = (Leaf) selection.getFirstElement();
				String type = node.getType();

				// Enable 'Open With Text Editor'
				if (Type.FILE.equals(type)) {

					String category = node.getProperty(Node.CATEGORY_PROPERTY);
					if ("header".equals(category) || category.startsWith("source") || "linkerScript".equals(category)) {
						fOpenWithText.setEnabled(true);
					}
				} else if (Type.HEADER.equals(type)) {
					fOpenWithText.setEnabled(true);
				} else if (Type.DEBUG.equals(type)) {
					fOpenWithText.setEnabled(true);
				}

				// Enable 'Open With System Editor'
				if (Type.FILE.equals(type)) {

					String category = node.getProperty(Node.CATEGORY_PROPERTY);
					if ("doc".equals(category)) {
						fOpenWithSystem.setEnabled(true);
					}
				} else if (Type.DEBUG.equals(type)) {
					fOpenWithSystem.setEnabled(true);
				} else if (Type.BOOK.equals(type)) {

					String relativeFile = node.getProperty(Node.FILE_PROPERTY);
					if (relativeFile.length() > 0) {
						fOpenWithSystem.setEnabled(true);
					}

					String url = node.getProperty(Node.URL_PROPERTY);
					if (url.length() > 0) {
						fOpenWithSystem.setEnabled(true);
					}
				}
			}
		});

	}

	/**
	 * Hook to the global selection bus.
	 */
	private void hookPostSelectionListener() {

		fPageSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {

				if (part instanceof PacksView) {
					// The selection comes from the packs view
					packsViewSelectionChanged(part, selection);
				} else {
					// System.out.println("Outline: " + part + " selection="
					// + selection);
				}
			}
		};
		getSite().getPage().addPostSelectionListener(fPageSelectionListener);
	}

	public void dispose() {

		super.dispose();

		if (fPageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(fPageSelectionListener);
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("OutlineView.dispose()");
		}
	}

	/**
	 * Called when selection in the Packs view changes.
	 * 
	 * @param part
	 *            the Packs view.
	 * @param selection
	 *            a TreeSelection with a PackNode as first element.
	 */
	protected void packsViewSelectionChanged(IWorkbenchPart part, ISelection selection) {

		// System.out.println("Outline: packs selection=" + selection);

		Node outline = fNoOutlineNode;

		if ((!selection.isEmpty()) && selection instanceof TreeSelection) {

			// Limit to a single selection, the first one.
			Leaf node = (Leaf) ((TreeSelection) selection).getFirstElement();

			// Only PackNode can have an outline.
			if (node instanceof PackNode) {

				if (((PackNode) node).hasOutline()) {

					// If the node already has outline, show it
					outline = ((PackNode) node).getOutline();

				} else if (node.isType(Type.VERSION)) {

					if (node.isBooleanProperty(Property.INSTALLED)) {

						// If the version node is installed, get outline.
						parseOutline((PackNode) node);
						outline = ((PackNode) node).getOutline();
					} else {

						// For not installed nodes, get brief outline.
						outline = getBriefOutline((Node) node);
						((PackNode) node).setOutline(outline);
					}
				} else if (node.isType(Type.PACKAGE)) {

					// Get package brief outline.
					outline = getBriefOutline((Node) node);
					((PackNode) node).setOutline(outline);

				} else if (node.isType(Type.EXAMPLE)) {

					Node versionNode = (Node) node.getParent();

					if (versionNode.isBooleanProperty(Property.INSTALLED)) {

						// If the example version node is installed, get outline
						parseOutline((PackNode) versionNode);
						outline = ((PackNode) node).getOutline();
					}
				}
			}
		}

		fViewer.setAutoExpandLevel(OutlineView.AUTOEXPAND_LEVEL);
		fViewer.setInput(outline);
	}

	/**
	 * Parse the node outline.
	 * 
	 * @param versionNode
	 * @return
	 */
	private void parseOutline(PackNode versionNode) {

		// If the version node is installed, get outline
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		try {
			progressService.busyCursorWhile(new ParsePdscRunnable("Parse Outline", (PackNode) versionNode));

		} catch (InvocationTargetException e1) {
			Activator.log(e1);
		} catch (InterruptedException e1) {
			Activator.log(e1);
		}

	}

	private void makeActions() {

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

		fOpenWithText = new Action() {
			public void run() {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("openWithText");
				}

				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof Leaf) {
					try {
						openWithTextAction((Node) obj);
					} catch (PartInitException e) {
					}
				}
			}
		};

		fOpenWithText.setText("Open With Text Viewer");
		fOpenWithText.setEnabled(false);

		fOpenWithSystem = new Action() {
			public void run() {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("openWithSystem");
				}

				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof Node) {
					try {
						openWithSystemAction((Node) obj);
					} catch (PartInitException e) {
					} catch (MalformedURLException e) {
					}
				}
			}
		};

		fOpenWithSystem.setText("Open With System Viewer");
		fOpenWithSystem.setEnabled(false);

		fDoubleClickAction = new Action() {
			public void run() {
				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
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
		Menu menu = menuMgr.createContextMenu(fViewer.getControl());
		fViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fViewer);
	}

	private void hookDoubleClickAction() {
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				fDoubleClickAction.run();
			}
		});
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(fExpandAll);
		manager.add(fCollapseAll);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(fOpenWithText);
		manager.add(fOpenWithSystem);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(fExpandAll);
		manager.add(fCollapseAll);
	}

	private void doubleClickAction(Node node) throws PartInitException, MalformedURLException {

		String type = node.getType();
		if (fPackageAbsolutePath == null) {
			Activator.log("doubleClickAction() null fPackageAbsolutePath");
			return;
		}

		if (Type.FILE.equals(type)) {

			String category = node.getProperty(Node.CATEGORY_PROPERTY);
			String relativeFile = node.getProperty(Node.FILE_PROPERTY);
			if ("header".equals(category) || category.startsWith("source") || "linkerScript".equals(category)) {

				// System.out.println("Edit " + relativeFile);

				// Open external file in Eclipse editor (as read only, since the
				// packages were marked as read only.
				EclipseUtils.openFileWithInternalEditor(fPackageAbsolutePath.append(relativeFile));

			} else if ("doc".equals(category)) {

				// System.out.println("Document " + node);
				EclipseUtils.openExternalFile(fPackageAbsolutePath.append(relativeFile));

			} else if ("include".equals(category) || "library".equals(category)) {
				; // ignore folders
			} else {
				Activator.log("File " + node + "  " + category + " ignored");
			}

		} else if (Type.BOOK.equals(type)) {

			String url = node.getProperty(Node.URL_PROPERTY);
			String relativeFile = node.getProperty(Node.FILE_PROPERTY);
			if (url.length() > 0) {

				// System.out.println("Open " + url);
				EclipseUtils.openExternalBrowser(new URL(url));

			} else if (relativeFile.length() > 0) {

				// System.out.println("Path " + relativeFile);
				EclipseUtils.openExternalFile(fPackageAbsolutePath.append(relativeFile));

			} else {
				Activator.log("Book " + node + " ignored");
			}

		} else if (Type.HEADER.equals(type)) {

			// System.out.println("Header " + node );

			String relativeFile = node.getProperty(Node.FILE_PROPERTY);

			// Open external file in Eclipse editor (as read only, since the
			// packages were marked as read only
			EclipseUtils.openFileWithInternalEditor(fPackageAbsolutePath.append(relativeFile));

		} else if (Type.DEBUG.equals(type)) {

			// System.out.println("Debug " + node );

			String relativeFile = node.getProperty(Node.FILE_PROPERTY);

			// Open external file in Eclipse editor (as read only, since the
			// packages were marked as read only
			EclipseUtils.openFileWithInternalEditor(fPackageAbsolutePath.append(relativeFile));
		} else {
			// System.out.println("Double-click detected on " + node + " " +
			// type);
		}
	}

	private void openWithTextAction(Leaf node) throws PartInitException {

		String relativeFile = node.getProperty(Node.FILE_PROPERTY);

		if (relativeFile.length() > 0) {

			assert (fPackageAbsolutePath != null);
			EclipseUtils.openFileWithInternalEditor(fPackageAbsolutePath.append(relativeFile));
		}
	}

	private void openWithSystemAction(Leaf node) throws PartInitException, MalformedURLException {

		String relativeFile = node.getProperty(Node.FILE_PROPERTY);
		if (relativeFile.length() > 0) {

			assert (fPackageAbsolutePath != null);
			EclipseUtils.openExternalFile(fPackageAbsolutePath.append(relativeFile));
			return;
		}

		String url = node.getProperty(Node.URL_PROPERTY);
		if (url.length() > 0) {

			EclipseUtils.openExternalBrowser(new URL(url));
			return;
		}
	}

	// ------------------------------------------------------------------------

	private Node getBriefOutline(Node node) {

		Node outlineNode = fNoOutlineNode;

		Node input = null;
		// Identify original node
		if (node.isType(Type.PACKAGE)) {

			String vendorName = node.getProperty(Property.VENDOR_NAME);
			String packName = node.getName();

			input = (Node) fDataManager.findPackLatest(vendorName, packName);

		} else if (node.isType(Type.VERSION)) {

			String vendorName = node.getProperty(Property.VENDOR_NAME);
			String packName = node.getProperty(Property.PACK_NAME);
			String versionName = node.getName();

			input = (Node) fDataManager.findPackVersion(vendorName, packName, versionName);
		}

		assert (input != null);

		if (input.isType(Type.VERSION)) {

			Node oNode = (Node) input.findChild(Type.OUTLINE);
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
