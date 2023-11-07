package org.eclipse.embedcdt.debug.gdbjtag.core.datamodel;

import org.eclipse.cdt.dsf.datamodel.AbstractDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;

public class PeripheralGroupDMContext extends AbstractDMContext implements IPeripheralGroupDMContext {

	private String name;

	// ------------------------------------------------------------------------
	public PeripheralGroupDMContext(DsfSession session, IDMContext[] parents, String name) {
		super(session, parents);
		this.name = name;
	}

	// ------------------------------------------------------------------------

	@Override
	public int compareTo(IPeripheralGroupDMContext context) {
		return getName().compareTo(context.getName());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PeripheralGroupDMContext) {
			PeripheralGroupDMContext comp = (PeripheralGroupDMContext) obj;
			return baseEquals(obj) && getName().equals(comp.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return baseHashCode() + name.hashCode();
	}

}
