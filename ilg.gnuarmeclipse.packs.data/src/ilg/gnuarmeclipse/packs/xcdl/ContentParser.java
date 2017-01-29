/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.xcdl;

import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DocumentParseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContentParser extends GenericParser {

	Set<String> fLeafNodes;
	Set<String> fPackNodes;
	String[] fGroupsToIgnore;
	Map<String, String> fReplaceMap;

	public ContentParser() {

		super();

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
	public void checkSchemaVersion(String schemaVersion) throws DocumentParseException {

		if (PacksStorage.CONTENT_XML_VERSION.equals(schemaVersion)) {
			;
		} else {
			throw new DocumentParseException("Unrecognised schema version " + schemaVersion + ", refresh");
		}
	}

}
