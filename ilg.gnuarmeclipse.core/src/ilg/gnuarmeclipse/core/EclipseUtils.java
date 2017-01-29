/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.core;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

/**
 * Various utilities that use Eclipse global classes, grouped together more like
 * a reference.
 * <ul>
 * <li>Platform</li>
 * <li>PlatformUI</li>
 * <li>ManagedBuildManager</li>
 * <li>ImmediateExecutor</li>
 * </ul>
 * 
 * Other interesting places to search for utility functions are:
 * <ul>
 * <li>Plugin</li>
 * </ul>
 *
 * For debugging, use
 * 
 * <pre>
 * private static final boolean DEBUG_TWO =
 *     ExamplesPlugin.getDefault().isDebugging() &&
 *        "true".equalsIgnoreCase(Platform.getDebugOption(
 *        "org.eclipse.faq.examples/debug/option2"));
 *  ...
 *  if (DEBUG_TWO)
 *     System.out.println("Debug statement two.");
 * </pre>
 * 
 * This will test two properties like
 * <ul>
 * <li>org.eclipse.faq.examples/debug=true</li>
 * <li>org.eclipse.faq.examples/debug/option2=true</li>
 * </ul>
 * These properties should be stored in a .option file in the plug-in root, or
 * in a custom file whose name is passed to the Eclipse -debug option.
 * <p>
 * See also the <a href=
 * "https://wiki.eclipse.org/FAQ_How_do_I_use_the_platform_debug_tracing_facility"
 * >Eclipse Wiki</a>.
 * 
 */
@SuppressWarnings("restriction")
public class EclipseUtils {

	// ------------------------------------------------------------------------

	private static final String PROPERTY_OS_NAME = "os.name"; //$NON-NLS-1$

	static public boolean isWindows() {

		// Platform.OS_WIN32 might be not appropriate, use shorter prefix
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().startsWith("win");
	}

