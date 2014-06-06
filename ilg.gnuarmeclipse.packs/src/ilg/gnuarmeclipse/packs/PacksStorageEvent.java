package ilg.gnuarmeclipse.packs;

import java.util.EventObject;

public class PacksStorageEvent extends EventObject {

	private static final long serialVersionUID = 7474246541971515868L;

	public class Type {
		public static final String REFRESH = "refresh";
	}

	private String m_type;

	public PacksStorageEvent(PacksStorage source, String type) {
		super(source);

		m_type = type;
	}

	public String getType() {
		return m_type;
	}
}
