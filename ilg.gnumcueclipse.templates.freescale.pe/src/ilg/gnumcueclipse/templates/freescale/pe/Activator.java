package ilg.gnumcueclipse.templates.freescale.pe;

import org.osgi.framework.BundleContext;

import ilg.gnumcueclipse.core.AbstractUIActivator;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnumcueclipse.templates.freescale.pe"; //$NON-NLS-1$

	@Override
	public String getBundleId() {
		return PLUGIN_ID;
	}

	// ------------------------------------------------------------------------

	// The shared instance
	private static Activator fgInstance;

	public static Activator getInstance() {
		return fgInstance;
	}

	public Activator() {

		super();
		fgInstance = this;
	}

	// ------------------------------------------------------------------------

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	// ------------------------------------------------------------------------
}
