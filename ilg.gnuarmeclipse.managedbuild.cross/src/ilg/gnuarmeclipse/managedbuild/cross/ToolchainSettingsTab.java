/*******************************************************************************
 * Copyright (c) 2007, 2010 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Intel Corporation - Initial API and implementation
 *    James Blackburn (Broadcom Corp.)
 *******************************************************************************/
package ilg.gnuarmeclipse.managedbuild.cross;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICMultiItemsHolder;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObjectProperties;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IMultiConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
//import org.eclipse.cdt.managedbuilder.internal.ui.Messages;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.cdt.managedbuilder.ui.properties.ManagedBuilderUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ToolchainSettingsTab extends AbstractCBuildPropertyTab {

	// public static final String PROPERTY =
	// ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_ID;

	private static String BUILD_ARTEFACT_TYPE = "org.eclipse.cdt.build.core.buildArtefactType";
	private static String BUILD_ARTEFACT_TYPE_EXE = BUILD_ARTEFACT_TYPE
			+ ".exe";
	private static String BUILD_ARTEFACT_TYPE_STATICLIB = BUILD_ARTEFACT_TYPE
			+ ".staticLib";

	private IConfiguration fCfg;
	private IBuildPropertyValue[] values;

	private Label l4;
	private Combo t2, t3, t4;
	private Combo c1;
	private int savedPos = -1; // current project type
	private ITool tTool;
	private boolean canModify = true;

	private enum FIELD {
		NAME, EXT, PREF
	}

	private Set<String> set2 = new TreeSet<String>();
	private Set<String> set3 = new TreeSet<String>();
	private Set<String> set4 = new TreeSet<String>();

	// ---

	private Combo m_toolchainCombo;
	private int m_selectedToolchainIndex;
	private String m_selectedToolchainName;

	private Combo m_architectureCombo;
	private String m_architectures[] = { "ARM (AArch32)", "ARM64 (AArch64)" };

	private Text m_pathText;
	private Text m_prefixText;
	private Text m_suffixText;
	private Text m_commandCText;
	private Text m_commandCppText;
	private Text m_commandArText;
	private Text m_commandObjcopyText;
	private Text m_commandObjdumpText;
	private Text m_commandSizeText;
	private Text m_commandMakeText;
	private Text m_commandRmText;

	private Button m_flashButton;
	private Button m_listingButton;
	private Button m_sizeButton;

	private boolean m_isExecutable;
	private boolean m_isStaticLibrary;

	@Override
	public void createControls(Composite parent) {
		super.createControls(parent);

		if (false) {
			usercomp.setLayout(new GridLayout(2, false));

			Label l1 = new Label(usercomp, SWT.NONE);
			l1.setLayoutData(new GridData(GridData.BEGINNING));
			l1.setText(Messages.ArtifactTab_0);

			c1 = new Combo(usercomp, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER);
			c1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			c1.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// typeChanged();
				}
			});
			c1.setOrientation(SWT.LEFT_TO_RIGHT);

			Label l2 = new Label(usercomp, SWT.NONE);
			l2.setLayoutData(new GridData(GridData.BEGINNING));
			l2.setText(Messages.ArtifactTab_1);
			// t2 = setCombo(FIELD.NAME, set2);
			t2.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					if (canModify)
						fCfg.setArtifactName(t2.getText());
				}
			});

			Label l3 = new Label(usercomp, SWT.NONE);
			l3.setLayoutData(new GridData(GridData.BEGINNING));
			l3.setText(Messages.ArtifactTab_2);
			// t3 = setCombo(FIELD.EXT, set3);
			t3.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					if (canModify)
						fCfg.setArtifactExtension(t3.getText());
				}
			});

			l4 = new Label(usercomp, SWT.NONE);
			l4.setLayoutData(new GridData(GridData.BEGINNING));
			l4.setText(Messages.ArtifactTab_3);
			// t4 = setCombo(FIELD.PREF, set4);
			t4.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					if (canModify) {
						if (tTool != null)
							tTool.setOutputPrefixForPrimaryOutput(t4.getText());
						else if (fCfg instanceof IMultiConfiguration)
							((IMultiConfiguration) fCfg)
									.setOutputPrefixForPrimaryOutput(t4
											.getText());
					}
				}
			});

			updateData(getResDesc());
		}
		// m_composite = new Composite(parent, SWT.NULL);

		fCfg = getCfg();
		IBuildPropertyValue propertyValue = fCfg.getBuildArtefactType();
		if (BUILD_ARTEFACT_TYPE_EXE.equals(propertyValue.getId()))
			m_isExecutable = true;
		else
			m_isExecutable = false;

		if (BUILD_ARTEFACT_TYPE_STATICLIB.equals(propertyValue.getId()))
			m_isStaticLibrary = true;
		else
			m_isStaticLibrary = false;

		usercomp.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData();
		usercomp.setLayoutData(layoutData);

		// ----- Toolchain ----------------------------------------------------
		Label toolchainLbl = new Label(usercomp, SWT.NONE);
		toolchainLbl.setLayoutData(new GridData(GridData.BEGINNING));
		toolchainLbl.setText(Messages.ToolChainSettingsTab_name);

		m_toolchainCombo = new Combo(usercomp, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_toolchainCombo.setLayoutData(layoutData);

		// create the selection array
		String[] toolchains = new String[ToolchainDefinition.getSize()];
		for (int i = 0; i < ToolchainDefinition.getSize(); ++i) {
			toolchains[i] = ToolchainDefinition.getToolchain(i).getFullName();
		}
		m_toolchainCombo.setItems(toolchains);

		m_selectedToolchainName = Option.getOptionStringValue(fCfg,
				Option.OPTION_TOOLCHAIN_NAME);
		System.out
				.println("Previous toolchain name " + m_selectedToolchainName);
		if (m_selectedToolchainName != null
				&& m_selectedToolchainName.length() > 0) {
			m_selectedToolchainIndex = ToolchainDefinition
					.findToolchainByName(m_selectedToolchainName);
		} else {
			m_selectedToolchainIndex = ToolchainDefinition.getDefault();
			m_selectedToolchainName = ToolchainDefinition.getToolchain(
					m_selectedToolchainIndex).getName();
		}

		String toolchainSel = toolchains[m_selectedToolchainIndex];
		m_toolchainCombo.setText(toolchainSel);

		m_toolchainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				updateAfterToolchainChange();
			}
		});

		// ----- Architecture -------------------------------------------------
		Label architectureLbl = new Label(usercomp, SWT.NONE);
		architectureLbl.setLayoutData(new GridData(GridData.BEGINNING));
		architectureLbl.setText(Messages.ToolChainSettingsTab_architecture);

		m_architectureCombo = new Combo(usercomp, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_architectureCombo.setLayoutData(layoutData);

		m_architectureCombo.setItems(m_architectures);

		String sSelectedArchitecture = Option.getOptionStringValue(fCfg,
				Option.OPTION_ARCHITECTURE);
		int index;
		if (sSelectedArchitecture.endsWith(".arm"))
			index = 0;
		else
			index = 1;
		m_architectureCombo.setText(m_architectures[index]);

		m_toolchainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

			}
		});

		// ----- Prefix -------------------------------------------------------
		Label prefixLabel = new Label(usercomp, SWT.NONE);
		prefixLabel.setText(Messages.ToolChainSettingsTab_prefix);

		m_prefixText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String prefix = Option.getOptionStringValue(fCfg,
				Option.OPTION_COMMAND_PREFIX);
		if (prefix != null) {
			m_prefixText.setText(prefix);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_prefixText.setLayoutData(layoutData);
		m_prefixText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		// ----- Suffix -------------------------------------------------------
		Label suffixLabel = new Label(usercomp, SWT.NONE);
		suffixLabel.setText(Messages.ToolChainSettingsTab_suffix);

		m_suffixText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String suffix = Option.getOptionStringValue(fCfg,
				Option.OPTION_COMMAND_SUFFIX);
		if (suffix != null) {
			m_suffixText.setText(suffix);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_suffixText.setLayoutData(layoutData);
		m_suffixText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		// ----- Command c ----------------------------------------------------
		Label commandCLabel = new Label(usercomp, SWT.NONE);
		commandCLabel.setText(Messages.ToolChainSettingsTab_cCmd);

		m_commandCText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandC = Option.getOptionStringValue(fCfg,
				Option.OPTION_COMMAND_C);
		if (commandC != null) {
			m_commandCText.setText(commandC);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandCText.setLayoutData(layoutData);
		m_commandCText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		// ----- Command cpp --------------------------------------------------
		Label commandCppLabel = new Label(usercomp, SWT.NONE);
		commandCppLabel.setText(Messages.ToolChainSettingsTab_cppCmd);

		m_commandCppText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandCpp = Option.getOptionStringValue(fCfg,
				Option.OPTION_COMMAND_CPP);
		if (commandCpp != null) {
			m_commandCppText.setText(commandCpp);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandCppText.setLayoutData(layoutData);
		m_commandCppText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		if (m_isStaticLibrary) {
			// ----- Command ar -----------------------------------------------
			Label commandArLabel = new Label(usercomp, SWT.NONE);
			commandArLabel.setText(Messages.ToolChainSettingsTab_arCmd);

			m_commandArText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandAr = Option.getOptionStringValue(fCfg,
					Option.OPTION_COMMAND_AR);
			if (commandAr != null) {
				m_commandArText.setText(commandAr);
				// updatePathProperty();
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandArText.setLayoutData(layoutData);
			m_commandArText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {

					// updatePathProperty();
				}
			});
		}

		if (m_isExecutable) {
			// ----- Command objcopy ------------------------------------------
			Label commandObjcopyLabel = new Label(usercomp, SWT.NONE);
			commandObjcopyLabel
					.setText(Messages.ToolChainSettingsTab_objcopyCmd);

			m_commandObjcopyText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandObjcopy = Option.getOptionStringValue(fCfg,
					Option.OPTION_COMMAND_OBJCOPY);
			if (commandObjcopy != null) {
				m_commandObjcopyText.setText(commandObjcopy);
				// updatePathProperty();
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandObjcopyText.setLayoutData(layoutData);
			m_commandObjcopyText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {

					// updatePathProperty();
				}
			});

			// ----- Command objdump ------------------------------------------
			Label commandObjdumpLabel = new Label(usercomp, SWT.NONE);
			commandObjdumpLabel
					.setText(Messages.ToolChainSettingsTab_objdumpCmd);

			m_commandObjdumpText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandObjdump = Option.getOptionStringValue(fCfg,
					Option.OPTION_COMMAND_OBJDUMP);
			if (commandObjdump != null) {
				m_commandObjdumpText.setText(commandObjdump);
				// updatePathProperty();
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandObjdumpText.setLayoutData(layoutData);
			m_commandObjdumpText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {

					// updatePathProperty();
				}
			});

			// ----- Command size ---------------------------------------------
			Label commandSizeLabel = new Label(usercomp, SWT.NONE);
			commandSizeLabel.setText(Messages.ToolChainSettingsTab_sizeCmd);

			m_commandSizeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandSize = Option.getOptionStringValue(fCfg,
					Option.OPTION_COMMAND_SIZE);
			if (commandSize != null) {
				m_commandSizeText.setText(commandSize);
				// updatePathProperty();
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandSizeText.setLayoutData(layoutData);
			m_commandSizeText.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {

					// updatePathProperty();
				}
			});
		}

		// ----- Command make ----------------------------------------------
		Label commandMakeLabel = new Label(usercomp, SWT.NONE);
		commandMakeLabel.setText(Messages.ToolChainSettingsTab_makeCmd);

		m_commandMakeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandMake = Option.getOptionStringValue(fCfg,
				Option.OPTION_COMMAND_MAKE);
		if (commandMake != null) {
			m_commandMakeText.setText(commandMake);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandMakeText.setLayoutData(layoutData);
		m_commandMakeText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		// ----- Command rm ----------------------------------------------
		Label commandRmLabel = new Label(usercomp, SWT.NONE);
		commandRmLabel.setText(Messages.ToolChainSettingsTab_rmCmd);

		m_commandRmText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandRm = Option.getOptionStringValue(fCfg,
				Option.OPTION_COMMAND_RM);
		if (commandRm != null) {
			m_commandRmText.setText(commandRm);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandRmText.setLayoutData(layoutData);
		m_commandRmText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		// ----- Path ---------------------------------------------------------
		Label pathLabel = new Label(usercomp, SWT.NONE);
		pathLabel.setText(Messages.ToolChainSettingsTab_path);

		m_pathText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String toolchainPath = Option.getOptionStringValue(fCfg,
				Option.OPTION_TOOLCHAIN_PATH);
		if (toolchainPath != null) {
			m_pathText.setText(toolchainPath);
			// updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		m_pathText.setLayoutData(layoutData);
		m_pathText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				// updatePathProperty();
			}
		});

		Button pathButton = new Button(usercomp, SWT.NONE);
		pathButton.setText(Messages.ToolChainSettingsTab_browse);
		pathButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(usercomp
						.getShell(), SWT.APPLICATION_MODAL);
				String browsedDirectory = dirDialog.open();
				if (browsedDirectory != null) {
					m_pathText.setText(browsedDirectory);
				}
			}
		});
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		pathButton.setLayoutData(layoutData);

		if (m_isExecutable) {
			// ----- Flash ----------------------------------------------------
			m_flashButton = new Button(usercomp, SWT.CHECK);
			m_flashButton.setText(Messages.ToolChainSettingsTab_flash);

			Boolean isCreateFlash = Option.getOptionBooleanValue(fCfg,
					Option.OPTION_ADDTOOLS_CREATEFLASH);
			if (isCreateFlash != null) {
				m_flashButton.setSelection(isCreateFlash);
			}
			m_flashButton.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
				}
			});
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			m_flashButton.setLayoutData(layoutData);

			// ----- Listing --------------------------------------------------
			m_listingButton = new Button(usercomp, SWT.CHECK);
			m_listingButton.setText(Messages.ToolChainSettingsTab_listing);

			Boolean isCreateListing = Option.getOptionBooleanValue(fCfg,
					Option.OPTION_ADDTOOLS_CREATELISTING);
			if (isCreateListing != null) {
				m_listingButton.setSelection(isCreateListing);
			}
			m_listingButton.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
				}
			});
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			m_listingButton.setLayoutData(layoutData);

			// ----- Size -----------------------------------------------------
			m_sizeButton = new Button(usercomp, SWT.CHECK);
			m_sizeButton.setText(Messages.ToolChainSettingsTab_size);

			Boolean isPrintSize = Option.getOptionBooleanValue(fCfg,
					Option.OPTION_ADDTOOLS_CREATELISTING);
			if (isPrintSize != null) {
				m_sizeButton.setSelection(isPrintSize);
			}
			if (false)
				m_sizeButton.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
					}
				});
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			m_sizeButton.setLayoutData(layoutData);
		}

		// --------------------------------------------------------------------

	}

	private void updateAfterToolchainChange() {
		String sSelectedCombo = m_toolchainCombo.getText();
		int index = ToolchainDefinition.findToolchainByFullName(sSelectedCombo);
		ToolchainDefinition td = ToolchainDefinition.getToolchain(index);

		String sArchitecture = td.getArchitecture();
		if ("arm".equals(sArchitecture))
			index = 0;
		else
			index = 1;
		m_architectureCombo.setText(m_architectures[index]);

		m_prefixText.setText(td.getPrefix());
		m_suffixText.setText(td.getSuffix());
		m_commandCText.setText(td.getCmdC());
		m_commandCppText.setText(td.getCmdCpp());
		if (m_isStaticLibrary) {
			m_commandArText.setText(td.getCmdAr());
		}
		if (m_isExecutable) {
			m_commandObjcopyText.setText(td.getCmdObjcopy());
			m_commandObjdumpText.setText(td.getCmdObjdump());
			m_commandSizeText.setText(td.getCmdSize());
		}
		m_commandMakeText.setText(td.getCmdMake());
		m_commandRmText.setText(td.getCmdRm());

		String pathKey = SetCrossCommandWizardPage.SHARED_CROSS_TOOLCHAIN_PATH
				+ "." + td.getName().hashCode();
		String path = SharedDefaults.getInstance().getSharedDefaultsMap()
				.get(pathKey);
		if (path == null)
			path = "";

		m_pathText.setText(path);

		// if (m_isExecutable) {
		// m_flashButton.setSelection(true);
		// m_listingButton.setSelection(true);
		// m_sizeButton.setSelection(true);
		// }
	}

	// private void typeChanged() {
	// int n = c1.getSelectionIndex();
	// if (n != savedPos) {
	// setProjectType(n);
	// savedPos = n;
	// updateData(getResDesc());
	// }
	// }

	// private void setProjectType(int n) {
	// try {
	// String s = values[n].getId();
	// fCfg.setBuildArtefactType(s);
	// } catch (BuildException ex) {
	// ManagedBuilderUIPlugin.log(ex);
	// }
	// }

	@Override
	public void updateData(ICResourceDescription cfgd) {
		if (cfgd == null)
			return;

		/*
		 * if (false) { fCfg = getCfg(); if (page.isMultiCfg()) { values =
		 * ((IMultiConfiguration) fCfg) .getSupportedValues(PROPERTY); } else {
		 * values = fCfg.getBuildProperties().getSupportedValues(PROPERTY); }
		 * c1.removeAll(); c1.setData(values); for (int i = 0; i <
		 * values.length; i++) { c1.add(values[i].getName()); }
		 * c1.setText(EMPTY_STR); IBuildPropertyValue pv =
		 * fCfg.getBuildArtefactType(); if (pv != null) { String s = pv.getId();
		 * for (int i = 0; i < values.length; i++) { if
		 * (s.equals(values[i].getId())) { c1.select(i); savedPos = i; break; }
		 * } }
		 * 
		 * //updateCombo(t2); //updateCombo(t3); //updateCombo(t4);
		 * 
		 * String s = fCfg.getArtifactName(); if (!page.isMultiCfg() && (s ==
		 * null || s.trim().length() == 0)) { s =
		 * getResDesc().getConfiguration().getProjectDescription() .getName();
		 * getCfg().setArtifactName(s); }
		 * 
		 * canModify = false;
		 * 
		 * t2.setText(s); t3.setText(fCfg.getArtifactExtension());
		 * 
		 * if (page.isMultiCfg()) { if (l4 != null) l4.setVisible(true); if (t4
		 * != null) { t4.setVisible(true); t4.setText(((IMultiConfiguration)
		 * fCfg) .getToolOutputPrefix()); } } else { tTool =
		 * fCfg.calculateTargetTool(); if (tTool != null) { if (l4 != null)
		 * l4.setVisible(true); if (t4 != null) { t4.setVisible(true);
		 * t4.setText(tTool.getOutputPrefix()); } } else { if (l4 != null)
		 * l4.setVisible(false); if (t4 != null) t4.setVisible(false); } }
		 * canModify = true; }
		 */}

	@Override
	protected void performApply(ICResourceDescription src,
			ICResourceDescription dst) {
		// IConfiguration srcCfg = getCfg(src.getConfiguration());
		// IConfiguration dstCfg = getCfg(dst.getConfiguration());

		/*
		 * if (false) { cfg2.setArtifactName(cfg1.getArtifactName());
		 * cfg2.setArtifactExtension(cfg1.getArtifactExtension());
		 * 
		 * ITool t1 = cfg1.calculateTargetTool(); ITool t2 =
		 * cfg2.calculateTargetTool(); if (t1 != null && t2 != null)
		 * t2.setOutputPrefixForPrimaryOutput(t1.getOutputPrefix());
		 * 
		 * try { IBuildPropertyValue bv = cfg1.getBuildArtefactType(); if (bv !=
		 * null) cfg2.setBuildArtefactType(bv.getId()); } catch (BuildException
		 * e) { ManagedBuilderUIPlugin.log(e); } }
		 */

		IConfiguration config = getCfg(src.getConfiguration());
		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		try {
			// Do NOT use ManagedBuildManager.setOption() to avoid sending
			// events to the option. Also do not use option.setValue()
			// since this does not propagate notifications and the
			// values are not saved to .cproject.

			String sSelectedArchitecture = m_architectureCombo.getText();
			if (m_architectures[0].equals(sSelectedArchitecture)) {
				val = Option.OPTION_ARCHITECTURE_ARM;
			} else {
				val = Option.OPTION_ARCHITECTURE_AARCH64;
			}
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); //$NON-NLS-1$
			config.setOption(toolchain, option, val);

			String sSelectedCombo = m_toolchainCombo.getText();
			int index = ToolchainDefinition
					.findToolchainByFullName(sSelectedCombo);
			ToolchainDefinition td = ToolchainDefinition.getToolchain(index);
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getName());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); //$NON-NLS-1$
			config.setOption(toolchain, option, m_prefixText.getText());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); //$NON-NLS-1$
			config.setOption(toolchain, option, m_suffixText.getText());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandCText.getText());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandCppText.getText());

			if (m_isStaticLibrary) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
				config.setOption(toolchain, option, m_commandArText.getText());
			}

			if (m_isExecutable) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
				config.setOption(toolchain, option,
						m_commandObjcopyText.getText());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
				config.setOption(toolchain, option,
						m_commandObjdumpText.getText());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
				config.setOption(toolchain, option, m_commandSizeText.getText());
			}

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandMakeText.getText());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandRmText.getText());

			if (m_isExecutable) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); //$NON-NLS-1$
				config.setOption(toolchain, option, m_flashButton.getSelection());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); //$NON-NLS-1$
				config.setOption(toolchain, option, m_listingButton.getSelection());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); //$NON-NLS-1$
				config.setOption(toolchain, option, m_sizeButton.getSelection());
			}
			
		} catch (BuildException e) {
			e.printStackTrace();
		}
		System.out.println("performApply()");

	}

	@Override
	protected void performDefaults() {

		/*
		 * if (false) { fCfg.setArtifactName(fCfg.getManagedProject()
		 * .getDefaultArtifactName()); fCfg.setArtifactExtension(null); //
		 * workaround for bad extension setting (always "exe"): // set wrong
		 * project type temporary // and then set right one back if
		 * (c1.getItemCount() > 1) { int right = c1.getSelectionIndex(); int
		 * wrong = (right == 0) ? 1 : 0; setProjectType(wrong);
		 * setProjectType(right); } if (tTool != null)
		 * tTool.setOutputPrefixForPrimaryOutput(null); else if (fCfg instanceof
		 * IMultiConfiguration) ((IMultiConfiguration) fCfg)
		 * .setOutputPrefixForPrimaryOutput(null); updateData(getResDesc()); }
		 */
		updateAfterToolchainChange();
		System.out.println("performDefaults()");
	}

	@Override
	public boolean canBeVisible() {
		if (page.isForProject()) {
			if (page.isMultiCfg()) {
				// ICMultiItemsHolder mih = (ICMultiItemsHolder) getCfg();
				// IConfiguration[] cfs = (IConfiguration[]) mih.getItems();
				// for (int i = 0; i < cfs.length; i++) {
				// if (cfs[i].getBuilder().isManagedBuildOn())
				// return true;
				// }
				return false;
			} else
				return getCfg().getBuilder().isManagedBuildOn();
		} else
			return false;
	}

	@Override
	protected void updateButtons() {
	} // Do nothing. No buttons to update.

	/*
	 * private Combo setCombo(FIELD field, Set<String> set) {
	 * 
	 * Combo combo = new Combo(usercomp, SWT.BORDER); combo.setLayoutData(new
	 * GridData(GridData.FILL_HORIZONTAL)); combo.setData(ENUM, field);
	 * combo.setData(SSET, set); updateCombo(combo); return combo; }
	 * 
	 * @SuppressWarnings("unchecked") private void updateCombo(Combo combo) {
	 * FIELD field = (FIELD) combo.getData(ENUM); Set<String> set =
	 * (Set<String>) combo.getData(SSET); if (field == null || set == null)
	 * return;
	 * 
	 * canModify = false; String oldStr = combo.getText(); combo.removeAll();
	 * for (ICConfigurationDescription cf : page.getCfgsEditable()) {
	 * IConfiguration c = getCfg(cf); String s = null; switch (field) { case
	 * NAME: s = c.getArtifactName(); break; case EXT: s =
	 * c.getArtifactExtension(); break; case PREF: ITool t =
	 * c.calculateTargetTool(); if (t != null) s = t.getOutputPrefix(); } if (s
	 * != null && s.trim().length() > 0) set.add(s.trim()); } if (set.size() >
	 * 0) combo.setItems(set.toArray(new String[set.size()]));
	 * combo.setText(oldStr); canModify = true; }
	 */
}
