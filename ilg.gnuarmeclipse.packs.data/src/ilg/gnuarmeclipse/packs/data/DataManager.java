package ilg.gnuarmeclipse.packs.data;

import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.storage.PacksStorage;

public class DataManager implements IDataManager {

	private static DataManager sfInstance;

	public static synchronized DataManager getInstance() {

		if (sfInstance == null) {
			sfInstance = new DataManager();
		}

		return sfInstance;
	}

	// ------------------------------------------------------------------------

	public DataManager() {
		;
	}

	// Called from TabDevice in managedbuild.cross
	public Node getInstalledDevicesForBuild() {

		// Forward call to initial old packs storage
		return PacksStorage.getInstance().getInstalledDevicesForBuild();
	}
}
