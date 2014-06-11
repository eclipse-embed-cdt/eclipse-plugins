package ilg.gnuarmeclipse.packs;

import java.util.EventObject;

public class PacksStorageEvent extends EventObject {

	private static final long serialVersionUID = 7474246541971515868L;

	public class Type {
		public static final String REFRESH = "refresh";
		public static final String UPDATE_PACKS = "update.packs";
		public static final String UPDATE_DEVICES = "update.devices";
		public static final String UPDATE_BOARDS = "update.boards";
		public static final String UPDATE_VERSIONS = "update.versions";
		
	}

	private String m_type;
	private Object m_payload;

	public PacksStorageEvent(PacksStorage source, String type) {

		super(source);

		m_type = type;
		m_payload = null;
	}

	public PacksStorageEvent(PacksStorage source, String type, Object payload) {

		super(source);

		m_type = type;
		m_payload = payload;
	}

	public String getType() {
		return m_type;
	}

	public Object getPayload() {
		return m_payload;
	}
}
