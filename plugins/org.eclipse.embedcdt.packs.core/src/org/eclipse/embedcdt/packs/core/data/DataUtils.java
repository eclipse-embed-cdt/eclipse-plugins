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

package org.eclipse.embedcdt.packs.core.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.internal.packs.core.Activator;
import org.eclipse.embedcdt.packs.core.IConsoleStream;

public class DataUtils {

	private final static int TIME_OUT = 60 * 1000;

	public static InputStream checkForUtf8BOM(InputStream inputStream) throws IOException {

		PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
		byte[] bom = new byte[3];
		if (pushbackInputStream.read(bom) != -1) {
			if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
				pushbackInputStream.unread(bom);
			}
		}
		return pushbackInputStream;
	}

	public static void copyFile(URL sourceUrl, File destinationFile, IConsoleStream out, IProgressMonitor monitor)
			throws IOException {

		URL url = sourceUrl;
		URLConnection connection;
		while (true) {
			connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				connection.setConnectTimeout(TIME_OUT);
				connection.setReadTimeout(TIME_OUT);
				HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

				int responseCode = httpURLConnection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					break;
				} else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
						|| responseCode == HttpURLConnection.HTTP_MOVED_PERM
						|| responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
					String newUrl = connection.getHeaderField("Location");
					url = new URL(newUrl);
					continue;
					// System.out.println("Redirect to URL : " + newUrl);
				} else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					httpURLConnection.disconnect();
					throw new FileNotFoundException(
							"File \"" + url + "\" not found (" + responseCode + "), pack not installed.");
				} else {
					httpURLConnection.disconnect();
					throw new IOException("Failed to open connection, response code " + responseCode);
				}

			}
			break; // When non http protocol, for example.
		}

		destinationFile.getParentFile().mkdirs();

		InputStream input = connection.getInputStream();
		OutputStream output = new FileOutputStream(destinationFile);

		// conn.getContentLength() returns -1 when the file is sent in
		// chunks, so it cannot be used; instead it is computed.
		int totalBytes = 0;

		byte[] buf = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buf)) > 0) {
			output.write(buf, 0, bytesRead);
			if (monitor != null) {
				monitor.worked(bytesRead);
			}
			totalBytes += bytesRead;
		}
		output.close();
		input.close();

		if (out != null) {
			String s = destinationFile.getCanonicalPath();
			if (s.endsWith(".download")) {
				s = s.substring(0, s.length() - ".download".length());
			}
			out.println("Copied " + totalBytes + " bytes");
			out.println(" from \"" + url + "\"");
			if (!url.equals(sourceUrl)) {
				out.println(" redirected from \"" + sourceUrl + "\"");
			}
			out.println(" to   \"" + s + "\"");
		}

		if (connection instanceof HttpURLConnection) {
			((HttpURLConnection) connection).disconnect();
			// System.out.println("Disconnect "+connection);
		}
	}

	public static void copyFile(File sourceFile, File destinationFile, IConsoleStream out, IProgressMonitor monitor)
			throws IOException {

		if (out != null) {
			int size = (int) sourceFile.length();
			String sizeString = StringUtils.convertSizeToString(size);

			out.println("Copy " + sizeString);
			out.println(" from \"" + sourceFile.getCanonicalPath() + "\"");
			out.println(" to   \"" + destinationFile.getCanonicalPath() + "\"");
		}

		destinationFile.getParentFile().mkdirs();

		InputStream input = new FileInputStream(sourceFile);
		OutputStream output = new FileOutputStream(destinationFile);

		byte[] buf = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buf)) > 0) {
			output.write(buf, 0, bytesRead);
			if (monitor != null) {
				monitor.worked(bytesRead);
			}
		}
		output.close();
		input.close();
	}

	public static int deleteFolderRecursive(File folder) {

		int count = 0;

		if (folder == null)
			return count;

		if (folder.exists()) {
			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {
					count += deleteFolderRecursive(f);
					f.setWritable(true, false);
					f.delete();
				} else {
					f.setWritable(true, false);
					f.delete();
					count++;
				}
			}
			folder.setWritable(true, false);
			folder.delete();
		}

		return count;
	}

	public static void makeFolderReadOnlyRecursive(File folder) {

		if (folder == null)
			return;

		if (folder.exists()) {
			for (File f : folder.listFiles()) {
				if (f.isDirectory()) {
					makeFolderReadOnlyRecursive(f);
					f.setWritable(false, false);
				} else {
					f.setWritable(false, false);
				}
			}
			folder.setWritable(false, false);
		}
	}

	static final private String MARKER_ID = "org.eclipse.embedcdt.packs.core.marker";

	// Sample:
	//
	// try {
	//
	// } catch (Exception e) {
	// String msg = e.getMessage() + ", file: "+ file.getName();
	// fOut.println("Error: " + msg);
	// DataUtils.reportError(msg);
	// Activator.log(e);
	// }

	public static String reportError(String message) {

		try {
			IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.LOCATION, "-");
		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		return message;
	}

	public static String reportWarning(String message) {

		try {
			IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.LOCATION, "-");
		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		return message;
	}

	public static String reportInfo(String message) {

		try {
			IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			marker.setAttribute(IMarker.LOCATION, "-");
		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		return message;
	}
}
