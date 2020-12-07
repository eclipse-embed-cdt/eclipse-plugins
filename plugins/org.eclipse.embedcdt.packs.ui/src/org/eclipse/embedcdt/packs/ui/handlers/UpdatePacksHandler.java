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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.ui.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.internal.packs.ui.Activator;
import org.eclipse.embedcdt.packs.core.IConsoleStream;
import org.eclipse.embedcdt.packs.core.data.DataManager;
import org.eclipse.embedcdt.packs.core.data.DataUtils;
import org.eclipse.embedcdt.packs.core.data.PacksStorage;
import org.eclipse.embedcdt.packs.core.data.Repos;
import org.eclipse.embedcdt.packs.core.data.cmsis.Index;
import org.eclipse.embedcdt.packs.core.data.cmsis.PdscParserForContent;
import org.eclipse.embedcdt.packs.core.data.xcdl.ContentSerialiser;
import org.eclipse.embedcdt.packs.core.tree.Node;
import org.eclipse.embedcdt.packs.core.tree.Property;
import org.eclipse.embedcdt.packs.core.tree.Type;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.xml.sax.SAXParseException;

/**
 * Update content.xml files from all repositories. This is the equivalent of
 * discovering new packages.
 * <p>
 * It executes as a separate job, scheduled and executed asynchronously, the
 * caller does not wait for it to complete.
 * <p>
 * On completion, the job notifies the DataManager, to clear caches.
 */
public class UpdatePacksHandler extends AbstractHandler {

	private IConsoleStream fOut;
	private boolean fRunning;

	private Repos fRepos;
	private DataManager fDataManager;

