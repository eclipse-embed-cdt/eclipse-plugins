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

import java.util.ArrayList;
import java.util.List;

public class ToolchainDefinition {

	// ------------------------------------------------------------------------

	public static final String DEFAULT_TOOLCHAIN_NAME = "GNU Tools for ARM Embedded Processors";

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
	private static List<ToolchainDefinition> ms_list;

	public static List<ToolchainDefinition> getList() {
		return ms_list;
	}

	public static ToolchainDefinition getToolchain(int index) {
		return ms_list.get(index);
	}

	public static ToolchainDefinition getToolchain(String index) {
		return ms_list.get(Integer.parseInt(index));
	}

	public static int getSize() {
		return ms_list.size();
	}

	public static int findToolchainByName(String sName) {
		int i = 0;
		for (ToolchainDefinition td : ms_list) {
			if (td.fName.equals(sName))
				return i;
			i++;
		}
		// not found
		throw new IndexOutOfBoundsException();
	}

	public static int findToolchainByFullName(String sName) {
		int i = 0;
		for (ToolchainDefinition td : ms_list) {
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
		ms_list = new ArrayList<ToolchainDefinition>();

		// 0
		ms_list.add(new ToolchainDefinition(DEFAULT_TOOLCHAIN_NAME,
				"arm-none-eabi-"));
		// 1
		ToolchainDefinition tc;
		tc = new ToolchainDefinition("Sourcery CodeBench Lite for ARM EABI",
				"arm-none-eabi-");
		if (Utils.isPlatform("windows"))
			tc.setWin("cs-make", "cs-rm");
		ms_list.add(tc);

		// 2
		tc = new ToolchainDefinition(
				"Sourcery CodeBench Lite for ARM GNU/Linux",
				"arm-none-linux-gnueabi-");
		if (Utils.isPlatform("windows"))
			tc.setWin("cs-make", "cs-rm");
		ms_list.add(tc);

		// 3
		ms_list.add(new ToolchainDefinition("devkitPro ARM EABI", "arm-eabi-"));

		// 4
		ms_list.add(new ToolchainDefinition("Yagarto, Summon, etc. ARM EABI",
				"arm-none-eabi-"));

		// 5
		ms_list.add(new ToolchainDefinition("Linaro ARMv7 Linux GNU EABI HF",
				"arm-linux-gnueabihf-"));

		// 6
		ms_list.add(new ToolchainDefinition(
				"Linaro ARMv7 Big-Endian Linux GNU EABI HF",
				"armeb-linux-gnueabihf-"));

		// 64 bit toolchains
		// 7
		ms_list.add(new ToolchainDefinition("Linaro AArch64 bare-metal ELF",
				"aarch64-none-elf-", "aarch64"));

		// 8
		ms_list.add(new ToolchainDefinition(
				"Linaro AArch64 big-endian bare-metal ELF",
				"aarch64_be-none-elf-", "aarch64"));

		// 9
		ms_list.add(new ToolchainDefinition("Linaro AArch64 Linux GNU",
				"aarch64-linux-gnu-", "aarch64"));

		// 10
		ms_list.add(new ToolchainDefinition(
				"Linaro AArch64 big-endian Linux GNU", "aarch64_be-linux-gnu-",
				"aarch64"));

		// 11
		ms_list.add(new ToolchainDefinition("Custom", "arm-none-eabi-"));

		// 12
		// tc = new ToolchainDefinition("test", "myPrefix");
		// tc.setTest();
		// ms_list.add(tc);

	}

	// ------------------------------------------------------------------------
}
