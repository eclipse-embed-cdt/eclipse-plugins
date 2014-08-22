/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.dsf;

import java.math.BigInteger;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMEvent;
import org.eclipse.cdt.dsf.debug.service.IModules;
import org.eclipse.cdt.dsf.debug.service.IRunControl;
import org.eclipse.cdt.dsf.service.IDsfService;

public interface IPeripheral extends IDsfService {

	// ----- Embedded interfaces ----------------------------------------------

	/**
	 * Data model interface for using peripheral blocks.
	 */
	public static abstract interface IPeripheralDMContext extends IDMContext {

		/**
		 * Get peripheral name.
		 * 
		 * @return a short string, generally upper case.
		 */
		public abstract String getName();

		/**
		 * Get the peripheral start address.
		 * 
		 * @return the string hex representation of the peripheral address.
		 */
		public abstract String getAddress();

		/**
		 * Get the peripheral block length.
		 * 
		 * @return the length of the peripheral block, in bytes.
		 */
		public abstract long getLength();

		/**
		 * Get the peripheral start address.
		 * 
		 * @return the numeric version of start address.
		 */
		public abstract BigInteger getBigAddress();

		/**
		 * Get the peripheral description.
		 * 
		 * @return a short string describing the peripheral.
		 */
		public abstract String getDescription();

		/**
		 * Tell if the peripheral is enabled.
		 * 
		 * @return true if the peripheral is enabled.
		 */
		public abstract boolean isEnabled();

		/**
		 * Enable/disable the peripheral.
		 * 
		 * @param flag
		 *            a boolean.
		 */
		public abstract void setEnabled(boolean flag);
	}

	public static abstract interface PeripheralLoadedDMEvent extends
			IPeripheral.PeripheralsChangedDMEvent {
		public abstract IPeripheral.IPeripheralDMContext getLoadedPeripheralContext();
	}

	public static abstract interface PeripheralUnloadedDMEvent extends
			IPeripheral.PeripheralsChangedDMEvent {
		public abstract IPeripheral.IPeripheralDMContext getUnloadedPeripheralContext();
	}

	public static abstract interface PeripheralsChangedDMEvent extends
			IDMEvent<IModules.ISymbolDMContext> {
	}

	// ------------------------------------------------------------------------

	public abstract void getPeripheralData(
			IPeripheralDMContext peripheralDMContext,
			DataRequestMonitor<IPeripheralDMContext> dataRequestMonitor);

	public abstract void getPeripherals(
			IRunControl.IContainerDMContext containerDMContext,
			DataRequestMonitor<IPeripheralDMContext[]> dataRequestMonitor);

	// ------------------------------------------------------------------------

}
