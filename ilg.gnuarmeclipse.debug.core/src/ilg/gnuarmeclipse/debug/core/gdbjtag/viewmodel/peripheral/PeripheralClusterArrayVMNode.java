package ilg.gnuarmeclipse.debug.core.gdbjtag.viewmodel.peripheral;

import java.math.BigInteger;

import org.eclipse.debug.core.DebugException;

import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.SvdDMNode;

public class PeripheralClusterArrayVMNode extends PeripheralGroupVMNode {

	// ------------------------------------------------------------------------

	public PeripheralClusterArrayVMNode(PeripheralTreeVMNode parent,
			SvdDMNode dmNode) {

		super(parent, dmNode);

	}

	// ------------------------------------------------------------------------

	@Override
	public String getDisplayNodeType() {
		return "Cluster array";
	}

	@Override
	public String getImageName() {
		return "registergroup_obj";
	}

	@Override
	public String getDisplaySize() {
		int dim = fDMNode.getArrayDim();
		if (dim != 0) {
			return dim + " elements";
		}

		return null;
	}

	@Override
	public BigInteger getBigAbsoluteAddress() {

		BigInteger base;
		try {
			base = ((PeripheralGroupVMNode) getRegisterGroup())
					.getBigAbsoluteAddress();
		} catch (DebugException e) {
			base = BigInteger.ZERO;
		}
		BigInteger offset;
		offset = fDMNode.getBigAddressOffset();

		return base.add(offset);
	}

	// ------------------------------------------------------------------------
}
