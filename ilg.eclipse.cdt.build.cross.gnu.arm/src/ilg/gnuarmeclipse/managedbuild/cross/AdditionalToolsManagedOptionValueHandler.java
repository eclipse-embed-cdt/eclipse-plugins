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
		
		if (false) {
			// The following is for debug purposes and thus normally commented
			// out
			String configLabel = "config"; //$NON-NLS-1$
			String holderLabel = "holder"; //$NON-NLS-1$
			String eventLabel = "event"; //$NON-NLS-1$

			if (configuration instanceof IConfiguration) {
				configLabel = "IConfiguration"; //$NON-NLS-1$
			} else if (configuration instanceof IResourceConfiguration) {
				configLabel = "IResourceConfiguration"; //$NON-NLS-1$
			}

			if (holder instanceof IToolChain) {
				holderLabel = "IToolChain"; //$NON-NLS-1$
			} else if (holder instanceof ITool) {
				holderLabel = "ITool"; //$NON-NLS-1$
			}

			switch (event) {
			case EVENT_OPEN:
				eventLabel = "EVENT_OPEN";break; //$NON-NLS-1$
			case EVENT_APPLY:
				eventLabel = "EVENT_APPLY";break; //$NON-NLS-1$
			case EVENT_SETDEFAULT:
				eventLabel = "EVENT_SETDEFAULT";break; //$NON-NLS-1$
			case EVENT_CLOSE:
				eventLabel = "EVENT_CLOSE";break; //$NON-NLS-1$
			}

			// Print the event
			System.out.println(eventLabel + "(" + //$NON-NLS-1$
					configLabel + " = " + //$NON-NLS-1$
					configuration.getId() + ", " + //$NON-NLS-1$
					holderLabel + " = " + //$NON-NLS-1$
					holder.getId() + ", " + //$NON-NLS-1$
					"IOption = " + //$NON-NLS-1$
					option.getId() + ", " + //$NON-NLS-1$
					"String = " + //$NON-NLS-1$
					extraArgument + ")"); //$NON-NLS-1$
		} // end of debug code
		
		if (event == EVENT_APPLY){
			if (configuration instanceof FolderInfo) {
				FolderInfo oFolderInfo;
				oFolderInfo = (FolderInfo)configuration;
				//oFolderInfo.setDirty(true); //does not update interface :-(
				// TODO: find the proper sequence to update the interface
				System.out.println("should update configuration window!");
				
				return true; // should we return true?
				
			} else if (configuration instanceof ResourceConfiguration) {
				// TODO: find the proper sequence to update the interface
				System.out.println("should update resource configuration window!");
				
				return true; // should we return true?
				
			} else {
				System.out.println("unexpected instanceof configuration " + configuration.getClass().getCanonicalName());
			}
			
		}
		// The event was not handled, thus return false
		return false;
	}

}
