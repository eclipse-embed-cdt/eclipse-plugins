package ilg.gnuarmeclipse.packs.core.data;

import ilg.gnuarmeclipse.packs.core.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public class DataManagerFactory implements IDataManagerFactory {

	private static final String FACTORY_ELEMENT = "factory";
	private static final String CLASS_ATTRIBUTE = "class";

	// ------------------------------------------------------------------------

	private static DataManagerFactory sfInstance;

	public static DataManagerFactory getInstance() {

		if (sfInstance == null) {
			sfInstance = new DataManagerFactory();
		}
		return sfInstance;
	}

	// ------------------------------------------------------------------------

	private IDataManager fDataManager;

	public DataManagerFactory() {
		fDataManager = null;
	}

	@Override
	public IDataManager createDataManager() {

		if (fDataManager != null) {
			return fDataManager;
		}

		System.out.println("data.DataManagerFactory.createDataManager()");

		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint(Activator.PLUGIN_ID, "data").getExtensions();

		if (extensions.length != 1) {
			System.out.println("no single core.data xp");
			return null;
		}

		IExtension extension = extensions[0];
		IConfigurationElement[] configElements = extension
				.getConfigurationElements();
		IConfigurationElement configElement = configElements[0];

		if (!FACTORY_ELEMENT.equals(configElement.getName())) {
			System.out.println("no <factory> element");
			return null;
		}

		IDataManagerFactory factory;
		try {
			factory = (IDataManagerFactory) configElement
					.createExecutableExtension(CLASS_ATTRIBUTE);
			fDataManager = factory.createDataManager();
			return fDataManager;
		} catch (CoreException e) {
			System.out.println("cannot get factory");
			return null;
		}
	}
}
