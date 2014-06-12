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
import ilg.gnuarmeclipse.packs.Repos;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.cmsis.PdscParser;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.PackNode;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.ui.views.OutlineView;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

public class ParsePdscJob extends Job {

	private static boolean m_running = false;

	private MessageConsoleStream m_out;
	private PackNode m_versionNode;
	private PackNode m_selectionNode;
	private TreeViewer m_outlineViewer;

	// private TreeViewer m_packsViewer;

	// private IPath m_folderPath;

	// private IProgressMonitor m_monitor;

	public ParsePdscJob(String name, PackNode versionNode,
			PackNode selectionNode, TreeViewer viewer) {

		super(name);

		m_out = Activator.getConsoleOut();

		m_versionNode = versionNode;
		m_selectionNode = selectionNode;

		m_outlineViewer = viewer;
		// m_packsViewer = Activator.getPacksView().getTreeViewer();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		IPath folderPath;
		try {
			folderPath = Repos.getInstance().getFolderPath();
		} catch (IOException e1) {
			return Status.CANCEL_STATUS;
		}

		if (m_running) {
			return Status.CANCEL_STATUS;
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
			PdscParser pdsc = new PdscParser();
			pdsc.parseXml(path);
			outlineNode = pdsc.parsePdscFull();

			// Required to resolve path for actions
			outlineNode.putProperty(Property.DEST_FOLDER, destFolder);
			m_versionNode.setOutline(outlineNode);

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

		if (outlineNode != null) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					m_outlineViewer
							.setAutoExpandLevel(OutlineView.AUTOEXPAND_LEVEL);
					m_outlineViewer.setInput(m_selectionNode.getOutline());
				}
			});
		}

		m_running = false;
		return Status.OK_STATUS;
	}

}
