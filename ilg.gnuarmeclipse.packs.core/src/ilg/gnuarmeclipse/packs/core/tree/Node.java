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

package ilg.gnuarmeclipse.packs.core.tree;

import java.util.LinkedList;
import java.util.List;

public class Node extends Leaf {

	// Properties (sorted)
	public static final String AP_PROPERTY = "ap";
	public static final String APIVERSION_PROPERTY = "apiversion";
	public static final String ARCHIVE_PROPERTY = "archive";
	public static final String ATTR_PROPERTY = "attr";
	public static final String BUNDLE_PROPERTY = "bundle";
	public static final String CATEGORY_PROPERTY = "category";
	public static final String CLASS_PROPERTY = "class";
	public static final String CLOCK_PROPERTY = "clock";
	public static final String CONDITION_PROPERTY = "condition";
	public static final String CONNECTOR_PROPERTY = "connector";
	public static final String CORE_PROPERTY = "core";
	public static final String DATE_PROPERTY = "date";
	public static final String DEFAULT_PROPERTY = "default";
	public static final String DEFINE_PROPERTY = "define";
	public static final String DEPRECATED_PROPERTY = "deprecated";
	public static final String DEVICEINDEX_PROPERTY = "deviceindex";
	public static final String DOC_PROPERTY = "doc";
	public static final String DP_PROPERTY = "dp";
	public static final String ENDIAN_PROPERTY = "endian";
	public static final String EXCLUSIVE_PROPERTY = "exclusive";
	public static final String FAMILY_PROPERTY = "family";
	public static final String FILE_PROPERTY = "file";
	public static final String FOLDER_PROPERTY = "folder";
	public static final String FPU_PROPERTY = "fpu";
	public static final String GENERATOR_PROPERTY = "generator";
	public static final String GROUP_PROPERTY = "group";
	public static final String ID_PROPERTY = "id";
	public static final String INIT_PROPERTY = "init";
	public static final String LICENSE_PROPERTY = "license";
	public static final String LOAD_PROPERTY = "load";
	public static final String MAXINSTANCES_PROPERTY = "maxinstances";
	public static final String MPU_PROPERTY = "mpu";
	public static final String M_PROPERTY = "m";
	public static final String N_PROPERTY = "n";
	public static final String NAME_PROPERTY = "name";
	public static final String PNAME_PROPERTY = "pname";
	public static final String REVISION_PROPERTY = "revision";
	public static final String RTE_PROPERTY = "rte";
	public static final String SCHEMA_PROPERTY = "schema";
	public static final String SELECT_PROPERTY = "select";
	public static final String SIZE_PROPERTY = "size";
	public static final String SRC_PROPERTY = "src";
	public static final String START_PROPERTY = "start";
	public static final String STARTUP_PROPERTY = "startup";
	public static final String SUBGROUP_PROPERTY = "subgroup";
	public static final String SUBFAMILY_PROPERTY = "subfamily";
	public static final String TITLE_PROPERTY = "title";
	public static final String URL_PROPERTY = "url";
	public static final String VARIANT_PROPERTY = "variant";
	public static final String VENDORID_PROPERTY = "vendorid";
	public static final String VENDOR_PROPERTY = "vendor";
	public static final String VERSION_PROPERTY = "version";

	public static final String CONDITION_ATTRIBUTES[] = { "Dfamily", "DsubFamily", "Dvariant", "Dvendor", "Dname",
			"Dcore", "Dfpu", "Dmpu", "Dendian", "Cvendor", "Cbundle", "Cclass", "Cgroup", "Csub", "Cvariant",
			"Cversion", "Capiversion", "Tcompiler", "condition" };

	protected List<Leaf> fChildren;

	public Node(String type) {

		super(type);

		fChildren = null;
	}

	public Node(Leaf node) {

		super(node);

		fChildren = null;
	}

	@Override
	public boolean hasChildren() {
		return (fChildren != null && !fChildren.isEmpty());
	}

	public List<Leaf> getChildren() {
		return fChildren;
	}

	public void addChild(Leaf node) {

		if (fChildren == null) {
			fChildren = new LinkedList<Leaf>();
		}
		fChildren.add(node);

		// Protect against attempts to link the node to multiple parents
		assert node.fParent == null : "Parent field not null";

		node.fParent = this;
	}

	/**
	 * Iterate over the list of children and return the first child with the
	 * given type.
	 * 
	 * @param type
	 *            a string with the desired node type; if null, return null
	 * @return the first node that matches the requirements, or null if not
	 *         found
	 */
	public Leaf findChild(String type) {
		return findChild(type, null);
	}

	/**
	 * Iterate the list of children, if any, and return the fist child with the
	 * given type and name. If the name is null, return the first child with the
	 * given type.
	 * 
	 * @param type
	 *            a string with the desired node type; if null, return null
	 * @param name
	 *            a string with the desired node name, or null
	 * @return the first node that matches the requirements, or null if not
	 *         found
	 */
	public Leaf findChild(String type, String name) {

		if (type == null) {
			return null;
		}
		if (fChildren == null) {
			return null;
		}

		for (Leaf node : fChildren) {
			if (node.fType.equals(type)) {
				if (name == null) {
					// Node of given type, any name, found
					return node;
				} else {
					if (node.getName().equals(name)) {
						return node;
					}
				}
			}
		}

		return null;
	}

	public Leaf getFirstChild() {

		if (!hasChildren()) {
			return null;
		}
		return getChildren().get(0);
	}

	public void removeChild(Leaf node) {
		if (fChildren != null) {
			fChildren.remove(node);
		}
	}

	public void removeChildren() {
		fChildren = null;
	}

	// ------

	public void copyChildren(Node node) {
		fChildren = node.fChildren;
	}

	// ------------------------------------------------------------------------

	public static Node addUniqueChild(Node parent, String type, String name) {

		assert (parent != null);

		Node node = (Node) parent.findChild(type, name);
		if (node == null) {

			node = new Node(type);
			parent.addChild(node);

			node.setName(name);
		}

		return node;
	}

	public static Node addNewChild(Node parent, String type) {

		assert (parent != null);

		Node node = new Node(type);
		parent.addChild(node);

		return node;
	}

	public static Node addNewChild(Node parent, Leaf from) {

		assert (parent != null);

		Node node = new Node(from);
		parent.addChild(node);
		return node;
	}

	// ------------------------------------------------------------------------

}
