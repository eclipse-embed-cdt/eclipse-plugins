package ilg.gnuarmeclipse.packs.core.data;

import ilg.gnuarmeclipse.packs.core.tree.Node;

public interface IDataManager {

	// Return a tree of devices from the installed packs, to be used in the
	// device selection.
	public Node getInstalledDevicesForBuild();

}
