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

package ilg.gnumcueclipse.managedbuild.packs.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.MultiConfiguration;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ilg.gnumcueclipse.core.CProjectPacksStorage;
import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.JsonUtils;
import ilg.gnumcueclipse.core.StringUtils;
import ilg.gnumcueclipse.managedbuild.packs.Activator;
import ilg.gnumcueclipse.packs.core.PackType;
import ilg.gnumcueclipse.packs.core.data.IPacksDataManager;
import ilg.gnumcueclipse.packs.core.data.PacksDataManagerFactoryProxy;
import ilg.gnumcueclipse.packs.core.tree.Leaf;
import ilg.gnumcueclipse.packs.core.tree.Node;
import ilg.gnumcueclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnumcueclipse.packs.core.tree.Property;
import ilg.gnumcueclipse.packs.core.tree.Type;
import ilg.gnumcueclipse.packs.data.DataManager;
import ilg.gnumcueclipse.packs.xcdl.Utils;

/**
 * @noextend This class is not intended to be sub-classed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("restriction")
public class TabDevices extends AbstractCBuildPropertyTab {

	private static final int AUTOEXPAND_LEVEL = 2;

	// ------------------------------------------------------------------------

	class DevicesContentProvider extends NodeViewContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {

			Leaf[] children = (Leaf[]) super.getChildren(parentElement);

			List<Leaf> list = new LinkedList<Leaf>();
			for (Leaf child : children) {
				// Filter out memory and book nodes
				if (!child.isType(Type.MEMORY) && !child.isType(Type.BOOK)) {
					list.add(child);
				}
			}
			if (children.length != list.size()) {
				return list.toArray(new Leaf[list.size()]);
			} else {
				return children;
			}
		}
	}

	class DevicesLabelProvider implements ITableLabelProvider {

		public String getColumnText(Object obj, int columnIndex) {

			Leaf node = (Leaf) obj;
			String name = "";

			switch (columnIndex) {

			case 0:
				// In the first column is displayed the node name
				name = " " + node.getName();
				break;

			case 1:
				// The second column displays the node details
				String description = "";

				if (node.isType(Type.VENDOR)) {
					description = "Vendor";
				} else if (node.isType(Type.FAMILY)) {
					description = computeDescription(node);
				} else if (node.isType(Type.SUBFAMILY)) {
					description = computeDescription(node);
				} else if (node.isType(Type.DEVICE)) {
					description = computeDescription(node);
				} else if (node.isType(Type.VARIANT)) {
					description = computeDescription(node);
				} else if (node.isType(Type.BOARD)) {
					description = computeBoardDescription(node);
				}
				name = " " + description;
				break;
			}
			return name;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		private String computeBoardDescription(Leaf node) {

			String description = "";

			String revision = node.getProperty(Property.BOARD_REVISION);
			if (revision.length() > 0) {
				if (description.length() > 0) {
					description += ", ";
				}
				if (!revision.toLowerCase().startsWith("rev") && !revision.toLowerCase().startsWith("ver")) {
					description += "Rev ";
				}
				description += revision;
			}

			String clock = node.getProperty(Property.CLOCK);
			if (clock.length() > 0) {
				try {
					int clockMHz = Integer.parseInt(clock) / 1000000;
					if (description.length() > 0) {
						description += ", ";
					}
					description += String.valueOf(clockMHz) + " MHz XTAL";

				} catch (NumberFormatException e) {
					// Ignore not number
				}
			}

			String hfosc = node.getProperty(Property.HFXTAL);
			if (hfosc.length() > 0) {
				if (description.length() > 0) {
					description += ", ";
				}
				description += hfosc + " HFXTAL";
			}

			String lfosc = node.getProperty(Property.LFXTAL);
			if (lfosc.length() > 0) {
				if (description.length() > 0) {
					description += ", ";
				}
				description += lfosc + " LFXTAL";
			}

			String prefix = "Board";

			if (description.length() > 0) {
				return prefix + " (" + description + ")";
			} else {
				return prefix;
			}
		}

		// Compose the details string for device nodes
		protected String computeDescription(Leaf node) {

			String prefix = "";
			if (node.isType(Type.FAMILY)) {
				prefix = "Family";
			} else if (node.isType(Type.SUBFAMILY)) {
				prefix = "Subfamily";
			} else if (node.isType(Type.DEVICE)) {
				prefix = "Device";
			} else if (node.isType(Type.VARIANT)) {
				prefix = "Variant";
			}

			String summary = "";

			String packType = node.getProperty(Property.PACK_TYPE);
			if (PackType.XPACK_XCDL.equals(packType)) {

				String arch = node.getProperty(Property.ARCH);
				if (arch.length() > 0) {
					if (summary.length() > 0) {
						summary += ", ";
					}
					summary += arch;
				}

				String hfosc = node.getProperty(Property.HFOSC);
				if (hfosc.length() > 0) {
					if (summary.length() > 0) {
						summary += ", ";
					}
					summary += hfosc + " HFOSC";
				}

				String lfosc = node.getProperty(Property.LFOSC);
				if (lfosc.length() > 0) {
					if (summary.length() > 0) {
						summary += ", ";
					}
					summary += lfosc + " LFOSC";
				}

			} else {
				String core = node.getProperty(Property.CORE);
				if (core.length() > 0) {
					if (summary.length() > 0) {
						summary += ", ";
					}
					summary += core;

					String version = node.getProperty(Property.CORE_VERSION);
					if (version.length() > 0) {
						if (summary.length() > 0) {
							summary += ", ";
						}
						if (!version.toLowerCase().startsWith("rev") && !version.toLowerCase().startsWith("ver")) {
							summary += "Rev ";
						}
						summary += version;
					}
				}

				String fpu = node.getProperty(Property.FPU);
				if (fpu.length() > 0 && "1".equals(fpu)) {
					if (summary.length() > 0) {
						summary += ", ";
					}
					summary += "FPU";
				}

				String mpu = node.getProperty(Property.MPU);
				if (mpu.length() > 0 && "1".equals(mpu)) {
					if (summary.length() > 0) {
						summary += ", ";
					}
					summary += "MPU";
				}

				String clock = node.getProperty(Property.CLOCK);
				if (clock.length() > 0) {
					try {
						int clockMHz = Integer.parseInt(clock) / 1000000;
						if (summary.length() > 0) {
							summary += ", ";
						}
						summary += String.valueOf(clockMHz) + " MHz";
					} catch (NumberFormatException e) {
						// Ignore not number
					}
				}
			}

			int ramKB = 0;
			int romKB = 0;

			// TODO: iterate on parents too
			if (node.hasChildren()) {
				for (Leaf childNode : ((Node) node).getChildren()) {

					if (Type.MEMORY.equals(childNode.getType())) {
						String size = childNode.getProperty(Property.SIZE);
						long sizeKB;
						try {
							sizeKB = StringUtils.convertHexLong(size) / 1024;
						} catch (NumberFormatException e) {
							sizeKB = 0;
						}
						String id = childNode.getName();
						if (id.contains("ROM")) {
							romKB += sizeKB;
						} else if (id.contains("RAM")) {
							ramKB += sizeKB;
						} else {
						}
					}
				}
			}

			if (ramKB > 0) {
				if (summary.length() > 0) {
					summary += ", ";
				}

				summary += String.valueOf(ramKB) + " kB RAM";
			}

			if (romKB > 0) {
				if (summary.length() > 0) {
					summary += ", ";
				}

				summary += String.valueOf(romKB) + " kB ROM";
			}

			if (summary.length() > 0) {
				summary = prefix + " (" + summary + ")";
			} else {
				summary = prefix;
			}

			return summary;
		}

	}

	class DevicesViewerComparator extends ViewerComparator {

		public DevicesViewerComparator() {
			;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {

			Node p1 = (Node) e1;
			Node p2 = (Node) e2;

			int comparison = p1.getName().compareToIgnoreCase(p2.getName());
			return comparison;
		}

	}

	// ------------------------------------------------------------------------

	private TreeViewer fDevicesTree;
	private Label fArchitectureLabel;
	private Label fDeviceLabel;
	private Table fMemoryTable;
	private Button fMemoryEditButton;

	private IPacksDataManager fDataManager;
	private Leaf fSelectedDeviceNode;
	private Leaf fSelectedBoardDeviceNode;

	// ------------------------------------------------------------------------

	private IConfiguration fConfig;

	// private IConfiguration fLastUpdatedConfig = null;

	public TabDevices() {
		fConfig = null;
	}

	@Override
	public void createControls(Composite parent) {

		// m_composite = parent;
		// Disabled, otherwise toolchain changes fail
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.createControls()");
		}

		if (!page.isForProject()) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("Devices.not this project");
			}
			return;
		}
		super.createControls(parent);

		fConfig = getCfg();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.createControls() fConfig=" + fConfig);
		}

		fDataManager = PacksDataManagerFactoryProxy.getInstance().createDataManager();

		// usercomp is defined in parent class

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		// layout.marginHeight = 0;
		// layout.marginWidth = 0;
		usercomp.setLayout(gridLayout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		usercomp.setLayoutData(gridData);

		createDeviceGroup(usercomp);

		createMemoryGroup(usercomp);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.createControls() completed");
		}
	}

	private void createDeviceGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		group.setLayoutData(gridData);
		group.setText(Messages.DevicesTab_DeviceGroup_name);

		Composite treeComposite = new Composite(group, SWT.NONE);
		fDevicesTree = new TreeViewer(treeComposite, SWT.SINGLE | SWT.BORDER);

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.heightHint = 200;
		gridData.horizontalSpan = 2;
		treeComposite.setLayoutData(gridData);

		Tree tree = fDevicesTree.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		TreeColumnLayout layout = new TreeColumnLayout();
		treeComposite.setLayout(layout);

		TreeColumn nameColumn = new TreeColumn(tree, SWT.NONE);
		nameColumn.setText("  Name");
		layout.setColumnData(nameColumn, new ColumnPixelData(200));

		TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
		descriptionColumn.setAlignment(SWT.LEFT);
		descriptionColumn.setText(" Details");
		// descriptionColumn.setWidth(450);
		layout.setColumnData(descriptionColumn, new ColumnPixelData(450));

		DevicesContentProvider contentProvider = new DevicesContentProvider();
		fDevicesTree.setContentProvider(contentProvider);

		fDevicesTree.setLabelProvider(new DevicesLabelProvider());

		fDevicesTree.setComparator(new DevicesViewerComparator());

		fDevicesTree.setAutoExpandLevel(AUTOEXPAND_LEVEL);
		fDevicesTree.setInput(getDevicesTree(fConfig));
		// ---

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.DevicesTab_DeviceGroup_architecture_label);

		fArchitectureLabel = new Label(group, SWT.NONE);
		fArchitectureLabel.setText("-");

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		fArchitectureLabel.setLayoutData(gridData);

		// --------------------------------------------------------------------

		fDevicesTree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				devicesTreeSelectionChanged(event);

			}
		});
	}

	private void createMemoryGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		group.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		group.setLayoutData(gridData);
		group.setText(Messages.DevicesTab_MemoryGroup_name);

		{
			fDeviceLabel = new Label(group, SWT.NONE);
			fDeviceLabel.setText("-");

			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = SWT.FILL;
			fDeviceLabel.setLayoutData(gridData);
		}

		{
			fMemoryTable = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
			fMemoryTable.setHeaderVisible(true);
			fMemoryTable.setLinesVisible(true);

			gridData = new GridData();
			// gridData.verticalAlignment = SWT.FILL;
			// gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.heightHint = 62;
			fMemoryTable.setLayoutData(gridData);

			TableColumn columnSection = new TableColumn(fMemoryTable, SWT.NULL);
			columnSection.setText("Section");
			columnSection.setWidth(80);
			columnSection.setResizable(true);
			columnSection.setToolTipText(Messages.DevicesTab_MemoryGroup_nameColumn_toolTipText);

			TableColumn columnStart = new TableColumn(fMemoryTable, SWT.NULL);
			columnStart.setText("Start");
			columnStart.setWidth(100);
			columnStart.setResizable(true);
			columnStart.setToolTipText(Messages.DevicesTab_MemoryGroup_startColumn_toolTipText);

			TableColumn columnSize = new TableColumn(fMemoryTable, SWT.NULL);
			columnSize.setText("Size");
			columnSize.setWidth(100);
			columnSize.setResizable(true);
			columnSize.setToolTipText(Messages.DevicesTab_MemoryGroup_sizeColumn_toolTipText);

			TableColumn columnStartup = new TableColumn(fMemoryTable, SWT.NULL);
			columnStartup.setText("Startup");
			columnStartup.setWidth(55);
			columnStartup.setResizable(true);
			columnStartup.setToolTipText(Messages.DevicesTab_MemoryGroup_startupColumn_toolTipText);

		}

		{
			fMemoryEditButton = new Button(group, SWT.NONE);
			fMemoryEditButton.setText(Messages.DevicesTab_MemoryGroup_editButton_text);

			// For the moment Edit is not implemented
			fMemoryEditButton.setEnabled(false);
		}
	}

	// -------------------------------------------------------------------------

	// Called for each selection in the devices tree
	private void devicesTreeSelectionChanged(SelectionChangedEvent event) {

		IStructuredSelection selection = (IStructuredSelection) event.getSelection();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.devicesTreeSelectionChanged() " + selection);
		}

		Object element = selection.getFirstElement();
		if (!(element instanceof Node)) {
			return;
		}

		Node node = (Node) element;

		if (!node.isType(Type.DEVICE)) {
			// Do not allow selections for other nodes than devices
			fDevicesTree.setSelection(StructuredSelection.EMPTY);
			return;
		}

		fSelectedDeviceNode = null;
		fSelectedBoardDeviceNode = null;
		if (node.getParent().isType(Type.BOARD)) {

			// Search for true device node
			fSelectedDeviceNode = findBoardDevice(node);
			if (fSelectedDeviceNode == null) {
				// Do not allow selections for undefined board devices
				fDevicesTree.setSelection(StructuredSelection.EMPTY);
				return;
			}
			fSelectedBoardDeviceNode = node;
		} else {
			// System.out.println("Device " + element);
			fSelectedDeviceNode = node;
		}

		Map<String, String[]> map = collectMemoryMap(fSelectedDeviceNode);

		updateMemoryTableContent(map);

		String arch = DataManager.collectProperty(fSelectedDeviceNode, Node.CORE_PROPERTY, Type.DEVICES_SUBTREE);
		fArchitectureLabel.setText(arch);

		if (fSelectedDeviceNode != null) {
			fDeviceLabel.setText(fSelectedDeviceNode.getName());
		}
	}

	private Leaf findBoardDevice(Node boardDevice) {

		String packType = boardDevice.getProperty(Property.PACK_TYPE, PackType.DEFAULT);
		String deviceVendorId = boardDevice.getProperty(Property.VENDOR_ID);
		String deviceName;
		if (PackType.XPACK_XCDL.equals(packType)) {
			deviceName = boardDevice.getProperty(Property.KEY_);
		} else {
			deviceName = boardDevice.getName();
		}

		return fDataManager.findInstalledDevice(packType, deviceVendorId, deviceName, null);
	}

	private Map<String, String[]> collectMemoryMap(Leaf node) {

		// Collect the memory map from node and parents
		Map<String, String[]> map = new TreeMap<String, String[]>();
		if (node != null) {
			do {
				if (node.hasChildren()) {
					for (Leaf child : ((Node) node).getChildren()) {
						if (child.isType(Type.MEMORY)) {
							String key = child.getName();

							String[] line;
							if (map.containsKey(key)) {
								line = map.get(key);
							} else {
								line = new String[] { "", "", "", "", "" };
								line[0] = key;

								map.put(key, line);
							}

							// 'line' is either new or partly filled in
							// The order is
							// section,start,size,startup
							if (line[1].length() == 0) {
								line[1] = child.getProperty(Node.START_PROPERTY);
							}
							if (line[2].length() == 0) {
								line[2] = child.getProperty(Node.SIZE_PROPERTY);
							}
							if (line[3].length() == 0) {
								line[3] = child.getProperty(Node.STARTUP_PROPERTY);
							}

						}
					}
				}
				// Iterate up
				node = node.getParent();
				// Stop when we reached the top node
			} while (node != null);
		}

		for (String[] line : map.values()) {

			// Default [startup] values to 0
			for (int i = 3; i < line.length; ++i) {
				if (line[i].length() == 0) {
					line[i] = "0";
				}
			}
		}
		return map;
	}

	/**
	 * Fill in the memory map table using the values in the map.
	 * 
	 * @param map
	 *            a map of string arrays, indexed by section name
	 */
	private void updateMemoryTableContent(Map<String, String[]> map) {

		fMemoryTable.removeAll();

		if (map != null) {
			TableItem item;
			for (String key : map.keySet()) {
				String[] line = map.get(key);
				item = new TableItem(fMemoryTable, SWT.NULL);
				item.setText(line);
			}
		}
	}

	// -------------------------------------------------------------------------

	// This event comes when the tab is selected after the windows is
	// displayed, to account for content change.
	// It also comes when the configuration is changed in the selection.
	@Override
	public void updateData(ICResourceDescription cfgd) {

		if (cfgd == null)
			return;

		// fConfig = getCfg();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.updateData() " + getCfg().getName());
		}

		IConfiguration config = getCfg(cfgd.getConfiguration());
		if (config instanceof MultiConfiguration) {
			MultiConfiguration multi = (MultiConfiguration) config;

			// Take the first config in the multi-config
			config = (IConfiguration) multi.getItems()[0];
		}

		updateControlsForConfig(config);

		fDevicesTree.setInput(getDevicesTree(config));
	}

	@Override
	protected void performApply(ICResourceDescription src, ICResourceDescription dst) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.performApply() " + src.getName() + " -> " + dst.getName());
		}

		if (page.isForProject()) {
			IConfiguration cfg1 = getCfg(src.getConfiguration());
			IConfiguration cfg2 = getCfg(dst.getConfiguration());

			updateStorage(cfg1);
			updateStorage(cfg2);
		}
	}

	@Override
	protected void performOK() {

		IConfiguration config = getCfg();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.performOK() " + config.getName());
		}

		updateStorage(config);

		// The DocsView is notified of the change via IPropertyChangeListener
	}

	private void updateControlsForConfig(IConfiguration config) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.updateControlsForConfig() " + config.getName());
		}

		fConfig = config;

		boolean mustUpdate = false;

		if (config instanceof Configuration) {

			try {
				CProjectPacksStorage st = new CProjectPacksStorage(config);

				String packType;
				packType = st.getOption(CProjectPacksStorage.PACK_TYPE);

				String deviceId = "";
				String coreName = "";
				String boardId = "";
				String boardSupplierName = "";
				String boardSupplierId = "";
				String deviceSupplierId = "";

				if (packType == null || PackType.CMSIS.equals(packType)) {
					packType = PackType.CMSIS;
					deviceId = st.getOption(CProjectPacksStorage.CMSIS_DEVICE_NAME, "");
					deviceSupplierId = st.getOption(CProjectPacksStorage.CMSIS_DEVICE_VENDOR_ID, "");

					coreName = st.getOption(CProjectPacksStorage.CMSIS_CORE_NAME, "");

					boardId = st.getOption(CProjectPacksStorage.CMSIS_BOARD_NAME, "");
					boardSupplierName = st.getOption(CProjectPacksStorage.CMSIS_BOARD_VENDOR_NAME, "");
				} else if (PackType.XPACK_XCDL.equals(packType)) {
					deviceId = st.getOption(CProjectPacksStorage.DEVICE_KEY, "");
					deviceSupplierId = st.getOption(CProjectPacksStorage.DEVICE_VENDOR_ID, "");

					coreName = st.getOption(CProjectPacksStorage.CORE_ARCH, "");

					boardId = st.getOption(CProjectPacksStorage.BOARD_KEY, "");
					boardSupplierName = st.getOption(CProjectPacksStorage.BOARD_VENDOR_NAME, "");
					boardSupplierId = st.getOption(CProjectPacksStorage.BOARD_VENDOR_ID, "");
				} else {
					System.out.println("Unsupported pack type.");
				}

				IProject project = EclipseUtils.getProjectFromConfiguration(config);
				if (project != null) {
					try {
						JSONObject packageJson = Utils.getPackageJson(project);

						packType = PackType.XPACK_XCDL;
						if (deviceId.isEmpty()) {
							deviceId = (String) JsonUtils.get(packageJson, "config.xcdl.device.id", "");
							mustUpdate = true;
						}
						if (deviceSupplierId.isEmpty()) {
							deviceSupplierId = (String) JsonUtils.get(packageJson, "config.xcdl.device.supplier.id",
									"");
							mustUpdate = true;
						}
						if (boardId.isEmpty()) {
							boardId = (String) JsonUtils.get(packageJson, "config.xcdl.board.id", "");
							mustUpdate = true;
						}
						if (boardSupplierId.isEmpty()) {
							boardSupplierId = (String) JsonUtils.get(packageJson, "config.xcdl.board.supplier.id", "");
							mustUpdate = true;
						}
					} catch (FileNotFoundException e) {
						;
					} catch (IOException e) {
						Activator.log(e);
					} catch (ParseException e) {
						Activator.log(e);
					}
				}

				boolean wasSelected = false;

				if (!boardId.isEmpty() && (!boardSupplierId.isEmpty() || !boardSupplierName.isEmpty())
						&& !deviceId.isEmpty() && !deviceSupplierId.isEmpty()) {

					// First try to select the device below board.
					Leaf board;
					board = fDataManager.findInstalledBoard(packType, boardSupplierId, boardSupplierName, boardId,
							config);
					if (board != null && board.hasChildren()) {

						for (Leaf child : ((Node) board).getChildren()) {

							String name;
							if (PackType.XPACK_XCDL.equals(packType)) {
								name = child.getProperty(Property.KEY_);
							} else {
								name = child.getName();
							}
							if (child.isType(Type.DEVICE) && deviceId.equals(name)
									&& deviceSupplierId.equals(child.getProperty(Property.VENDOR_ID))) {

								fDevicesTree.reveal(child);
								fDevicesTree.setSelection(new StructuredSelection(child), true);
								wasSelected = true;
								break;
							}
						}
					}
				}

				if (!wasSelected) {
					// Was not selected by the board, try device.
					if (!deviceId.isEmpty() && !deviceSupplierId.isEmpty()) {

						Leaf device = fDataManager.findInstalledDevice(packType, deviceSupplierId, deviceId, config);
						if (device != null) {
							fDevicesTree.setSelection(new StructuredSelection(device), true);
							wasSelected = true;
						}
					}
				}

				if (!deviceId.isEmpty()) {
					fDeviceLabel.setText(deviceId);
				}

				if (!coreName.isEmpty()) {
					fArchitectureLabel.setText(coreName);
				}

				if (mustUpdate) {
					updateStorage(config);
				}

				// Clear any possible selection
				fSelectedDeviceNode = null;
				fSelectedBoardDeviceNode = null;

				updateMemoryTableContent(st.getMemoryMap());

			} catch (CoreException e) {
				;
			}

		}
		// fLastUpdatedConfig = config;
	}

	/**
	 * Update the C project storage with the packs related definitions.
	 * <p>
	 * Multiple configurations might be updated at the same time.
	 * 
	 * @param config
	 *            the configuration to update.
	 */
	private void updateStorage(IConfiguration config) {

		// System.out.println("Devices.updateStorage() " + config.getName() +
		// " "
		// + config.getClass());

		if (fSelectedDeviceNode == null) {
			return;
		}

		// If multi config, iterate over individual configs
		if (config instanceof MultiConfiguration) {
			MultiConfiguration multi = (MultiConfiguration) config;
			for (Object obj : multi.getItems()) {
				IConfiguration cfg = (IConfiguration) obj;
				updateStorage(cfg);
			}
			return;
		} else

		if (config instanceof Configuration) {

			try {
				CProjectPacksStorage st = new CProjectPacksStorage(config);
				st.clear();

				String packType = null;
				Leaf node = fSelectedDeviceNode;
				while (node != null) {
					if (node.isType(Type.DEVICE)) {
						packType = node.getProperty(Property.PACK_TYPE, PackType.CMSIS);
						if (PackType.CMSIS.equals(packType)) {
							st.setOption(CProjectPacksStorage.CMSIS_DEVICE_NAME, node.getName());
						} else if (PackType.XPACK_XCDL.equals(packType)) {
							st.setOption(CProjectPacksStorage.PACK_TYPE, packType);
							st.setOption(CProjectPacksStorage.DEVICE_KEY, node.getProperty(Property.KEY_));

							st.setOption(CProjectPacksStorage.DEVICE_COMPILER_DEFINES,
									node.getProperty(Property.COMPILER_DEFINES));
							st.setOption(CProjectPacksStorage.DEVICE_COMPILER_HEADERS,
									node.getProperty(Property.COMPILER_HEADERS));
						}
						if (Activator.getInstance().isDebugging()) {
							System.out.println("Devices.updateStorage() device.name=" + node.getName());
						}
					} else if (node.isType(Type.SUBFAMILY)) {
						if (PackType.CMSIS.equals(packType)) {
							st.setOption(CProjectPacksStorage.CMSIS_SUBFAMILY_NAME, node.getName());
						} else if (PackType.XPACK_XCDL.equals(packType)) {
							st.setOption(CProjectPacksStorage.SUBFAMILY_KEY, node.getProperty(Property.KEY_));
						}
					} else if (node.isType(Type.FAMILY)) {
						if (PackType.CMSIS.equals(packType)) {
							st.setOption(CProjectPacksStorage.CMSIS_FAMILY_NAME, node.getName());

							st.setOption(CProjectPacksStorage.CMSIS_DEVICE_VENDOR_NAME,
									node.getProperty(Property.VENDOR_NAME));
							st.setOption(CProjectPacksStorage.CMSIS_DEVICE_VENDOR_ID,
									node.getProperty(Property.VENDOR_ID));

							// Package details
							st.setOption(CProjectPacksStorage.CMSIS_DEVICE_PACK_VENDOR,
									node.getProperty(Property.PACK_VENDOR));
							st.setOption(CProjectPacksStorage.CMSIS_DEVICE_PACK_NAME,
									node.getProperty(Property.PACK_NAME));
							st.setOption(CProjectPacksStorage.CMSIS_DEVICE_PACK_VERSION,
									node.getProperty(Property.PACK_VERSION));
						} else if (PackType.XPACK_XCDL.equals(packType)) {
							st.setOption(CProjectPacksStorage.FAMILY_KEY, node.getProperty(Property.KEY_));

							st.setOption(CProjectPacksStorage.DEVICE_VENDOR_NAME,
									node.getProperty(Property.VENDOR_NAME));
							st.setOption(CProjectPacksStorage.DEVICE_VENDOR_ID, node.getProperty(Property.VENDOR_ID));

							// Package details
							st.setOption(CProjectPacksStorage.DEVICE_PACK_NAME, node.getProperty(Property.PACK_NAME));
							st.setOption(CProjectPacksStorage.DEVICE_PACK_VERSION,
									node.getProperty(Property.PACK_VERSION));
						}
					}
					node = node.getParent();
				}

				if (fSelectedBoardDeviceNode != null) {
					node = fSelectedBoardDeviceNode.getParent();

					if (node.isType(Type.BOARD)) {
						if (PackType.CMSIS.equals(packType)) {
							st.setOption(CProjectPacksStorage.CMSIS_BOARD_NAME, node.getName());
							st.setOption(CProjectPacksStorage.CMSIS_BOARD_REVISION,
									node.getProperty(Property.BOARD_REVISION));
							st.setOption(CProjectPacksStorage.CMSIS_BOARD_VENDOR_NAME,
									node.getProperty(Property.VENDOR_NAME));
							st.setNonEmptyOption(CProjectPacksStorage.CMSIS_BOARD_CLOCK,
									node.getProperty(Property.CLOCK));

							// Package details
							st.setOption(CProjectPacksStorage.CMSIS_BOARD_PACK_VENDOR,
									node.getProperty(Property.PACK_VENDOR));
							st.setOption(CProjectPacksStorage.CMSIS_BOARD_PACK_NAME,
									node.getProperty(Property.PACK_NAME));
							st.setOption(CProjectPacksStorage.CMSIS_BOARD_PACK_VERSION,
									node.getProperty(Property.PACK_VERSION));
						} else if (PackType.XPACK_XCDL.equals(packType)) {
							st.setOption(CProjectPacksStorage.BOARD_KEY, node.getProperty(Property.KEY_));
							st.setOption(CProjectPacksStorage.BOARD_REVISION,
									node.getProperty(Property.BOARD_REVISION));
							st.setOption(CProjectPacksStorage.BOARD_VENDOR_NAME,
									node.getProperty(Property.VENDOR_NAME));
							st.setOption(CProjectPacksStorage.BOARD_VENDOR_ID, node.getProperty(Property.VENDOR_ID)); // Mandatory

							String hfxtal = node.getProperty(Property.HFXTAL);
							if (!hfxtal.isEmpty()) {
								try {
									BigInteger clock = Utils.convertUnits(hfxtal);
									st.setOption(CProjectPacksStorage.BOARD_CLOCK, clock.toString());
								} catch (NumberFormatException ex) {
									;
								}
							}

							// Package details
							st.setOption(CProjectPacksStorage.BOARD_PACK_NAME, node.getProperty(Property.PACK_NAME));
							st.setOption(CProjectPacksStorage.BOARD_PACK_VERSION,
									node.getProperty(Property.PACK_VERSION));

							st.setOption(CProjectPacksStorage.BOARD_COMPILER_DEFINES,
									node.getProperty(Property.COMPILER_DEFINES));
							st.setOption(CProjectPacksStorage.BOARD_COMPILER_HEADERS,
									node.getProperty(Property.COMPILER_HEADERS));
						}
					}
				}

				if (PackType.CMSIS.equals(packType)) {
					String core = DataManager.collectProperty(fSelectedDeviceNode, Property.CORE, Type.DEVICES_SUBTREE);
					st.setOption(CProjectPacksStorage.CMSIS_CORE_NAME, core);

					String define = DataManager.collectProperty(fSelectedDeviceNode, Property.DEFINE,
							Type.DEVICES_SUBTREE);
					st.setOption(CProjectPacksStorage.CMSIS_COMPILER_DEFINE, define);
				} else if (PackType.XPACK_XCDL.equals(packType)) {
					String arch = DataManager.collectProperty(fSelectedDeviceNode, Property.ARCH, Type.DEVICES_SUBTREE);
					st.setOption(CProjectPacksStorage.CORE_ARCH, arch);
				}

				for (int i = 0; i < fMemoryTable.getItemCount(); ++i) {
					TableItem item = fMemoryTable.getItem(i);
					st.setMemory(item.getText(0), item.getText(1), item.getText(2), item.getText(3));
				}
				st.update();
			} catch (CoreException e) {
				Activator.log(e);
			}
		}

	}

	@Override
	protected void performDefaults() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Devices.performDefaults()");
		}

		// No defaults to apply
	}

	@Override
	public boolean canBeVisible() {

		if (!page.isForProject()) {
			return false;
		}

		// if (!getCfg().getBuilder().isManagedBuildOn()) {
		// return false;
		// }

		// GNU MCU Eclipse toolchains have this hidden option.
		if (getCfg().getToolChain()
				.getOptionBySuperClassId("ilg.gnumcueclipse.managedbuild.cross.option.base.showDevicesTab") == null) {
			return false;
		}

		return true;
	}

	/**
	 * Inform upper class that this page accepts multiple configurations.
	 */
	public boolean canSupportMultiCfg() {
		return true;
	}

	@Override
	protected void updateButtons() {
		// Do nothing. No buttons to update.
	}

	/**
	 * Check if the configuration refers to a GNU MCU Eclipse project by checking
	 * the toolchain prefix.
	 */
	// private boolean isThisPlugin() {
	//
	// fConfig = getCfg();
	// if (Activator.getInstance().isDebugging()) {
	// System.out.println("Devices.isThisPlugin() fConfig=" + fConfig);
	// }
	//
	// IToolChain toolchain = fConfig.getToolChain();
	// String sToolchainId = toolchain.getBaseId();
	// if (sToolchainId.startsWith(IDs.TOOLCHAIN_ID + "."))
	// return true;
	//
	// return false;
	// }

	private Node getDevicesTree(IConfiguration config) {

		// Call the data manager. If the tree is not cached, will busy wait.
		Node devicesRoot = fDataManager.getInstalledObjectsForBuild(config);

		if (devicesRoot == null) {

			devicesRoot = new Node(Type.ROOT);
			Node emptyNode = Node.addNewChild(devicesRoot, Type.NONE);
			emptyNode.setName("No devices available, install xPacks or CMSIS Packs first.");
		}

		return devicesRoot;
	}
}
