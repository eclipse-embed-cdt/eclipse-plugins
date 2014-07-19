package ilg.gnuarmeclipse.packs.data;

import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.data.IDataManagerFactory;

public class DataManagerFactory implements IDataManagerFactory {

	public DataManagerFactory() {
		;
	}

	@Override
	public IDataManager createDataManager() {

		// Return the static manager object
		return DataManager.getInstance();
	}

}
