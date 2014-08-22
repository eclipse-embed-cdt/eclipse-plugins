package ilg.gnuarmeclipse.debug.packs.ui;

import ilg.gnuarmeclipse.debug.packs.Activator;
import ilg.gnuarmeclipse.packs.cmsis.SvdGenericParser;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
//import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.DataManager;
import ilg.gnuarmeclipse.packs.data.PacksStorage;
import ilg.gnuarmeclipse.packs.data.Utils;
import ilg.gnuarmeclipse.packs.data.Xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.part.ViewPart;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PeripheralsView1 extends ViewPart {

	class MyContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getElements(Object inputElement) {
			// TODO Auto-generated method stub
			Node node = (Node) inputElement;
			return node.getChildren().toArray();
		}

	}

	class CheckboxEditingSupport extends EditingSupport {

		private CellEditor fEditor;
		private TableViewer fViewer;

		public CheckboxEditingSupport(TableViewer viewer) {
			super(viewer);
			fViewer = viewer;
			fEditor = new CheckboxCellEditor(viewer.getTable(), SWT.CHECK);
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return fEditor;
		}

		@Override
		protected Object getValue(Object element) {
			return Boolean.valueOf(((Leaf) element)
					.isBooleanProperty("selected"));
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (value instanceof Boolean) {
				((Leaf) element).setBooleanProperty("selected",
						((Boolean) value).booleanValue());
			}

			fViewer.update(element, null);
		}
	}

	// ------------------------------------------------------------------------

	class NameSorter extends ViewerSorter {

		@SuppressWarnings("unchecked")
		public int compare(Viewer viewer, Object e1, Object e2) {

			Leaf n1 = (Leaf) e1;
			String name1 = n1.getName();
			String name2 = ((Leaf) e2).getName();

			return getComparator().compare(name1, name2);
		}
	}

	// ------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private Composite fComposite;

	private TableViewer fViewer;
	private MessageConsoleStream fOut;
	private DataManager fDataManager;

	public PeripheralsView1() {

		fOut = ConsoleStream.getConsoleOut();

		fDataManager = DataManager.getInstance();
	}

	@Override
	public void createPartControl(Composite parent) {

		fComposite = parent;

		Table table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		fViewer = new CheckboxTableViewer(table);
		// fViewer = new TableViewer(table);

		TableViewerColumn column;

		column = new TableViewerColumn(fViewer, SWT.LEFT);
		column.getColumn().setText("Peripheral");
		column.getColumn().setWidth(130);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return " " + ((Leaf) element).getName();
			}

			@Override
			public Image getImage(Object element) {
				if (((Leaf) element).isType("arm")) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/system_peripheral.png")
							.createImage();
				} else if (((Leaf) element).isType("mem")) {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/memory.png")
							.createImage();
				} else if (((Leaf) element).isType(Type.NONE)) {
					return null;
				} else {
					return Activator.imageDescriptorFromPlugin(
							Activator.PLUGIN_ID, "icons/peripheral.png")
							.createImage();
				}
			}

		});

		column = new TableViewerColumn(fViewer, SWT.LEFT);
		column.getColumn().setText("Address");
		column.getColumn().setWidth(90);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return " " + ((Leaf) element).getProperty("address");
			}
		});

		column = new TableViewerColumn(fViewer, SWT.LEFT);
		column.getColumn().setText("Description");
		column.getColumn().setWidth(300);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return " " + ((Leaf) element).getDescription();
			}
		});
		// fViewer.setLabelProvider(new PersonTableLabelProvider());
		fViewer.setContentProvider(new MyContentProvider());
		fViewer.setSorter(new NameSorter());

		fViewer.setInput(getPeripherals());

	}

	@Override
	public void setFocus() {
		fViewer.getControl().setFocus();
	}

	private Node getPeripherals() {

		String deviceVendorId = "13";
		String deviceName = "STM32F407VG";

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
					System.out.println(peripheral);
					continue;
				} else {
					periphDescription = basePeriph.getProperty("description");
					periphGroup = basePeriph.getProperty("groupName");
				}
			} else {
				periphDescription = peripheral.getProperty("description");
				periphGroup = peripheral.getProperty("groupName");
			}

			long l = Utils.convertHexLong(periphAddress);
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
}
