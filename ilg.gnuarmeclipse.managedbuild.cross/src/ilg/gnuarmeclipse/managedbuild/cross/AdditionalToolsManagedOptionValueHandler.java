package ilg.gnuarmeclipse.managedbuild.cross;


import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceConfiguration;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.internal.core.FolderInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ResourceConfiguration;

public class AdditionalToolsManagedOptionValueHandler extends
		ManagedOptionValueHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler#handleValue
	 * (IConfiguration,IToolChain,IOption,String,int)
	 */
	@SuppressWarnings("unused")
	public boolean handleValue(IBuildObject configuration,
			IHoldsOptions holder, IOption option, String extraArgument,
			int event) {
		
		//ManagedOptionValueHandlerDebug.dump(configuration, holder, option, extraArgument, event);
		
		if (event == EVENT_APPLY){
			if (configuration instanceof FolderInfo) {
				FolderInfo oFolderInfo;
				oFolderInfo = (FolderInfo)configuration;
				//oFolderInfo.setDirty(true); //does not update interface :-(
				// TODO: find the proper sequence to update the interface
				System.out.println("should update configuration window!");
				//PlatformUI.getWorkbench().getModalDialogShellProvider().getShell().redraw();
				
				return false; // should we return true?
				
			} else if (configuration instanceof ResourceConfiguration) {
				// TODO: find the proper sequence to update the interface
				System.out.println("should update resource configuration window!");
				
				return false; // should we return true?
				
			} else {
				System.out.println("unexpected instanceof configuration " + configuration.getClass().getCanonicalName());
			}
			
		}
		// The event was not handled, thus return false
		return false;
	}

}
