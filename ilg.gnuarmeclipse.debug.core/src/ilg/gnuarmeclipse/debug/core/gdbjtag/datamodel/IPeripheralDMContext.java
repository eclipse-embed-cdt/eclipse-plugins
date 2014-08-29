package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import org.eclipse.cdt.dsf.datamodel.IDMContext;

/**
 * Data model interface for peripheral blocks.
 */
public abstract interface IPeripheralDMContext extends IDMContext {

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
	 * Get the peripheral description.
	 * 
	 * @return a short string describing the peripheral.
	 */
	public abstract String getDescription();

	public abstract boolean isSystem();

	public abstract boolean isChecked();

	public abstract void setChecked(boolean flag);

	public abstract boolean isShown();

}