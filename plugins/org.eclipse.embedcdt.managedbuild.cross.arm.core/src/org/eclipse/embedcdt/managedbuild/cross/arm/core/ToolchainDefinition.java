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

package org.eclipse.embedcdt.managedbuild.cross.arm.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.core.Activator;

public class ToolchainDefinition extends org.eclipse.embedcdt.managedbuild.cross.core.ToolchainDefinition {

	// ------------------------------------------------------------------------

	public static final String XPACK_ARM_GCC = "xPack GNU Arm Embedded GCC";

	public static final String XPACK_AARCH64_GCC = "xPack GNU AArch64 Embedded GCC";

	public static final String GME_ARM_GCC = "GNU MCU Eclipse ARM Embedded GCC";

	public static final String GNU_TOOLS_FOR_ARM_EMBEDDED = "GNU Tools for ARM Embedded Processors";

	public static final String DEFAULT_TOOLCHAIN_NAME = XPACK_ARM_GCC;

	// ------------------------------------------------------------------------

	// Static members
	protected static List<ToolchainDefinition> fgList = new ArrayList<>();
	protected static String fgArchitectures[] = { "ARM (AArch32)", "ARM64 (AArch64)" };

	// ------------------------------------------------------------------------

	public ToolchainDefinition(String sName) {
		super(sName);
		fArchitecture = "arm";
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

	@Override
	public void setArchitecture(String architecture) {
		assert (architecture.equals("arm") || architecture.equals("aarch64"));
		super.setArchitecture(architecture);
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
	 * Try to identify toolchain by name. If not possible, throw
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

	public static int findToolchainById(String sId) {

		int i = 0;
		for (ToolchainDefinition td : fgList) {
			if (td.getId().equals(sId.trim()))
				return i;
			i++;
		}
		// not found
		throw new IndexOutOfBoundsException();
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

			String id = element.getAttribute("id");
			String name = element.getAttribute("name");

			if (id != null && !id.isEmpty()) {
				try {
					findToolchainById(id);
					Activator.log("Duplicate toolchain id '" + id + "', ignored.");
					continue;
				} catch (IndexOutOfBoundsException e) {
				}
			} else {
				try {
					findToolchainByName(name);
					Activator.log("Duplicate toolchain name '" + name + "', ignored.");
					continue;
				} catch (IndexOutOfBoundsException e) {
				}
			}

			ToolchainDefinition td = new ToolchainDefinition(name);
			if (id != null && !id.isEmpty()) {
				td.setId(id);
			}
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

	// ------------------------------------------------------------------------

	private static final String CUSTOM_TOOLCHAINS_EXTENSION_POINT_ID = "org.eclipse.embedcdt.managedbuild.cross.arm.core.toolchains";

	// Initialise the list of known toolchains
	static {
		ToolchainDefinition tc;

		tc = new ToolchainDefinition(XPACK_ARM_GCC, "arm-none-eabi-");
		tc.setId("435435382");
		addToolchain(tc);

		tc = new ToolchainDefinition(GNU_TOOLS_FOR_ARM_EMBEDDED, "arm-none-eabi-");
		tc.setId("1287942917");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro ARMv7 bare-metal EABI", "arm-none-eabi-");
		tc.setId("976189281");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro ARMv7 big-endian bare-metal EABI", "armeb-none-eabi-");
		tc.setId("1341156853");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro ARMv7 Linux GNU EABI HF", "arm-linux-gnueabihf-");
		tc.setId("4014586055");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro ARMv7 big-endian Linux GNU EABI HF", "armeb-linux-gnueabihf-");
		tc.setId("2561104475");
		addToolchain(tc);

		tc = new ToolchainDefinition(GME_ARM_GCC, "arm-none-eabi-");
		tc.setId("962691777");
		tc.setIsDeprecated(true);
		addToolchain(tc);

		tc = new ToolchainDefinition("Sourcery CodeBench Lite for ARM EABI", "arm-none-eabi-");
		tc.setId("730666677");
		if (EclipseUtils.isWindows()) {
			tc.setWin("cs-make", "cs-rm");
		}
		tc.setIsDeprecated(true);
		addToolchain(tc);

		tc = new ToolchainDefinition("Sourcery CodeBench Lite for ARM GNU/Linux", "arm-none-linux-gnueabi-");
		tc.setId("4162764353");
		if (EclipseUtils.isWindows()) {
			tc.setWin("cs-make", "cs-rm");
		}
		tc.setIsDeprecated(true);
		addToolchain(tc);

		tc = new ToolchainDefinition("devkitPro ARM EABI", "arm-eabi-");
		tc.setId("3695067803");
		tc.setIsDeprecated(true);
		addToolchain(tc);

		tc = new ToolchainDefinition("Yagarto, Summon, etc. ARM EABI", "arm-none-eabi-");
		tc.setId("1195655929");
		tc.setIsDeprecated(true);
		addToolchain(tc);

		// 64 bit toolchains
		tc = new ToolchainDefinition(XPACK_AARCH64_GCC, "aarch64-none-elf-", "aarch64");
		tc.setId("596462749");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro AArch64 bare-metal ELF", "aarch64-elf-", "aarch64");
		tc.setId("1871385609");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro AArch64 big-endian bare-metal ELF", "aarch64_be-elf-", "aarch64");
		tc.setId("2048201829");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro AArch64 Linux GNU", "aarch64-linux-gnu-", "aarch64");
		tc.setId("1246627524");
		addToolchain(tc);

		tc = new ToolchainDefinition("Linaro AArch64 big-endian Linux GNU", "aarch64_be-linux-gnu-", "aarch64");
		tc.setId("681852136");
		addToolchain(tc);

		// fgList.add(new ToolchainDefinition("Custom", "arm-none-eabi-"));

		// Enumerate extension points and add custom toolchains.
		addExtensionsToolchains(CUSTOM_TOOLCHAINS_EXTENSION_POINT_ID);
	}

	// ------------------------------------------------------------------------
}
