package ilg.gnuarmeclipse.core;

import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class Openers {

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

		// Open external file in Eclipse editor (as read only, since the
		// packages were marked as read only
		IDE.openEditorOnFileStore(page, fileStore);

	}
}
