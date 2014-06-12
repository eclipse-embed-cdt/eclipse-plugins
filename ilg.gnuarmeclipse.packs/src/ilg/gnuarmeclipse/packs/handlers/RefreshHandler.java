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

package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.PacksStorage;
import ilg.gnuarmeclipse.packs.Repos;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.cmsis.Index;
import ilg.gnuarmeclipse.packs.cmsis.PdscParser;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.ContentSerialiser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class RefreshHandler extends AbstractHandler {

	private MessageConsoleStream m_out;
	private boolean m_running;

	private Repos m_repos;
	private PacksStorage m_storage;

	private IProgressMonitor m_monitor;

	/**
	 * The constructor.
	 */
	public RefreshHandler() {
		System.out.println("RefreshHandler()");
		m_running = false;

		m_out = Activator.getConsoleOut();

		m_repos = Repos.getInstance();
		m_storage = PacksStorage.getInstance();
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Job job = new Job("Refresh Packs") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return myRun(monitor);
			}
		};

		job.schedule();
		return null;
	}

	// ------------------------------------------------------------------------

	private IStatus myRun(IProgressMonitor monitor) {

		if (m_running) {
			// return Status.CANCEL_STATUS;
		}

		m_running = true;
		m_monitor = monitor;

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());
		m_out.println("Refresh packs job started.");

		int workUnits = 0;

		try {

			// keys: { "type", "url" }
			List<Map<String, Object>> reposList = m_repos.getList();

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

				} else if (Repos.XCDL_CMSIS_PACK_TYPE.equals(type)) {

					// One work unit, to copy the file locally.
					workUnits++;

				} else {
					m_out.println("Repo type \"" + type + "\" not supported.");
				}
			}

			workUnits += m_storage.computeParseReposWorkUnits();

			// Set total number of work units to the number of pdsc files
			monitor.beginTask("Refresh packs", workUnits);

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

				} else if (Repos.XCDL_CMSIS_PACK_TYPE.equals(type)) {

					cacheXcdlContent(repo);

				}

			}

			if (!monitor.isCanceled()) {

				m_out.println();

				// The content.xml files were just created, parse them
				m_storage.parseRepos(monitor);
			}

			// } catch (FileNotFoundException e) {
			// m_out.println("Error: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		IStatus status;
		if (monitor.isCanceled()) {

			m_out.println("Job cancelled.");
			status = Status.CANCEL_STATUS;

		} else {

			m_storage.notifyRefresh();

			long endTime = System.currentTimeMillis();
			long duration = endTime - beginTime;
			if (duration == 0) {
				duration = 1;
			}

			m_out.println("Refresh packs job completed in " + (duration + 500)
					/ 1000 + "s.");

			status = Status.OK_STATUS;
		}

		m_running = false;
		return status;
	}

	private void readCmsisIndex(String indexUrl, List<String[]> pdscList) {

		m_out.println("Parsing \"" + indexUrl + "\"...");

		try {

			int count = Index.readIndex(indexUrl, pdscList);

			m_out.println("Contributed " + count + " pack(s).");

			return;

		} catch (FileNotFoundException e) {
			m_out.println("Failed: " + e.toString());
		} catch (Exception e) {
			Activator.log(e);
			m_out.println("Failed: " + e.toString());
		}

		return;
	}

	private void aggregateCmsis(Map<String, Object> repo) {

		// repo keys: { "type", "url", "list" }

		@SuppressWarnings("unchecked")
		List<String[]> list = (List<String[]>) repo.get("list");

		String repoUrl = (String) repo.get("url");
		Node contentRoot = new Node(Type.REPOSITORY);

		String domainName = m_repos.getDomaninNameFromUrl(repoUrl);
		domainName = Utils.capitalize(domainName);

		contentRoot.setName(domainName);
		contentRoot.setDescription(domainName + " CMSIS packs repository");

		contentRoot.putProperty(Property.TYPE, "cmsis.repo");
		contentRoot.putProperty(Property.REPO_URL, repoUrl);
		contentRoot.putProperty(Property.GENERATOR, "GNU ARM Eclipse Plug-ins");

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Calendar cal = Calendar.getInstance();
		contentRoot
				.putProperty(Property.DATE, dateFormat.format(cal.getTime()));

		PdscParser parser = new PdscParser();
		parser.setIsBrief(true);

		// String[] { url, name, version }
		for (String[] pdsc : list) {

			if (m_monitor.isCanceled()) {
				break;
			}

			// Make url always end in '/'
			String pdscUrl = Utils.cosmetiseUrl(pdsc[0]);
			String pdscName = pdsc[1];
			String pdscVersion = pdsc[2];

			m_monitor.subTask(pdscName);

			try {

				URL sourceUrl = new URL(pdscUrl + pdscName);

				String cachedFileName = m_storage.makeCachePdscName(pdscName,
						pdscVersion);
				File cachedFile = m_storage.getFile(new Path(
						PacksStorage.CACHE_FOLDER), cachedFileName);
				if (!cachedFile.exists()) {

					// If local file does not exist, create it
					Utils.copyFile(sourceUrl, cachedFile, m_out, null);
				}

				if (cachedFile.exists()) {

					parser.parseXml(cachedFile);
					parser.parsePdscContent(pdscName, pdscVersion, contentRoot);

				} else {
					m_out.println("Missing \"" + cachedFile + "\", ignored");
					return;
				}

			} catch (Exception e) {
				m_out.println("Failed with \"" + e.getMessage() + "\", ignored");
				return;
			}

			// One more unit completed
			m_monitor.worked(1);
		}

		if (!m_monitor.isCanceled()) {

			// If all's well, serialise collected content to local cache.
			try {

				String fileName = m_repos.getRepoContentXmlFromUrl(repoUrl);

				ContentSerialiser serialiser = new ContentSerialiser();
				serialiser.serialiseToXml(contentRoot, fileName);

				File file;
				file = m_repos.getFileObject(fileName);
				m_out.println("File \"" + file.getPath() + "\" written.");

			} catch (IOException e) {
				m_out.println("Failed: " + e.toString());
			}
		}
	}

	private void cacheXcdlContent(Map<String, Object> repo) {

		try {

			String contentUrl = (String) repo.get("url");
			String fileName = m_repos.getRepoContentXmlFromUrl(contentUrl);
			File cachedFile = m_repos.getFileObject(fileName);

			Utils.copyFile(new URL(contentUrl), cachedFile, m_out, null);

			m_monitor.worked(1);

		} catch (MalformedURLException e) {
			m_out.println("Failed: " + e.toString());
		} catch (IOException e) {
			m_out.println("Failed: " + e.toString());
		}
	}
}
