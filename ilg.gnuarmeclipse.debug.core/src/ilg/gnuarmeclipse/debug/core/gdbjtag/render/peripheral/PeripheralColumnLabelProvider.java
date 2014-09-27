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

package ilg.gnuarmeclipse.debug.core.gdbjtag.render.peripheral;

import ilg.gnuarmeclipse.debug.core.Activator;
import ilg.gnuarmeclipse.debug.core.gdbjtag.render.peripheral.PeripheralColumnInfo.ColumnType;
import ilg.gnuarmeclipse.debug.core.gdbjtag.viewmodel.peripheral.PeripheralTreeVMNode;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IMemoryBlockExtension;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

public class PeripheralColumnLabelProvider extends ColumnLabelProvider
		implements IPropertyChangeListener {

	// ------------------------------------------------------------------------

	private final String COLOR_PREFIX = "ilg.gnuarmeclipse.debug.core.peripheral.color.";

	private final String COLOR_READONLY = COLOR_PREFIX + "readonly";
	private final String COLOR_WRITEONLY = COLOR_PREFIX + "writeonly";
	private final String COLOR_CHANGED = COLOR_PREFIX + "changed";

	private Color fColorReadOnlyBackground;
	private Color fColorWriteOnlyBackground;
	private Color fColorChangedBackground;

	private ColumnType fColumnType;
	private TreeViewer fViewer;

	// ------------------------------------------------------------------------

	public PeripheralColumnLabelProvider(TreeViewer viewer,
			IMemoryBlockExtension fMemoryBlock, ColumnType type) {

		// System.out.println("PeripheralColumnLabelProvider() " + type);

		fViewer = viewer;
		fColumnType = type;

		IThemeManager themeManager = PlatformUI.getWorkbench()
				.getThemeManager();
		themeManager.addPropertyChangeListener(this);

		setupColors();
	}

	@Override
	public void dispose() {

		IThemeManager themeManager = PlatformUI.getWorkbench()
				.getThemeManager();
		themeManager.removePropertyChangeListener(this);
	}

	// ------------------------------------------------------------------------

	@Override
	public void propertyChange(PropertyChangeEvent event) {

		String str = event.getProperty();
		if (str.startsWith(COLOR_PREFIX)) {
			setupColors();
			fViewer.refresh();
		}
	}

	// ------------------------------------------------------------------------

	private void setupColors() {

		IThemeManager themeManager = PlatformUI.getWorkbench()
				.getThemeManager();
		ITheme theme = themeManager.getCurrentTheme();
		ColorRegistry colorRegistry = theme.getColorRegistry();

		fColorReadOnlyBackground = colorRegistry.get(COLOR_READONLY);
		fColorWriteOnlyBackground = colorRegistry.get(COLOR_WRITEONLY);
		fColorChangedBackground = colorRegistry.get(COLOR_CHANGED);
	}

	@Override
	public Color getBackground(Object element) {

		Color color = null;

		boolean isReadOnly = false;
		boolean isWriteOnly = false;
		boolean isReadAllowed = true;
		boolean hasChanged = false;
		if ((element instanceof PeripheralTreeVMNode)) {

			PeripheralTreeVMNode node = (PeripheralTreeVMNode) element;
			if (!node.isPeripheral()) {

				isReadOnly = node.isReadOnly();
				isWriteOnly = node.isWriteOnly();

				isReadAllowed = node.isReadAllowed();
			}

			if (node.isRegister() || node.isField()) {
				try {
					hasChanged = node.hasValueChanged();
				} catch (DebugException e) {
					;
				}
			}
		}

		// Set the colours in order of importance.
		if (isWriteOnly || !isReadAllowed) {
			color = fColorWriteOnlyBackground;
		} else if (isReadOnly) {
			color = fColorReadOnlyBackground;
		}

		if (hasChanged && (fColumnType == ColumnType.VALUE)) {
			// Only the value column is changed.
			color = fColorChangedBackground;
		}

		return color;
	}

	@Override
	public Font getFont(Object element) {

		// No special fonts in use
		return null;
	}

	@Override
	public Color getForeground(Object element) {

		return null;
	}

	@Override
	public Image getImage(Object element) {

		if (fColumnType == ColumnType.REGISTER) {
			if ((element instanceof PeripheralTreeVMNode)) {

				// Get the image from the named file.
				String name = ((PeripheralTreeVMNode) element).getImageName();
				return Activator.getInstance().getImageDescriptor(name)
						.createImage();
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {

		if (element instanceof PeripheralTreeVMNode) {

			PeripheralTreeVMNode treeNode = (PeripheralTreeVMNode) element;

			switch (fColumnType) {

			case REGISTER:
				try {
					// Add a space to separate from the image
					return " " + treeNode.getName();
				} catch (DebugException e) {
				}

			case ADDRESS:
				return treeNode.getDisplayAddress();

			case VALUE:
				return treeNode.getDisplayValue();

			default:
				break;
			}
		}
		return null;
	}

	@Override
	public String getToolTipText(Object element) {

		StringBuilder sb = new StringBuilder();
		if (element instanceof PeripheralTreeVMNode) {

			PeripheralTreeVMNode treeNode = (PeripheralTreeVMNode) element;

			sb.append(treeNode.getDisplayNodeType());

			switch (fColumnType) {

			case REGISTER:
				try {
					appendText(sb, "", treeNode.getName());
				} catch (DebugException e) {
				}
				appendText(sb, "", treeNode.getDescription());
				break;

			case ADDRESS:
				if (treeNode.isField()) {
					appendText(sb, "Range=", treeNode.getDisplayOffset());
				} else {
					appendText(sb, "Offset=", treeNode.getDisplayOffset());
				}
				appendText(sb, "Size=", treeNode.getDisplaySize());
				break;

			case VALUE:
				if (treeNode.isPeripheral() || treeNode.isCluster()) {
					return null; // No value tooltip for groups
				}
				break;

			default:
				break;
			}
			appendText(sb, "", getDisplayAccess(treeNode));
			appendText(sb, "", getDisplayReadAction(treeNode));
		}
		if (sb.length() != 0) {
			return sb.toString();
		}
		return null;
	}

	public String getDisplayAccess(PeripheralTreeVMNode element) {

		String str = element.getAccess();
		if ("read-only".equals(str)) {
			return "Read only";
		} else if ("write-only".equals(str)) {
			return "Write only";
		} else if ("read-write".equals(str)) {
			return "Read/Write";
		} else if ("writeOnce".equals(str)) {
			return "Write once";
		} else if ("read-writeOnce".equals(str)) {
			return "Read/Write once";
		}
		return "";
	}

	public String getDisplayReadAction(PeripheralTreeVMNode element) {

		String str = element.getReadAction();
		return str;
	}

	private void appendText(StringBuilder sb, String name, String value) {

		if ((value == null) || (value.trim().isEmpty())) {
			return;
		}
		if (sb.length() > 0) {
			if (sb.indexOf(":") == -1) {
				sb.append(": ");
			} else {
				sb.append(", ");
			}
		}
		sb.append(name);
		sb.append(value.trim());
	}

	// ------------------------------------------------------------------------
}
