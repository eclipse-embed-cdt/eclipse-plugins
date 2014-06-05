package ilg.gnuarmeclipse.packs;

import java.util.EventObject;

public class PacksStorageEvent extends EventObject {

	private static final long serialVersionUID = 7474246541971515868L;

	public PacksStorageEvent(PacksStorage source) {
		super(source);
	}
}
