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

package ilg.gnuarmeclipse.debug.gdbjtag.memory;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMContext;
import ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral.PeripheralRendering;
import ilg.gnuarmeclipse.debug.gdbjtag.ui.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.debug.core.model.IMemoryBlockRetrievalExtension;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.memory.IMemoryRendering;
import org.eclipse.debug.ui.memory.IMemoryRenderingContainer;
import org.eclipse.debug.ui.memory.IMemoryRenderingSite;
import org.eclipse.debug.ui.memory.IMemoryRenderingType;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * This class manages adding/removing memory monitors. The memory monitors are
 * used to display the content of a memory area. In our case the memory areas
 * are peripheral blocks.
 * <p>
 * The PeripheralsView listens for changes, to update the view when monitors are
 * removed.
 */
public class MemoryBlockMonitor {

	// ------------------------------------------------------------------------

	// The shared instance
	private static final MemoryBlockMonitor fgInstance;

	static {
		// Use the class loader to avoid using synchronisation on getInstance().
		fgInstance = new MemoryBlockMonitor();
	}

	public static MemoryBlockMonitor getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	public MemoryBlockMonitor() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("MemoryBlockMonitor()");
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Called from UI Thread.
	 * 
	 * @param workbenchWindow
	 * @param peripheralDMContext
	 * @param isChecked
	 */
	public void displayPeripheralMonitor(final IWorkbenchWindow workbenchWindow,
			final PeripheralDMContext peripheralDMContext, final boolean isChecked) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("MemoryBlockMonitor.displayPeripheralMonitor(" + isChecked + ")");
		}

		Object object;
		object = peripheralDMContext.getAdapter(PeripheralMemoryBlockRetrieval.class);

		if ((object instanceof IMemoryBlockRetrieval)) {

			final IMemoryBlockRetrieval memoryBlockRetrieval = (IMemoryBlockRetrieval) object;

			if (isChecked) {
				addMemoryBlock(workbenchWindow, peripheralDMContext, memoryBlockRetrieval);

				// In case the Memory view was not visible, make it
				// visible now.
				showMemoryView(workbenchWindow);
			} else {
				removeMemoryBlock(workbenchWindow, peripheralDMContext);
			}
		}
	}

	/**
	 * Find memory block retrieval and save names.
	 * 
	 * @param memoryBlocks
	 */
	public void savePeripheralNames(IMemoryBlock[] memoryBlocks) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("MemoryBlockMonitor.savePeripheralNames()");
		}

		for (int i = 0; i < memoryBlocks.length; ++i) {
			if (memoryBlocks[i] instanceof PeripheralMemoryBlockExtension) {

				PeripheralMemoryBlockExtension memBlock = (PeripheralMemoryBlockExtension) memoryBlocks[i];

				IMemoryBlockRetrieval memRetrieval;
				memRetrieval = memBlock.getMemoryBlockRetrieval();

				if (memRetrieval instanceof PeripheralMemoryBlockRetrieval) {
					((PeripheralMemoryBlockRetrieval) memRetrieval).saveMemoryBlocks();
					break;
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Called by displayPeripheralMonitor() and addPersistentPeripherals() from
	 * the UI thread.
	 * 
	 * @param workbenchWindow
	 * @param peripheralDMContext
	 * @param memoryBlockRetrieval
	 */
	// @SuppressWarnings("restriction")
	public void addMemoryBlock(IWorkbenchWindow workbenchWindow, PeripheralDMContext peripheralDMContext,
			IMemoryBlockRetrieval memoryBlockRetrieval) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("MemoryBlockMonitor.addMemoryBlock() " + peripheralDMContext.getName());
		}

		String addr = peripheralDMContext.getPeripheralInstance().getHexAddress();
		try {
			if ((memoryBlockRetrieval instanceof IMemoryBlockRetrievalExtension)) {

				IMemoryBlock memoryBlockToAdd = ((IMemoryBlockRetrievalExtension) memoryBlockRetrieval)
						.getExtendedMemoryBlock(addr, peripheralDMContext);
				if (memoryBlockToAdd != null) {

					// Instruct the memory block manager to add the new memory
					// block.
					DebugPlugin.getDefault().getMemoryBlockManager()
							.addMemoryBlocks(new IMemoryBlock[] { memoryBlockToAdd });

					// Add a custom rendering for the memory block.
					addDefaultRenderings(workbenchWindow, memoryBlockToAdd, PeripheralRendering.ID);

				} else {
					EclipseUtils.openError(Messages.AddMemoryBlockAction_title,
							Messages.AddMemoryBlockAction_noMemoryBlock, null);
				}
			} else {
				Activator.log("Cannot process memory block retrieval " + memoryBlockRetrieval);
			}
		} catch (DebugException e) {
			EclipseUtils.openError(Messages.AddMemoryBlockAction_title, Messages.AddMemoryBlockAction_failed, e);
		} catch (NumberFormatException e) {
			String msg = Messages.AddMemoryBlockAction_failed + "\n" + Messages.AddMemoryBlockAction_input_invalid;
			EclipseUtils.openError(Messages.AddMemoryBlockAction_title, msg, null);
		}
	}

	private void removeMemoryBlock(IWorkbenchWindow workbenchWindow, PeripheralDMContext peripheralDMContext) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("MemoryBlockMonitor.removeMemoryBlock() " + peripheralDMContext.getName());
		}

		IMemoryBlock[] memoryBlocks = DebugPlugin.getDefault().getMemoryBlockManager().getMemoryBlocks();
		for (IMemoryBlock memoryBlock : memoryBlocks) {

			if ((memoryBlock instanceof PeripheralMemoryBlockExtension)) {

				// The expression identifies the block (the display name).
				String expression = ((PeripheralMemoryBlockExtension) memoryBlock).getExpression();
				if (expression.equals(peripheralDMContext.getPeripheralInstance().getDisplayName())) {

					DebugPlugin.getDefault().getMemoryBlockManager()
							.removeMemoryBlocks(new IMemoryBlock[] { memoryBlock });
					// Continue, to allow it clean all possible duplicates
				}
			} else {
				Activator.log("Cannot process memory block " + memoryBlock);
			}
		}
	}

	public void removeMemoryBlocks(IMemoryBlock[] memoryBlocks) {

		DebugPlugin.getDefault().getMemoryBlockManager().removeMemoryBlocks(memoryBlocks);
	}

	/**
	 * Called from UI thread.
	 * 
	 * @param workbenchWindow
	 * @param memoryBlock
	 * @param renderingId
	 */
	private void addDefaultRenderings(IWorkbenchWindow workbenchWindow, IMemoryBlock memoryBlock, String renderingId) {

		if (renderingId == null)
			renderingId = "";

		Object type = null;
		IMemoryRenderingType primaryType = DebugUITools.getMemoryRenderingManager()
				.getPrimaryRenderingType(memoryBlock);
		if ((primaryType != null) && (renderingId.equals(primaryType.getId()))) {
			type = primaryType;
		}
		if (type == null) {
			IMemoryRenderingType[] defaultTypes = DebugUITools.getMemoryRenderingManager()
					.getDefaultRenderingTypes(memoryBlock);
			for (IMemoryRenderingType defaultType : defaultTypes) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("addDefaultRenderings() " + (defaultType.getId()));
				}
				type = defaultType;
				if (renderingId.equals(defaultType.getId())) {
					break;
				}
			}
		}
		try {
			if (type != null) {
				createRenderingInContainer(workbenchWindow, memoryBlock, (IMemoryRenderingType) type,
						IDebugUIConstants.ID_RENDERING_VIEW_PANE_1);
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
	}

	/**
	 * Create a new rendering in the Memory view, and initialise it with the
	 * memory block.
	 * 
	 * @param workbenchWindow
	 * @param memoryBlock
	 * @param memoryRenderingType
	 * @param paneId
	 * @throws CoreException
	 */
	private void createRenderingInContainer(IWorkbenchWindow workbenchWindow, IMemoryBlock memoryBlock,
			IMemoryRenderingType memoryRenderingType, String paneId) throws CoreException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(String.format("MemoryBlockMonitor.createRenderingInContainer() 0x%X",
					memoryBlock.getStartAddress()));
		}

		IMemoryRenderingSite site = getRenderingSite(workbenchWindow);
		if (site != null) {

			IMemoryRenderingContainer container = site.getContainer(paneId);
			IMemoryRendering rendering = memoryRenderingType.createRendering();
			if (rendering != null) {
				rendering.init(container, memoryBlock);
				container.addMemoryRendering(rendering);
			}
		}
	}

	/**
	 * Identify the rendering site of the "Memory" view.
	 * 
	 * @param workbenchWindow
	 * @return the rendering site, or null if not found.
	 */
	private IMemoryRenderingSite getRenderingSite(IWorkbenchWindow workbenchWindow) {

		if (workbenchWindow != null) {
			IViewPart viewPart = workbenchWindow.getActivePage().findView(IDebugUIConstants.ID_MEMORY_VIEW);
			return (IMemoryRenderingSite) viewPart;
		}
		return null;
	}

	/**
	 * Make the "Memory" view visible, by calling showView(). Called from the UI
	 * thread, no need to start a separate job.
	 * 
	 * @param workbenchWindow
	 */
	public void showMemoryView(final IWorkbenchWindow workbenchWindow) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("showView(MemoryView)");
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					workbenchWindow.getActivePage().showView(IDebugUIConstants.ID_MEMORY_VIEW);
					if (Activator.getInstance().isDebugging()) {
						System.out.println("showView(MemoryView) done");
					}
				} catch (PartInitException e) {
					Activator.log(e);
				}
			}
		});
	}

	// ------------------------------------------------------------------------
}
