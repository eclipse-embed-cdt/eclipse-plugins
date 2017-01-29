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

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripherals;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.SystemUIJob;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.memory.MemoryBlockMonitor;
import ilg.gnuarmeclipse.debug.gdbjtag.memory.PeripheralMemoryBlockExtension;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.IMemoryBlockListener;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.internal.ui.viewers.model.provisional.TreeModelViewer;
import org.eclipse.debug.internal.ui.views.variables.VariablesView;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.progress.UIJob;

@SuppressWarnings("restriction")
public class PeripheralsView extends VariablesView implements IMemoryBlockListener, IDebugEventSetListener {

	// ------------------------------------------------------------------------

	public final static String PRESENTATION_CONTEXT_ID = "PeripheralsView";

	private UIJob fRefreshUIjob = new SystemUIJob(PeripheralsView.class.getSimpleName() + "#refreshUIjob") {

		public IStatus runInUIThread(IProgressMonitor pm) {

			Viewer viewer = getViewer();
			if (viewer != null) {
				viewer.refresh();
			}
			return Status.OK_STATUS;
		}
	};

	// ------------------------------------------------------------------------

	Set<PeripheralMemoryBlockExtension> fMemoryBlocks;

	// ------------------------------------------------------------------------

	public PeripheralsView() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsView()");
		}

		fMemoryBlocks = new HashSet<PeripheralMemoryBlockExtension>();
	}

	protected String getPresentationContextId() {
		return PRESENTATION_CONTEXT_ID;
	}

	protected int getViewerStyle() {
		return SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.TITLE | SWT.VIRTUAL | SWT.FULL_SELECTION;
	}

	// ------------------------------------------------------------------------

	private void addDebugEventListener() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	private void removeDebugEventListener() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	// Contributed by IDebugEventSetListener

	@Override
	public void handleDebugEvents(DebugEvent[] events) {

		if (Activator.getInstance().isDebugging()) {
			System.out.print("PeripheralsView.handleDebugEvents() " + events.length);
			for (int i = 0; i < events.length; ++i) {
				System.out.print(" " + events[i]);
			}
			System.out.println();
		}

		for (int i = 0; i < events.length; ++i) {

			// Currently there are no special events to be processed here, only
			// cleanup actions are handled, the MODEL_SPECIFIC is no yet used.
			if (events[i].getKind() == DebugEvent.TERMINATE) {

				// Clear possible messages.
				EclipseUtils.clearStatusMessage();

			} else if (events[i].getKind() == DebugEvent.MODEL_SPECIFIC) {
				// Currently no longer fired
				Object object = events[i].getSource();
				if ((object instanceof PeripheralMemoryBlockExtension)) {
					refresh();
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	private void addMemoryBlockListener() {
		DebugPlugin.getDefault().getMemoryBlockManager().addListener(this);
	}

	private void removeMemoryBlockListener() {
		DebugPlugin.getDefault().getMemoryBlockManager().removeListener(this);
	}

	// Contributed by IMemoryBlockListener

	@Override
	public void memoryBlocksAdded(IMemoryBlock[] memoryBlocks) {

		// These are notifications that the memory blocks were already added
		for (int i = 0; i < memoryBlocks.length; i++) {
			if ((memoryBlocks[i] instanceof PeripheralMemoryBlockExtension)) {

				PeripheralMemoryBlockExtension memoryBlockExtension = (PeripheralMemoryBlockExtension) memoryBlocks[i];
				if (Activator.getInstance().isDebugging()) {
					System.out.println("PeripheralsView.memoryBlocksAdded() [] " + memoryBlockExtension);
				}

				fMemoryBlocks.add(memoryBlockExtension);
			}
		}
	}

	// Contributed by IMemoryBlockListener

	@Override
	public void memoryBlocksRemoved(IMemoryBlock[] memoryBlocks) {

		// These are notifications that the memory blocks were already removed
		for (int i = 0; i < memoryBlocks.length; i++) {
			if ((memoryBlocks[i] instanceof PeripheralMemoryBlockExtension)) {

				PeripheralMemoryBlockExtension memoryBlockExtension = (PeripheralMemoryBlockExtension) memoryBlocks[i];
				if (Activator.getInstance().isDebugging()) {
					System.out.println("PeripheralsView.memoryBlocksRemoved() [] " + memoryBlockExtension);
				}

				fMemoryBlocks.remove(memoryBlockExtension);
			}
		}

		// Update the check box status when the memory block is removed from the
		// monitor view.
		refresh();
	}

	// ------------------------------------------------------------------------

	@Override
	public Viewer createViewer(Composite composite) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsView.createViewer()");
		}

		TreeModelViewer viewer = (TreeModelViewer) super.createViewer(composite);

		addMemoryBlockListener();
		addDebugEventListener();

		initStates(getMemento());

		return viewer;
	}

	@Override
	public void dispose() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsView.dispose()");
		}

		if (!fMemoryBlocks.isEmpty()) {

			IMemoryBlock[] memoryBlocks = fMemoryBlocks.toArray(new IMemoryBlock[fMemoryBlocks.size()]);

			// Save open monitors
			MemoryBlockMonitor.getInstance().savePeripheralNames(memoryBlocks);
			// Remove all peripheral monitors
			MemoryBlockMonitor.getInstance().removeMemoryBlocks(memoryBlocks);
		}

		// Confirm that all peripheral monitors were removed
		assert (fMemoryBlocks.isEmpty());

		removeDebugEventListener();
		removeMemoryBlockListener();

		super.dispose();
	}

	private void refresh() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsView.refresh()");
		}
		fRefreshUIjob.schedule();
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class required) {
		return super.getAdapter(required);
	}

	// ------------------------------------------------------------------------

	private void initStates(IMemento memento) {
		;
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
	}

	public void saveViewerState(IMemento memento) {
		super.saveViewerState(memento);
	}

	// ------------------------------------------------------------------------
}
