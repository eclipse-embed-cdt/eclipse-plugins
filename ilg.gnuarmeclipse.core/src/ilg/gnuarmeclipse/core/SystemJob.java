package ilg.gnuarmeclipse.core;

import org.eclipse.core.runtime.jobs.Job;

public abstract class SystemJob extends Job {

	public SystemJob(String string) {
		super(string);
		setSystem(true);
	}

}
