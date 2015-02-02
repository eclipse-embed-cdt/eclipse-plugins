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
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral.PeripheralRendering;
import ilg.gnuarmeclipse.debug.gdbjtag.ui.Messages;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.debug.core.model.IMemoryBlockRetrievalExtension;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.memory.IMemoryRendering;
import org.eclipse.debug.ui.memory.IMemoryRenderingContainer;
import org.eclipse.debug.ui.memory.IMemoryRenderingSite;
import org.eclipse.debug.ui.memory.IMemoryRenderingType;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

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
		System.out.println("MemoryBlockMonitor()");
	}

	// ------------------------------------------------------------------------

	public void displayPeripheralMonitor(
			final IWorkbenchWindow workbenchWindow,
			final PeripheralDMContext peripheralDMContext,
			final boolean isChecked) {

		System.out.println("MemoryBlockMonitor.displayPeripheralMonitor("
				+ isChecked + ")");

		Object object;
		object = peripheralDMContext.getAdapter(PeripheralDMNode.class);

		if ((object instanceof IMemoryBlockRetrieval)) {

			final IMemoryBlockRetrieval memoryBlockRetrieval = (IMemoryBlockRetrieval) object;

			/*
			 * To improve rendering refresh, run this on the display thread
			 */
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (isChecked) {
						addMemoryBlock(workbenchWindow, peripheralDMContext,
								memoryBlockRetrieval);
					} else {
						removeMemoryBlock(workbenchWindow, peripheralDMContext);
					}
				}
			});
		}
	}

	/**
	 * Called when the debug session is terminated, to remove all memory
	 * monitors, regardless the type.
	 */
	public void removeAllMemoryBlocks() {

		System.out.println("MemoryBlockMonitor.removeAllMemoryBlocks()");

		IMemoryBlock[] memoryBlocks = DebugPlugin.getDefault()
				.getMemoryBlockManager().getMemoryBlocks();

		DebugPlugin.getDefault().getMemoryBlockManager()
				.removeMemoryBlocks(memoryBlocks);
	}

	// ------------------------------------------------------------------------

	/**
	 * Called by displayPeripheralMonitor() from the UI thread.
	 * 
	 * @param workbenchWindow
	 * @param peripheralDMContext
	 * @param memoryBlockRetrieval
	 */
	// @SuppressWarnings("restriction")
	private void addMemoryBlock(IWorkbenchWindow workbenchWindow,
			PeripheralDMContext peripheralDMContext,
			IMemoryBlockRetrieval memoryBlockRetrieval) {

		System.out.println("MemoryBlockMonitor.addMemoryBlock() "
				+ peripheralDMContext.getName());

		String addr = peripheralDMContext.getPeripheralInstance()
				.getHexAddress();
		try {
			if ((memoryBlockRetrieval instanceof IMemoryBlockRetrievalExtension)) {

				IMemoryBlock memoryBlockToAdd = ((IMemoryBlockRetrievalExtension) memoryBlockRetrieval)
						.getExtendedMemoryBlock(addr, peripheralDMContext);
				if (memoryBlockToAdd != null) {

					// Instruct the memory block manager to add the new memory
					// block.
					DebugPlugin
							.getDefault()
							.getMemoryBlockManager()
							.addMemoryBlocks(
									new IMemoryBlock[] { memoryBlockToAdd });

					// Render it using the custom rendering.
					addDefaultRenderings(workbenchWindow, memoryBlockToAdd,
							PeripheralRendering.ID);
				} else {
					EclipseUtils.openError(Messages.AddMemoryBlockAction_title,
							Messages.AddMemoryBlockAction_noMemoryBlock, null);
				}
			} else {
				Activator.log("Cannot process memory block retrieval "
						+ memoryBlockRetrieval);
			}
		} catch (DebugException e) {
			EclipseUtils.openError(Messages.AddMemoryBlockAction_title,
					Messages.AddMemoryBlockAction_failed, e);
		} catch (NumberFormatException e) {
			String msg = Messages.AddMemoryBlockAction_failed + "\n"
					+ Messages.AddMemoryBlockAction_input_invalid;
			EclipseUtils.openError(Messages.AddMemoryBlockAction_title, msg,
					null);
		}
	}

	private void removeMemoryBlock(IWorkbenchWindow workbenchWindow,
			PeripheralDMContext peripheralDMContext) {

		System.out.println("MemoryBlockMonitor.removeMemoryBlock() "
				+ peripheralDMContext.getName());

		IMemoryBlock[] memoryBlocks = DebugPlugin.getDefault()
				.getMemoryBlockManager().getMemoryBlocks();
		for (IMemoryBlock memoryBlock : memoryBlocks) {

			if ((memoryBlock instanceof PeripheralMemoryBlockExtension)) {

				// The expression identifies the block (the display name).
				String expression = ((PeripheralMemoryBlockExtension) memoryBlock)
						.getExpression();
				if (expression.equals(peripheralDMContext
						.getPeripheralInstance().getDisplayName())) {

					DebugPlugin
							.getDefault()
							.getMemoryBlockManager()
							.removeMemoryBlocks(
									new IMemoryBlock[] { memoryBlock });
					// Continue, to allow it clean all possible duplicates
				}
			} else {
				Activator.log("Cannot process memory block " + memoryBlock);
			}
		}
	}

	public void removeMemoryBlocks(IMemoryBlock[] memoryBlocks) {

		DebugPlugin.getDefault().getMemoryBlockManager()
				.removeMemoryBlocks(memoryBlocks);
	}

	/**
	 * Called from UI thread.
	 * 
	 * @param workbenchWindow
	 * @param memoryBlock
	 * @param id
	 */
	private void addDefaultRenderings(IWorkbenchWindow workbenchWindow,
			IMemoryBlock memoryBlock, String id) {

		if (id == null)
			id = "";

		Object type = null;
		IMemoryRenderingType primaryType = DebugUITools
				.getMemoryRenderingManager().getPrimaryRenderingType(
						memoryBlock);
		if ((primaryType != null) && (id.equals(primaryType.getId())))
			type = primaryType;
		if (type == null) {
			IMemoryRenderingType[] defaultTypes = DebugUITools
					.getMemoryRenderingManager().getDefaultRenderingTypes(
							memoryBlock);
			for (IMemoryRenderingType defaultType : defaultTypes) {
				System.out.println((defaultType.getId()));
				type = defaultType;
				if (id.equals(defaultType.getId()))
					break;
			}
		}
		try {
			if (type != null) {
				createRenderingInContainer(workbenchWindow, memoryBlock,
						(IMemoryRenderingType) type,
						"org.eclipse.debug.ui.MemoryView.RenderingViewPane.1");
			}
		} catch (CoreException e) {
			Activator.log(e);
		}

		// In case the Memory view was not visible, make it visible now.
		showMemoryView(workbenchWindow);
	}

	/**
	 * Create a new rendering in the Memory view, and initialise it with the
	 * memory block.
	 * 
	 * @param workbenchWindow
	 * @param memoryBlock
	 * @param memoryRenderingType
	 * @param id
	 * @throws CoreException
	 */
	private void createRenderingInContainer(IWorkbenchWindow workbenchWindow,
			IMemoryBlock memoryBlock, IMemoryRenderingType memoryRenderingType,
			String id) throws CoreException {

		System.out.println(String.format(
				"MemoryBlockMonitor.createRenderingInContainer() 0x%X",
				memoryBlock.getStartAddress()));

		IMemoryRenderingSite memoryRenderingSite = getRenderingSite(workbenchWindow);
		if (memoryRenderingSite != null) {

			IMemoryRenderingContainer memoryRenderingContainer = memoryRenderingSite
					.getContainer(id);
			IMemoryRendering memoryRendering = memoryRenderingType
					.createRendering();
			if (memoryRendering != null) {
				memoryRendering.init(memoryRenderingContainer, memoryBlock);
				memoryRenderingContainer.addMemoryRendering(memoryRendering);
			}
		}
	}

	/**
	 * Identify the rendering site of the "Memory" view.
	 * 
	 * @param workbenchWindow
	 * @return the rendering site, or null if not found.
	 */
	private IMemoryRenderingSite getRenderingSite(
			IWorkbenchWindow workbenchWindow) {

		if (workbenchWindow != null) {
			IViewPart viewPart = workbenchWindow.getActivePage().findView(
					"org.eclipse.debug.ui.MemoryView");
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
	private void showMemoryView(final IWorkbenchWindow workbenchWindow) {
		try {
			workbenchWindow.getActivePage().showView(
					"org.eclipse.debug.ui.MemoryView");
		} catch (Exception e) {
		}
	}

	// ------------------------------------------------------------------------
}
