/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripherals;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.IPeripheralDMContext;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMContext;
import ilg.gnuarmeclipse.debug.gdbjtag.memory.MemoryBlockMonitor;
import ilg.gnuarmeclipse.debug.gdbjtag.memory.PeripheralMemoryBlockRetrieval;
import ilg.gnuarmeclipse.debug.gdbjtag.render.peripherals.PeripheralsColumnPresentation;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IPeripheralsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.eclipse.cdt.dsf.concurrent.ConfinedToDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.ImmediateExecutor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.concurrent.ViewerDataRequestMonitor;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.IDMVMContext;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IElementPropertiesProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelAttribute;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelColumnInfo;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelImage;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ICheckUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

@SuppressWarnings("restriction")
public class PeripheralsVMNode extends AbstractDMVMNode
		implements IElementLabelProvider, IElementPropertiesProvider, IElementEditor {

	// ------------------------------------------------------------------------
	// This node uses properties to store content, and these are the names.

	public static final String PROPERTY_NAME = "prop.name";
	public static final String PROPERTY_ADDRESS = "prop.address";
	public static final String PROPERTY_DESCRIPTION = "prop.description";

	public static final String PROPERTY_ISSYSTEM = "prop.isSystem";

	private IElementLabelProvider fLabelProvider = createLabelProvider();

	private IDMContext[] fPeripherals;

	// ------------------------------------------------------------------------

	/**
	 * Peripherals view model context. Contains a reference to the data model.
	 */
	public class PeripheralsVMContext extends AbstractDMVMNode.DMVMContext {

		protected PeripheralsVMContext(IDMContext context) {
			super(context);
			// System.out.println("PeripheralsVMContext(" + context + ")");
		}
	}

	protected IDMVMContext createVMContext(IDMContext context) {
		return new PeripheralsVMContext(context);
	}

	// ------------------------------------------------------------------------

	public PeripheralsVMNode(AbstractDMVMProvider provider, DsfSession session,
			Class<? extends IDMContext> dmcClassType) {
		super(provider, session, dmcClassType);
	}

	public PeripheralsVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IPeripheralDMContext.class);
	}

	@Override
	public int getDeltaFlags(Object event) {

		if ((event instanceof IRunControl.ISuspendedDMEvent)) {
			return IModelDelta.CONTENT;
		}

		return 0;
	}

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset, RequestMonitor requestMonitor) {

		if ((event instanceof IRunControl.ISuspendedDMEvent)) {
			parent.setFlags(parent.getFlags() | IModelDelta.CONTENT);
		}
		requestMonitor.done();
	}

	@Override
	public CellEditor getCellEditor(IPresentationContext context, String columnId, Object element, Composite parent) {

		return null; // No cell editor.
	}

	@Override
	public ICellModifier getCellModifier(IPresentationContext context, Object element) {

		return null; // No cell modifier.
	}

	@Override
	public void update(final IPropertiesUpdate[] updates) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMNode.update() properties " + this + ", " + updates.length + " objs");
		}

		try {
			getSession().getExecutor().execute(new DsfRunnable() {

				public void run() {
					updatePropertiesInSessionThread(updates);
				}
			});
		} catch (RejectedExecutionException e) {
			for (IPropertiesUpdate update : updates)
				handleFailedUpdate(update);
		}
	}

	@Override
	public void update(ILabelUpdate[] updates) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMNode.update() labels " + this + ", " + updates.length + " objs");
		}

		{
			for (int i = 0; i < updates.length; i++) {

				ILabelUpdate update = updates[i];
				IPresentationContext presentationContext = update.getPresentationContext();
				TreePath path = update.getElementPath();
				if (((update instanceof ICheckUpdate))
						&& (Boolean.TRUE.equals(presentationContext.getProperty("org.eclipse.debug.ui.check"))))
					try {
						boolean checked = getChecked(path, presentationContext);
						boolean grayed = getGrayed(path, presentationContext);

						// Update the check button
						((ICheckUpdate) update).setChecked(checked, grayed);
					} catch (CoreException e) {
					}
			}
		}
		// Update the tree content, using the updates
		fLabelProvider.update(updates);
	}

	@Override
	protected boolean checkUpdate(IViewerUpdate update) {

		// System.out.println("PeripheralsVMNode.checkUpdate() " + this + " "
		// + update);

		// As recommended, first check parent.
		if (!super.checkUpdate(update))
			return false;

		// Currently always update, although the view content is static and
		// does not depend on debug state or other views.
		return true; // Conservative;
	}

	@Override
	protected void updateElementsInSessionThread(final IChildrenUpdate update) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMNode.updateElementsInSessionThread() " + this + " " + update);
		}

		DsfServicesTracker tracker = getServicesTracker();
		IPeripheralsService peripheralsService = (IPeripheralsService) tracker.getService(IPeripheralsService.class);
		// System.out.println("got service " + peripheralsService);

		final IRunControl.IContainerDMContext containerDMContext = (IRunControl.IContainerDMContext) findDmcInPath(
				update.getViewerInput(), update.getElementPath(), IRunControl.IContainerDMContext.class);

		if ((peripheralsService == null) || (containerDMContext == null)) {
			// Leave the view empty. This also happens after closing the
			// session, since service is no longer there.
			handleFailedUpdate(update);
			return;
		}

		if (fPeripherals != null) {
			// On subsequent calls, use cached values.
			if (Activator.getInstance().isDebugging()) {
				System.out.println("PeripheralsVMNode.updateElementsInSessionThread() use cached values");
			}
			fillUpdateWithVMCs(update, fPeripherals);
			update.done();
			return;
		}

		Executor executor;
		executor = ImmediateExecutor.getInstance();
		// executor = getSession().getExecutor();

		// Get peripherals only on first call.
		peripheralsService.getPeripherals(containerDMContext,
				new ViewerDataRequestMonitor<IPeripheralDMContext[]>(executor, update) {

					public void handleCompleted() {

						if (isSuccess()) {
							fPeripherals = (IDMContext[]) getData();
							fillUpdateWithVMCs(update, fPeripherals);

							addPersistentPeripherals(containerDMContext);

							update.done();
						} else {
							EclipseUtils.showStatusErrorMessage(getStatus().getMessage());
							handleFailedUpdate(update);
						}
					}
				});
	}

	/**
	 * Add memory monitors for persistent peripherals.
	 * 
	 * @param containerDMContext
	 */
	private void addPersistentPeripherals(final IRunControl.IContainerDMContext containerDMContext) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMNode.addPersistentPeripherals()");
		}

		final List<String> persistentPeripherals = new ArrayList<String>();

		Object object = containerDMContext.getAdapter(PeripheralMemoryBlockRetrieval.class);
		if (object instanceof PeripheralMemoryBlockRetrieval) {
			persistentPeripherals.addAll(((PeripheralMemoryBlockRetrieval) object).getPersistentPeripherals());
		}

		if (persistentPeripherals.isEmpty()) {
			return;
		}

		// Call addMemoryBlock() on the UI thread to avoid refresh problems.
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {

				IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IMemoryBlockRetrieval retrieval = (IMemoryBlockRetrieval) containerDMContext
						.getAdapter(PeripheralMemoryBlockRetrieval.class);

				for (String peripheralName : persistentPeripherals) {
					for (int i = 0; i < fPeripherals.length; ++i) {
						PeripheralDMContext peripheralDMContext = (PeripheralDMContext) fPeripherals[i];
						if (peripheralName.equals(peripheralDMContext.getName())) {
							if (Activator.getInstance().isDebugging()) {
								System.out.println(fPeripherals[i] + " must be rendered");
							}

							MemoryBlockMonitor.getInstance().addMemoryBlock(workbenchWindow, peripheralDMContext,
									retrieval);
						}
					}
				}

				MemoryBlockMonitor.getInstance().showMemoryView(workbenchWindow);
			}
		});

	}

	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void updatePropertiesInSessionThread(IPropertiesUpdate[] updates) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"PeripheralsVMNode.updatePropertiesInSessionThread() " + this + ", " + updates.length + " objs");
		}

		for (final IPropertiesUpdate update : updates) {

			IPeripheralDMContext peripheralDMContext = (IPeripheralDMContext) findDmcInPath(update.getViewerInput(),
					update.getElementPath(), IPeripheralDMContext.class);

			if (peripheralDMContext == null) {
				handleFailedUpdate(update);
				return;
			}

			setProperties(update, peripheralDMContext);
			// System.out.println("updatePropertiesInSessionThread() "
			// + propertiesUpdate);
			update.done();
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Create the label provider, that will assign content to the table columns
	 * from the properties of the node.
	 * 
	 * @return the label provider.
	 */
	protected IElementLabelProvider createLabelProvider() {

		PropertiesBasedLabelProvider labelProvider = new PropertiesBasedLabelProvider();

		LabelAttribute labelAttributes[];
		LabelColumnInfo labelColumnInfo;

		LabelImage labelImage = new LabelImage() {

			@Override
			public void updateAttribute(ILabelUpdate update, int columnIndex, IStatus status,
					Map<String, Object> properties) {

				ImageDescriptor descriptor = null;
				Boolean isSystem = (Boolean) properties.get(PROPERTY_ISSYSTEM);
				if (isSystem != null && isSystem.booleanValue()) {
					descriptor = Activator.getInstance().getImageDescriptor("system_peripheral");
				} else {
					descriptor = Activator.getInstance().getImageDescriptor("peripheral");
				}

				if (descriptor != null) {
					update.setImageDescriptor(descriptor, columnIndex);
				}
			}
		};

		// The PROPERTY_ISSYSTEM was added, although not used here, because
		// it needs to be referred somewhere to be available for tests in the
		// above updateAttribute().
		labelAttributes = new LabelAttribute[] {
				new LabelText("{0}", new String[] { PROPERTY_NAME, PROPERTY_ISSYSTEM }), labelImage };
		labelColumnInfo = new LabelColumnInfo(labelAttributes);

		// Define content for "Peripheral" column
		labelProvider.setColumnInfo(PeripheralsColumnPresentation.COLUMN_PERIPHERAL_ID, labelColumnInfo);

		labelAttributes = new LabelAttribute[] { new LabelText("{0}", new String[] { PROPERTY_ADDRESS }) };
		labelColumnInfo = new LabelColumnInfo(labelAttributes);

		// Define content for "Address" column
		labelProvider.setColumnInfo(PeripheralsColumnPresentation.COLUMN_ADDRESS_ID, labelColumnInfo);

		labelAttributes = new LabelAttribute[] { new LabelText("{0}", new String[] { PROPERTY_DESCRIPTION }) };
		labelColumnInfo = new LabelColumnInfo(labelAttributes);

		// Define content for "Description" column
		labelProvider.setColumnInfo(PeripheralsColumnPresentation.COLUMN_DESCRIPTION_ID, labelColumnInfo);

		return labelProvider;
	}

	// ------------------------------------------------------------------------

	/**
	 * Fill-in the view node properties from a data view context.
	 * 
	 * @param update
	 *            the properties object.
	 * @param context
	 *            the data model context.
	 */
	protected void setProperties(IPropertiesUpdate update, IPeripheralDMContext context) {

		assert (context != null);

		update.setProperty(PROPERTY_NAME, context.getName());
		update.setProperty(PROPERTY_ADDRESS, context.getHexAddress());
		update.setProperty(PROPERTY_DESCRIPTION, context.getDescription());

		update.setProperty(PROPERTY_ISSYSTEM, new Boolean(context.isSystem()));
	}

	/**
	 * Tell if the peripheral should be displayed as checked, by testing if the
	 * peripheral is shown in the memory monitor window.
	 * 
	 * @param treePath
	 *            the peripheral path.
	 * @param presentationContext
	 *            the presentation context (unused).
	 * @return true if the peripheral should be checked.
	 * @throws CoreException
	 */
	protected boolean getChecked(TreePath treePath, IPresentationContext presentationContext) throws CoreException {

		Object pathSegment = treePath.getLastSegment();
		if ((pathSegment instanceof PeripheralsVMContext)) {
			PeripheralsVMContext peripheralVMContext = (PeripheralsVMContext) pathSegment;
			PeripheralDMContext peripheralDMContext = (PeripheralDMContext) peripheralVMContext.getDMContext();

			// System.out.println("PeripheralsVMNode.getChecked()="
			// + peripheralDMContext.hasMemoryMonitor() + " "
			// + peripheralDMContext);
			return peripheralDMContext.hasMemoryMonitor();
		}
		return false;
	}

	protected boolean getGrayed(TreePath treePath, IPresentationContext presentationContext) throws CoreException {
		return false;
	}

	// ------------------------------------------------------------------------

	public String toString() {
		return "PeripheralsVMNode(" + getSession().getId() + ")";
	}

	// ------------------------------------------------------------------------
}
