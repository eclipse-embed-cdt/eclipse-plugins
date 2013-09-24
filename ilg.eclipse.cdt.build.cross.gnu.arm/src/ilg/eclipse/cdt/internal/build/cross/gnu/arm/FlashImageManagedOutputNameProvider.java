package ilg.eclipse.cdt.internal.build.cross.gnu.arm;

import org.eclipse.cdt.managedbuilder.core.IManagedOutputNameProvider;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class FlashImageManagedOutputNameProvider implements
		IManagedOutputNameProvider {

	public IPath[] getOutputNames(ITool tool, IPath[] primaryInputNames) {

		String value = null;

		IOption option = tool
				.getOptionBySuperClassId("cdt.managedbuild.option.cross.gnu.arm.base.createflash.choice");
		if (option != null)
			value = (String)option.getValue();
		// System.out.println(value);

		String ext = "unknown";
		if (value != null)
		{
			if (value.endsWith(".choice.ihex"))
				ext = "hex";
			else if (value.endsWith(".choice.srec"))
				ext = "srec";
			else if (value.endsWith(".choice.symbolsrec"))
				ext = "symbolsrec";
			else if (value.endsWith(".choice.binary"))
				ext = "bin";				
		}

		IPath[] iPath = new IPath[1];
		iPath[0] = new Path("${BuildArtifactFileBaseName}." + ext);
		return iPath;
	}

}
