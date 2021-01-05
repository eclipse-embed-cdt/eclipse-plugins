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

package org.eclipse.embedcdt.debug.gdbjtag.core.memory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.model.DsfMemoryBlockRetrieval;
import org.eclipse.cdt.dsf.debug.service.IMemory;
import org.eclipse.cdt.dsf.debug.service.IMemory.IMemoryDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IExitedDMEvent;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.internal.DsfPlugin;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockExtension;
import org.eclipse.embedcdt.debug.gdbjtag.core.datamodel.PeripheralDMContext;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class PeripheralMemoryBlockRetrieval extends DsfMemoryBlockRetrieval {

	// ------------------------------------------------------------------------

	private static final String PERIPHERALS_MEMENTO_ID = Activator.PLUGIN_ID + ".PERIPHERALS";
	private static final String PERIPHERALS_MEMENTO_COMPAT_ID = "ilg.gnumcueclipse.debug.gdbjtag" + ".PERIPHERALS";

	// Duplicate hear for not being public in parent class
	private static final String DSF_LAUNCH_ID = "org.eclipse.dsf.launch"; //$NON-NLS-1$
	private static final String ATTR_DEBUGGER_MEMORY_BLOCKS = DSF_LAUNCH_ID + ".MEMORY_BLOCKS"; //$NON-NLS-1$

	private static final String MEMORY_BLOCK_EXPRESSION_LIST = "memoryBlockExpressionList"; //$NON-NLS-1$
	private static final String ATTR_EXPRESSION_LIST_CONTEXT = "context"; //$NON-NLS-1$
	private static final String MEMORY_BLOCK_EXPRESSION = "memoryBlockExpression"; //$NON-NLS-1$
	private static final String ATTR_MEMORY_BLOCK_EXPR_LABEL = "label"; //$NON-NLS-1$
	private static final String ATTR_MEMORY_BLOCK_EXPR_ADDRESS = "address"; //$NON-NLS-1$
	private static final String CONTEXT_STRING = "Context string";

	// ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfig;
	private List<String> fPersistentPeripherals;

	// ------------------------------------------------------------------------

	public PeripheralMemoryBlockRetrieval(String modelId, ILaunchConfiguration config, DsfSession session)
			throws DebugException {
		super(modelId, config, session);

		fLaunchConfig = config;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralMemoryBlockRetrieval()");
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final IMemoryDMContext memoryCtx) {
		super.initialize(memoryCtx);
	}

	public List<String> getPersistentPeripherals() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralMemoryBlockRetrieval.getPersistentPeripherals()");
		}

		if (fPersistentPeripherals == null) {
			String memento;
			try {
				memento = fLaunchConfig.getAttribute(PERIPHERALS_MEMENTO_ID, "");
				if (memento == null || memento.trim().isEmpty()) {
					memento = fLaunchConfig.getAttribute(PERIPHERALS_MEMENTO_COMPAT_ID, "");
				}
				if (memento != null && !memento.trim().isEmpty()) {
					fPersistentPeripherals = parsePeripheralsMemento(memento);
				}
			} catch (CoreException e) {

			}
			if (fPersistentPeripherals == null) {
				fPersistentPeripherals = new ArrayList<>();
			}
		}
		return fPersistentPeripherals;
	}

	@Override
	public IMemoryBlockExtension getExtendedMemoryBlock(String addr, Object context) throws DebugException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralMemoryBlockRetrieval.getExtendedMemoryBlock(" + addr + "," + context + ")");
		}
		IMemoryBlockExtension memoryBlockExtension = null;
		if (context instanceof PeripheralDMContext) {
			PeripheralDMContext peripheralDMContext = (PeripheralDMContext) context;
			IMemory.IMemoryDMContext memoryDMContext = null;
			IDMContext dmContext = null;
			if (context instanceof IAdaptable
					&& (dmContext = ((IAdaptable) context).getAdapter(IDMContext.class)) != null) {

				memoryDMContext = DMContexts.getAncestorOfType(dmContext, IMemory.IMemoryDMContext.class);
			}
			if (memoryDMContext == null) {
				return null;
			}
			memoryBlockExtension = new PeripheralMemoryBlockExtension(this, memoryDMContext, getModelId(),
					peripheralDMContext);
		}

		if (memoryBlockExtension != null) {
			return memoryBlockExtension;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralMemoryBlockRetrieval.getExtendedMemoryBlock(" + addr
					+ ") super.getExtendedMemoryBlock()");
		}
		// Needed for regular memory blocks
		memoryBlockExtension = super.getExtendedMemoryBlock(addr, context);

		return memoryBlockExtension;

	}

	@Override
	public IMemoryBlock getMemoryBlock(long addr, long length) throws DebugException {

		// Do not return any memory block, use extended memory block above.
		return null;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return true;
	}

	@DsfServiceEventHandler
	public void eventDispatched(IExitedDMEvent event) {
		// If a memory context is exiting, save expressions and clean its used
		// resources.
		// Moved to MemoryBlockMonitor, to avoid race conditions.
		// saveMemoryBlocks();
	}

	@Override
	public void saveMemoryBlocks() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralMemoryBlockRetrieval.saveMemoryBlocks()");
		}

		try {
			ILaunchConfigurationWorkingCopy wc = fLaunchConfig.getWorkingCopy();

			if (fPersistentPeripherals != null) {
				// Save peripherals only if the Peripherals view was used,
				// otherwise leave them as before.
				wc.setAttribute(PERIPHERALS_MEMENTO_ID, getPeripheralsMemento());
			}

			wc.setAttribute(ATTR_DEBUGGER_MEMORY_BLOCKS, getMemoryMemento());

			wc.doSave();
		} catch (CoreException e) {
			DsfPlugin.getDefault().getLog().log(e.getStatus());
		}
	}

	/**
	 * Get a memento with the current peripheral memory blocks.
	 */
	public String getPeripheralsMemento() throws CoreException {

		IMemoryBlock[] blocks = DebugPlugin.getDefault().getMemoryBlockManager().getMemoryBlocks(this);
		Document document = DebugPlugin.newDocument();
		Element expressionList = document.createElement("peripherals");
		for (IMemoryBlock block : blocks) {
			if (block instanceof PeripheralMemoryBlockExtension) {
				PeripheralMemoryBlockExtension memoryBlock = (PeripheralMemoryBlockExtension) block;
				Element expression = document.createElement("peripheral");
				expression.setAttribute("name", memoryBlock.getExpression());
				expressionList.appendChild(expression);

				if (Activator.getInstance().isDebugging()) {
					System.out.println("Remember " + memoryBlock.getExpression());
				}
			}
		}
		document.appendChild(expressionList);
		return DebugPlugin.serializeDocument(document);
	}

	/**
	 * Return a memento to represent all active blocks created by this retrieval
	 * object (blocks currently registered with the platform's
	 * IMemoryBlockManager). We will be expected to recreate the blocks in
	 * {@link #createBlocksFromConfiguration(IMemoryDMContext, String)}.
	 *
	 * @return a string memento
	 * @throws CoreException
	 */
	public String getMemoryMemento() throws CoreException {
		IMemoryBlock[] blocks = DebugPlugin.getDefault().getMemoryBlockManager().getMemoryBlocks(this);
		Document document = DebugPlugin.newDocument();
		Element expressionList = document.createElement(MEMORY_BLOCK_EXPRESSION_LIST);
		expressionList.setAttribute(ATTR_EXPRESSION_LIST_CONTEXT, CONTEXT_STRING);
		for (IMemoryBlock block : blocks) {
			if (block instanceof PeripheralMemoryBlockExtension) {
				continue; // skip peripherals
			}
			if (block instanceof IMemoryBlockExtension) {
				IMemoryBlockExtension memoryBlock = (IMemoryBlockExtension) block;
				Element expression = document.createElement(MEMORY_BLOCK_EXPRESSION);
				expression.setAttribute(ATTR_MEMORY_BLOCK_EXPR_LABEL, memoryBlock.getExpression());
				expression.setAttribute(ATTR_MEMORY_BLOCK_EXPR_ADDRESS, memoryBlock.getBigBaseAddress().toString());
				expressionList.appendChild(expression);

				if (Activator.getInstance().isDebugging()) {
					System.out.println("Remember " + memoryBlock.getExpression());
				}
			}
		}
		document.appendChild(expressionList);
		return DebugPlugin.serializeDocument(document);
	}

	/**
	 * Parse the list of peripherals in an existing memento.
	 *
	 * @param memento
	 *            a string containing the memento xml.
	 * @return a list of peripheral names.
	 * @throws CoreException
	 */
	public List<String> parsePeripheralsMemento(String memento) throws CoreException {

		Element root = DebugPlugin.parseDocument(memento);
		if (!root.getNodeName().equalsIgnoreCase("peripherals")) {
			IStatus status = new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugPlugin.INTERNAL_ERROR,
					"Memory monitor initialization: invalid memento", null);//$NON-NLS-1$
			throw new CoreException(status);
		}

		List<String> peripherals = new ArrayList<>();
		NodeList expressionList = root.getChildNodes();
		int length = expressionList.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = expressionList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element entry = (Element) node;
				if (entry.getNodeName().equalsIgnoreCase("peripheral")) {
					String name = entry.getAttribute("name");
					if (Activator.getInstance().isDebugging()) {
						System.out.println("Restore " + name);
					}
					peripherals.add(name);
				}
			}
		}

		return peripherals;
	}

	// ------------------------------------------------------------------------
}
