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

import ilg.gnuarmeclipse.core.CProjectPacksStorage;
import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.managedbuild.cross.IDs;
import ilg.gnuarmeclipse.managedbuild.packs.Activator;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.data.IDataManagerListener;
import ilg.gnuarmeclipse.packs.ui.IconUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.internal.core.model.CElement;
import org.eclipse.cdt.internal.ui.cview.IncludeRefContainer;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.eclipse.ui.part.ViewPart;

@SuppressWarnings("restriction")
public class DocsView extends ViewPart implements IDataManagerListener, IPropertyChangeListener {

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
				return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
			} else if (Type.BOOK.equals(type)) {
				return IconUtils.getBookIcon(node);
			} else {
				return null;
			}
		}

		@Override
		public String getToolTipText(Object obj) {

			Leaf node = ((Leaf) obj);

			String description = null;
			if (node.isType(Type.BOOK)) {
				description = "Document: " + node.getName();

				String category = node.getProperty(Property.CATEGORY);
				if (category.length() > 0) {
					description += "\n" + "category: " + category;
				}
				String file = node.getProperty(Property.FILE_ABSOLUTE);
				if (file.length() > 0) {
					description += "\n" + "file: " + file;
				}
				String url = node.getProperty(Property.URL);
				if (url.length() > 0) {
					description += "\n" + "url: " + url;
				}
			}
			return description;
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
	private ISelectionListener fPostSelectionListener;
	private IPartListener fPartListener;

	private ViewContentProvider fContentProvider;

	// private PacksStorage fStorage;
	private DataManager fDataManager;
	private Action fDoubleClickAction;
	private Action fRightClickOpen;

	private IConfiguration fConfig;
	private IConfiguration fLatestSelectedConfig;

	// private MessageConsoleStream fOut;

	public DocsView() {

		// fOut = ConsoleStream.getConsoleOut();

		// fStorage = PacksStorage.getInstance();
		fDataManager = DataManager.getInstance();
		fConfig = null;
		fLatestSelectedConfig = null;
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialise
	 * it.
	 */
	public void createPartControl(Composite parent) {

		// System.out.println("DocsView.createPartControl()");

		fViewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

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

		fLatestSelectedConfig = getConfigurationForProject(EclipseUtils.getSelectedProject());
		setInputForConfig(fLatestSelectedConfig, true);

		addProviders();
		addListners();

		hookPostSelectionListener();
		hookPartListener();

		makeActions();

		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void dispose() {

		super.dispose();
		fDataManager.removeListener(this);

		if (fPostSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(fPostSelectionListener);
		}

		if (fPartListener != null) {
			getSite().getPage().removePartListener(fPartListener);
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DocsView.dispose()");
		}
	}

	private void addProviders() {

		// Register this viewer as the selection provider
		getSite().setSelectionProvider(fViewer);
	}

	private void addListners() {
		// None
	}

	// ------------------------------------------------------------------------

	/**
	 * Notification when the TabDevices changes.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		// System.out.println(event + " " + event.getSource());
		Object source = event.getSource();
		if (source instanceof IProject) {
			// fViewer.setInput(getDocsTree());
			if (EclipseUtils.getSelectedProject() == (IProject) source) {
				fLatestSelectedConfig = getConfigurationForProject(EclipseUtils.getSelectedProject());
				setInputForConfig(fLatestSelectedConfig, false);
			}
		}
	}

	/**
	 * Update input based on selected project or project resource.
	 */
	private void hookPostSelectionListener() {

		fPostSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {

				if ((!selection.isEmpty()) && selection instanceof IStructuredSelection) {

					if (part instanceof ProjectExplorer) {

						// Accept selection only from the Project Explorer
						// view
						IStructuredSelection structSel = (IStructuredSelection) selection;
						if (structSel.size() == 1) {
							Object firstSel = structSel.getFirstElement();

							IProject project = null;
							if (firstSel instanceof IProject) {
								project = (IProject) firstSel;
							} else if (firstSel instanceof IncludeRefContainer) {
								project = ((IncludeRefContainer) firstSel).getCProject().getProject();
							} else if (firstSel instanceof CElement) {
								project = ((CElement) firstSel).getCProject().getProject();
							} else if (firstSel instanceof File) {
								project = ((File) firstSel).getProject();
							} else if (firstSel instanceof Folder) {
								project = ((Folder) firstSel).getProject();
							} else {
								Activator.log(firstSel + " ignored");
							}

							fLatestSelectedConfig = getConfigurationForProject(project);
							setInputForConfig(fLatestSelectedConfig, false);
						}
					}
				}
			}
		};
		getSite().getPage().addPostSelectionListener(fPostSelectionListener);
		// Don't forget to remove it at dispose()
	}

	/**
	 * Detect when DocsView needs to update its input, by listening to parts
	 * changes.
	 */
	private void hookPartListener() {

		fPartListener = new IPartListener() {

			@Override
			public void partActivated(IWorkbenchPart part) {

				if (part instanceof DocsView) {
					if (Activator.getInstance().isDebugging()) {
						System.out.println("partActivated() " + part);
					}

					setInputForConfig(fLatestSelectedConfig, false);
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				;
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				;
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				;
			}

			@Override
			public void partOpened(IWorkbenchPart part) {
				;
			}
		};

		getSite().getPage().addPartListener(fPartListener);
		// Don't forget to remove it at dispose()
	}

	private IConfiguration getConfigurationForProject(IProject project) {

		IConfiguration newConfig = null;
		try {

			if (project != null && project.hasNature("org.eclipse.cdt.core.cnature")) {
				IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
				IConfiguration config = info.getDefaultConfiguration();

				IToolChain toolchain = config.getToolChain();
				String sToolchainId = toolchain.getBaseId();
				if (sToolchainId.startsWith(IDs.TOOLCHAIN_ID + ".")) {

					// Only our projects are candidates
					newConfig = config;
				}
			}
		} catch (CoreException e) {
			;
		}

		return newConfig;
	}

	private void setInputForConfig(IConfiguration newConfig, boolean force) {

		// Perform this complicated thing only if the view is visible
		if (getSite().getPage().isPartVisible(this)) {

			if (force || ((newConfig != null && !newConfig.equals(fConfig))
					|| (fConfig != null && !fConfig.equals(newConfig)))) {
				fConfig = newConfig;
				fViewer.setInput(getDocsTree());
			}
		}
	}

	private void makeActions() {

		fDoubleClickAction = new Action() {
			public void run() {

				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof Leaf) {
					try {
						doubleClickAction((Leaf) obj);
					} catch (PartInitException e) {
					} catch (IOException e) {
					}
				}
			}
		};

		fRightClickOpen = new Action() {
			public void run() {

				ISelection selection = fViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				if (obj instanceof Leaf) {
					try {
						rightClickAction((Leaf) obj);
					} catch (PartInitException e) {
					} catch (IOException e) {
					}
				}
			}
		};
		fRightClickOpen.setText("Open");
		fRightClickOpen.setEnabled(true);

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
		; // none
	}

	private void fillContextMenu(IMenuManager manager) {

		manager.add(fRightClickOpen);

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void hookDoubleClickAction() {

		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				fDoubleClickAction.run();
			}
		});
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

		// System.out.println("DocsView.sView.refresh() " + obj);
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
	}

	public String toString() {
		return "DocsView";
	}

	// ------------------------------------------------------------------------

	private void doubleClickAction(Leaf node) throws PartInitException, IOException {

		openBook(node);
	}

	private void rightClickAction(Leaf node) throws PartInitException, IOException {

		openBook(node);
	}

	private void openBook(Leaf node) throws PartInitException, IOException {

		if (node.isType(Type.BOOK)) {

			String url = node.getProperty(Property.URL);
			String absoluteFile = node.getProperty(Property.FILE_ABSOLUTE);
			if (url.length() > 0) {
				EclipseUtils.openExternalBrowser(new URL(url));
			} else if (absoluteFile.length() > 0) {
				IPath path = PacksStorage.getFolderPath().append(absoluteFile);
				EclipseUtils.openExternalFile(path);
			}
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public void packsChanged(DataManagerEvent event) {

		String type = event.getType();
		// System.out.println("DocsView.packsChanged(), type=\"" + type +
		// "\".");

		if (DataManagerEvent.Type.NEW_INPUT.equals(type)) {

			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("DevicesView NEW_INPUT");

					fViewer.setInput(getDocsTree());
				}
			});

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

		Node tree = DataManager.getInstance().getInstalledObjectsForBuild();

		Node devicesRoot = new Node(Type.ROOT);
		devicesRoot.setName("Docs");

		CProjectPacksStorage st;
		try {
			st = new CProjectPacksStorage(fConfig);

			String deviceVendorId = st.getOption(CProjectPacksStorage.DEVICE_VENDOR_ID);
			String deviceName = st.getOption(CProjectPacksStorage.DEVICE_NAME);

			String boardVendorName = st.getOption(CProjectPacksStorage.BOARD_VENDOR_NAME);
			String boardName = st.getOption(CProjectPacksStorage.BOARD_NAME);

			if (deviceName != null) {
				Node deviceDocsNode = Node.addNewChild(devicesRoot, Type.FOLDER);
				deviceDocsNode.setName(deviceName + " device docs");

				Node devicesSubtree = (Node) tree.findChild(Type.DEVICES_SUBTREE);
				if (devicesSubtree != null) {

					copyDeviceDocs(devicesSubtree, deviceVendorId, deviceName, deviceDocsNode);
				}
			}

			if (boardName != null) {
				Node boardDocsNode = Node.addNewChild(devicesRoot, Type.FOLDER);
				boardDocsNode.setName(boardName + " board docs");

				Node boardsSubtree = (Node) tree.findChild(Type.BOARDS_SUBTREE);
				if (boardsSubtree != null) {

					copyBoardDocs(boardsSubtree, boardVendorName, boardName, boardDocsNode);
				}
			}
		} catch (CoreException e) {
			;
		}

		if (!devicesRoot.hasChildren()) {
			Node emptyNode = Node.addNewChild(devicesRoot, Type.NONE);
			emptyNode.setName("No documents are available.");
		}

		return devicesRoot;
	}

	private void copyBoardDocs(Node boards, String boardVendorName, String boardName, Node parent) {

		// Identify vendor
		Node vendor = (Node) boards.findChild(Type.VENDOR, boardVendorName);
		if (vendor != null && vendor.hasChildren()) {

			for (Leaf board : vendor.getChildren()) {

				// Identify board
				if (board.isType(Type.BOARD) && boardName.equals(board.getName())) {

					String destFolder = fDataManager.getDestinationFolder(board);

					if (board != null && board.hasChildren()) {
						for (Leaf bookNode : ((Node) board).getChildren()) {

							if (bookNode.isType(Type.BOOK)) {
								// Copy book nodes
								Leaf newBook = Leaf.addNewChild(parent, Type.BOOK);
								newBook.copyProperties(bookNode);

								if (newBook.hasProperty(Property.FILE)) {
									IPath path = new Path(destFolder).append(newBook.getProperty(Property.FILE));
									newBook.putProperty(Property.FILE_ABSOLUTE, path.toString());
								}
							}
						}
					}
				}
			}
		}
	}

	private void copyDeviceDocs(Node devicesSubtree, String deviceVendorId, String deviceName, Node parent) {

		for (Leaf vendor : devicesSubtree.getChildren()) {

			// Identify vendor by vendor ID, not name
			if (vendor.isType(Type.VENDOR) && deviceVendorId.equals(vendor.getProperty(Property.VENDOR_ID))) {

				ITreeIterator deviceNodes = new AbstractTreePreOrderIterator() {

					@Override
					public boolean isIterable(Leaf node) {

						if (node.isType(Type.DEVICE)) {
							return true;
						}
						return false;
					}

					@Override
					public boolean isLeaf(Leaf node) {

						if (node.isType(Type.DEVICE)) {
							return true;
						}
						return false;
					}

				};
				// Iterate only the current vendor devices
				deviceNodes.setTreeNode(vendor);

				for (Leaf deviceNode : deviceNodes) {

					// Identify device
					if (deviceName.equals(deviceNode.getName())) {

						Leaf node = deviceNode;

						String destFolder = fDataManager.getDestinationFolder(node);
						do {
							if (node.hasChildren()) {
								for (Leaf bookNode : ((Node) node).getChildren()) {
									if (bookNode.isType(Type.BOOK)) {
										// Copy book nodes
										Leaf newBook = Leaf.addNewChild(parent, Type.BOOK);
										newBook.copyProperties(bookNode);

										if (newBook.hasProperty(Property.FILE)) {
											IPath path = new Path(destFolder)
													.append(newBook.getProperty(Property.FILE));
											newBook.putProperty(Property.FILE_ABSOLUTE, path.toString());
										}
									}
								}
							}
							node = node.getParent();
							// collect subfamily and family books too
						} while (node != null && !node.isType(Type.DEVICES_SUBTREE));
					}
				}
			}
		}
	}

	// ------------------------------------------------------------------------
}