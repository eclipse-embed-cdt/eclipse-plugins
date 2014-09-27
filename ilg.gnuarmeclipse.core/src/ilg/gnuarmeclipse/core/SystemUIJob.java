package ilg.gnuarmeclipse.core;

import org.eclipse.ui.progress.UIJob;

public abstract class SystemUIJob extends UIJob {

	public SystemUIJob(String string) {
		super(string);
		this.setSystem(true);
	}
}
