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

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMContext;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.model.DsfMemoryBlockRetrieval;
import org.eclipse.cdt.dsf.debug.service.IMemory;
import org.eclipse.cdt.dsf.debug.service.IMemory.IMemoryDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IExitedDMEvent;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.internal.memory.GdbMemoryBlockRetrieval;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("restriction")
public class PeripheralMemoryBlockRetrieval extends GdbMemoryBlockRetrieval {

	// ------------------------------------------------------------------------

    private static final String PERIPHERALS_MEMENTO_ID = Activator.PLUGIN_ID
			+ ".PERIPHERALS";

    // ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfig;
	private List<String> fPersistentPeripherals;

	// ------------------------------------------------------------------------

	public PeripheralMemoryBlockRetrieval(String modelId,
			ILaunchConfiguration config, DsfSession session)
			throws DebugException {
		super(modelId, config, session);

		fLaunchConfig = config;

		System.out.println("PeripheralMemoryBlockRetrieval()");
	}

	// ------------------------------------------------------------------------

	public void initialize(final IMemoryDMContext memoryCtx) {
		super.initialize(memoryCtx);
	}

	public List<String> getPersistentPeripherals() {

		if (fPersistentPeripherals == null) {
			String memento;
			try {
				memento = fLaunchConfig
						.getAttribute(PERIPHERALS_MEMENTO_ID, "");
				if (memento != null && memento.trim().length() != 0) {
					fPersistentPeripherals = parsePeripheralsMemento(memento);
				}
			} catch (CoreException e) {
				;
			}
			if (fPersistentPeripherals == null) {
				fPersistentPeripherals = new ArrayList<String>();
			}
		}
		return fPersistentPeripherals;
	}

	@Override
	public IMemoryBlockExtension getExtendedMemoryBlock(String addr,
			Object context) throws DebugException {

		System.out.println("getExtendedMemoryBlock(" + addr + "," + context
				+ ")");
		IMemoryBlockExtension memoryBlockExtension = null;
		if (context instanceof PeripheralDMContext) {
			PeripheralDMContext peripheralDMContext = (PeripheralDMContext) context;
			IMemory.IMemoryDMContext memoryDMContext = null;
			IDMContext dmContext = null;
			if (context instanceof IAdaptable
					&& (dmContext = (IDMContext) ((IAdaptable) context)
							.getAdapter((Class<IDMContext>) IDMContext.class)) != null) {

				memoryDMContext = (IMemory.IMemoryDMContext) DMContexts
						.getAncestorOfType(
								(IDMContext) dmContext,
								(Class<IMemory.IMemoryDMContext>) IMemory.IMemoryDMContext.class);
			}
			if (memoryDMContext == null) {
				return null;
			}
			memoryBlockExtension = new PeripheralMemoryBlockExtension(
					(DsfMemoryBlockRetrieval) this, memoryDMContext,
					getModelId(), peripheralDMContext);
		}

		if (memoryBlockExtension == null) {
			System.out.println("getExtendedMemoryBlock(" + addr + "," + context
					+ ") super.getExtendedMemoryBlock()");
			// Needed for regular memory blocks
			memoryBlockExtension = super.getExtendedMemoryBlock(addr, context);
		}
		return memoryBlockExtension;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long addr, long length)
			throws DebugException {

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
		// resources
		saveMemoryBlocks();
	}

	public void saveMemoryBlocks() {
		try {
			ILaunchConfigurationWorkingCopy wc = fLaunchConfig.getWorkingCopy();
			wc.setAttribute(PERIPHERALS_MEMENTO_ID, getMemento());
			wc.doSave();
		} catch (CoreException e) {
			DsfPlugin.getDefault().getLog().log(e.getStatus());
		}
	}

	/**
	 * Get a memento with the current peripheral memory blocks.
	 */
	@Override
	public String getMemento() throws CoreException {

		IMemoryBlock[] blocks = DebugPlugin.getDefault()
				.getMemoryBlockManager().getMemoryBlocks(this);
		Document document = DebugPlugin.newDocument();
		Element expressionList = document.createElement("peripherals");
		for (IMemoryBlock block : blocks) {
			if (block instanceof PeripheralMemoryBlockExtension) {
				PeripheralMemoryBlockExtension memoryBlock = (PeripheralMemoryBlockExtension) block;
				Element expression = document.createElement("peripheral");
				expression.setAttribute("name", memoryBlock.getExpression());
				expressionList.appendChild(expression);
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
	public List<String> parsePeripheralsMemento(String memento)
			throws CoreException {

		Element root = DebugPlugin.parseDocument(memento);
		if (!root.getNodeName().equalsIgnoreCase("peripherals")) {
			IStatus status = new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID,
					DebugPlugin.INTERNAL_ERROR,
					"Memory monitor initialization: invalid memento", null);//$NON-NLS-1$
			throw new CoreException(status);
		}

		List<String> peripherals = new ArrayList<String>();
		NodeList expressionList = root.getChildNodes();
		int length = expressionList.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = expressionList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element entry = (Element) node;
				if (entry.getNodeName().equalsIgnoreCase("peripheral")) {
					String name = entry.getAttribute("name");
					System.out.println("Memento " + name);
					peripherals.add(name);
				}
			}
		}

		return peripherals;
	}

	// ------------------------------------------------------------------------
}
