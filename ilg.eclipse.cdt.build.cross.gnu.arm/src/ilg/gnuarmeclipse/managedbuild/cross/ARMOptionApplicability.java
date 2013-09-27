package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;
import org.eclipse.cdt.managedbuilder.core.IToolChain;

public class ARMOptionApplicability implements IOptionApplicability {

	@Override
	public boolean isOptionUsedInCommandLine(IBuildObject configuration,
			IHoldsOptions holder, IOption option) {

		return isOption(configuration, holder, option);
	}

	@Override
	public boolean isOptionVisible(IBuildObject configuration,
			IHoldsOptions holder, IOption option) {

		return isOption(configuration, holder, option);
	}

	@Override
	public boolean isOptionEnabled(IBuildObject configuration,
			IHoldsOptions holder, IOption option) {

		return isOption(configuration, holder, option);
	}

	private boolean isOption(IBuildObject configuration, IHoldsOptions holder,
			IOption option) {

		IToolChain toolchain = (IToolChain) holder;
		String sFamilyId = Activator.getOptionPrefix() + ".family";
		IOption checkedOption = toolchain.getOptionBySuperClassId(sFamilyId); //$NON-NLS-1$
		if (checkedOption != null) {
			String sValue;
			try {
				sValue = checkedOption.getStringValue();
				if (sValue.endsWith(".arm"))
					return true;
			} catch (BuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}
}
