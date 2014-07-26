package ilg.gnuarmeclipse.packs.xcdl;

import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.PacksStorage;

import java.util.Map;
import java.util.TreeMap;

public class ContentSerialiser extends GenericSerialiser {

	Map<String, ElementOptions> fMap;

	public ContentSerialiser() {

		super();

		fMap = new TreeMap<String, ElementOptions>();
	}

	@Override
	public String getSchemaVersion() {
		return PacksStorage.CONTENT_XML_VERSION;
	}

	@Override
	public ElementOptions getElementOptions(String nodeType) {

		ElementOptions res;
		res = fMap.get(nodeType);
		if (res != null) {
			return res;
		}

		String nodeElementName = "";
		String nodesElementName = "";
		boolean doOutputNodes = true;
		boolean doOutputName = true;
		boolean doOutputProperties = true;
		boolean doIgnoreChildren = false;
		boolean hasNoChildrenElements = false;

		if (Type.REPOSITORY.equals(nodeType)) {
			nodeElementName = "repository";
			nodesElementName = "packages";
		} else if (Type.PACKAGE.equals(nodeType)) {
			nodeElementName = "package";
			nodesElementName = "versions";
		} else if (Type.VERSION.equals(nodeType)) {
			nodeElementName = "version";
			doOutputNodes = false;
		} else if (Type.OUTLINE.equals(nodeType)) {
			nodeElementName = "outline";
			doOutputNodes = false;
			doOutputName = false;
		} else if (Type.EXTERNAL.equals(nodeType)) {
			nodeElementName = "external";
			doOutputNodes = false;
			doOutputName = false;
		} else if (Type.FAMILY.equals(nodeType)) {
			nodeElementName = "devicefamily";
			doOutputNodes = false;
			doOutputProperties = false;
			doIgnoreChildren = true;
		} else if (Type.BOARD.equals(nodeType)) {
			nodeElementName = "board";
			doOutputNodes = false;
			doOutputProperties = false;
			doIgnoreChildren = true;
		} else if (Type.KEYWORD.equals(nodeType)) {
			nodeElementName = "keyword";
			doOutputNodes = false;
			doOutputProperties = false;
			doIgnoreChildren = true;
			hasNoChildrenElements = true;
		} else if (Type.COMPONENT.equals(nodeType)) {
			nodeElementName = "component";
			doOutputNodes = false;
			doOutputProperties = false;
			doIgnoreChildren = true;
		} else if (Type.BUNDLE.equals(nodeType)) {
			nodeElementName = "bundle";
			doOutputNodes = false;
			doOutputProperties = false;
			doIgnoreChildren = true;
		} else if (Type.EXAMPLE.equals(nodeType)) {
			nodeElementName = "example";
			doOutputNodes = false;
			doOutputProperties = false;
			doIgnoreChildren = true;
		} else {
			return null; // use default
		}

		ElementOptions el = new ElementOptions();
		el.fNodeElementName = nodeElementName;
		el.fNodesElementName = nodesElementName;
		el.fDoOutputNodes = doOutputNodes;
		el.fDoOutputName = doOutputName;
		el.fDoOutputProperties = doOutputProperties;
		el.fHasNoChildrenElements = hasNoChildrenElements;
		el.doIgnoreChildren = doIgnoreChildren;

		fMap.put(nodeType, el);

		return el;
	}

}
