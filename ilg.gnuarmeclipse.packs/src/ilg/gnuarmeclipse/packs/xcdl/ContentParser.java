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

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.TreeNode;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;

public class ContentParser {

	private MessageConsoleStream m_out;
	private Document m_document;

	public ContentParser(Document document){
	
		m_out = Activator.getConsoleOut();
		m_document = document;
	}
	
	public void parseDocument(TreeNode parent){
		
	}
}
