/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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

package org.eclipse.embedcdt.managedbuild.cross.core;

// Base class for Arm and RISC-V toolchain definitions.
public abstract class ToolchainDefinition {

	// ------------------------------------------------------------------------

	// Basically the name string hash.
	protected long fUniqueId; // Actually unsigned long.

	protected String fName; // May not be unique.
	protected String fPrefix;
	protected String fSuffix;
	protected String fArchitecture;
	protected String fCmdMake;
	protected String fCmdRm;
	protected String fCmdWinMake;
	protected String fCmdWinRm;
	protected String fCmdC;
	protected String fCmdCpp;
	protected String fCmdAr;
	protected String fCmdObjcopy;
	protected String fCmdObjdump;
	protected String fCmdSize;

	protected boolean fIsDeprecated;

	// ------------------------------------------------------------------------

	public ToolchainDefinition(String sName) {
		fName = sName;
		fUniqueId = Integer.toUnsignedLong(fName.hashCode());
		fPrefix = "";
		fSuffix = "";
		fArchitecture = "?";
		fCmdMake = "make";
		fCmdRm = "rm";
		fCmdC = "gcc";
		fCmdCpp = "g++";
		fCmdAr = "ar";
		fCmdObjcopy = "objcopy";
		fCmdObjdump = "objdump";
		fCmdSize = "size";
		fIsDeprecated = false;
	}

	public ToolchainDefinition(String sName, String sPrefix) {
		this(sName);
		fPrefix = sPrefix;
	}

	public ToolchainDefinition(String sName, String sPrefix, String sArchitecture) {
		this(sName, sPrefix);
		setArchitecture(sArchitecture);
	}

	public ToolchainDefinition(String sName, String sPrefix, String sArchitecture, String cmdMake, String cmdRm) {
		this(sName, sPrefix, sArchitecture);
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

	@Deprecated
	public long getHash() {
		return fUniqueId;
	}

	// Accommodate for negative values.
	@Deprecated
	public void setHash(int hash) {
		fUniqueId = Integer.toUnsignedLong(hash);
	}

	public String getId() {
		return Long.toString(fUniqueId);
	}

	public void setId(String id) {
		long val = Long.parseUnsignedLong(id);
		if (val != fUniqueId) {
			System.out.println("Toolchain '" + fName + "' has custom ID '" + id + "' instead of '" + fUniqueId + "'");
		}
		fUniqueId = val;
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

	// Override it in derived classes to validate the string.
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

	public boolean isDeprecated() {
		return fIsDeprecated;
	}

	public void setIsDeprecated(boolean flag) {
		fIsDeprecated = flag;
	}

	public String getFullCmdC() {
		return getPrefix() + getCmdC() + getSuffix();
	}

	public String getFullName() {
		String name = getName();
		String cmd = getFullCmdC();
		String val;
		if (name.contains(cmd)) {
			val = name;
		} else {
			val = name + " (" + cmd + ")";
		}
		if (fIsDeprecated) {
			val += " DEPRECATED";
		}
		return val;
	}

	// ------------------------------------------------------------------------
}
