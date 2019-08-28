package ilg.gnumcueclipse.templates.gd.ui;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.cdt.ui.templateengine.IWizardDataPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardPageTargetSettings extends MBSCustomPage
  implements IWizardDataPage
{
	
  class chipSize{
	  String flshSize;
	  String ramSize;
  }
  private String pageTitle;
  private String pageDescription;
  private Composite pageControl;
  private Combo comboChipLst;
  private Label chipLbl;
  private Text flashSize;
  private Text ramSize;
  private Text Clock;
  private Button checkSomeWarnings;
  private Button checkMostWarnings;
  private Button enableWerror;
  private Button useOg;
  private Button useNano;
  private Button excludeUnused;
  private Button useLinkOpt;
  private boolean pageComplete = false;
  private Map<String,chipSize> chipMap = new HashMap<String, chipSize>();
  private IWizardPage next;
  private IWizardPage prev;
  private int template;
  private String ldscripts;

  public WizardPageTargetSettings()
  {
    super("ilg.gnumcueclipse.templates.gd.ui.WizardPageTargetSettings");
    setTitle("Target processor settings");
    setDescription("Select the target processor family and define flash and RAM sizes.");
    pageID = "Target processor settings";
  }

  public WizardPageTargetSettings(int temp) {
	    super("ilg.gnumcueclipse.templates.gd.ui.WizardPageTargetSettings");
	    setTitle("Target processor settings");
	    setDescription("Select the target processor family and define flash and RAM sizes.");
	    this.template = temp;
	  }

  
  public WizardPageTargetSettings(IWizardPage prev) {
    super("ilg.gnumcueclipse.templates.gd.ui.WizardPageTargetSettings");
    setTitle("Target processor settings");
    setDescription("Select the target processor family and define flash and RAM sizes.");
    this.prev = prev;
  }

  public WizardPageTargetSettings(String pageId) {
    super(pageId);
    setTitle("Target processor settings");
    setDescription("Select the target processor family and define flash and RAM sizes.");
  }

  protected boolean isCustomPageComplete()
  {
    return this.pageComplete;
  }

  public boolean isPageComplete()
  {
    return this.pageComplete;
  }

  public String getName()
  {
	String a = "Target processor settings";
    return a;
  }

  public void createControl(Composite parent)
  {
    this.pageComplete = true;
    this.pageControl = new Composite(parent, 0);
    GridLayout layout = new GridLayout();
    this.pageControl.setLayout(layout); 
    this.pageControl.setLayoutData(new GridData(768));
    createGui(this.pageControl);
  }

  private void createGui(Composite parent)
  {     
    Composite Composite = new Composite(parent,0);
    GridLayout layout = new GridLayout(2,false);
    layout.verticalSpacing = 20;
    Composite.setLayout(layout);
    GridData gridDataLab = new GridData();
    gridDataLab.widthHint = 140;
    GridData gridDataTex = new GridData();
    gridDataTex.widthHint = 430;
    gridDataTex.heightHint = 23;
    
    
    createChipSelectionGrp(Composite);
      
    Label Flash = new Label(Composite,0);
    Flash.setLayoutData(gridDataLab);
    Flash.setText("Flash size(kB):");
    this.flashSize = new Text(Composite,128);
    this.flashSize.setLayoutData(gridDataTex);
    
    Label Ram = new Label(Composite,0);
    Ram.setLayoutData(gridDataLab);
    Ram.setText("RAM size(kB):");
    this.ramSize = new Text(Composite,128);
    this.ramSize.setLayoutData(gridDataTex);
    
    Label Clock = new Label(Composite,0);
    Clock.setLayoutData(gridDataLab);
    Clock.setText("External clock(Hz):");
    this.Clock = new Text(Composite,128);
    this.Clock.setLayoutData(gridDataTex);
    String clock = "8000000";
    this.Clock.setText(clock);
    
    Label Checksomewarnings = new Label(Composite,0);
    Checksomewarnings.setLayoutData(gridDataLab);
    Checksomewarnings.setText("Check some warnings");
    this.checkSomeWarnings = new Button(Composite, 32);
    this.checkSomeWarnings.setLayoutData(gridDataTex);
    this.checkSomeWarnings.setSelection(false);
    
    Label Checkmostwarnings = new Label(Composite,0);
    Checkmostwarnings.setLayoutData(gridDataLab);
    Checkmostwarnings.setText("Check most warnings");
    this.checkMostWarnings = new Button(Composite, 32);
    this.checkMostWarnings.setLayoutData(gridDataTex);
    this.checkMostWarnings.setSelection(false);
    
    Label EnableW = new Label(Composite,0);
    EnableW.setLayoutData(gridDataLab);
    EnableW.setText("Enable -Werror");
    this.enableWerror = new Button(Composite, 32);
    this.enableWerror.setLayoutData(gridDataTex);
    this.enableWerror.setSelection(false);
    
    Label UseOg = new Label(Composite,0);
    UseOg.setLayoutData(gridDataLab);
    UseOg.setText("Use -Og on debug");
    this.useOg = new Button(Composite, 32);
    this.useOg.setLayoutData(gridDataTex);
    this.useOg.setSelection(true);
    
    Label UseNano = new Label(Composite,0);
    UseNano.setLayoutData(gridDataLab);
    UseNano.setText("Use newlib nano");
    this.useNano = new Button(Composite, 32);
    this.useNano.setLayoutData(gridDataTex);
    this.useNano.setSelection(true);
    
    Label ExcludeUnused = new Label(Composite,0);
    ExcludeUnused.setLayoutData(gridDataLab);
    ExcludeUnused.setText("Exclude unused");
    this.excludeUnused = new Button(Composite, 32);
    this.excludeUnused.setLayoutData(gridDataTex);
    this.excludeUnused.setSelection(true);
    
    Label UseLinkOpt = new Label(Composite,0);
    UseLinkOpt.setLayoutData(gridDataLab);
    UseLinkOpt.setText("Use link optimization");
    this.useLinkOpt = new Button(Composite, 32);
    this.useLinkOpt.setLayoutData(gridDataTex);
    this.useLinkOpt.setSelection(false);
    
    initGui();
  }

  private void createChipSelectionGrp(Composite parent)
  {
    this.chipLbl = new Label(parent, 0);
    GridData gridDataLab = new GridData();
    gridDataLab.widthHint = 140;
    this.chipLbl.setLayoutData(gridDataLab);
    this.chipLbl.setText("Chip family:");
    
    this.comboChipLst = new Combo(parent, 12);
    GridData gridDataTex = new GridData();
    gridDataTex.widthHint = 405;
    this.comboChipLst.setLayoutData(gridDataTex);

    this.chipMap.clear();
    chipSize chipSizeTemp = new chipSize();
    
    if(template == 0) {
    String name = "GD32E230C8T6";
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp); 
    name = "GD32E230C6T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230C4T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp); 
    name = "GD32E230K8T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230K6T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230K4T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230K8U6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230K6U6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230K4U6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230G8U6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230G6U6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230G4U6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230F8V6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230F6V6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230F4V6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230F8P6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230F6P6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E230F4P6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E231C8T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "64";
    chipSizeTemp.ramSize = "8";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E231C6T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "32";
    chipSizeTemp.ramSize = "6";
    this.chipMap.put(name, chipSizeTemp);
    name = "GD32E231C4T6";
    chipSizeTemp = new chipSize();
    chipSizeTemp.flshSize = "16";
    chipSizeTemp.ramSize = "4";
    this.chipMap.put(name, chipSizeTemp);
    }
    
    else if(template == 1) {
        String name = "GD32VF103T4U6";
        chipSizeTemp.flshSize = "16";
        chipSizeTemp.ramSize = "6";
        this.chipMap.put(name, chipSizeTemp); 
        name = "GD32VF103T6U6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "32";
        chipSizeTemp.ramSize = "10";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103T8U6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "64";
        chipSizeTemp.ramSize = "20";
        this.chipMap.put(name, chipSizeTemp); 
        name = "GD32VF103TBU6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "128";
        chipSizeTemp.ramSize = "36";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103C4T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "16";
        chipSizeTemp.ramSize = "6";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103C6T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "32";
        chipSizeTemp.ramSize = "10";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103C8T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "64";
        chipSizeTemp.ramSize = "20";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103CBT6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "128";
        chipSizeTemp.ramSize = "32";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103R4T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "16";
        chipSizeTemp.ramSize = "6";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103R6T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "32";
        chipSizeTemp.ramSize = "10";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103R8T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "64";
        chipSizeTemp.ramSize = "20";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103RBT6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "128";
        chipSizeTemp.ramSize = "32";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103V8T6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "64";
        chipSizeTemp.ramSize = "20";
        this.chipMap.put(name, chipSizeTemp);
        name = "GD32VF103VBT6";
        chipSizeTemp = new chipSize();
        chipSizeTemp.flshSize = "128";
        chipSizeTemp.ramSize = "32";
        this.chipMap.put(name, chipSizeTemp);  	
    }

    this.comboChipLst.addSelectionListener(new SelectionAdapter()
    {
      public void widgetSelected(SelectionEvent event) {
        WizardPageTargetSettings.this.chipTypeChanged();
      }
    });
    
    
  }

  public void reValidate()
  {
    if (this.comboChipLst != null) {
      this.comboChipLst.removeAll();
      
      int selIndex = 1;
      int size = 0;
      String chipPartNo = null;
      
      if(template == 0) {
      chipPartNo = "GD32E230C8T6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230C6T6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230C4T6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230K8T6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230K6T6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230K4T6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230K8U6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230K6U6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230K4U6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230G8U6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230G6U6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230G4U6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230F8V6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230F6V6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230F4V6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230F8P6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230F6P6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E230F4P6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E231C8T6";
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E231C6T6";	
      this.comboChipLst.add(chipPartNo);
      chipPartNo = "GD32E231C4T6";
      this.comboChipLst.add(chipPartNo);
      size = 18;
      }
      
      else if(template == 1) {
          chipPartNo = "GD32VF103T4U6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103T6U6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103T8U6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103TBU6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103C4T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103C6T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103C8T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103CBT6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103R4T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103R6T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103R8T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103RBT6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103V8T6";
          this.comboChipLst.add(chipPartNo);
          chipPartNo = "GD32VF103VBT6";
          this.comboChipLst.add(chipPartNo);     
    	  size = 14;
      }
      
      this.comboChipLst.setVisibleItemCount(size);

      this.comboChipLst.select(selIndex - 1);
      chipTypeChanged();
    }
	  return;
  }

  private void initGui()
  {
    reValidate();
  }

  private void chipTypeChanged()
  {
    int selIndex = this.comboChipLst.getSelectionIndex();
    if (selIndex == -1) {
      return;
    }
    String selected = this.comboChipLst.getItem(selIndex);
    String FlashValue = this.chipMap.get(selected).flshSize;
    String RamValue = this.chipMap.get(selected).ramSize;
    
    String FlshInfo = FlashValue.replace("\\n", "\n");
    String RamInfo = RamValue.replace("\\n", "\n");
    showInfoText(FlshInfo,RamInfo);
  }

  private void showInfoText(String flshInfo,String ramInfo)
  {
	  this.flashSize.setText(flshInfo);
	  this.ramSize.setText(ramInfo);
  }

  public void dispose()
  {
  }

  public Control getControl()
  {
    return this.pageControl;
  }

  public String getDescription()
  {
    return this.pageDescription;
  }

  public String getErrorMessage()
  {
    return null;
  }

  public Image getImage()
  {
    return null;
  }

  public String getMessage()
  {
    return null;
  }

  public String getTitle()
  {
    return this.pageTitle;
  }

  public void performHelp()
  {
  }

  public void setDescription(String description)
  {
    this.pageDescription = description;
  }

  public void setImageDescriptor(ImageDescriptor image)
  {
  }

  public void setTitle(String title)
  {
    this.pageTitle = title;
  }

  public void setVisible(boolean visible)
  {
    this.pageControl.setVisible(visible);
  }

  public int getSelectedDebugType() {
    if (getSelectedChip().equalsIgnoreCase("Simulator")) {
      return 1;
    }
    return 0;
  }

  public String getSelectedChip() {
    return this.comboChipLst.getText();
  }

  public String getSelectedChipShort() {
	  String chipPartNo = this.comboChipLst.getText();
	  String chipShortPartNo = chipPartNo.substring(0, 8);
	  return chipShortPartNo;
  }
  public String getChipFlshSize() {
	  return  this.flashSize.getText();
  }
  
  public String getChipRamSize() {
	  return  this.ramSize.getText();
  }
  
  public String getClock() {
	  return this.Clock.getText();
  }
  
  public String getSelectionCheckSomeWarnings() {
	  return this.checkSomeWarnings.getSelection() ? "true" : "false";
  }
  
  public String getSelectionCheckMostWarnings() {
	  return this.checkMostWarnings.getSelection() ? "true" : "false";
  }
  
  public String getSelectionEnableWerror() {
	  return this.enableWerror.getSelection() ? "true" : "false";
  }
  
  public String getSelectionUseOg() {
	  return this.useOg.getSelection() ? "true" : "false";
  }
  
  public String getSelectionUseNano() {
	  return this.useNano.getSelection() ? "true" : "false";
  }
  
  public String getSelectionExcludeUnused() {
	  return this.excludeUnused.getSelection() ? "true" : "false";
  }
  
  public String getSelectionUseLinkOpt() {
	  return this.useLinkOpt.getSelection() ? "true" : "false";
  }
  
  public String getLdscripts() {
	  this.ldscripts = "GD32VF103x4.lds";
	  String lable;
	  if(this.template == 1) {
		  String chipPartNo = this.comboChipLst.getText();
		  lable = chipPartNo.substring(10,11);
		  if(lable.equals("4")) {
			  this.ldscripts = "GD32VF103x4.lds";		  
		  }
		  else if(lable.equals("6")) {
			  this.ldscripts = "GD32VF103x6.lds";		  
		  }
		  else if(lable.equals("8")) {
			  this.ldscripts = "GD32VF103x8.lds";		  
		  }
		  else if(lable.equals("B")) {
			  this.ldscripts = "GD32VF103xB.lds";		  
		  }
	  }
	   return this.ldscripts;
  }
  
  public Map<String, String> getPageData()
  {
    HashMap<String, String> valMap = new HashMap<String, String>();
    valMap.put("chipPartNo", getSelectedChip());
    valMap.put("chipShortPartNo", getSelectedChipShort());
    valMap.put("chipFlashSize", getChipFlshSize());
    valMap.put("chipRamSize", getChipRamSize());
    valMap.put("chipClock", getClock());
    valMap.put("checkSomeWarnings", getSelectionCheckSomeWarnings());
    valMap.put("checkMostWarnings", getSelectionCheckMostWarnings());
    valMap.put("enableWerror", getSelectionEnableWerror());
    valMap.put("useOg", getSelectionUseOg());
    valMap.put("useNano", getSelectionUseNano());
    valMap.put("excludeUnused", getSelectionExcludeUnused());
    valMap.put("useLinkOpt", getSelectionUseLinkOpt());
    valMap.put("lds", getLdscripts());
    return valMap;
  }

  public void setNextPage(IWizardPage arg0)
  {
    this.next = arg0;
  }

  public IWizardPage getNextPage()
  {
    return this.next;
  }
  
  public void setPreviousPage(IWizardPage arg0)
  {
	  this.prev = arg0;
  }

  public IWizardPage getPreviousPage()
  {
	  return this.prev;
  }
}