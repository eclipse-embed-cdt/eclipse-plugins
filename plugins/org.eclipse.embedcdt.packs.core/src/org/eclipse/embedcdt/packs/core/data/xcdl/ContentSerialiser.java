/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.data.xcdl;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.embedcdt.packs.core.data.PacksStorage;
import org.eclipse.embedcdt.packs.core.tree.Type;
import org.eclipse.embedcdt.packs.core.xml.GenericSerialiser;

public class ContentSerialiser extends GenericSerialiser {

	Map<String, ElementOptions> fMap;

	public ContentSerialiser() {

		super();

		fMap = new TreeMap<>();
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
