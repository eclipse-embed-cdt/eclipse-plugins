package ilg.gnuarmeclipse.packs.xcdl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DocumentParseException;
import ilg.gnuarmeclipse.packs.data.PacksStorage;

import org.w3c.dom.Document;

public class ContentParser extends GenericParser {

	Set<String> fLeafNodes;
	Set<String> fPackNodes;
	String[] fGroupsToIgnore;
	Map<String, String> fReplaceMap;

	public ContentParser(Document document) {

		super(document);

		fPackNodes = new HashSet<String>();
		fPackNodes.add("package");
		fPackNodes.add("version");

		fLeafNodes = new HashSet<String>();
		fLeafNodes.add("devicefamily");
		fLeafNodes.add("board");
		fLeafNodes.add("component");
		fLeafNodes.add("bundle");
		fLeafNodes.add("example");
		fLeafNodes.add("keyword");

		fGroupsToIgnore = new String[] { "packages", "versions" };

		fReplaceMap = new HashMap<String, String>();
		// The explicit name is used in the content file, but
		// internally it is shortened.
		fReplaceMap.put("devicefamily", Type.FAMILY);
	}

	@Override
	public String[] getGroupsToIgnore() {
		return fGroupsToIgnore;
	}

	@Override
	public Leaf addNewChild(Node parent, String type) {

		Leaf node;

		if (fPackNodes.contains(type)) {
			node = PackNode.addNewChild(parent, type);
		} else if (fLeafNodes.contains(type)) {
			node = Leaf.addNewChild(parent, type);
		} else {
			node = Node.addNewChild(parent, type);
		}
		return node;
	}

	@Override
	public Map<String, String> getReplacements() {
		return fReplaceMap;
	}

	@Override
	public void checkSchemaVersion(String schemaVersion)
			throws DocumentParseException {

		if (PacksStorage.CONTENT_XML_VERSION.equals(schemaVersion)) {
			;
		} else {
			throw new DocumentParseException("Unrecognised schema version "
					+ schemaVersion + ", refresh");
		}
	}

}
