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

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.PersistentPreferences;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.SvdEnumeratedValueDMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral.PeripheralColumnInfo.ColumnType;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterFieldVMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterVMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralTopVMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralTreeVMNode;

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

public class PeripheralColumnLabelProvider extends ColumnLabelProvider implements IPropertyChangeListener {

	// ------------------------------------------------------------------------

	private static final String COLOR_PREFIX = "ilg.gnuarmeclipse.debug.gdbjtag.peripherals.color.";

	public static final String COLOR_READONLY = COLOR_PREFIX + "readonly";
	public static final String COLOR_WRITEONLY = COLOR_PREFIX + "writeonly";
	public static final String COLOR_CHANGED = COLOR_PREFIX + "changed";
	public static final String COLOR_CHANGED_MEDIUM = COLOR_PREFIX + "changed.medium";
	public static final String COLOR_CHANGED_LIGHT = COLOR_PREFIX + "changed.light";

	private Color fColorReadOnlyBackground;
	private Color fColorWriteOnlyBackground;
	private Color fColorChangedBackground;
	private Color fColorChangedMediumBackground;
	private Color fColorChangedLightBackground;

	private ColumnType fColumnType;
	private TreeViewer fViewer;

	private boolean fUseFadingBackground;

	// ------------------------------------------------------------------------

	public PeripheralColumnLabelProvider(TreeViewer viewer, IMemoryBlockExtension fMemoryBlock, ColumnType type) {

		// System.out.println("PeripheralColumnLabelProvider() " + type);

		fViewer = viewer;
		fColumnType = type;

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		themeManager.addPropertyChangeListener(this);

		setupColors();

		fUseFadingBackground = PersistentPreferences.getPeripheralsChangedUseFadingBackground();
	}

	@Override
	public void dispose() {

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
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

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme theme = themeManager.getCurrentTheme();
		ColorRegistry colorRegistry = theme.getColorRegistry();

		fColorReadOnlyBackground = colorRegistry.get(COLOR_READONLY);
		fColorWriteOnlyBackground = colorRegistry.get(COLOR_WRITEONLY);
		fColorChangedBackground = colorRegistry.get(COLOR_CHANGED);
		fColorChangedMediumBackground = colorRegistry.get(COLOR_CHANGED_MEDIUM);
		fColorChangedLightBackground = colorRegistry.get(COLOR_CHANGED_LIGHT);
	}

	@Override
	public Color getBackground(Object element) {

		Color color = null;

		boolean isReadOnly = false;
		boolean isWriteOnly = false;
		boolean isReadAllowed = true;
		boolean hasChanged = false;

		int fadingLevel = 0;

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

			fadingLevel = node.getFadingLevel();
		}

		// Set the colours in order of importance.
		if (isWriteOnly || !isReadAllowed) {
			color = fColorWriteOnlyBackground;
		} else if (isReadOnly) {
			color = fColorReadOnlyBackground;
		}

		// Initially was && (fColumnType == ColumnType.VALUE),
		// but was aligned with the Registers view behaviour.
		if (fUseFadingBackground) {
			if (fadingLevel == 3) {
				color = fColorChangedBackground;
			} else if (fadingLevel == 2) {
				color = fColorChangedMediumBackground;
			} else if (fadingLevel == 1) {
				color = fColorChangedLightBackground;
			}
		} else {
			if (hasChanged) {
				color = fColorChangedBackground;
			}
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
				return Activator.getInstance().getImage(name);
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
					String str = " " + treeNode.getName();
					return str;

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
				String description = "\"" + treeNode.getDescription() + "\"";
				appendText(sb, "", description);
				if (treeNode instanceof PeripheralTopVMNode) {
					appendText(sb, "Version=", ((PeripheralTopVMNode) treeNode).getDisplayVersion());
					appendText(sb, "Group=", ((PeripheralTopVMNode) treeNode).getDisplayGroupName());
				}
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
				if (treeNode instanceof PeripheralRegisterVMNode) {
					appendText(sb, "Reset=", ((PeripheralRegisterVMNode) treeNode).getDisplayResetValue());
				}
				break;

			default:
				break;
			}
			appendText(sb, "", getDisplayAccess(treeNode));
			appendText(sb, "", getDisplayReadAction(treeNode));

			switch (fColumnType) {

			case VALUE:
				if (treeNode instanceof PeripheralRegisterFieldVMNode
						&& (((PeripheralRegisterFieldVMNode) treeNode).isEnumeration())) {

					// For enumerations, add the enumeration description.
					SvdEnumeratedValueDMNode node = ((PeripheralRegisterFieldVMNode) treeNode)
							.getEnumeratedValueDMNode();
					if (node == null) {
						break;
					}

					appendText(sb, "Enumeration=", "\"" + node.getDescription() + "\"");
				}
				break;

			default:
				break;
			}
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
