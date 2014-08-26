package ilg.gnuarmeclipse.core;

import java.net.URL;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
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

	/**
	 * Find the project with the given project name.
	 * 
	 * @param name
	 *            a string with the project name
	 * @return the project or null, if not found
	 */
	public static IProject getProjectByName(String name) {

		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}

	/**
	 * Find the project selected in the Project viewer. This works only if the
	 * project is really selected (i.e. the Project Explorer has the focus, and
	 * the project name is coloured in blue); if the focus is lost, the function
	 * returns null.
	 *
	 * @return the project or null, if not found
	 */
	public static IProject getSelectedProject() {

		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window
					.getSelectionService().getSelection();
			if ((selection != null) && (selection.size() == 1)) {
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof IAdaptable) {
					IProject project = (IProject) ((IAdaptable) firstElement)
							.getAdapter(IProject.class);
					return project;
				}
			}
		}

		return null;
	}

	/**
	 * Find the active page. Used, for example, to check if a part (like a view)
	 * is visible (page.ispartVisible(part)).
	 * <p>
	 * Preferably use getSite().getPage().
	 * 
	 * @return the active page.
	 */
	public static IWorkbenchPage getActivePage() {

		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
	}

	public static IWorkbenchPage getActivePage(IWorkbenchPart part) {
		return part.getSite().getWorkbenchWindow().getActivePage();
	}

	// ------------------------------------------------------------------------

	public static IConfiguration getConfigurationFromDescription(
			ICConfigurationDescription configDescription) {
		return ManagedBuildManager
				.getConfigurationForDescription(configDescription);
	}

}