	private IProgressMonitor fMonitor;

	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public UpdatePacksHandler() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("UpdatePacksHandler()");
		}
		fRunning = false;

		fOut = Activator.getInstance().getConsoleOutput();

		fRepos = Repos.getInstance();
		fDataManager = DataManager.getInstance();
	}

	/**
	 * the command has been executed, so extract the needed information from the
	 * application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		window = HandlerUtil.getActiveWorkbenchWindow(event);
		IRunnableContext context = window.getWorkbench().getProgressService();
		try {
			context.run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					myRun(monitor);
				}
			});

		} catch (InvocationTargetException e) {
			Activator.log(e);
		} catch (InterruptedException e) {
			Activator.log(e);
		}
		return null;
	}

	// ------------------------------------------------------------------------

	private IStatus myRun(IProgressMonitor monitor) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("UpdatePacksHandler.myRun()");
		}

		if (fRunning) {
			return Status.CANCEL_STATUS;
		}

		fRunning = true;
		fMonitor = monitor;

		long beginTime = System.currentTimeMillis();

		fOut.println();
		fOut.println(org.eclipse.embedcdt.packs.core.Utils.getCurrentDateTime());
		fOut.println("Update packs job started.");

		int workUnits = 0;

		try {

			// keys: { "type", "url" }
			List<Map<String, Object>> reposList = fRepos.getList();

			for (Map<String, Object> repo : reposList) {

				if (monitor.isCanceled()) {
					break;
				}

				String type = (String) repo.get("type");
				String indexUrl = (String) repo.get("url");
				if (Repos.CMSIS_PACK_TYPE.equals(type)) {

					// String[] { url, name, version }
					List<String[]> list = new LinkedList<String[]>();

					// collect all pdsc references in this site
					readCmsisIndex(indexUrl, list);
					repo.put("list", list);

					// One work unit for each file to process.
					workUnits += list.size();

				} else if (Repos.UNUSED_PACK_TYPE.equals(type)) {
					fOut.println(DataUtils.reportWarning("Repo \"" + indexUrl + "\" ignored."));
				} else {
					fOut.println(DataUtils.reportWarning("Repo type \"" + type + "\" not supported."));
				}
			}

			workUnits += 1; // One more to avoid reaching 100% too early

			// Set total number of work units to the number of pdsc files
			monitor.beginTask("Refresh all packs from all repositories.", workUnits);

			for (Map<String, Object> repo : reposList) {

				if (monitor.isCanceled()) {
					break;
				}

				String type = (String) repo.get("type");
				// String indexUrl = (String) repo.get("url");

				if (Repos.CMSIS_PACK_TYPE.equals(type)) {

					if (repo.containsKey("list")) {

						// Read all .pdsc files and collect summary
						aggregateCmsis(repo);
					}

				}

			}

			monitor.worked(1); // Should reach 100% now

		} catch (Exception e) {
			Activator.log(e);
			fOut.println(DataUtils.reportError(e.toString()));
		}

		IStatus status;
		if (monitor.isCanceled()) {

			fOut.println("Job cancelled.");
			status = Status.CANCEL_STATUS;

		} else {

			fDataManager.notifyNewInput();

			long endTime = System.currentTimeMillis();
			long duration = endTime - beginTime;
			if (duration == 0) {
				duration = 1;
			}

			fOut.println(DataUtils.reportInfo("Update packs completed in " + (duration + 500) / 1000 + "s."));

			status = Status.OK_STATUS;
		}

		fRunning = false;
		return status;
	}

	private void readCmsisIndex(String indexUrl, List<String[]> pdscList) {

		fOut.println("Parsing \"" + indexUrl + "\"...");

		try {

			int count = Index.readIndex(indexUrl, pdscList);

			if (count == 0) {
				fOut.println("Malformed index, no packs contributed.");
			} else {
				fOut.println("Contributed " + count + " pack(s).");
			}

			return;

		} catch (FileNotFoundException e) {
			fOut.println(DataUtils.reportError("File not found: " + e.getMessage()));
		} catch (Exception e) {
			fOut.println(DataUtils.reportError(e.toString()));
		}

		return;
	}

	private void aggregateCmsis(Map<String, Object> repo) {

		// repo keys: { "type", "url", "list" }

		@SuppressWarnings("unchecked")
		List<String[]> list = (List<String[]>) repo.get("list");

		String repoUrl = (String) repo.get("url");
		Node contentRoot = new Node(Type.REPOSITORY);

		String domainName = Repos.getDomaninNameFromUrl(repoUrl);
		domainName = StringUtils.capitalizeFirst(domainName);

		contentRoot.setName(domainName);
		contentRoot.setDescription(domainName + " CMSIS packs repository");

		contentRoot.putProperty(Property.TYPE, "cmsis.repo");
		contentRoot.putProperty(Property.REPO_URL, repoUrl);
		contentRoot.putProperty(Property.GENERATOR, "Eclipse Embedded C/C++");

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		contentRoot.putProperty(Property.DATE, dateFormat.format(cal.getTime()));

		PdscParserForContent parser = new PdscParserForContent();
		// parser.setIsBrief(true);

		boolean ignoreError = false;

		// String[] { url, name, version }
		for (String[] pdsc : list) {

			if (fMonitor.isCanceled()) {
				break;
			}

			// Make url always end in '/'
			String pdscUrl = StringUtils.cosmetiseUrl(pdsc[0]);
			String pdscName = pdsc[1];
			String pdscVersion = pdsc[2];

			fMonitor.subTask(pdscName);

			try {

				URL sourceUrl = new URL(pdscUrl + pdscName);

				String cachedFileName = PacksStorage.makeCachedPdscName(pdscName, pdscVersion);
				File cachedFile = PacksStorage.getCachedFileObject(cachedFileName);
				if (!cachedFile.exists()) {

					// If local file does not exist, create it
					int ret = org.eclipse.embedcdt.packs.ui.Utils.copyFileWithShell(sourceUrl, cachedFile, fOut, null, window.getShell(),
							ignoreError);
					if (ret == 0) {

						DataUtils.reportInfo("File " + pdscName + " version " + pdscVersion + " cached locally.");
					} else {
						fOut.println(DataUtils.reportWarning("Missing \"" + cachedFile + "\", ignored by user request."));
						if (ret == 3) {
							ignoreError = true;
						}
						continue;
					}
				}

				if (cachedFile.exists()) {

					parser.parseXml(cachedFile);
					parser.parse(pdscName, pdscVersion, contentRoot);

				} else {
					fOut.println(DataUtils.reportWarning("Missing \"" + cachedFile + "\", ignored."));
					// return;
				}

			} catch (SAXParseException e) {
				String xmsg = "line=" + e.getLineNumber() + ", column=" + e.getColumnNumber() + ", \"" + e.getMessage()
						+ "\"";
				fOut.println(xmsg + ",  ignored.");
				DataUtils.reportWarning(
						"File " + pdscName + " version " + pdscVersion + " parse error (" + xmsg + "), ignored");
			} catch (Exception e) {
				fOut.println(DataUtils.reportWarning("\"" + e.getMessage() + "\", ignored."));
				DataUtils.reportWarning(
						"File " + pdscName + " version " + pdscVersion + "  error (" + e.getMessage() + "), ignored");
			}

			// One more unit completed
			fMonitor.worked(1);
		}

		if (!fMonitor.isCanceled()) {

			// If all's well, serialise collected content to local cache.
			try {

				String fileName = fRepos.getRepoContentXmlFromUrl(repoUrl);

				ContentSerialiser serialiser = new ContentSerialiser();
				serialiser.serialise(contentRoot, PacksStorage.getFileObject(fileName));

				File file;
				file = PacksStorage.getFileObject(fileName);
				fOut.println("File \"" + file.getCanonicalPath() + "\" written.");
				fOut.println();

			} catch (IOException e) {
				fOut.println(DataUtils.reportError(e.toString()));
			}
		}
	}

}
