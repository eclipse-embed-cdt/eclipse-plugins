/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import ilg.gnuarmeclipse.core.EclipseUtils;

import java.util.ArrayList;
import java.util.List;

public class ToolchainDefinition {

	// ------------------------------------------------------------------------

	public static final String GNU_TOOLS_FOR_ARM_EMBEDDED = "GNU Tools for ARM Embedded Processors";
	public static final String DEFAULT_TOOLCHAIN_NAME = GNU_TOOLS_FOR_ARM_EMBEDDED;

	// ------------------------------------------------------------------------

	private String fName;
	private String fPrefix;
	private String fSuffix;
	private String fArchitecture;
	private String fCmdMake;
	private String fCmdRm;
	private String fCmdWinMake;
	private String fCmdWinRm;
	private String fCmdC;
	private String fCmdCpp;
	private String fCmdAr;
	private String fCmdObjcopy;
	private String fCmdObjdump;
	private String fCmdSize;

	private static String fArchitectures[] = { "ARM (AArch32)",
			"ARM64 (AArch64)" };

	// ------------------------------------------------------------------------

	public ToolchainDefinition(String sName, String sPrefix) {
		fName = sName;
		fPrefix = sPrefix;
		fSuffix = "";
		fArchitecture = "arm";
		fCmdMake = "make";
		fCmdRm = "rm";
		fCmdC = "gcc";
		fCmdCpp = "g++";
		fCmdAr = "ar";
		fCmdObjcopy = "objcopy";
		fCmdObjdump = "objdump";
		fCmdSize = "size";
	}

	public ToolchainDefinition(String sName, String sPrefix,
			String sArchitecture) {
		this(sName, sPrefix);
		fArchitecture = sArchitecture;
	}

	public ToolchainDefinition(String sName, String sPrefix,
			String sArchitecture, String cmdMake, String cmdRm) {
		this(sName, sPrefix, sArchitecture);
		fArchitecture = sArchitecture;
		fCmdMake = cmdMake;
		fCmdRm = cmdRm;
	}

	// ------------------------------------------------------------------------

	public void setWin(String cmdMake, String cmdRm) {
		fCmdMake = cmdMake;
		fCmdRm = cmdRm;
	}

	public String getName() {
		return fName;
	}

	public String getPrefix() {
		return fPrefix;
	}

	public String getSuffix() {
		return fSuffix;
	}

	public String getArchitecture() {
		return fArchitecture;
	}

	public String getCmdMake() {
		return fCmdMake;
	}

	public String getCmdRm() {
		return fCmdRm;
	}

	public String getCmdWinMake() {
		return fCmdWinMake;
	}

	public String getCmdWinRm() {
		return fCmdWinRm;
	}

	public String getCmdC() {
		return fCmdC;
	}

	public String getCmdCpp() {
		return fCmdCpp;
	}

	public String getCmdAr() {
		return fCmdAr;
	}

	public String getCmdObjcopy() {
		return fCmdObjcopy;
	}

	public String getCmdObjdump() {
		return fCmdObjdump;
	}

	public String getCmdSize() {
		return fCmdSize;
	}

	public String getFullCmdC() {
		return getPrefix() + getCmdC() + getSuffix();
	}

	public String getFullName() {
		return getName() + " (" + getFullCmdC() + ")";
	}

	// Static members
	private static List<ToolchainDefinition> fgList;

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

	/**
	 * Try to identify toolcahin by name. If not possible, throw
	 * IndexOutOfBoundsException().
	 * 
	 * @param sName
	 *            a string with the toolchain name.
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
		return fArchitectures;
	}

	public static String getArchitecture(int index) {
		return fArchitectures[index];
	}

	// Initialise the list of known toolchains
	static {
		fgList = new ArrayList<ToolchainDefinition>();

		// 0
		fgList.add(new ToolchainDefinition(DEFAULT_TOOLCHAIN_NAME,
				"arm-none-eabi-"));
		// 1
		ToolchainDefinition tc;
		tc = new ToolchainDefinition("Sourcery CodeBench Lite for ARM EABI",
				"arm-none-eabi-");
		if (EclipseUtils.isWindows()) {
			tc.setWin("cs-make", "cs-rm");
		}
		fgList.add(tc);

		// 2
		tc = new ToolchainDefinition(
				"Sourcery CodeBench Lite for ARM GNU/Linux",
				"arm-none-linux-gnueabi-");
		if (EclipseUtils.isWindows()) {
			tc.setWin("cs-make", "cs-rm");
		}
		fgList.add(tc);

		// 3
		fgList.add(new ToolchainDefinition("devkitPro ARM EABI", "arm-eabi-"));

		// 4
		fgList.add(new ToolchainDefinition("Yagarto, Summon, etc. ARM EABI",
				"arm-none-eabi-"));

		// 5
		fgList.add(new ToolchainDefinition("Linaro ARMv7 Linux GNU EABI HF",
				"arm-linux-gnueabihf-"));

		// 6
		fgList.add(new ToolchainDefinition(
				"Linaro ARMv7 Big-Endian Linux GNU EABI HF",
				"armeb-linux-gnueabihf-"));

		// 64 bit toolchains
		// 7
		fgList.add(new ToolchainDefinition("Linaro AArch64 bare-metal ELF",
				"aarch64-none-elf-", "aarch64"));

		// 8
		fgList.add(new ToolchainDefinition(
				"Linaro AArch64 big-endian bare-metal ELF",
				"aarch64_be-none-elf-", "aarch64"));

		// 9
		fgList.add(new ToolchainDefinition("Linaro AArch64 Linux GNU",
				"aarch64-linux-gnu-", "aarch64"));

		// 10
		fgList.add(new ToolchainDefinition(
				"Linaro AArch64 big-endian Linux GNU", "aarch64_be-linux-gnu-",
				"aarch64"));

		// 11
		fgList.add(new ToolchainDefinition("Custom", "arm-none-eabi-"));

		// 12
		// tc = new ToolchainDefinition("test", "myPrefix");
		// tc.setTest();
		// ms_list.add(tc);

	}

	// ------------------------------------------------------------------------
}
