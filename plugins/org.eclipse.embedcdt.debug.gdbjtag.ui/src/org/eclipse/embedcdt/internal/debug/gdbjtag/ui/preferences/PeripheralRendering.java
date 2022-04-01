/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package org.eclipse.embedcdt.internal.debug.gdbjtag.ui.preferences;

import java.math.BigInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.MemoryByte;
import org.eclipse.debug.ui.memory.AbstractTableRendering;
import org.eclipse.debug.ui.memory.IMemoryRendering;
import org.eclipse.debug.ui.memory.IMemoryRenderingContainer;
import org.eclipse.debug.ui.memory.IMemoryRenderingSynchronizationService;
import org.eclipse.embedcdt.debug.gdbjtag.core.memory.PeripheralMemoryBlockExtension;
import org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral.PeripheralClusterArrayVMNode;
import org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral.PeripheralGroupVMNode;
import org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral.PeripheralRegisterArrayVMNode;
import org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral.PeripheralRegisterVMNode;
import org.eclipse.embedcdt.debug.gdbjtag.core.viewmodel.peripheral.PeripheralTreeVMNode;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.ILinkToolTipListener;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.LinkToolTip;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.PeripheralColumnInfo;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.PeripheralContentProvider;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.PeripheralEditingSupport;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.PeripheralFilterDialog;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.PeripheralNameFilter;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripheral.PeripheralViewerComparator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.Activator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.render.peripheral.PeripheralColumnLabelProvider;
import org.eclipse.embedcdt.ui.SystemUIJob;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PluginActionContributionItem;
import org.eclipse.ui.progress.UIJob;

