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

package ilg.gnuarmeclipse.packs.tree;

public class PackNode extends Node {

	public PackNode(String type) {

		super(type);
	}

	public static PackNode addUniqueChild(Node parent, String type, String name) {

		PackNode packNode = (PackNode) parent.getChild(type, name);
		if (packNode == null) {

			packNode = new PackNode(type);
			parent.addChild(packNode);

			packNode.setName(name);
		}

		return packNode;
	}

	public static PackNode addNewChild(Node parent, String type) {

		PackNode node = new PackNode(type);
		parent.addChild(node);

		return node;
	}

}
