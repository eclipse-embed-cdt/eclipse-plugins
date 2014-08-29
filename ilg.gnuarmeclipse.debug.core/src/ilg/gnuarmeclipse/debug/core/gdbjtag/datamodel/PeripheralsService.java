/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import ilg.gnuarmeclipse.core.CProjectPacksStorage;
import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.debug.core.Activator;
import ilg.gnuarmeclipse.debug.core.DebugUtils;
import ilg.gnuarmeclipse.debug.core.ILaunchConfigurationProvider;
import ilg.gnuarmeclipse.packs.cmsis.SvdGenericParser;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.PacksStorage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControlService;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IMemoryBlockListener;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PeripheralsService extends AbstractDsfService implements
		IPeripheralsService, IMemoryBlockListener {

	private ICommandControlService fCommandControl;
	private PeripheralDMContext[] fPeripheralsDMContexts = null;

	private DataManager fDataManager;
	private MessageConsoleStream fOut;

	// ------------------------------------------------------------------------

	public PeripheralsService(DsfSession session) {
		super(session);

		fOut = ConsoleStream.getConsoleOut();
		fDataManager = DataManager.getInstance();
	}

	@Override
	protected BundleContext getBundleContext() {

		return Activator.getInstance().getBundle().getBundleContext();
	}

	public void initialize(final RequestMonitor rm) {

		System.out.println("PeripheralsService.initialize()");

		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void doInitialize(RequestMonitor rm) {

		System.out.println("PeripheralsService.doInitialize()");

		// Get and remember the command control service
		fCommandControl = ((ICommandControlService) getServicesTracker()
				.getService(ICommandControlService.class));

		// Register this service to OSGi.
		// For completeness, use both the interface and the class name.
		// Used in PeripheralVMNode by interface name.
		register(
				new String[] { IMemoryBlockListener.class.getName(),
						IPeripheralsService.class.getName(),
						PeripheralsService.class.getName() }, new Hashtable());

		System.out.println("PeripheralsService registered " + this);

		rm.done();
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		System.out.println("PeripheralsService.shutdown()");

		// Remove this service from OSGi
		unregister();

		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	@Override
	public void memoryBlocksAdded(IMemoryBlock[] memory) {

		System.out.println("PeripheralsService.memoryBlocksAdded()");
		; // Nothing to do
	}

	@Override
	public void memoryBlocksRemoved(IMemoryBlock[] memory) {

		System.out.println("PeripheralsService.memoryBlocksRemoved()");
		; // Nothing to do
	}

	// ------------------------------------------------------------------------

	@Override
	public void getPeripherals(IContainerDMContext containerDMContext,
			DataRequestMonitor<IPeripheralDMContext[]> dataRequestMonitor) {

		System.out.println("PeripheralsService.getPeripherals()");

		if (fCommandControl instanceof ILaunchConfigurationProvider) {

			// The launch configuration is obtained from the command control,
			// using a dedicated interface.
			ILaunchConfiguration launchConfiguration = ((ILaunchConfigurationProvider) fCommandControl)
					.getLaunchConfiguration();

			// The second step is to get the build configuration description.
			ICConfigurationDescription cConfigDescription = DebugUtils
					.getBuildConfigDescription(launchConfiguration);

			if (cConfigDescription != null) {
				System.out.println(cConfigDescription);

				// The third step is to get the CDT configuration.
				IConfiguration config = EclipseUtils
						.getConfigurationFromDescription(cConfigDescription);
				System.out.println(config);

				try {
					// The custom storage is specific to the CDT configuration.
					CProjectPacksStorage storage = new CProjectPacksStorage(
							config);

					String vendorId = storage
							.getOption(CProjectPacksStorage.DEVICE_VENDOR_ID);
					String vendorName = storage
							.getOption(CProjectPacksStorage.DEVICE_NAME);

					if (vendorId != null && vendorName != null) {

						Node tree = getPeripherals(vendorId, vendorName);

						dataRequestMonitor.setData(makePeripheralsContexts(
								containerDMContext, tree));
						dataRequestMonitor.done();

						return;
					}
				} catch (CoreException e) {
				}
			}
		}

		dataRequestMonitor.setStatus(new Status(IStatus.ERROR,
				Activator.PLUGIN_ID, "No peripherals."));
		dataRequestMonitor.done();
	}

	private IPeripheralDMContext[] makePeripheralsContexts(
			IDMContext parentIDMContext, Node tree) {

		if (tree != null && tree.hasChildren()) {

			fPeripheralsDMContexts = new PeripheralDMContext[tree.getChildren()
					.size()];
			int i = 0;
			for (Leaf child : tree.getChildren()) {
				PeripheralDMNode node = new PeripheralDMNode(child);
				fPeripheralsDMContexts[i] = new PeripheralDMContext(this,
						new IDMContext[] { parentIDMContext }, node);
				++i;
			}
			Arrays.sort(fPeripheralsDMContexts);
		} else {
			fPeripheralsDMContexts = new PeripheralDMContext[0];
		}
		return fPeripheralsDMContexts;
	}

	// ------------------------------------------------------------------------

	protected void removeMemoryBlock(IWorkbenchWindow iWorkbenchWindow,
			PeripheralDMContext peripheralDMContext) {

		System.out.println("removeMemoryBlock()");

		// TODO: add
	}

	protected void addMemoryBlock(IWorkbenchWindow iWorkbenchWindow,
			PeripheralDMContext peripheralDMContext,
			IMemoryBlockRetrieval iMemoryBlockRetrieval) {

		System.out.println("addMemoryBlock()");
	}

	// ------------------------------------------------------------------------

	public void displayPeripheral(final IWorkbenchWindow workbenchWindow,
			final PeripheralDMContext peripheralDMContext,
			final boolean isChecked) {

		System.out.println("displayPeripheral()");

		// TODO: add content
	}

	// ------------------------------------------------------------------------

	private Node getPeripherals(String deviceVendorId, String deviceName) {

		Leaf installedDeviceNode = fDataManager.findInstalledDevice(
				deviceVendorId, deviceName);

		Node root = null;

		if (installedDeviceNode != null) {

			String svdFile = installedDeviceNode.getProperty(Property.SVD_FILE);

			String destFolder = fDataManager
					.getDestinationFolder(installedDeviceNode);
			IPath path = new Path(destFolder).append(svdFile);

			try {

				fOut.println("Parsing SVD file \"" + path.toString() + "\"...");
				File file = PacksStorage.getFileObject(path.toString());
				Document document = Xml.parseFile(file);

				SvdGenericParser parser = new SvdGenericParser();

				Node svdTree = parser.parse(document);

				// GenericSerialiser serialiser = new GenericSerialiser();

				// serialiser.serialise(svdTree, new File("/tmp/svd.xml"));

				root = svdParse(svdTree);

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (root != null) {
				return root;
			}
		}

		root = new Node(Type.ROOT);
		Leaf row1 = Leaf.addNewChild(root, Type.NONE);
		row1.setName("-");
		row1.setDescription("No peripheral descriptions available.");
		row1.putProperty("address", "");

		return root;
	}

	private class SvdPeriphIterator extends AbstractTreePreOrderIterator {

		@Override
		public boolean isIterable(Leaf node) {
			if (node.isType("peripheral")) {
				return true;
			}
			return false;
		}

		@Override
		public boolean isLeaf(Leaf node) {
			if (node.isType("peripheral")) {
				return true;
			}
			return false;
		}
	};

	private class SvdPeriph {

		private Map<String, Leaf> fMap;
		private Leaf fTree;
		private ITreeIterator fPeripheralNodes;

		public SvdPeriph(Leaf tree) {

			fTree = tree;
			fMap = new HashMap<String, Leaf>();

			fPeripheralNodes = new SvdPeriphIterator();
		}

		public Leaf getPeriphNode(String name) {

			Leaf node = fMap.get(name);
			if (node != null) {
				return node;
			}

			fPeripheralNodes.setTreeNode(fTree);
			for (Leaf peripheral : fPeripheralNodes) {

				if (name.equals(peripheral.getProperty("name"))) {
					fMap.put(name, peripheral);
					return peripheral;
				}
			}

			fMap.put(name, node);
			return node;
		}
	}

	private Node svdParse(Node svdTree) {

		Node root = new Node(Type.ROOT);

		ITreeIterator peripheralNodes = new SvdPeriphIterator();

		SvdPeriph svdPeriph = new SvdPeriph(svdTree);

		peripheralNodes.setTreeNode(svdTree);
		for (Leaf peripheral : peripheralNodes) {

			String periphName = peripheral.getProperty("name");
			String periphDescription;
			String periphAddress = peripheral.getProperty("baseAddress");
			String periphGroup;

			String derivedFrom = peripheral.getProperty("derivedFrom");
			if (derivedFrom.length() > 0) {

				Leaf basePeriph = svdPeriph.getPeriphNode(derivedFrom);
				if (basePeriph == null) {
					// System.out.println(peripheral);
					continue;
				} else {
					periphDescription = basePeriph.getProperty("description");
					periphGroup = basePeriph.getProperty("groupName");
				}
			} else {
				periphDescription = peripheral.getProperty("description");
				periphGroup = peripheral.getProperty("groupName");
			}

			long l = StringUtils.convertHexLong(periphAddress);
			String type = "periph";
			long lmax = 0x000000E0000000L;
			if (l >= lmax) {
				type = "arm";
			}

			Leaf p = Leaf.addNewChild(root, type);
			p.setName(periphName);
			p.setDescription(periphDescription);
			p.putProperty("address", periphAddress);
			p.putProperty("groupName", periphGroup);

			// System.out.println(periphName + ", " + periphGroup + ", "
			// + periphAddress + ", " + periphDescription);

		}

		return root;
	}

	// ------------------------------------------------------------------------

}