@SuppressWarnings("restriction")
public class PeripheralRendering extends AbstractTableRendering
		implements IMemoryRendering, IDebugEventSetListener, IPropertyChangeListener, ILinkToolTipListener {

	// ------------------------------------------------------------------------

	public static final String ID = "org.eclipse.embedcdt.internal.debug.gdbjtag.ui.memoryRendering";

	private static final PeripheralColumnInfo[] fgColumnInfo = new PeripheralColumnInfo[] {
			new PeripheralColumnInfo("Register", 4, PeripheralColumnInfo.ColumnType.REGISTER, true), // sortable
			new PeripheralColumnInfo("Address", 2, PeripheralColumnInfo.ColumnType.ADDRESS, true), // sortable
			new PeripheralColumnInfo("Value", 2, PeripheralColumnInfo.ColumnType.VALUE, false) };

	// ------------------------------------------------------------------------

	private SystemUIJob fRefreshJob;

	private Action fAddFilterAction;
	private Action fRemoveFilterAction;
	private TreeViewer fPeripheralViewer;
	private String fRenderingId;
	private Action fRefreshMenuAction;
	// private Action fExpandAction;
	private Action fCollapseRegistersAction;
	private Action fShowFieldsAction;
	private Action fForceReadAction;
	private PeripheralMemoryBlockExtension fMemoryBlock;
	private IMemoryRenderingContainer fContainer;
	private PeripheralViewerComparator fComparator;

	// ------------------------------------------------------------------------

	public PeripheralRendering(String renderingId) {
		super(renderingId);

		fRefreshJob = new SystemUIJob(String.valueOf(getClass().getSimpleName()) + "#refresh") {

			@Override
			public IStatus runInUIThread(IProgressMonitor pm) {
				if (!fPeripheralViewer.getTree().isDisposed()) {
					refresh();
				}
				return Status.OK_STATUS;
			}
		};
		fRefreshJob.setPriority(UIJob.INTERACTIVE);

		fAddFilterAction = null;
		fRemoveFilterAction = null;
		fRefreshMenuAction = null;
		fCollapseRegistersAction = null;
		fShowFieldsAction = null;
		fForceReadAction = null;

		fRenderingId = renderingId;
	}

	@Override
	public void init(IMemoryRenderingContainer container, IMemoryBlock block) {
		super.init(container, block);

		fContainer = container;
		if (block instanceof PeripheralMemoryBlockExtension) {
			fMemoryBlock = (PeripheralMemoryBlockExtension) block;
		}
	}

	@Override
	public void dispose() {
		removeDebugEventListener();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class clazz) {
		return super.getAdapter(clazz);
	}

	@Override
	public Control createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.BORDER);
		fPeripheralViewer = new TreeViewer(composite, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);
		Control control = fPeripheralViewer.getControl();
		TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
		composite.setLayout(treeColumnLayout);
		control.setLayoutData(new GridData(1808));
		fPeripheralViewer.setAutoExpandLevel(-1);
		Tree tree = fPeripheralViewer.getTree();

		fPeripheralViewer.setContentProvider(new PeripheralContentProvider(fMemoryBlock.getPeripheralRegisterGroup()));

		fComparator = new PeripheralViewerComparator();
		fPeripheralViewer.setComparator(fComparator);

		LinkToolTip.enableFor(fPeripheralViewer, SWT.ICON_INFORMATION, this);

		for (int i = 0; i < PeripheralRendering.fgColumnInfo.length; ++i) {

			TreeViewerColumn treeViewerColumn = new TreeViewerColumn(fPeripheralViewer, SWT.ON_TOP);
			TreeColumn treeColumn = treeViewerColumn.getColumn();

			String headerName = PeripheralRendering.fgColumnInfo[i].header;
			treeColumn.setText(headerName);

			treeColumn.setResizable(true);
			treeColumn.setMoveable(true);

			treeColumnLayout.setColumnData(treeColumn, new ColumnWeightData(PeripheralRendering.fgColumnInfo[i].weight,
					PeripheralRendering.fgColumnInfo[i].weight * 5, true));

			// Set column label provider
			treeViewerColumn.setLabelProvider(new PeripheralColumnLabelProvider(fPeripheralViewer, fMemoryBlock,
					PeripheralRendering.fgColumnInfo[i].type));

			if (PeripheralRendering.fgColumnInfo[i].type == PeripheralColumnInfo.ColumnType.VALUE) {

				// For VALUE columns, add editing support
				treeViewerColumn.setEditingSupport(new PeripheralEditingSupport(fPeripheralViewer));
			}
			if (PeripheralRendering.fgColumnInfo[i].sortable) {
				// Add a selection listener to make sortable
				treeColumn.addSelectionListener(getSelectionAdapter(treeColumn, i));
			}
		}

		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		fPeripheralViewer.setInput(getMemoryBlock());
		performExpandAction(true);

		createPopupMenu(control);
		createActions();

		IMenuListener menuListener = new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		};

		addPopupMenu(menuListener);
		addDebugEventListener();
		addToSyncService();
		trackTreeSelectionChanges();
		// Refresh the UI, since while initialising the controls 
		// it would have missed some debug events
		fRefreshJob.schedule();		
		return composite;
	}

	private SelectionAdapter getSelectionAdapter(final TreeColumn column, final int index) {

		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fComparator.setColumn(index);
				int dir = fComparator.getDirection();
				fPeripheralViewer.getTree().setSortDirection(dir);
				fPeripheralViewer.getTree().setSortColumn(column);
				refresh();
			}
		};
		return selectionAdapter;
	}

	private void addPopupMenu(IMenuListener menuListener) {
		MenuManager menuManager = getPopupMenuManager();
		menuManager.addMenuListener(menuListener);
	}

	private void addToSyncService() {

		IMemoryRenderingSynchronizationService synchronizationService = getMemoryRenderingContainer()
				.getMemoryRenderingSite().getSynchronizationService();
		if (synchronizationService != null) {
			synchronizationService.addPropertyChangeListener(this, new String[] { PROPERTY_SELECTED_ADDRESS });
		}
	}

	private void addDebugEventListener() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	private void removeDebugEventListener() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	// ------------------------------------------------------------------------

	private void trackTreeSelectionChanges() {

		fPeripheralViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent selectionChangedEvent) {

				if (selectionChangedEvent.getSelection() instanceof IStructuredSelection) {

					Object object;
					IStructuredSelection selection = (IStructuredSelection) selectionChangedEvent.getSelection();
					PeripheralRegisterVMNode peripheralRegister = null;
					object = selection.getFirstElement();
					if (object instanceof PeripheralRegisterVMNode) {
						peripheralRegister = (PeripheralRegisterVMNode) object;
					}
					postChange(peripheralRegister);
				}
			}
		});
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; ++i) {
			if (events[i].getKind() != 16 || events[i].getSource() != fMemoryBlock)
				continue;
			fRefreshJob.schedule();
		}
	}

	@Override
	public void activated() {
		if (fContainer.getMemoryRenderingSite().getSynchronizationService() != null) {
			fContainer.getMemoryRenderingSite().getSynchronizationService().setSynchronizationProvider(this);
		}
	}

	@Override
	public void deactivated() {

	}

	@Override
	public void becomesHidden() {

	}

	@Override
	public void becomesVisible() {

	}

	private void handleSelectedAddressChanged(BigInteger bigInteger) {

		// System.out.println("handleSelectedAddressChanged() "
		// + Long.toString(bigInteger.longValue(), 16));

		Object selection = findSelection(bigInteger);
		if (selection != null) {
			StructuredSelection structuredSelection = new StructuredSelection(selection);
			fPeripheralViewer.setSelection(structuredSelection);
			Object element = structuredSelection.getFirstElement();
			if (element != null) {
				fPeripheralViewer.reveal(element);
			}
		}
	}

	private void postChange(PeripheralRegisterVMNode peripheralRegister) {

		if (peripheralRegister != null) {
			try {
				if (peripheralRegister.isField()) {
					peripheralRegister = (PeripheralRegisterVMNode) peripheralRegister.getParent();
				}
				PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, PROPERTY_SELECTED_ADDRESS,
						(Object) null, peripheralRegister.getBigAbsoluteAddress());
				firePropertyChangedEvent(propertyChangeEvent);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

		if (propertyChangeEvent.getSource() == this) {
			return;
		}
		if (propertyChangeEvent.getProperty().equals(PROPERTY_SELECTED_ADDRESS)
				&& propertyChangeEvent.getNewValue() instanceof BigInteger) {
			handleSelectedAddressChanged((BigInteger) propertyChangeEvent.getNewValue());
		}
	}

	@Override
	public void refresh() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralRendering.refresh()");
		}
		fPeripheralViewer.refresh();

		if (fMemoryBlock != null) {
			PeripheralTreeVMNode node = fMemoryBlock.getPeripheralRegisterGroup();
			node.decrementFadingLevel();
			if (Activator.getInstance().isDebugging()) {
				System.out.println("PeripheralRendering.refresh() decrementRecentChanges");
			}
		}
	}

	// ------------------------------------------------------------------------

	@Override
	protected void createActions() {
		{
			fCollapseRegistersAction = new Action() {

				@Override
				public void run() {
					performExpandAction(true);
				}
			};
			fCollapseRegistersAction.setText("Collapse registers");
			fCollapseRegistersAction.setToolTipText("Collapse registers");
			fCollapseRegistersAction.setImageDescriptor(
					Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/register_obj.png"));
		}
		{
			fShowFieldsAction = new Action() {

				@Override
				public void run() {
					performExpandAction(false);
				}
			};
			fShowFieldsAction.setText("Show fields");
			fShowFieldsAction.setToolTipText("Show fields");
			fShowFieldsAction
					.setImageDescriptor(Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/field.png"));
		}

		{
			fRefreshMenuAction = new Action() {

				@Override
				public void run() {
					refresh();
				}
			};
			fRefreshMenuAction.setText("Refresh");
			fRefreshMenuAction.setToolTipText("Refresh view");
			fRefreshMenuAction.setImageDescriptor(
					PlatformUI.getWorkbench().getSharedImages().getImageDescriptor("IMG_TOOL_REDO"));
		}

		{
			fAddFilterAction = new Action() {

				@Override
				public void run() {
					performAddFilterAction();
				}
			};
			fAddFilterAction.setText("Add filter...");
			fAddFilterAction.setImageDescriptor(Activator.getInstance().getImageDescriptor("filter"));
		}

		{
			fRemoveFilterAction = new Action() {

				@Override
				public void run() {
					performRemoveFilterAction();
				}
			};
			fRemoveFilterAction.setText("Remove filter...");
			fRemoveFilterAction.setImageDescriptor(Activator.getInstance().getImageDescriptor("filter_rem"));
		}

		{
			fForceReadAction = new Action() {

				@Override
				public void run() {
					performForceReadAction();
				}
			};
			fForceReadAction.setText("Force read of register with side effects");
			// TODO: add force_read.png
			fForceReadAction.setImageDescriptor(Activator.getInstance().getImageDescriptor("force_read"));
		}
	}

	private void performExpandAction(boolean collapseRegisters) {

		if (!(fPeripheralViewer == null || fPeripheralViewer.getControl().isDisposed())) {
			expandRecursive(fPeripheralViewer.getTree().getItems(), !collapseRegisters);
		}
	}

	private void expandRecursive(TreeItem[] items, boolean expandRegisters) {

		if (items == null) {
			return;
		}
		for (TreeItem treeItem : items) {
			boolean isExpanded = true;
			Object node = treeItem.getData();
			if (node instanceof PeripheralRegisterArrayVMNode) {
				isExpanded = false;
			} else if (node instanceof PeripheralClusterArrayVMNode) {
				isExpanded = false;
			} else if (node instanceof PeripheralGroupVMNode) {
				isExpanded = true;
			} else if (node instanceof PeripheralRegisterVMNode) {

				if (((PeripheralRegisterVMNode) node).isRegister()) {
					isExpanded = expandRegisters;
				} else {
					isExpanded = false;
				}
			}
			treeItem.setExpanded(isExpanded);

			// Recurse to next level.
			expandRecursive(treeItem.getItems(), expandRegisters);
		}
	}

	private void performAddFilterAction() {

		String currentFilter = "";
		ViewerFilter[] filters = fPeripheralViewer.getFilters();
		if (filters.length > 0 && filters[0] instanceof PeripheralNameFilter) {
			currentFilter = ((PeripheralNameFilter) filters[0]).getFilterText();
		}
		performRemoveFilterAction();

		PeripheralFilterDialog dialog = new PeripheralFilterDialog(getControl().getShell(), currentFilter);
		if (dialog.open() != Window.OK) {
			return;
		}

		String filter = dialog.getValue();
		if (filter == null || filter.isEmpty()) {
			return;
		}

		PeripheralNameFilter peripheralNameFilter = new PeripheralNameFilter(filter);
		fPeripheralViewer.addFilter(peripheralNameFilter);
	}

	private void performRemoveFilterAction() {
		ViewerFilter[] filters = fPeripheralViewer.getFilters();
		for (int i = 0; i < filters.length; ++i) {
			fPeripheralViewer.removeFilter(filters[i]);
			filters[i] = null;
		}
	}

	private void performForceReadAction() {

		Object object = getSelection();
		if (object instanceof PeripheralRegisterVMNode) {
			PeripheralRegisterVMNode peripheralRegister = (PeripheralRegisterVMNode) object;
			peripheralRegister.forceReadRegister();
		}
	}

	// ------------------------------------------------------------------------

	@Override
	protected void fillContextMenu(IMenuManager menuManager) {

		PeripheralRegisterVMNode peripheralRegister;

		// Identify the AddWatchpoint action.
		IContributionItem itemToRemove = null;
		IContributionItem items[] = menuManager.getItems();
		for (int i = 0; i < items.length; ++i) {
			if (items[i] instanceof PluginActionContributionItem) {
				PluginActionContributionItem item = (PluginActionContributionItem) items[i];
				if ("org.eclipse.cdt.debug.internal.ui.actions.AddWatchpointOnMemoryActionDelegate.1"
						.equals(item.getAction().getId())) {
					itemToRemove = item;
					break;
				}
			}
		}

		if (itemToRemove != null) {
			// Remove non-functional AddWatchpoint.
			menuManager.remove(itemToRemove);
		}

		menuManager.add(new Separator());
		menuManager.add(fRefreshMenuAction);
		menuManager.add(fAddFilterAction);
		menuManager.add(fRemoveFilterAction);
		menuManager.add(new Separator());
		menuManager.add(fCollapseRegistersAction);
		menuManager.add(fShowFieldsAction);
		boolean hasForceRead = false;
		Object object = getSelection();
		if (object instanceof PeripheralRegisterVMNode
				&& !(peripheralRegister = (PeripheralRegisterVMNode) object).isField()) {
			hasForceRead = peripheralRegister.hasReadAction();
		}
		if (hasForceRead) {
			menuManager.add(new Separator());
			menuManager.add(fForceReadAction);
		}
	}

	@Override
	public String getString(String renderingTypeId, BigInteger address, MemoryByte[] data) {
		return null;
	}

	@Override
	public byte[] getBytes(String renderingTypeId, BigInteger address, MemoryByte[] currentValues, String newValue) {
		return null;
	}

	@Override
	public void linkSelected(String link) {

	}

	// ------------------------------------------------------------------------

	private Object findSelection(BigInteger bigInteger) {
		return null;
	}

	@Override
	public Control getControl() {
		return fPeripheralViewer.getControl();
	}

	@Override
	public Image getImage() {
		return Activator.getInstance().getImage("peripheral");
	}

	@Override
	public String getLabel() {

		String string = null;
		if (fMemoryBlock != null) {
			String string2 = new String();
			try {
				string2 = fMemoryBlock.getBigBaseAddress().toString(16).toUpperCase();
			} catch (DebugException e) {
			}
			string = String.format("%s: 0x%s", fMemoryBlock.getPeripheralInstance().getDisplayName(), string2);
		}
		return string == null ? getClass().getSimpleName() : string;
	}

	@Override
	public IMemoryBlock getMemoryBlock() {
		return fMemoryBlock;
	}

	@Override
	public String getRenderingId() {
		return fRenderingId;
	}

	private Object getSelection() {

		TreeSelection treeSelection;
		Object object = null;
		ISelection selection = fPeripheralViewer.getSelection();
		if (selection instanceof TreeSelection && (treeSelection = (TreeSelection) selection).size() == 1) {
			object = treeSelection.getFirstElement();
		}
		return object;
	}

	// ------------------------------------------------------------------------
}
