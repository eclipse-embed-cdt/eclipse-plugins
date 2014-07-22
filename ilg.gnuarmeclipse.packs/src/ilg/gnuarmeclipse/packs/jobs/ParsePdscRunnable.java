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
import ilg.gnuarmeclipse.packs.data.Repos;
import ilg.gnuarmeclipse.packs.data.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.console.MessageConsoleStream;

public class ParsePdscRunnable implements IRunnableWithProgress {

	private static boolean m_running = false;

	private MessageConsoleStream m_out;
	private PackNode m_versionNode;

	public ParsePdscRunnable(String name, PackNode versionNode) {

		m_out = ConsoleStream.getConsoleOut();

		m_versionNode = versionNode;
	}

	@Override
	public void run(IProgressMonitor monitor) {

		IPath folderPath;
		try {
			folderPath = Repos.getInstance().getFolderPath();
		} catch (IOException e1) {
			return;
		}

		if (m_running) {
			return;
		}
		m_running = true;
		// m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());

		String destFolder = m_versionNode.getProperty(Property.DEST_FOLDER);
		String pdscName = m_versionNode.getProperty(Property.PDSC_NAME);

		IPath path = folderPath.append(destFolder).append(pdscName);

		m_out.println("Parsing \"" + path.toString() + "\"...");

		Node outlineNode = null;
		try {
			PdscParserFull pdsc = new PdscParserFull();
			pdsc.parseXml(path);
			outlineNode = pdsc.parsePdscFull();

			// Required to resolve path for actions
			outlineNode.putProperty(Property.DEST_FOLDER, destFolder);

			m_versionNode.setOutline(outlineNode);
			PackNode packNode = (PackNode) m_versionNode.getParent();
			if (packNode.getChildren().get(0).getName()
					.equals(m_versionNode.getName())) {
				// If most recent child, make the outline available for the
				// package node too.
				packNode.setOutline(outlineNode);
			}

			// Parse examples again, with full outlines
			// (will reuse existing example nodes)
			pdsc.parseExamples(m_versionNode);

		} catch (FileNotFoundException e) {
			m_out.println("Failed: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		m_out.println("Parse completed in " + duration + "ms.");

		m_running = false;
		return;
	}

}
