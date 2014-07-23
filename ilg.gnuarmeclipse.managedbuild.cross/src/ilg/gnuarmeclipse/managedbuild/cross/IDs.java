package ilg.gnuarmeclipse.managedbuild.cross;

public class IDs {

	// ------------------------------------------------------------------------

	public static String getIdPrefix() {
		
		// keep it explicitly defined, since it must not be changed, even if the
		// plug-in id is changed
		return "ilg.gnuarmeclipse.managedbuild.cross";
	}

	public static final String TOOLCHAIN_ID = getIdPrefix()
			+ ".toolchain";

	// ------------------------------------------------------------------------

}
