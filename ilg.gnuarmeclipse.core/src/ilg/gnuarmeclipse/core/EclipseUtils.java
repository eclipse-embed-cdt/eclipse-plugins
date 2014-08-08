package ilg.gnuarmeclipse.core;

import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class EclipseUtils {

	// ------------------------------------------------------------------------

	private static final String PROPERTY_OS_NAME = "os.name"; //$NON-NLS-1$
	public static final String PROPERTY_OS_VALUE_WINDOWS = "windows";//$NON-NLS-1$
	public static final String PROPERTY_OS_VALUE_LINUX = "linux";//$NON-NLS-1$
	public static final String PROPERTY_OS_VALUE_MACOSX = "mac";//$NON-NLS-1$

	static public boolean isWindows() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase()
				.startsWith(PROPERTY_OS_VALUE_WINDOWS);
	}

	static public boolean isLinux() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase()
				.startsWith(PROPERTY_OS_VALUE_LINUX);
	}

	static public boolean isMacOSX() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase()
				.startsWith(PROPERTY_OS_VALUE_MACOSX);
	}

	// ------------------------------------------------------------------------

	public static void openExternalBrowser(URL url) throws PartInitException {

		// System.out.println("Open " + url);
		PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
				.openURL(url);

	}

	public static void openExternalFile(IPath path) throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		// System.out.println("Path " + relativeFile);
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);

		// Open external file. If the file is text, it will be opened
		// in the Eclipse editor, otherwise a system viewer is selected.
		IDE.openEditorOnFileStore(page, fileStore);
	}

	public static void openFileWithInternalEditor(IPath path)
			throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);

		// Open a non-resource regular file with the Eclipse internal text
		// editor
		IDE.openInternalEditorOnFileStore(page, fileStore);
	}

	// ------------------------------------------------------------------------

	public static IProject getProjectByName(String name) {

		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}

	public static IWorkbenchPage getActivePage() {

		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
	}

	// ------------------------------------------------------------------------

}
