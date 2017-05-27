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

package ilg.gnumcueclipse.managedbuild.cross.riscv;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class ToolchainDefinition {

	// ------------------------------------------------------------------------

	public static final String RISC_V_GCC_NEWLIB = "RISC-V GCC/Newlib";
	public static final String DEFAULT_TOOLCHAIN_NAME = RISC_V_GCC_NEWLIB;

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

	private static String fArchitectures[] = { "RISC-V" };

	// ------------------------------------------------------------------------

	public ToolchainDefinition(String sName) {
		fName = sName;
		fPrefix = "";
		fSuffix = "";
		fArchitecture = "risc-v";
		fCmdMake = "make";
		fCmdRm = "rm";
		fCmdC = "gcc";
		fCmdCpp = "g++";
		fCmdAr = "ar";
		fCmdObjcopy = "objcopy";
		fCmdObjdump = "objdump";
		fCmdSize = "size";
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

	public void setName(String name) {
		fName = name;
	}

	public String getPrefix() {
		return fPrefix;
	}

	public void setPrefix(String prefix) {
		fPrefix = prefix;
	}

	public String getSuffix() {
		return fSuffix;
	}

	public void setSuffix(String suffix) {
		fSuffix = suffix;
	}

	public String getArchitecture() {
		return fArchitecture;
	}

	public void setArchitecture(String architecture) {
		fArchitecture = architecture;
	}

	public String getCmdMake() {
		return fCmdMake;
	}

	public void setCmdMake(String cmdMake) {
		fCmdMake = cmdMake;
	}

	public String getCmdRm() {
		return fCmdRm;
	}

	public void setCmdRm(String cmdRm) {
		fCmdRm = cmdRm;
	}

	public String getCmdWinMake() {
		return fCmdWinMake;
	}

	public void setCmdWinMake(String cmdWinMake) {
		fCmdWinMake = cmdWinMake;
	}

	public String getCmdWinRm() {
		return fCmdWinRm;
	}

	public void setCmdWinRm(String cmdWinRm) {
		fCmdWinRm = cmdWinRm;
	}

	public String getCmdC() {
		return fCmdC;
	}

	public void setCmdC(String cmdC) {
		fCmdC = cmdC;
	}

	public String getCmdCpp() {
		return fCmdCpp;
	}

	public void setCmdCpp(String cmdCpp) {
		fCmdCpp = cmdCpp;
	}

	public String getCmdAr() {
		return fCmdAr;
	}

	public void setCmdAr(String cmdAr) {
		fCmdAr = cmdAr;
	}

	public String getCmdObjcopy() {
		return fCmdObjcopy;
	}

	public void setCmdObjcopy(String cmdObjcopy) {
		fCmdObjcopy = cmdObjcopy;
	}

	public String getCmdObjdump() {
		return fCmdObjdump;
	}

	public void setCmdObjdump(String cmdObjdump) {
		fCmdObjdump = cmdObjdump;
	}

	public String getCmdSize() {
		return fCmdSize;
	}

	public void setCmdSize(String cmdSize) {
		fCmdSize = cmdSize;
	}

	public String getFullCmdC() {
		return getPrefix() + getCmdC() + getSuffix();
	}

	public String getFullName() {
		return getName() + " (" + getFullCmdC() + ")";
	}

	// Static members
	private static List<ToolchainDefinition> fgList;

	private static final String CUSTOM_TOOLCHAINS_EXT_POINT_ID = Activator.PLUGIN_ID + ".toolchains";

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

	/*
	 * Additional toolchains to be considered.
	 */
	private static void addToolchains() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(CUSTOM_TOOLCHAINS_EXT_POINT_ID);
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

	// Initialise the list of known toolchains
	static {
		fgList = new ArrayList<ToolchainDefinition>();

		// 0
		fgList.add(new ToolchainDefinition(RISC_V_GCC_NEWLIB, "riscv64-unknown-elf-"));

		// Enumerate extension points and add custom toolchains.
		addToolchains();
	}

	// ------------------------------------------------------------------------
}
