package org.eclipse.embedcdt.debug.gdbjtag.ui.viewmodel.peripherals;

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
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IElementPropertiesProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelAttribute;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelColumnInfo;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.IPeripheralGroupDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IPeripheralsService;
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripherals.PeripheralsColumnPresentation;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.Activator;
import org.eclipse.embedcdt.ui.EclipseUiUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class PeripheralGroupsVMNode extends AbstractDMVMNode
		implements IElementLabelProvider, IElementPropertiesProvider, IElementEditor {

	public static final String PROPERTY_NAME = "prop.name";

	private IElementLabelProvider fLabelProvider;

	private IDMContext[] fPeripheralGroups;

	// ------------------------------------------------------------------------

	public PeripheralGroupsVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IPeripheralGroupDMContext.class);
		fLabelProvider = createLabelProvider();
	}

	// ------------------------------------------------------------------------
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
			System.out.println("PeripheralGroupsVMNode.update() properties " + this + ", " + updates.length + " objs");
		}

		try {
			getSession().getExecutor().execute(new DsfRunnable() {

				@Override
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
			System.out.println("PeripheralGroupsVMNode.update() labels " + this + ", " + updates.length + " objs");
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
		IPeripheralsService peripheralsService = tracker.getService(IPeripheralsService.class);
		// System.out.println("got service " + peripheralsService);

		final IRunControl.IContainerDMContext containerDMContext = findDmcInPath(update.getViewerInput(),
				update.getElementPath(), IRunControl.IContainerDMContext.class);

		if ((peripheralsService == null) || (containerDMContext == null)) {
			// Leave the view empty. This also happens after closing the
			// session, since service is no longer there.
			handleFailedUpdate(update);
			return;
		}

		if (fPeripheralGroups != null) {
			// On subsequent calls, use cached values.
			if (Activator.getInstance().isDebugging()) {
				System.out.println("PeripheralsVMNode.updateElementsInSessionThread() use cached values");
			}
			fillUpdateWithVMCs(update, fPeripheralGroups);
			update.done();
			return;
		}

		Executor executor;
		executor = ImmediateExecutor.getInstance();
		// executor = getSession().getExecutor();

		// Get peripherals only on first call.
		peripheralsService.getPeripheralGroups(containerDMContext,
				new ViewerDataRequestMonitor<IPeripheralGroupDMContext[]>(executor, update) {

					@Override
					public void handleCompleted() {

						if (isSuccess()) {
							fPeripheralGroups = getData();
							fillUpdateWithVMCs(update, fPeripheralGroups);

							update.done();
						} else {
							EclipseUiUtils.showStatusErrorMessage(getStatus().getMessage());
							handleFailedUpdate(update);
						}
					}
				});
	}

	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void updatePropertiesInSessionThread(IPropertiesUpdate[] updates) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralGroupsVMNode.updatePropertiesInSessionThread() " + this + ", "
					+ updates.length + " objs");
		}

		for (final IPropertiesUpdate update : updates) {

			IPeripheralGroupDMContext peripheralDMContext = findDmcInPath(update.getViewerInput(),
					update.getElementPath(), IPeripheralGroupDMContext.class);

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

	protected IElementLabelProvider createLabelProvider() {

		PropertiesBasedLabelProvider labelProvider = new PropertiesBasedLabelProvider();

		LabelColumnInfo labelColumnInfo = new LabelColumnInfo(
				new LabelAttribute[] { new LabelText("{0}", new String[] { PROPERTY_NAME }) });
		labelProvider.setColumnInfo(PeripheralsColumnPresentation.COLUMN_PERIPHERAL_ID, labelColumnInfo);

		return labelProvider;
	}

	protected void setProperties(IPropertiesUpdate update, IPeripheralGroupDMContext context) {

		assert (context != null);

		update.setProperty(PROPERTY_NAME, context.getName());
	}

}
