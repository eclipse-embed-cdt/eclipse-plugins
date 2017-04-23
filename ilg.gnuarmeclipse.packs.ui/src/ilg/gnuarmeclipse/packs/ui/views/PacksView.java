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

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.DurationMonitor;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Selector;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.DataManagerEvent;
import ilg.gnuarmeclipse.packs.data.IDataManagerListener;
import ilg.gnuarmeclipse.packs.jobs.CopyExampleJob;
import ilg.gnuarmeclipse.packs.jobs.InstallJob;
import ilg.gnuarmeclipse.packs.jobs.RemoveJob;
import ilg.gnuarmeclipse.packs.ui.Activator;
import ilg.gnuarmeclipse.packs.ui.Messages;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IServiceLocator;

import com.github.zafarkhaja.semver.Version;

public class PacksView extends ViewPart implements IDataManagerListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "ilg.gnuarmeclipse.packs.ui.views.PackagesView";

	private static final int AUTOEXPAND_LEVEL = 2;

	// ------------------------------------------------------------------------

	class ViewContentProvider extends NodeViewContentProvider {

		// public Object[] getElements(Object inputElement) {
		// Object[] children = getChildren(inputElement);
		// m_out.print("getElements() =");
		// for (Object child : children) {
		// m_out.print(" " + child.toString());
		// }
		// m_out.println();
		// return children;
		// }

	}

	// ------------------------------------------------------------------------

	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object obj, int columnIndex) {

			switch (columnIndex) {
			case 0:
				// String imageKey;
				Leaf node = ((Leaf) obj);
				String type = node.getType();

				if (Type.VENDOR.equals(type)) {
					// imageKey = ISharedImages.IMG_OBJ_FOLDER;
					// return PlatformUI.getWorkbench().getSharedImages()
					// .getImage(imageKey);
					return Activator.getInstance().getImage("pack_folder");
				} else if (Type.PACKAGE.equals(type)) {
					if (node.isBooleanProperty(Property.INSTALLED)) {
						return Activator.getInstance().getImage("package_obj");
					} else {
						return Activator.getInstance().getImage("package_obj_grey");
					}
				} else if (Type.VERSION.equals(type)) {
					if (node.isBooleanProperty(Property.INSTALLED)) {
						return Activator.getInstance().getImage("jtypeassist_co");
					} else {
						return Activator.getInstance().getImage("jtypeassist_co_grey");
					}
				} else if (Type.EXAMPLE.equals(type)) {
					return Activator.getInstance().getImage("binaries_obj");
				}
			}
			return null;
		}

		public String getColumnText(Object obj, int columnIndex) {

			Leaf node = ((Leaf) obj);

			switch (columnIndex) {
			case 0:
				String name = node.getName();
				if (node.isBooleanProperty(Property.INSTALLED)) {
					name += " (installed)";
				} else {
					if (node.isType(Type.VERSION)) {
						String size = node.getProperty(Property.ARCHIVE_SIZE);
						if (size.length() > 0) {
							try {
								int n = Integer.parseInt(size);
								if (n <= 0) {
									name += " (n/a)";
								} else {
									name += " (" + StringUtils.convertSizeToString(n) + ")";
								}
							} catch (NumberFormatException e) {
								;
							}
						}
					}
				}
				return " " + name;

			case 1:
				// On Linux, the cell is multi-line, so we keep only the first
				// line of a multi-line description.
				// TODO: check if it can be set to single line.
				String description = node.getDescription();
				String[] lines = description.split("\\r?\\n"); //$NON-NLS-1$
				description = lines[0];
				return " " + description;
			}
			return null;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}

	// ------------------------------------------------------------------------

	class NameSorter extends ViewerSorter {

		@SuppressWarnings("unchecked")
		public int compare(Viewer viewer, Object e1, Object e2) {

			Leaf n1 = (Leaf) e1;
			String name1 = n1.getName();
			String name2 = ((Leaf) e2).getName();

			if (n1.isType(Type.VERSION)) {
				Version v1 = Version.valueOf(name1);
				Version v2 = Version.valueOf(name2);

				// Reverse the order for versions, and use version comparator.
				return v2.compareTo(v1);
				// return getComparator().compare(name2, name1);
			} else {
				return getComparator().compare(name1, name2);
			}
		}
	}

	// ------------------------------------------------------------------------

	private Composite fComposite;
	private TreeViewer fViewer;
	private ISelectionListener fPageSelectionListener;
	private ViewContentProvider fContentProvider;

	private Action fUpdateAction;
	private Action fInstallAction;
	private Action fRemoveAction;
	private Action fCopyExampleAction;
	private Action fExpandAll;
	private Action fCollapseAll;

	private PacksFilter fPacksFilter;
	private ViewerFilter[] fPacksFilters;
	private boolean fIsInstallEnabled;
	private boolean fIsRemoveEnabled;
	private boolean fIsCopyExampleEnabled;

	private DataManager fDataManager;
	private MessageConsoleStream fOut;

	public PacksView() {

		fOut = ConsoleStream.getConsoleOut();

		fDataManager = DataManager.getInstance();
	}

	public TreeViewer getTreeViewer() {
		return fViewer;
	}

	public void createPartControl(Composite parent) {

		// System.out.println("PacksView.createPartControl()");

		fComposite = parent;

		fPacksFilter = new PacksFilter();
		fPacksFilters = new PacksFilter[] { fPacksFilter };

		Tree tree = new Tree(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// You can only add the Layout to a container whose only child is the
		// Tree control you want the Layout applied to. Don't assign the layout
		// directly the Tree

		TreeColumnLayout layout = new TreeColumnLayout();
		parent.setLayout(layout);

		TreeColumn nameColumn = new TreeColumn(tree, SWT.NONE);
		nameColumn.setText("  Name");
		// nameColumn.setWidth(200);
		layout.setColumnData(nameColumn, new ColumnPixelData(200));

		TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
		descriptionColumn.setAlignment(SWT.LEFT);
		descriptionColumn.setText(" Description");
		// descriptionColumn.setWidth(450);
		layout.setColumnData(descriptionColumn, new ColumnPixelData(450));

		fViewer = new TreeViewer(tree);
		// TODO: change to a more elaborate widget, that allows tooltips

		fContentProvider = new ViewContentProvider();

		// Register this view to the packs storage notifications
		fDataManager.addListener(this);

		fViewer.setContentProvider(fContentProvider);
		fViewer.setLabelProvider(new TableLabelProvider());
		fViewer.setSorter(new NameSorter());

		fViewer.setAutoExpandLevel(AUTOEXPAND_LEVEL);
		fViewer.setInput(getPacksTree());

		addProviders();
		addListners();
		hookPageSelection();

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	public void dispose() {

		super.dispose();

		if (fPageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(fPageSelectionListener);
		}

		fDataManager.removeListener(this);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PacksView.dispose()");
		}
	}

	private void addProviders() {

		// Register this viewer as a selection provider
		getSite().setSelectionProvider(fViewer);
	}

	private void addListners() {

		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				IStructuredSelection selection = (IStructuredSelection) event.getSelection();

				updateButtonsEnableStatus(selection);

				// System.out.println("Packs Selected: " + selection.toList());
			}
		});
	}

	private void hookPageSelection() {

		fPageSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {

				if ((part instanceof DevicesView) || (part instanceof BoardsView) || (part instanceof KeywordsView)) {
					friendViewSelectionChanged(part, selection);
				}
			}
		};
		getSite().getPage().addPostSelectionListener(fPageSelectionListener);
	}

	// Called when selection in the _part_View change
	protected void friendViewSelectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection.isEmpty()) {

			// System.out.println("Packs: resetFilters()");
			fViewer.expandToLevel(AUTOEXPAND_LEVEL);
			fViewer.resetFilters();

			return;
		}

		// System.out.println("Packs: " + part + " selection=" + selection);

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		String selectorType = "";
		if (part instanceof DevicesView) {
			selectorType = Selector.DEVICEFAMILY_TYPE;
		} else if (part instanceof BoardsView) {
			selectorType = Selector.BOARD_TYPE;
		} else if (part instanceof KeywordsView) {
			selectorType = Selector.KEYWORD_TYPE;
		}

		fPacksFilter.setSelection(selectorType, structuredSelection);

		fViewer.expandToLevel(AUTOEXPAND_LEVEL);
		fViewer.setFilters(fPacksFilters);

		fViewer.expandToLevel(AUTOEXPAND_LEVEL);
		fViewer.setSelection(null);
	}

	public void updateButtonsEnableStatus(IStructuredSelection selection) {

		if (selection == null || selection.isEmpty()) {
			// System.out.println("Empty Selection");
			return;
		}

		if (((Leaf) selection.getFirstElement()).isType(Type.NONE)) {
			return;
		}

		fIsInstallEnabled = false;
		fIsRemoveEnabled = false;
		fIsCopyExampleEnabled = false;

		for (Object obj : selection.toArray()) {
			Leaf node = (Leaf) obj;
			String type = node.getType();

			boolean isInstalled = false;
			if (node.isBooleanProperty(Property.INSTALLED)) {
				isInstalled = true;
			}

			// Check if the selection contain any package or
			// version not installed
			if (Type.PACKAGE.equals(type)) {
				if (!isInstalled) {
					fIsInstallEnabled = true;
				}
			}
			if (Type.VERSION.equals(type)) {
				int size = 0;
				try {
					size = Integer.valueOf(node.getProperty(Property.ARCHIVE_SIZE, "0"));
				} catch (NumberFormatException e) {
					;
				}
				if (!isInstalled && size > 0) {
					fIsInstallEnabled = true;
				}
				if (isInstalled) {
					fIsRemoveEnabled = true;
				}
			}
			if ((Type.EXAMPLE.equals(type))) {
				fIsCopyExampleEnabled = true;
			}
		}
		fInstallAction.setEnabled(fIsInstallEnabled);
		fRemoveAction.setEnabled(fIsRemoveEnabled);
	}

	private void hookContextMenu() {

		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				PacksView.this.fillContextMenu(manager);
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

	// Top down arrow
	private void fillLocalPullDown(IMenuManager manager) {

		manager.add(fExpandAll);
		manager.add(fCollapseAll);
		manager.add(new Separator());
		manager.add(fInstallAction);
		manager.add(fRemoveAction);
		manager.add(new Separator());
		manager.add(fUpdateAction);

		// manager.add(action1);
		// manager.add(new Separator());
		// manager.add(action2);
	}

	// Right click actions
	private void fillContextMenu(IMenuManager manager) {

		if (fIsInstallEnabled) {
			manager.add(fInstallAction);
		}

		if (fIsRemoveEnabled) {
			manager.add(fRemoveAction);
		}

		if (fIsCopyExampleEnabled) {
			manager.add(fCopyExampleAction);
		}

		// manager.add(new Separator());

		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	// Top tool bar buttons
	private void fillLocalToolBar(IToolBarManager manager) {

		manager.add(fExpandAll);
		manager.add(fCollapseAll);
		manager.add(new Separator());
		manager.add(fInstallAction);
		manager.add(fRemoveAction);
		manager.add(new Separator());
		manager.add(fUpdateAction);
	}

	private void makeActions() {

		fUpdateAction = new Action() {

			public void run() {

				// Obtain IServiceLocator implementer, e.g. from
				// PlatformUI.getWorkbench():
				// IServiceLocator serviceLocator = PlatformUI.getWorkbench();
				// or a site from within a editor or view:
				IServiceLocator serviceLocator = getSite();

				ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);

				try {
					// Lookup commmand with its ID
					Command command = commandService.getCommand("ilg.gnuarmeclipse.packs.commands.updateCommand");

					// Optionally pass a ExecutionEvent instance, default
					// no-param arg creates blank event
					command.executeWithChecks(new ExecutionEvent());

				} catch (Exception e) {

					Activator.log(e);
				}
			}
		};
		fUpdateAction.setText(Messages.PacksView_UpdateAction_text);
		fUpdateAction.setToolTipText(Messages.PacksView_UpdateAction_toolTipText);
		fUpdateAction
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/refresh_nav.gif"));

		// -----
		fInstallAction = new Action() {

			public void run() {

				// System.out.println("m_installAction.run();");

				TreeSelection selection = (TreeSelection) fViewer.getSelection();
				if (Activator.getInstance().isDebugging()) {
					System.out.println(selection);
				}

				Job job = new InstallJob("Install Packs", selection);
				job.schedule();
			}
		};
		fInstallAction.setText(Messages.PacksView_InstallAction_text);
		fInstallAction.setToolTipText(Messages.PacksView_InstallAction_toolTipText);
		fInstallAction
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/package_mode.png"));
		fInstallAction.setEnabled(false);

		// -----
		fRemoveAction = new Action() {

			public void run() {

				// System.out.println("m_removeAction.run();");

				TreeSelection selection = (TreeSelection) fViewer.getSelection();
				// System.out.println(selection);

				Job job = new RemoveJob("Remove Packs", selection);
				job.schedule();
			}
		};
		fRemoveAction.setText(Messages.PacksView_RemoveAction_text);
		fRemoveAction.setToolTipText(Messages.PacksView_RemoveAction_toolTipText);
		fRemoveAction
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/removeall.png"));
		fRemoveAction.setEnabled(false);

		// -----
		fCopyExampleAction = new Action() {

			public void run() {

				TreeSelection selection = (TreeSelection) fViewer.getSelection();
				if (!selection.isEmpty()) {

					CopyExampleDialog dlg = new CopyExampleDialog(fComposite.getShell(), selection);
					if (dlg.open() == Dialog.OK) {
						String out[] = dlg.getData();

						if (checkCopyDestinationFolders(selection, out)) {
							Job job = new CopyExampleJob("Copy example", selection, out);
							job.schedule();
						}
					}
				}
			}
		};
		fCopyExampleAction.setText("Copy to folder");

		// -----
		fExpandAll = new Action() {

			public void run() {
				fViewer.expandAll();
			}
		};

		fExpandAll.setText(Messages.PacksView_ExpandAll_text);
		fExpandAll.setToolTipText(Messages.PacksView_ExpandAll_toolTipText);
		fExpandAll.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/expandall.png"));

		fCollapseAll = new Action() {

			public void run() {
				fViewer.collapseAll();
			}
		};

		fCollapseAll.setText(Messages.PacksView_CollapseAll_text);
		fCollapseAll.setToolTipText(Messages.PacksView_CollapseAll_toolTipText);
		fCollapseAll
				.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/collapseall.png"));
	}

	private void hookDoubleClickAction() {
		; // None
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	public void refresh() {
		fViewer.refresh();
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

		// Return focus to this view
		setFocus();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PacksView.refresh() " + obj);
		}
	}

	public void update(Object obj) {

		if (obj instanceof Collection<?>) {
			for (Object node : (Collection<?>) obj) {
				fViewer.update(node, null);
			}
		} else {
			fViewer.update(obj, null);
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("PacksView.updated() " + obj);
		}
	}

	public String toString() {
		return "PacksView";
	}

	// ------------------------------------------------------------------------

	private boolean checkCopyDestinationFolders(TreeSelection selection, String[] param) {

		IPath m_destFolderPath = new Path(param[0]);

		boolean isNonEmpty = false;
		for (Object sel : selection.toList()) {

			PackNode exampleNode = (PackNode) sel;

			Leaf outlineExampleNode = exampleNode.getOutline().findChild(Type.EXAMPLE);

			String exampleRelativeFolder = outlineExampleNode.getProperty(Node.FOLDER_PROPERTY);

			File destFolder = m_destFolderPath.append(exampleRelativeFolder).toFile();

			if (destFolder.isDirectory() && (destFolder.listFiles().length > 0)) {
				isNonEmpty = true;
				break;
			}

		}

		if (isNonEmpty) {

			String msg = "One of the destination folders is not empty.";
			msg += "\nDo you agree to delete the previous content?";

			String[] buttons = new String[] { "OK", "Cancel" };
			MessageDialog dlg = new MessageDialog(fComposite.getShell(), null, null, msg, MessageDialog.ERROR, buttons,
					0);
			if (dlg.open() == 0) {
				return true; // OK
			}

			return false;
		}
		return true;
	}

	// ------------------------------------------------------------------------

	// Warning, this code runs on the notifier thread, hopefully will not
	// interfere with GUI actions.

	@Override
	public void packsChanged(DataManagerEvent event) {

		String type = event.getType();
		// System.out.println("PacksView.packsChanged(), type=\"" + type +
		// "\".");

		if (DataManagerEvent.Type.NEW_INPUT.equals(type)) {

			// Run the refresh on the GUI thread
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// m_out.println("PacksView NEW_INPUT");

					((TreeViewer) fViewer).setAutoExpandLevel(AUTOEXPAND_LEVEL);
					fViewer.setInput(getPacksTree());
				}
			});

			// } else if (DataManagerEvent.Type.REFRESH_ALL.equals(type)) {
			//
			// // Run the refresh on the GUI thread
			// Display.getDefault().asyncExec(new Runnable() {
			//
			// @Override
			// public void run() {
			//
			// // m_out.println("PacksView REFRESH_ALL");
			//
			// fViewer.refresh();
			// }
			// });
			//
		} else if (DataManagerEvent.Type.UPDATE_VERSIONS.equals(type)) {

			@SuppressWarnings("unchecked")
			final List<PackNode> updatedList = (List<PackNode>) event.getPayload();

			final Map<String, Node> parentsMap = new HashMap<String, Node>();
			for (PackNode versionNode : updatedList) {
				String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
				String packName = versionNode.getProperty(Property.PACK_NAME);
				String versionName = versionNode.getProperty(Property.VERSION_NAME);

				Node modelNode = fDataManager.findPackVersion(vendorName, packName, versionName);
				updateVersioNode(versionNode, modelNode);

				String key = fDataManager.makeMapKey(vendorName, packName);

				Node parent = versionNode.getParent();
				if (!parentsMap.containsKey(key)) {
					parentsMap.put(key, parent);
				}
			}

			for (Node packNode : parentsMap.values()) {

				// Compute if the parent has any installed child
				boolean hasInstalledChildren = false;
				for (Leaf child : packNode.getChildren()) {
					if (child.isBooleanProperty(Property.INSTALLED)) {
						hasInstalledChildren = true;
						break;
					}
				}

				packNode.setBooleanProperty(Property.INSTALLED, hasInstalledChildren);
			}

			// Run the refresh on the GUI thread
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {

					// Refresh pack node, this will update all version
					// and examples below them
					// refresh(parentsMap.values());
					fViewer.refresh();

					updateButtonsEnableStatus((IStructuredSelection) fViewer.getSelection());
				}
			});
		}
	}

	// ------------------------------------------------------------------------

	// Get view data from storage.
	// Return a hierarchy of vendor/packages/versions/examples nodes.
	private Node getPacksTree() {

		// Node packsTree = fStorage.getPacksTree();
		final Node packsTree = DataManager.getInstance().getRepositoriesTree();

		final Node packsRoot = new Node(Type.ROOT);
		packsRoot.setName("Packs");

		if (packsTree.hasChildren()) {

			(new DurationMonitor()).displayTimeAndRun(new Runnable() {

				public void run() {

					fOut.println("Collecting packs...");

					int count = 0;
					try {
						count = getPacksRecursive(packsTree, null, packsRoot);
					} catch (Exception e) {
						Activator.log(e);
					}
					if (packsRoot.hasChildren()) {
						fOut.println("Found " + count + " package version(s), from " + packsRoot.getChildren().size()
								+ " vendor(s).");
					} else {
						fOut.println("Found none.");
					}
				}
			});
		}

		if (!packsRoot.hasChildren()) {

			Node empty = Node.addNewChild(packsRoot, Type.NONE);
			empty.setName("(no packages)");
			empty.setDescription("Press the Refresh button to register all available packages.");
		}

		return packsRoot;
	}

	// Identify outline & external nodes and collect devices from inside.
	private int getPacksRecursive(Leaf modelNode, PackNode parentPackNode, Node root) {

		int count = 0;

		if (modelNode.isType(Type.PACKAGE)) {
			parentPackNode = (PackNode) modelNode;
		}

		if (modelNode.isType(Type.VERSION)) {

			count += addVersion((PackNode) modelNode, parentPackNode, root);

		} else if (modelNode instanceof Node && modelNode.hasChildren()) {

			for (Leaf child : ((Node) modelNode).getChildren()) {

				// Recurse down.
				count += getPacksRecursive(child, parentPackNode, root);
			}
		}

		return count;
	}

	private int addVersion(PackNode modelNode, PackNode parentPackNode, Node tree) {

		int count = 0;
		String vendorName = modelNode.getProperty(Property.VENDOR_NAME);
		String packName = modelNode.getProperty(Property.PACK_NAME);
		String versionName = modelNode.getProperty(Property.VERSION_NAME);
		String description = modelNode.getDescription();

		Node vendorNode = Node.addUniqueChild(tree, Type.VENDOR, vendorName);

		PackNode packNode = PackNode.addUniqueChild(vendorNode, Type.PACKAGE, packName);

		if (parentPackNode != null) {
			packNode.setDescription(parentPackNode.getDescription());
		}

		// Copy properties like INSTALLED.
		packNode.copyProperties(parentPackNode);
		packNode.putProperty(Property.VENDOR_NAME, vendorName);

		// Copy selectors.
		packNode.copySelectorsRef(parentPackNode);

		PackNode versionNode = PackNode.addUniqueChild(packNode, Type.VERSION, versionName);

		versionNode.setDescription(description);

		// Copy properties to the view node.
		versionNode.copyProperties(modelNode);

		updateVersioNode(versionNode, modelNode);

		// To save space, the brief or full outlines are not prepared
		// for all nodes, but only for nodes needed by selections.

		// If there will be error conditions preventing this to work,
		// the count should not be incremented.
		count++;

		return count;
	}

	private void updateVersioNode(PackNode versionNode, Node modelNode) {

		if (versionNode.isBooleanProperty(Property.INSTALLED)) {

			assert (modelNode != null);

			// For the installed nodes, add examples as children.
			Node outlineNode = (Node) modelNode.findChild(Type.OUTLINE);
			if (outlineNode != null && outlineNode.hasChildren()) {

				for (Leaf child : outlineNode.getChildren()) {

					if (child.isType(Type.EXAMPLE)) {

						// New node with child personality
						// (must be PackNode to accommodate outline).
						Node node = PackNode.addNewChild(versionNode, child);

						// Pass "example.name" to view
						node.copyProperties(child);
					}
				}
			}
		} else {

			// For the removed nodes, remove all children.
			versionNode.removeChildren();
		}

		// Clear outline.
		versionNode.setOutline(null);
	}

	// ------------------------------------------------------------------------

}