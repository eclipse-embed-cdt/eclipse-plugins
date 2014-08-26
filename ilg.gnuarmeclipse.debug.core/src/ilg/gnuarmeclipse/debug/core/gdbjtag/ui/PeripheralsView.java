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

package ilg.gnuarmeclipse.debug.core.gdbjtag.ui;

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
import org.eclipse.ui.progress.UIJob;

@SuppressWarnings("restriction")
public class PeripheralsView extends VariablesView implements
		IMemoryBlockListener, IDebugEventSetListener {

	// ------------------------------------------------------------------------

	public final static String PRESENTATION_CONTEXT_ID = "PeripheralsView";

	private UIJob fRefreshUIjob = new UIJob(
			PeripheralsView.class.getSimpleName() + "#refreshUIjob") {

		public IStatus runInUIThread(IProgressMonitor pm) {
			getViewer().refresh();
			return Status.OK_STATUS;
		}
	};

	// ------------------------------------------------------------------------

	public PeripheralsView() {
		fRefreshUIjob.setSystem(true);
	}

	protected String getPresentationContextId() {
		return PRESENTATION_CONTEXT_ID;
	}

	protected int getViewerStyle() {
		return SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.TITLE
				| SWT.VIRTUAL | SWT.FULL_SELECTION;
	}

	// ------------------------------------------------------------------------

	private void addDebugEventListener() {
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	private void removeDebugEventListener() {
		DebugPlugin.getDefault().removeDebugEventListener(this);
	}

	@Override
	public void handleDebugEvents(DebugEvent[] events) {
		System.out.print("handleDebugEvents() " + events.length);
		for (int i = 0; i < events.length; ++i) {
			System.out.print(" " + events[i]);
		}
		System.out.println();
	}

	// ------------------------------------------------------------------------

	private void addMemoryBlockListener() {
		DebugPlugin.getDefault().getMemoryBlockManager().addListener(this);
	}

	private void removeMemoryBlockListener() {
		DebugPlugin.getDefault().getMemoryBlockManager().removeListener(this);
	}

	@Override
	public void memoryBlocksAdded(IMemoryBlock[] memory) {
		System.out.println("memoryBlocksAdded() " + memory);
	}

	@Override
	public void memoryBlocksRemoved(IMemoryBlock[] memory) {
		System.out.println("memoryBlocksRemoved() " + memory);
	}

	// ------------------------------------------------------------------------

	@Override
	public Viewer createViewer(Composite composite) {

		TreeModelViewer treeModelViewer = (TreeModelViewer) super
				.createViewer(composite);

		addMemoryBlockListener();
		addDebugEventListener();
		// initStates(getMemento());
		return treeModelViewer;
	}

	@Override
	public void dispose() {

		removeDebugEventListener();
		removeMemoryBlockListener();
		super.dispose();
	}

	private void refresh() {
		fRefreshUIjob.schedule();
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class required) {
		return super.getAdapter(required);
	}

	// ------------------------------------------------------------------------
}
