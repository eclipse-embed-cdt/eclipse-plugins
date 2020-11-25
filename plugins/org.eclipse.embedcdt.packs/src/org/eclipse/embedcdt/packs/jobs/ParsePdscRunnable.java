/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *     Alexander Fedorov (ArSysOp) - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.embedcdt.packs.Activator;
import org.eclipse.embedcdt.packs.core.PacksConsoleStream;
import org.eclipse.embedcdt.packs.core.data.PacksStorage;
import org.eclipse.embedcdt.packs.core.tree.Node;
import org.eclipse.embedcdt.packs.core.tree.PackNode;
import org.eclipse.embedcdt.packs.core.tree.Property;
import org.eclipse.embedcdt.packs.data.cmsis.PdscParserFull;
import org.xml.sax.SAXParseException;

public class ParsePdscRunnable implements ICoreRunnable {

	private static boolean fgRunning = false;

	private PacksConsoleStream fgOut;
	private PackNode fgVersionNode;

	public ParsePdscRunnable(String name, PackNode versionNode) {

		fgOut = org.eclipse.embedcdt.packs.core.Activator.getInstance().getConsoleOutput();

		fgVersionNode = versionNode;
	}

	@Override
	public void run(IProgressMonitor monitor) {

		IPath folderPath;
		try {
			folderPath = PacksStorage.getFolderPath();
		} catch (IOException e1) {
			return;
		}

		if (fgRunning) {
			return;
		}
		fgRunning = true;
		// m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		fgOut.println();
		fgOut.println(org.eclipse.embedcdt.packs.core.Utils.getCurrentDateTime());

		String destFolder = fgVersionNode.getProperty(Property.DEST_FOLDER);
		String pdscName = fgVersionNode.getProperty(Property.PDSC_NAME);

		IPath path = folderPath.append(destFolder).append(pdscName);

		fgOut.println("Parsing \"" + path.toOSString() + "\"...");

		Node outlineNode = null;
		try {
			PdscParserFull pdsc = new PdscParserFull();
			pdsc.parseXml(path);
			outlineNode = pdsc.parsePdscFull();

			// Required to resolve path for actions
			outlineNode.putProperty(Property.DEST_FOLDER, destFolder);

			fgVersionNode.setOutline(outlineNode);
			PackNode packNode = (PackNode) fgVersionNode.getParent();
			if (packNode.getFirstChild().getName().equals(fgVersionNode.getName())) {
				// If most recent child, make the outline available for the
				// package node too.
				packNode.setOutline(outlineNode);
			}

			// Parse examples again, with full outlines
			// (will reuse existing example nodes)
			pdsc.parseExamples(fgVersionNode);

		} catch (FileNotFoundException e) {
			fgOut.println("Failed: " + e.toString());
		} catch (SAXParseException e) {
			String msg = e.getMessage() + ", file: " + path.toString() + ", line: " + e.getLineNumber() + ", column: "
					+ e.getColumnNumber();
			Activator.log(e);
			fgOut.println("Failed: " + msg);
		} catch (Exception e) {
			Activator.log(e);
			fgOut.println("Failed: " + e.toString());
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		fgOut.println("Parse completed in " + duration + "ms.");

		fgRunning = false;
		return;
	}

}
