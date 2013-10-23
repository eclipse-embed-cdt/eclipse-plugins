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

	private String m_sName;
	private String m_sPrefix;
	private String m_sSuffix;
	private String m_sArchitecture;
	private String m_cmdMake;
	private String m_cmdRm;
	private String m_cmdWinMake;
	private String m_cmdWinRm;
	private String m_cmdC;
	private String m_cmdCpp;
	private String m_cmdAr;
	private String m_cmdObjcopy;
	private String m_cmdObjdump;
	private String m_cmdSize;

	private static String m_architectures[] = { "ARM (AArch32)",
			"ARM64 (AArch64)" };

	public ToolchainDefinition(String sName, String sPrefix) {
		m_sName = sName;
		m_sPrefix = sPrefix;
		m_sSuffix = "";
		m_sArchitecture = "arm";
		m_cmdMake = "make";
		m_cmdRm = "rm";
		m_cmdC = "gcc";
		m_cmdCpp = "g++";
		m_cmdAr = "ar";
		m_cmdObjcopy = "objcopy";
		m_cmdObjdump = "objdump";
		m_cmdSize = "size";
	}

	public ToolchainDefinition(String sName, String sPrefix,
			String sArchitecture) {
		this(sName, sPrefix);
		m_sArchitecture = sArchitecture;
	}

	public ToolchainDefinition(String sName, String sPrefix,
			String sArchitecture, String cmdMake, String cmdRm) {
		this(sName, sPrefix, sArchitecture);
		m_sArchitecture = sArchitecture;
		m_cmdMake = cmdMake;
		m_cmdRm = cmdRm;
	}

	public void setWin(String cmdMake, String cmdRm) {
		m_cmdMake = cmdMake;
		m_cmdRm = cmdRm;
	}

	public String getName() {
		return m_sName;
	}

	public String getPrefix() {
		return m_sPrefix;
	}

	public String getSuffix() {
		return m_sSuffix;
	}

	public String getArchitecture() {
		return m_sArchitecture;
	}

	public String getCmdMake() {
		return m_cmdMake;
	}

	public String getCmdRm() {
		return m_cmdRm;
	}

	public String getCmdWinMake() {
		return m_cmdWinMake;
	}

	public String getCmdWinRm() {
		return m_cmdWinRm;
	}

	public String getCmdC() {
		return m_cmdC;
	}

	public String getCmdCpp() {
		return m_cmdCpp;
	}

	public String getCmdAr() {
		return m_cmdAr;
	}

	public String getCmdObjcopy() {
		return m_cmdObjcopy;
	}

	public String getCmdObjdump() {
		return m_cmdObjdump;
	}

	public String getCmdSize() {
		return m_cmdSize;
	}

	public String getFullCmdC() {
		return getPrefix() + getCmdC() + getSuffix();
	}

	public String getFullName() {
		return getName() + " (" + getFullCmdC() + ")";
	}

	/*
	 * private void setTest() { m_sSuffix = "mySuffix"; m_sArchitecture =
	 * "myArch"; m_cmdMake = "myMake"; m_cmdRm = "myRm"; m_cmdC = "myGcc";
	 * m_cmdCpp = "myG++"; m_cmdAr = "myAr"; m_cmdObjcopy = "myObjcopy";
	 * m_cmdObjdump = "myObjdump"; m_cmdSize = "mySize"; }
	 */

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
			if (td.m_sName.equals(sName))
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
		return m_architectures;
	}

	public static String getArchitecture(int index) {
		return m_architectures[index];
	}

	// Initialise the list of known toolchains
	static {
		ms_list = new ArrayList<ToolchainDefinition>();

		// 0
		ms_list.add(new ToolchainDefinition(
				"GNU Tools for ARM Embedded Processors", "arm-none-eabi-"));
		// 1
		ToolchainDefinition tc;
		tc = new ToolchainDefinition("Sourcery CodeBench Lite for ARM EABI",
				"arm-none-eabi-");
		if (Utils.isPlatform("windows"))
			tc.setWin("cs_make", "cs_rm");
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
		ms_list.add(new ToolchainDefinition(
				"Custom", "arm-none-eabi-"));

		// 12
		// tc = new ToolchainDefinition("test", "myPrefix");
		// tc.setTest();
		// ms_list.add(tc);

	}
}
