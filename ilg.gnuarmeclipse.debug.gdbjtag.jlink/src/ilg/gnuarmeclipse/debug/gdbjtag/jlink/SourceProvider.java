package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.services.IServiceLocator;

public class SourceProvider extends AbstractSourceProvider {

	public final static String IS_RESTART_ENABLED = Activator.PLUGIN_ID + ".isRestartEnabled";
	public final static String IS_RESTART_ENABLED_YES = "yes";
	public final static String IS_RESTART_ENABLED_NO = "no";
	private boolean isRestartActive = false;
	
	public static SourceProvider sourceProvider;
	
	public void initialize(final IServiceLocator locator) {
		sourceProvider = this;
	}

	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> getCurrentState() {
		Map<String, String> currentState = new HashMap<String, String>(1);
		String str =  isRestartActive?IS_RESTART_ENABLED_YES:IS_RESTART_ENABLED_NO;
		currentState.put(IS_RESTART_ENABLED, str);
		return currentState;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] {IS_RESTART_ENABLED};
	}
	
	public void setRestartEnabled(boolean flag){
		if (isRestartActive == flag)
			return;
		
		isRestartActive = flag;
		
		fireSourceChanged(ISources.WORKBENCH, IS_RESTART_ENABLED, isRestartActive);
	}

}
