/*******************************************************************************
 * Copyright (c) 2007, 2010 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Intel Corporation - Initial API and implementation
 *    James Blackburn (Broadcom Corp.)
 *    Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import java.util.LinkedList;
import java.util.List;

import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.packs.storage.PacksStorage;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.NodeViewContentProvider;
import ilg.gnuarmeclipse.packs.tree.Type;

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.MultiConfiguration;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;

/**
 * @noextend This class is not intended to be sub-classed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("restriction")
public class TabDevice extends AbstractCBuildPropertyTab {

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

	class DevicesLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			Leaf node = (Leaf) obj;
			String name = " " + node.getName();
			if (node.isType(Type.FAMILY)) {
				name += " (family)";
			} else if (node.isType(Type.SUBFAMILY)) {
				name += " (subfamily)";
			} else if (node.isType(Type.VARIANT)) {
				name += " (variant)";
			}
			return name;
		}
	}

	// ------------------------------------------------------------------------

	TreeViewer fDevicesTree;
	Label fArchitectureLabel;
	Label fDeviceLabel;
	Button fMemoryEditButton;

	// ------------------------------------------------------------------------

	// private Composite m_composite;

	private IConfiguration m_config;
	private IConfiguration m_lastUpdatedConfig = null;
	private PacksStorage fStorage;

	public TabDevice() {
		fStorage = PacksStorage.getInstance();
	}

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

		// usercomp is defined in parent class

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		// layout.marginHeight = 0;
		// layout.marginWidth = 0;
		usercomp.setLayout(layout);

		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		usercomp.setLayoutData(layoutData);

		createDeviceGroup(usercomp);

		createMemoryGroup(usercomp);

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

		fDevicesTree = new TreeViewer(group, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.heightHint = 200;
		gridData.horizontalSpan = 2;
		Tree tree = fDevicesTree.getTree();
		tree.setLayoutData(gridData);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		DevicesContentProvider contentProvider = new DevicesContentProvider();
		fDevicesTree.setContentProvider(contentProvider);

		fDevicesTree.setLabelProvider(new DevicesLabelProvider());
		// fDevicesTree.setInput(getDevicesTree());

		// ---

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages
				.getString("DeviceTab_DeviceGroup_architecture_label"));

		fArchitectureLabel = new Label(group, SWT.NONE);
		fArchitectureLabel.setText("-");
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
		}

		{
			Table m_table;
			m_table = new Table(group, SWT.SINGLE | SWT.BORDER
					| SWT.FULL_SELECTION);
			m_table.setHeaderVisible(true);
			m_table.setLinesVisible(true);

			gridData = new GridData();
			// gridData.verticalAlignment = SWT.FILL;
			// gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = SWT.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.heightHint = 62;
			m_table.setLayoutData(gridData);

			TableColumn m_columnName = new TableColumn(m_table, SWT.NULL);
			m_columnName.setText("Name");
			m_columnName.setWidth(80);
			m_columnName.setResizable(true);

			TableColumn m_columnStart = new TableColumn(m_table, SWT.NULL);
			m_columnStart.setText("Start");
			m_columnStart.setWidth(100);
			m_columnStart.setResizable(true);

			TableColumn m_columnSize = new TableColumn(m_table, SWT.NULL);
			m_columnSize.setText("Size");
			m_columnSize.setWidth(100);
			m_columnSize.setResizable(true);

			TableColumn m_columnInit = new TableColumn(m_table, SWT.NULL);
			m_columnInit.setText("Init");
			m_columnInit.setWidth(55);
			m_columnInit.setResizable(true);

			TableColumn m_columnStartup = new TableColumn(m_table, SWT.NULL);
			m_columnStartup.setText("Startup");
			m_columnStartup.setWidth(55);
			m_columnStartup.setResizable(true);

			TableColumn m_columnDefault = new TableColumn(m_table, SWT.NULL);
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
		if (sToolchainId.startsWith(Activator.TOOLCHAIN_ID + "."))
			return true;

		return false;
	}

	private Node getDevicesTree() {

		// TODO: consider a static file with custom devices (custom_devices.xml)
		// to be merged with installed devices.

		Node devicesRoot = fStorage.getInstalledDevicesForBuild();
		return devicesRoot;
	}
}
