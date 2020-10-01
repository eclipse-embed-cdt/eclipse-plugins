/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross.riscv;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ToolchainDefinition extends ilg.gnumcueclipse.managedbuild.cross.ToolchainDefinition {

	// ------------------------------------------------------------------------

	// 2021824384
	public static final String XPACK_RISCV_GCC = "xPack GNU RISC-V Embedded GCC";

	// 512258282
	public static final String GME_RISCV_GCC = "GNU MCU RISC-V GCC";

	// 2032619395
	public static final String RISC_V_GCC_NEWLIB = "RISC-V GCC/Newlib";
	// 344399268
	public static final String RISC_V_GCC_LINUX = "RISC-V GCC/Linux";
	// 339524431
	public static final String RISC_V_GCC_RTEMS = "RISC-V GCC/RTEMS";

	public static final String DEFAULT_TOOLCHAIN_NAME = XPACK_RISCV_GCC;

	// ------------------------------------------------------------------------

	// Static members
	protected static List<ToolchainDefinition> fgList = new ArrayList<ToolchainDefinition>();
	protected static String fgArchitectures[] = { "RISC-V" };

	// ------------------------------------------------------------------------

	public ToolchainDefinition(String sName) {
		super(sName);
		fArchitecture = "risc-v";
	}

	public ToolchainDefinition(String sName, String sPrefix) {
		this(sName);
		fPrefix = sPrefix;
	}

	public ToolchainDefinition(String sName, String sPrefix, String sArchitecture) {
		this(sName, sPrefix);
		fArchitecture = sArchitecture;
	}

	public ToolchainDefinition(String sName, String sPrefix, String sArchitecture, String cmdMake, String cmdRm) {
		this(sName, sPrefix, sArchitecture);
		fCmdMake = cmdMake;
		fCmdRm = cmdRm;
	}

	// ------------------------------------------------------------------------

	public static List<ToolchainDefinition> getList() {
		return fgList;
	}

	public static ToolchainDefinition getToolchain(int index) {
		return fgList.get(index);
	}

	public static ToolchainDefinition getToolchain(String index) {
		return fgList.get(Integer.parseInt(index));
	}

	public static int getSize() {
		return fgList.size();
	}

	public static void addToolchain(ToolchainDefinition toolchain) {
		fgList.add(toolchain);
	}

	/**
	 * Try to identify toolcahin by name. If not possible, throw
	 * IndexOutOfBoundsException().
	 * 
	 * @param sName a string with the toolchain name.
	 * @return non-negative index.
	 */
	public static int findToolchainByName(String sName) {

		int i = 0;
		for (ToolchainDefinition td : fgList) {
			if (td.fName.equals(sName))
				return i;
			i++;
		}
		// not found
		throw new IndexOutOfBoundsException();
	}

	public static int findToolchainByFullName(String sName) {

		int i = 0;
		for (ToolchainDefinition td : fgList) {
			String sFullName = td.getFullName();
			if (sFullName.equals(sName))
				return i;
			i++;
		}
		// not found
		return getDefault();
	}

	public static int getDefault() {
		return 0;
	}

	public static String[] getArchitectures() {
		return fgArchitectures;
	}

	public static String getArchitecture(int index) {
		return fgArchitectures[index];
	}

	/*
	 * Additional toolchains to be considered.
	 */
	public static void addExtensionsToolchains(String extensionPointId) {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(extensionPointId);
		for (IConfigurationElement element : elements) {
			String name = element.getAttribute("name");

			try {
				findToolchainByName(name);
				Activator.log("Duplicate toolchain name '" + name + "', ignored.");
			} catch (IndexOutOfBoundsException e) {
				ToolchainDefinition td = new ToolchainDefinition(name);
				String prefix = element.getAttribute("prefix");
				if (prefix != null && !prefix.isEmpty()) {
					td.setPrefix(prefix);
				}
				String suffix = element.getAttribute("suffix");
				if (suffix != null && !suffix.isEmpty()) {
					td.setSuffix(suffix);
				}
				String architecture = element.getAttribute("architecture");
				if (architecture != null && !architecture.isEmpty()) {
					td.setArchitecture(architecture);
				}
				String cmdMake = element.getAttribute("make_cmd");
				if (cmdMake != null && !cmdMake.isEmpty()) {
					td.setCmdMake(cmdMake);
				}
				String cmdRm = element.getAttribute("remove_cmd");
				if (cmdRm != null && !cmdRm.isEmpty()) {
					td.setCmdRm(cmdRm);
				}
				fgList.add(td);
			}

		}
	}

	// ------------------------------------------------------------------------

	private static final String CUSTOM_TOOLCHAINS_EXT_POINT_ID = Activator.PLUGIN_ID + ".toolchains";

	// Initialise the list of known toolchains
	static {
		addToolchain(new ToolchainDefinition(XPACK_RISCV_GCC, "riscv-none-embed-"));
		addToolchain(new ToolchainDefinition(GME_RISCV_GCC, "riscv-none-embed-"));

		addToolchain(new ToolchainDefinition(RISC_V_GCC_NEWLIB, "riscv64-unknown-elf-"));

		addToolchain(new ToolchainDefinition(RISC_V_GCC_LINUX, "riscv64-unknown-linux-gnu-"));

		addToolchain(new ToolchainDefinition(RISC_V_GCC_RTEMS, "riscv64-unknown-rtems-"));

		// Enumerate extension points and add custom toolchains.
		addExtensionsToolchains(CUSTOM_TOOLCHAINS_EXT_POINT_ID);
	}

	// ------------------------------------------------------------------------
}
