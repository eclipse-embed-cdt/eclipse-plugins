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

package ilg.gnuarmeclipse.debug.core.gdbjtag.viewmodel.peripherals;

import java.util.concurrent.RejectedExecutionException;

import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.IPeripherals;
import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.IPeripherals.IPeripheralDMContext;
import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.PeripheralsService;

import org.eclipse.cdt.dsf.concurrent.ConfinedToDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
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
import org.eclipse.cdt.dsf.ui.viewmodel.properties.LabelText;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.PropertiesBasedLabelProvider;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IModelDelta;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class PeripheralVMNode extends AbstractDMVMNode implements
		IElementLabelProvider, IElementPropertiesProvider, IElementEditor {

	// This node uses properties to store content, and these are the names.

	public static final String PROPERTY_NAME = "prop.name";
	public static final String PROPERTY_ADDRESS = "prop.address";
	public static final String PROPERTY_DESCRIPTION = "prop.description";

	private IElementLabelProvider fLabelProvider = createLabelProvider();

	// ------------------------------------------------------------------------

	/**
	 * Peripheral view model context. Contains a reference to the data model.
	 */
	public class PeripheralVMContext extends AbstractDMVMNode.DMVMContext {

		protected PeripheralVMContext(IDMContext context) {
			super(context);
			// System.out.println("PeripheralVMContext(" + context + ")");
		}
	}

	protected IDMVMContext createVMContext(IDMContext context) {
		return new PeripheralVMContext(context);
	}

	// ------------------------------------------------------------------------

	public PeripheralVMNode(AbstractDMVMProvider provider, DsfSession session,
			Class<? extends IDMContext> dmcClassType) {
		super(provider, session, dmcClassType);
	}

	public PeripheralVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IPeripherals.IPeripheralDMContext.class);
	}

	@Override
	public int getDeltaFlags(Object event) {
		if ((event instanceof IRunControl.ISuspendedDMEvent)) {
			return IModelDelta.CONTENT;
		}

		return 0;
	}

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset,
			RequestMonitor requestMonitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public CellEditor getCellEditor(IPresentationContext context,
			String columnId, Object element, Composite parent) {

		return null; // No cell editor.
	}

	@Override
	public ICellModifier getCellModifier(IPresentationContext context,
			Object element) {

		return null; // No cell modifier.
	}

	@Override
	public void update(final IPropertiesUpdate[] updates) {

		System.out.println("update() properties " + this + ", "
				+ updates.length + " objs");

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

		System.out.println("update() labels " + this + ", " + updates.length
				+ " objs");

		// Update the tree content, using the updates
		fLabelProvider.update(updates);
	}

	@Override
	protected void updateElementsInSessionThread(final IChildrenUpdate update) {

		System.out.println("updateElementsInSessionThread() " + this + " "
				+ update);

		DsfServicesTracker tracker = getServicesTracker();
		IPeripherals peripheralsService = (IPeripherals) tracker
				.getService(IPeripherals.class);
		System.out.println("got service " + peripheralsService);

		IRunControl.IContainerDMContext containerDMContext = (IRunControl.IContainerDMContext) findDmcInPath(
				update.getViewerInput(), update.getElementPath(),
				IRunControl.IContainerDMContext.class);

		if ((peripheralsService == null) || (containerDMContext == null)) {
			handleFailedUpdate(update);
			return;
		}

		peripheralsService.getPeripherals(containerDMContext,
				new ViewerDataRequestMonitor<IPeripheralDMContext[]>(
						getSession().getExecutor(), update) {

					public void handleCompleted() {

						if (!isSuccess()) {
							update.done();
							return;
						}
						PeripheralVMNode.this.fillUpdateWithVMCs(update,
								(IDMContext[]) getData());
						update.done();
					}
				});
	}

	@ConfinedToDsfExecutor("getSession().getExecutor()")
	protected void updatePropertiesInSessionThread(
			IPropertiesUpdate[] propertiesUpdates) {

		for (final IPropertiesUpdate propertiesUpdate : propertiesUpdates) {

			IPeripherals.IPeripheralDMContext peripheralDMContext = (IPeripherals.IPeripheralDMContext) findDmcInPath(
					propertiesUpdate.getViewerInput(),
					propertiesUpdate.getElementPath(),
					IPeripherals.IPeripheralDMContext.class);

			if (peripheralDMContext == null) {
				handleFailedUpdate(propertiesUpdate);
				return;
			}

			setProperties(propertiesUpdate, peripheralDMContext);
			// System.out.println("updatePropertiesInSessionThread() "
			// + propertiesUpdate);
			propertiesUpdate.done();
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

		labelAttributes = new LabelAttribute[] { new LabelText("{0}",
				new String[] { PROPERTY_NAME }) };
		labelColumnInfo = new LabelColumnInfo(labelAttributes);

		// Define content for "Peripheral" column
		labelProvider.setColumnInfo(
				PeripheralsColumnPresentation.COLUMN_PERIPHERAL_ID,
				labelColumnInfo);

		labelAttributes = new LabelAttribute[] { new LabelText("{0}",
				new String[] { PROPERTY_ADDRESS }) };
		labelColumnInfo = new LabelColumnInfo(labelAttributes);

		// Define content for "Address" column
		labelProvider.setColumnInfo(
				PeripheralsColumnPresentation.COLUMN_ADDRESS_ID,
				labelColumnInfo);

		labelAttributes = new LabelAttribute[] { new LabelText("{0}",
				new String[] { PROPERTY_DESCRIPTION }) };
		labelColumnInfo = new LabelColumnInfo(labelAttributes);

		// Define content for "Description" column
		labelProvider.setColumnInfo(
				PeripheralsColumnPresentation.COLUMN_DESCRIPTION_ID,
				labelColumnInfo);

		return labelProvider;
	}

	// ------------------------------------------------------------------------

	/**
	 * Fill-in the view node properties from a data view context.
	 * 
	 * @param propertiesUpdate
	 *            the properties object.
	 * @param peripheralDMContext
	 *            the data model context.
	 */
	protected void setProperties(IPropertiesUpdate propertiesUpdate,
			IPeripherals.IPeripheralDMContext peripheralDMContext) {

		assert peripheralDMContext != null;

		propertiesUpdate.setProperty(PROPERTY_NAME,
				peripheralDMContext.getName());
		propertiesUpdate.setProperty(PROPERTY_ADDRESS,
				peripheralDMContext.getAddress());
		propertiesUpdate.setProperty(PROPERTY_DESCRIPTION,
				peripheralDMContext.getDescription());

	}

	protected boolean getChecked(TreePath treePath,
			IPresentationContext presentationContext) throws CoreException {

		Object pathSegment = treePath.getLastSegment();
		if ((pathSegment instanceof PeripheralVMContext)) {
			PeripheralVMContext peripheralVMContext = (PeripheralVMContext) pathSegment;
			PeripheralsService.PeripheralDMContext peripheralDMContext = (PeripheralsService.PeripheralDMContext) peripheralVMContext
					.getDMContext();
			return peripheralDMContext.isShown();
		}
		return false;
	}

	// ------------------------------------------------------------------------

	public String toString() {
		return "PeripheralsVMNode(" + getSession().getId() + ")";
	}

	// ------------------------------------------------------------------------

}
