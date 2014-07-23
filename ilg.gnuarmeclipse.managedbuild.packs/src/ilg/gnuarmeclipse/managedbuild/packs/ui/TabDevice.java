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

package ilg.gnuarmeclipse.managedbuild.packs.ui;

import ilg.gnuarmeclipse.managedbuild.cross.IDs;
import ilg.gnuarmeclipse.packs.core.data.DataManagerFactoryProxy;
import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.core.tree.Type;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.MultiConfiguration;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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

/**
 * @noextend This class is not intended to be sub-classed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("restriction")
public class TabDevice extends AbstractCBuildPropertyTab {

	private static final int AUTOEXPAND_LEVEL = 2;

	// ------------------------------------------------------------------------

	class DevicesContentProvider extends NodeViewContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {

			Leaf[] children = (Leaf[]) super.getChildren(parentElement);

			List<Leaf> list = new LinkedList<Leaf>();
			for (Leaf child : children) {
				// Filter out memory nodes
				if (!child.isType(Type.MEMORY)) {
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
				name = " " + node.getName();
				break;
			case 1:
				String description = node.getDescription();
				if (description.length() == 0) {
					if (node.isType(Type.VENDOR)) {
						description = "Vendor";
					} else if (node.isType(Type.FAMILY)) {
						description = "Family";
					} else if (node.isType(Type.SUBFAMILY)) {
						description = "Subfamily";
					} else if (node.isType(Type.DEVICE)) {
						description = "Device";
					} else if (node.isType(Type.VARIANT)) {
						description = "Variant";
					}
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
	}

	// ------------------------------------------------------------------------

	private TreeViewer fDevicesTree;
	private Label fArchitectureLabel;
	private Label fDeviceLabel;
	private Table fMemoryTable;
	private Button fMemoryEditButton;

	private IDataManager fDataManager;

	// ------------------------------------------------------------------------

	// private Composite m_composite;

	private IConfiguration m_config;
	private IConfiguration m_lastUpdatedConfig = null;

	// public TabDevice() {
	//
	// }

	// ---

	@Override
	public void createControls(Composite parent) {

		// m_composite = parent;
		// Disabled, otherwise toolchain changes fail
		System.out.println("Device.createControls()");
		if (!isThisPlugin()) {
			System.out.println("Device.not this plugin");
			return;
		}
		//
		if (!page.isForProject()) {
			System.out.println("Device.not this project");
			return;
		}
		//
		super.createControls(parent);

		m_config = getCfg();
		System.out.println("Device.createControls() m_config=" + m_config);

		fDataManager = DataManagerFactoryProxy.getInstance()
				.createDataManager();

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

		System.out.println("Device.createControls() completed");
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
		group.setText(Messages.getString("DeviceTab_DeviceGroup_name"));

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

		fDevicesTree.setAutoExpandLevel(AUTOEXPAND_LEVEL);

		// ---

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages
				.getString("DeviceTab_DeviceGroup_architecture_label"));

		fArchitectureLabel = new Label(group, SWT.NONE);
		fArchitectureLabel.setText("-");

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		fArchitectureLabel.setLayoutData(gridData);

		// --------------------------------------------------------------------

		fDevicesTree
				.addSelectionChangedListener(new ISelectionChangedListener() {

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
		group.setText(Messages.getString("DeviceTab_MemoryGroup_name"));

		{
			fDeviceLabel = new Label(group, SWT.NONE);
			fDeviceLabel.setText("-");

			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = SWT.FILL;
			fDeviceLabel.setLayoutData(gridData);
		}

		{
			fMemoryTable = new Table(group, SWT.SINGLE | SWT.BORDER
					| SWT.FULL_SELECTION);
			fMemoryTable.setHeaderVisible(true);
			fMemoryTable.setLinesVisible(true);

			gridData = new GridData();
			// gridData.verticalAlignment = SWT.FILL;
			// gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.heightHint = 62;
			fMemoryTable.setLayoutData(gridData);

			TableColumn m_columnSection = new TableColumn(fMemoryTable,
					SWT.NULL);
			m_columnSection.setText("Section");
			m_columnSection.setWidth(80);
			m_columnSection.setResizable(true);

			TableColumn m_columnStart = new TableColumn(fMemoryTable, SWT.NULL);
			m_columnStart.setText("Start");
			m_columnStart.setWidth(100);
			m_columnStart.setResizable(true);

			TableColumn m_columnSize = new TableColumn(fMemoryTable, SWT.NULL);
			m_columnSize.setText("Size");
			m_columnSize.setWidth(100);
			m_columnSize.setResizable(true);

			TableColumn m_columnInit = new TableColumn(fMemoryTable, SWT.NULL);
			m_columnInit.setText("Init");
			m_columnInit.setWidth(55);
			m_columnInit.setResizable(true);

			TableColumn m_columnStartup = new TableColumn(fMemoryTable,
					SWT.NULL);
			m_columnStartup.setText("Startup");
			m_columnStartup.setWidth(55);
			m_columnStartup.setResizable(true);

			TableColumn m_columnDefault = new TableColumn(fMemoryTable,
					SWT.NULL);
			m_columnDefault.setText("Default");
			m_columnDefault.setWidth(55);
			m_columnDefault.setResizable(true);
		}

		{
			fMemoryEditButton = new Button(group, SWT.NONE);
			fMemoryEditButton.setText(Messages
					.getString("DeviceTab_MemoryGroup_edit_button"));
		}
	}

	// -------------------------------------------------------------------------

	// Called for each selection in the devices tree
	private void devicesTreeSelectionChanged(SelectionChangedEvent event) {

		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();

		// System.out.println(selection);

		Object element = selection.getFirstElement();
		if (!(element instanceof Node)) {
			return;
		}

		Node node = (Node) element;
		if (node.isType(Type.DEVICE) && node.hasChildren()) {
			for (Leaf child : node.getChildren()) {
				if (child.isType(Type.VARIANT)) {
					// Do not allow selections for devices that have variants
					fDevicesTree.setSelection(StructuredSelection.EMPTY);
					return;
				}
			}
		}

		if (!(node.isType(Type.DEVICE) || node.isType(Type.VARIANT))) {
			// Do not allow selections for other than devices or variants
			fDevicesTree.setSelection(StructuredSelection.EMPTY);
			return;
		}

		Node memNode = null;
		if (node.getParent().isType(Type.BOARD)) {
			System.out
					.println("Board device " + element + " not yet processed");
			// TODO: search for true device node
		} else {
			// System.out.println("Device " + element);
			memNode = node;
		}

		Map<String, String[]> map = collectMemoryMap(memNode);

		updateMemoryTableContent(map);

		String arch = collectNodePropery(memNode, Node.CORE_PROPERTY, "");
		fArchitectureLabel.setText(arch);

		if (memNode != null) {
			fDeviceLabel.setText(memNode.getName());
		}
	}

	private Map<String, String[]> collectMemoryMap(Node node) {

		// Collect the memory map from node and parents
		Map<String, String[]> map = new TreeMap<String, String[]>();
		if (node != null) {
			do {
				if (node.hasChildren()) {
					for (Leaf child : node.getChildren()) {
						if (child.isType(Type.MEMORY)) {
							String key = child.getName();

							String[] line;
							if (map.containsKey(key)) {
								line = map.get(key);
							} else {
								line = new String[] { "", "", "", "", "", "" };
								line[0] = key;

								map.put(key, line);
							}

							// 'line' is either new or partly filled in
							// The order is
							// section,start,size,init,startup,default
							if (line[1].length() == 0) {
								line[1] = child.getProperty(
										Node.START_PROPERTY, "").trim();
							}
							if (line[2].length() == 0) {
								line[2] = child.getProperty(Node.SIZE_PROPERTY,
										"").trim();
							}
							if (line[3].length() == 0) {
								line[3] = child.getProperty(Node.INIT_PROPERTY,
										"").trim();
							}
							if (line[4].length() == 0) {
								line[4] = child.getProperty(
										Node.STARTUP_PROPERTY, "").trim();
							}
							if (line[5].length() == 0) {
								line[5] = child.getProperty(
										Node.DEFAULT_PROPERTY, "").trim();
							}

						}
					}
				}
				// Iterate up
				node = node.getParent();
				// Stop when we reached the node above family node
			} while (!node.isType(Type.NONE));
		}

		for (String[] line : map.values()) {

			// Default [init,startup,default] values to 0
			for (int i = 3; i <= 5; ++i) {
				if (line[i].length() == 0) {
					line[i] = "0";
				}
			}
		}
		return map;
	}

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

	private String collectNodePropery(Node node, String propertyName,
			String defaultValue) {

		String value = "";
		if (node != null) {
			do {

				value = node.getProperty(propertyName, "");

				if (value.length() > 0)
					break;

				// Iterate up
				node = node.getParent();
				// Stop when we reached the node above family node
			} while (!node.isType(Type.NONE));
		}

		if (value.length() == 0) {
			return defaultValue;
		}
		return value;
	}

	// -------------------------------------------------------------------------

	// This event comes when the tab is selected after the windows is
	// displayed, to account for content change
	// It also comes when the configuration is changed in the selection.
	@Override
	public void updateData(ICResourceDescription cfgd) {
		if (cfgd == null)
			return;

		// m_config = getCfg();
		System.out.println("Device.updateData() " + getCfg().getName());

		IConfiguration config = getCfg(cfgd.getConfiguration());
		if (config instanceof MultiConfiguration) {
			MultiConfiguration multi = (MultiConfiguration) config;

			// Take the first config in the multi-config
			config = (IConfiguration) multi.getItems()[0];
		}

		updateControlsForConfig(config);

		fDevicesTree.setInput(getDevicesTree());
	}

	@Override
	protected void performApply(ICResourceDescription src,
			ICResourceDescription dst) {

		System.out.println("Device.performApply() " + src.getName());
		IConfiguration config = getCfg(src.getConfiguration());

		updateOptions(config);
		// does not work like this
		// SpecsProvider.clear();

		// System.out.println("performApply()");
	}

	@Override
	protected void performOK() {

		IConfiguration config = getCfg();
		System.out.println("Device.performOK() " + config);

		if (m_lastUpdatedConfig.equals(config)) {
			updateOptions(config);
		} else {
			System.out.println("skipped " + m_config);
		}
	}

	private void updateControlsForConfig(IConfiguration config) {

		System.out.println("Device.updateControlsForConfig() "
				+ config.getName());

		m_config = config;
		System.out.println("Device.updateControlsForConfig() m_config="
				+ m_config);

		m_lastUpdatedConfig = config;
	}

	private void updateOptions(IConfiguration config) {

		System.out.println("Device.updateOptions() " + config.getName());

		if (config instanceof MultiConfiguration) {
			MultiConfiguration multi = (MultiConfiguration) config;
			for (Object obj : multi.getItems()) {
				IConfiguration cfg = (IConfiguration) obj;
				updateOptions(cfg);
			}
			return;
		}
	}

	@Override
	protected void performDefaults() {

		System.out.println("Device.performDefaults()");
	}

	@Override
	public boolean canBeVisible() {

		if (!isThisPlugin())
			return false;

		if (page.isForProject()) {
			return true;
			// if (page.isMultiCfg()) {
			// ICMultiItemsHolder mih = (ICMultiItemsHolder) getCfg();
			// IConfiguration[] cfs = (IConfiguration[]) mih.getItems();
			// for (int i = 0; i < cfs.length; i++) {
			// if (cfs[i].getBuilder().isManagedBuildOn())
			// return true;
			// }
			// return false;
			// } else {
			//
			// return getCfg().getBuilder().isManagedBuildOn();
			// }
		} else
			return false;
	}

	// Must be true, otherwise the page is not shown
	public boolean canSupportMultiCfg() {
		return true;
	}

	@Override
	protected void updateButtons() {
	} // Do nothing. No buttons to update.

	private boolean isThisPlugin() {

		m_config = getCfg();
		System.out.println("Device.isThisPlugin() m_config=" + m_config);

		IToolChain toolchain = m_config.getToolChain();
		String sToolchainId = toolchain.getBaseId();
		if (sToolchainId.startsWith(IDs.TOOLCHAIN_ID + "."))
			return true;

		return false;
	}

	private Node getDevicesTree() {

		// TODO: consider a static file with custom devices (custom_devices.xml)
		// to be merged with installed devices.
		Node devicesRoot;
		if (fDataManager != null) {
			devicesRoot = fDataManager.getInstalledDevicesForBuild();
		} else {
			devicesRoot = new Node(Type.ROOT);
			Node.addNewChild(devicesRoot, Type.NONE).setName("nu-i");
		}
		return devicesRoot;
	}
}
