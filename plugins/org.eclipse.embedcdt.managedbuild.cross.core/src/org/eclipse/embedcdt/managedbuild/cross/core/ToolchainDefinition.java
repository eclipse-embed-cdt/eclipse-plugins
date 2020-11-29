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

	protected String fName;
	protected long fHash; // Actually unsigned long.
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

	// ------------------------------------------------------------------------

	public ToolchainDefinition(String sName) {
		fName = sName;
		fHash = Integer.toUnsignedLong(fName.hashCode());
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

	public long getHash() {
		return fHash;
	}

	public void setHash(int hash) {
		fHash = Integer.toUnsignedLong(hash);
		;
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

	// ------------------------------------------------------------------------
}
