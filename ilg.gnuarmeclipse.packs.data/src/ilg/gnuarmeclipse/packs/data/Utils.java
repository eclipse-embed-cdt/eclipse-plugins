package ilg.gnuarmeclipse.packs.data;

import ilg.gnuarmeclipse.core.StringUtils;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.MessageConsoleStream;

public class Utils {

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
		if (connection instanceof HttpURLConnection) {
			int responseCode = ((HttpURLConnection) connection)
					.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new IOException(
						"Failed to open connection, response code "
								+ responseCode);
			}
		}

		int length = connection.getContentLength();

		if (connection instanceof HttpURLConnection) {
			((HttpURLConnection) connection).disconnect();
		}

		return length;
	}

	public static boolean copyFileWithShell(final URL sourceUrl,
			File destinationFile, MessageConsoleStream out,
			IProgressMonitor monitor, final Shell shell) throws IOException {

		while (true) {
			try {
				Utils.copyFile(sourceUrl, destinationFile, out, monitor);
				return true;
			} catch (final IOException e) {

				class ErrorMessageDialog implements Runnable {

					public int retCode = 0;

					@Override
					public void run() {
						String[] buttons = new String[] { "Retry", "Ignore",
								"Abort" };
						MessageDialog dialog = new MessageDialog(shell,
								"Read error", null, sourceUrl.toString() + "\n"
										+ e.getMessage(), MessageDialog.ERROR,
								buttons, 0);
						retCode = dialog.open();
					}
				}

				ErrorMessageDialog messageDialog = new ErrorMessageDialog();
				Display.getDefault().syncExec(messageDialog);

				if (messageDialog.retCode == 2) {
					throw e; // Abort
				} else if (messageDialog.retCode == 1) {
					return false; // Ignore
				}

				// Else try again
			}
		}

		// HandlerUtil.getActiveShell(event)
	}

	public static void copyFile(URL sourceUrl, File destinationFile,
			MessageConsoleStream out, IProgressMonitor monitor)
			throws IOException {

		URLConnection connection = sourceUrl.openConnection();
		if (connection instanceof HttpURLConnection) {
			int responseCode = ((HttpURLConnection) connection)
					.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new IOException(
						"Failed to open connection, response code "
								+ responseCode);
			}
		}
		int size = connection.getContentLength();
		if (size < 0) {
			throw new IOException("Illegal http file size " + size);
		}
		String sizeString = StringUtils.convertSizeToString(size);
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

		if (connection instanceof HttpURLConnection) {
			((HttpURLConnection) connection).disconnect();
			// System.out.println("Disconnect "+connection);
		}
	}

	public static void copyFile(File sourceFile, File destinationFile,
			MessageConsoleStream out, IProgressMonitor monitor)
			throws IOException {

		if (out != null) {
			int size = (int) sourceFile.length();
			String sizeString = StringUtils.convertSizeToString(size);

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
			if (Activator.getInstance().isDebugging()) {
				System.out.println(message);
			}
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
			if (Activator.getInstance().isDebugging()) {
				System.out.println(message);
			}
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
			if (Activator.getInstance().isDebugging()) {
				System.out.println(message);
			}
		}

		return message;
	}

}
