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

package ilg.gnuarmeclipse.packs.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.console.MessageConsoleStream;

public class Utils {

	public static String xmlEscape(String value) {
		value = value.replaceAll("\\&", "&amp;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\\"", "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\\'", "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\<", "&lt;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		value = value.replaceAll("\\>", "&gt;"); //$NON-NLS-1$ //$NON-NLS-2$ 
		return value;
	}

	public static String getCurrentDateTime() {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return dateFormat.format(cal.getTime());
	}

	public static InputStream checkForUtf8BOM(InputStream inputStream)
			throws IOException {

		PushbackInputStream pushbackInputStream = new PushbackInputStream(
				new BufferedInputStream(inputStream), 3);
		byte[] bom = new byte[3];
		if (pushbackInputStream.read(bom) != -1) {
			if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
				pushbackInputStream.unread(bom);
			}
		}
		return pushbackInputStream;
	}

	public static int getRemoteFileSize(URL url) throws IOException {

		URLConnection connection = url.openConnection();
		connection.getInputStream();

		return connection.getContentLength();
	}

	public static long convertHexLong(String hex) {

		boolean isNegative = false;
		if (hex.startsWith("+")) {
			hex = hex.substring(1);
		} else if (hex.startsWith("-")) {
			hex = hex.substring(1);
			isNegative = true;
		}

		if (hex.startsWith("0x") || hex.startsWith("0X")) {
			hex = hex.substring(2);
		}

		if (hex.startsWith("A") || hex.startsWith("E")) {
			System.out.println();
		}

		long value = Long.valueOf("0" + hex, 16);
		if (isNegative)
			value = -value;

		return value;
	}

	public static String cosmetiseUrl(String url) {
		if (url.endsWith("/")) {
			return url;
		} else {
			return url + "/";
		}
	}

	public static String convertSizeToString(int size) {

		String sizeString;
		if (size < 1024) {
			sizeString = String.valueOf(size) + "B";
		} else if (size < 1024 * 1024) {
			sizeString = String.valueOf((size + (1024 / 2)) / 1024) + "kB";
		} else {
			sizeString = String.valueOf((size + ((1024 * 1024) / 2))
					/ (1024 * 1024))
					+ "MB";
		}
		return sizeString;
	}

	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static void copyFile(URL sourceUrl, File destinationFile,
			MessageConsoleStream out, IProgressMonitor monitor)
			throws IOException {

		URLConnection connection = sourceUrl.openConnection();
		int size = connection.getContentLength();
		String sizeString = Utils.convertSizeToString(size);
		if (out != null) {
			String s = destinationFile.getPath();
			if (s.endsWith(".download")) {
				s = s.substring(0, s.length() - ".download".length());
			}
			out.println("Copy " + sizeString);
			out.println(" from \"" + sourceUrl + "\"");
			out.println(" to   \"" + s + "\"");
		}

		destinationFile.getParentFile().mkdirs();

		InputStream input = connection.getInputStream();
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

	public static void copyFile(File sourceFile, File destinationFile,
			MessageConsoleStream out, IProgressMonitor monitor)
			throws IOException {

		if (out != null) {
			int size = (int) sourceFile.length();
			String sizeString = Utils.convertSizeToString(size);

			out.println("Copy " + sizeString);
			out.println(" from \"" + sourceFile + "\"");
			out.println(" to   \"" + destinationFile + "\"");
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

	static final private String MARKER_ID = "ilg.gnuarmeclipse.packs.marker";

	// Sample:
	//
	// try {
	//
	// } catch (Exception e) {
	// String msg = e.getMessage() + ", file: "+ file.getName();
	// fOut.println("Error: " + msg);
	// Utils.reportError(msg);
	// Activator.log(e);
	// }

	public static String reportError(String message) {

		try {
			IMarker marker = ResourcesPlugin.getWorkspace().getRoot()
					.createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			marker.setAttribute(IMarker.LOCATION, "-");
		} catch (CoreException e) {
			System.out.println(message);
		}

		return message;
	}

	public static String reportWarning(String message) {

		try {
			IMarker marker = ResourcesPlugin.getWorkspace().getRoot()
					.createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.LOCATION, "-");
		} catch (CoreException e) {
			System.out.println(message);
		}

		return message;
	}

	public static String reportInfo(String message) {

		try {
			IMarker marker = ResourcesPlugin.getWorkspace().getRoot()
					.createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			marker.setAttribute(IMarker.LOCATION, "-");
		} catch (CoreException e) {
			System.out.println(message);
		}

		return message;
	}

}