	static public boolean isWindowsXP() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().equalsIgnoreCase("Windows XP");
	}

	static public boolean isLinux() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().startsWith(Platform.OS_LINUX);
	}

	static public boolean isMacOSX() {
		// Platform.OS_MACOSX is not appropriate, since the returned value
		// contains spaces "Mac OS X".
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().startsWith("mac");
	}

	/**
	 * Get a short string to identify the OS Family.
	 * 
	 * @return a String, one of "windows", "linux", "osx", "other".
	 */
	static public String getOsFamily() {

		if (isWindows()) {
			return "windows";
		} else if (isLinux()) {
			return "linux";
		} else if (isMacOSX()) {
			return "osx";
		} else {
			return "other";
		}
	}

	/**
	 * Get a platform specific key, where %s is replaced by the os family.
	 * 
	 * @param key
	 * @return
	 */
	static public String getKeyOs(String key) {

		String os = getOsFamily();
		String keyOs = String.format(key, os);
		return keyOs;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the separator used to compose PATHs.
	 * 
	 * @return a string.
	 */
	static public String getPathSeparator() {

		if (isWindows()) {
			return ";";
		} else {
			return ":";
		}
	}

	// ------------------------------------------------------------------------

	public static void openExternalBrowser(URL url) throws PartInitException {

		// System.out.println("Open " + url);
		PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(url);

	}

	public static void openExternalFile(IPath path) throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// System.out.println("Path " + relativeFile);
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);

		// Open external file. If the file is text, it will be opened
		// in the Eclipse editor, otherwise a system viewer is selected.
		IDE.openEditorOnFileStore(page, fileStore);
	}

	public static void openFileWithInternalEditor(IPath path) throws PartInitException {

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

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

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {

			Object selection = window.getSelectionService().getSelection();
			if ((selection != null) && (selection instanceof IStructuredSelection)
					&& (((IStructuredSelection) selection).size() == 1)) {
				Object firstElement = ((IStructuredSelection) selection).getFirstElement();
				if (firstElement instanceof IAdaptable) {
					IProject project = (IProject) ((IAdaptable) firstElement).getAdapter(IProject.class);
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

		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public static IWorkbenchPage getActivePage(IWorkbenchPart part) {
		return part.getSite().getWorkbenchWindow().getActivePage();
	}

	public static Shell getShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	// ------------------------------------------------------------------------

	public static IConfiguration getConfigurationFromDescription(ICConfigurationDescription configDescription) {
		return ManagedBuildManager.getConfigurationForDescription(configDescription);
	}

	// ------------------------------------------------------------------------

	/**
	 * Helper function to open an error dialog.
	 * 
	 * @param title
	 * @param message
	 * @param e
	 */
	static public void openError(final String title, final String message, final Exception e) {
		UIJob uiJob = new SystemUIJob("open error") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// open error for the exception
				String msg = message;
				if (e != null) {
					msg = "\n" + e.getMessage();
				}

				MessageDialog.openError(getShell(), title, msg); // $NON-NLS-1$
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	public static void clearStatusMessage() {

		// Display.getDefault().syncExec(new Runnable() {
		Display.getDefault().syncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					WorkbenchWindow w = (WorkbenchWindow) window;
					w.getStatusLineManager().setErrorMessage("");
				}
			}
		});
	}

	/**
	 * Shows status message in RCP
	 * 
	 * @param message
	 *            message to be displayed
	 * @param isError
	 *            if its an error message or normal message
	 */
	public static void showStatusMessage(final String message) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(message);
		}
		// Display.getDefault().syncExec(new Runnable() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					WorkbenchWindow w = (WorkbenchWindow) window;
					w.getStatusLineManager().setMessage(message);
				}
			}
		});
	}

	public static void showStatusErrorMessage(final String message) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(message);
		}
		Activator.log(message);
		// Display.getDefault().syncExec(new Runnable() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window instanceof WorkbenchWindow) {
					WorkbenchWindow w = (WorkbenchWindow) window;
					w.getStatusLineManager().setErrorMessage("  " + message);
				}
			}
		});
	}

	// ------------------------------------------------------------------------

	/**
	 * Search the given scopes and return the non empty trimmed string or the
	 * default.
	 * 
	 * @param pluginId
	 *            a string with the plugin id.
	 * @param key
	 *            a string with the key to search.
	 * @param defaultValue
	 *            a string with the default, possibly null.
	 * @param contexts
	 *            an array of IScopeContext.
	 * @return a trimmed string or the given default, possibly null.
	 */
	public static String getPreferenceValueForId(String pluginId, String key, String defaultValue,
			IScopeContext[] contexts) {

		String value = null;

		for (int i = 0; i < contexts.length; ++i) {
			value = contexts[i].getNode(pluginId).get(key, null);

			if (value != null) {
				value = value.trim();

				if (!value.isEmpty()) {
					break;
				}
			}
		}

		if (value != null) {
			return value;
		}

		return defaultValue;
	}

	/**
	 * Compute a maximum array of scopes where to search for.
	 * 
	 * @param project
	 *            the IProject reference to the project, possibly null.
	 * @return an array of IScopeContext.
	 */
	public static IScopeContext[] getPreferenceScopeContexts(IProject project) {

		// If the project is known, the contexts searched will include the
		// specific ProjectScope.
		IScopeContext[] contexts;
		if (project != null) {
			contexts = new IScopeContext[] { new ProjectScope(project), InstanceScope.INSTANCE,
					ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };
		} else {
			contexts = new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE,
					DefaultScope.INSTANCE };
		}
		return contexts;
	}

	/**
	 * Search all scopes and return the non empty trimmed string or the default.
	 * 
	 * @param pluginId
	 *            a string with the plugin id.
	 * @param key
	 *            a string with the key to search.
	 * @param defaultValue
	 *            a string with the default, possibly null.
	 * @param project
	 *            the IProject reference to the project, possibly null.
	 * @return a trimmed string or the given default, possibly null.
	 */
	public static String getPreferenceValueForId(String pluginId, String key, String defaultValue, IProject project) {

		IScopeContext[] contexts = getPreferenceScopeContexts(project);
		return getPreferenceValueForId(pluginId, key, defaultValue, contexts);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the variable value. The variables are accesible in the Run/Debug ->
	 * String Substitution preferences page.
	 * 
	 * @param name
	 *            a String with the variable name.
	 * @return a String with the variable value, or null if not found.
	 */
	public static String getVariableValue(String name) {

		IValueVariable variable = VariablesPlugin.getDefault().getStringVariableManager().getValueVariable(name);
		if (variable != null) {
			return variable.getValue();
		} else {
			Activator.log("Variable \"" + name + "\" not found.");
		}

		return null;
	}

	/**
	 * Set a variable value. The variables are accessible in the Run/Debug ->
	 * String Substitution preferences page.
	 * 
	 * @param name
	 *            a String with the variable name.
	 * @param value
	 *            a String with the variable value.
	 */
	public static void setVariableValue(String name, String value) {

		IValueVariable variable = VariablesPlugin.getDefault().getStringVariableManager().getValueVariable(name);
		if (variable != null) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("Variable \"" + name + "\"=\"" + value + "\"");
			}
			variable.setValue(value);
		} else {
			Activator.log("Variable \"" + name + "\" not set.");
		}
	}

	/**
	 * Set a variable value. The variables are accessible in the Run/Debug ->
	 * String Substitution preferences page.
	 * 
	 * If the variable does not exist, it is created. Unfortunately the
	 * 'Contributed By' field is not filled in.
	 * 
	 * @param name
	 *            a String with the variable name.
	 * @param description
	 *            a String with the variable description.
	 * @param value
	 *            a String with the variable value.
	 */
	public static void setVariableValue(String name, String description, String value) {

		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable variable = manager.getValueVariable(name);
		if (variable == null) {
			variable = manager.newValueVariable(name, description);
			try {
				manager.addVariables(new IValueVariable[] { variable });
			} catch (CoreException e) {
				Activator.log(e);
				variable = null;
			}
		}
		if (variable != null) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("Variable \"" + name + "\"=\"" + value + "\"");
			}
			variable.setValue(value);
		} else {
			Activator.log("Variable \"" + name + "\" not set.");
		}
	}

	/**
	 * Get the configuration of a project.
	 * 
	 * @param project
	 * @return array of configurations or null.
	 */
	public static IConfiguration[] getConfigurationsForProject(IProject project) {

		ICProjectDescription cProjectDescription = CoreModel.getDefault().getProjectDescription(project);
		if (cProjectDescription == null) {
			return null;
		}
		ICConfigurationDescription[] cfgs = cProjectDescription.getConfigurations();
		if (cfgs == null) {
			return null;
		}

		List<IConfiguration> list = new LinkedList<IConfiguration>();

		for (int i = 0; i < cfgs.length; ++i) {
			// System.out.println(cfgs[i].getName());
			IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
			if (info == null) {
				continue;
			}
			IConfiguration config = info.getManagedProject().getConfiguration(cfgs[i].getId());
			if (config == null) {
				continue;

			}
			list.add(config);
		}

		if (list.size() == 0) {
			return null;
		}

		return list.toArray(new IConfiguration[list.size()]);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the build configuration associated with the debug launch
	 * configuration, if defined in the first tab.
	 * 
	 * @param config
	 *            a debug launch configuration.
	 * @return the build configuration, or null if not found or not defined.
	 */
	public static ICConfigurationDescription getBuildConfigDescription(ILaunchConfiguration config) {

		ICConfigurationDescription cfg = null;

		// Get the project
		String projectName;
		try {
			projectName = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
			if (projectName == null) {
				return null;
			}
			projectName = projectName.trim();
			if (projectName.isEmpty()) {
				return null;
			}
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project == null || !project.isAccessible()) {
				return null;
			}

			ICProjectDescription projDesc = CoreModel.getDefault().getProjectDescription(project, false);

			// Not a CDT project?
			if (projDesc == null) {
				return null;
			}

			String buildConfigID = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID,
					""); //$NON-NLS-1$
			if (buildConfigID.length() != 0) {
				cfg = projDesc.getConfigurationById(buildConfigID);
			}
			// if configuration is null fall-back to active
			if (cfg == null) {
				cfg = projDesc.getActiveConfiguration();
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return cfg;
	}

	// ------------------------------------------------------------------------
}
