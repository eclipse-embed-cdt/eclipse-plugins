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

package ilg.gnuarmeclipse.packs.jobs;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.cmsis.PdscParserFull;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.data.PacksStorage;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.console.MessageConsoleStream;

public class ParsePdscRunnable implements IRunnableWithProgress {

	private static boolean sfRunning = false;

	private MessageConsoleStream sfOut;
	private PackNode sfVersionNode;

	public ParsePdscRunnable(String name, PackNode versionNode) {

		sfOut = ConsoleStream.getConsoleOut();

		sfVersionNode = versionNode;
	}

	@Override
	public void run(IProgressMonitor monitor) {

		IPath folderPath;
		try {
			folderPath = PacksStorage.getFolderPath();
		} catch (IOException e1) {
			return;
		}

		if (sfRunning) {
			return;
		}
		sfRunning = true;
		// m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		sfOut.println();
		sfOut.println(ilg.gnuarmeclipse.packs.core.Utils.getCurrentDateTime());

		String destFolder = sfVersionNode.getProperty(Property.DEST_FOLDER);
		String pdscName = sfVersionNode.getProperty(Property.PDSC_NAME);

		IPath path = folderPath.append(destFolder).append(pdscName);

		sfOut.println("Parsing \"" + path.toString() + "\"...");

		Node outlineNode = null;
		try {
			PdscParserFull pdsc = new PdscParserFull();
			pdsc.parseXml(path);
			outlineNode = pdsc.parsePdscFull();

			// Required to resolve path for actions
			outlineNode.putProperty(Property.DEST_FOLDER, destFolder);

			sfVersionNode.setOutline(outlineNode);
			PackNode packNode = (PackNode) sfVersionNode.getParent();
			if (packNode.getFirstChild().getName()
					.equals(sfVersionNode.getName())) {
				// If most recent child, make the outline available for the
				// package node too.
				packNode.setOutline(outlineNode);
			}

			// Parse examples again, with full outlines
			// (will reuse existing example nodes)
			pdsc.parseExamples(sfVersionNode);

		} catch (FileNotFoundException e) {
			sfOut.println("Failed: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			sfOut.println("Failed: " + e.toString());
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		sfOut.println("Parse completed in " + duration + "ms.");

		sfRunning = false;
		return;
	}

}
