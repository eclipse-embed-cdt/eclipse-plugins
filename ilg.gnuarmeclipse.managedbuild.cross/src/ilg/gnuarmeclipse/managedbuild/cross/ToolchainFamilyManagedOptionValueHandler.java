package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;

public class ToolchainFamilyManagedOptionValueHandler implements
		IManagedOptionValueHandler {

	@Override
	public boolean handleValue(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument,
			int event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDefaultValue(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnumValueAppropriate(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument,
			String enumValue) {

		if ("none".equals(enumValue))
			return false;

		return true;
	}

}
