package ilg.gnumcueclipse.templates.gd.ui;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.cdt.ui.templateengine.IWizardDataPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WizardPageFoldersSettings extends MBSCustomPage
  implements IWizardDataPage
{
  private String pageTitle;
  private String pageDescription;
  private Composite pageControl;
  private Combo comboChipLst;
  private Text includeTitle;
  private Text sourceTitle;
  private Text firmwareTitle;
  private Text ldTitle;
  private boolean pageComplete = false;
  private IWizardPage next;
  private IWizardPage prev;

  public WizardPageFoldersSettings()
  {
    super("ilg.gnumcueclipse.templates.gd.ui.WizardPageFoldersSettings");
    setTitle("Folders settings");
    setDescription("Define the project folders and other options.");
    pageID = "Target processor settings";
  }

  public WizardPageFoldersSettings(IWizardPage prev) {
    super("ilg.gnumcueclipse.templates.gd.ui.WizardPageFoldersSettings");
    setTitle("Folders settings");
    setDescription("Define the project folders and other options.");
    this.prev = prev;
  }

  public WizardPageFoldersSettings(String pageId) {
    super(pageId);
    setTitle("Folders settings");
    setDescription("Define the project folders and other options.");
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
	String a = "Folders settings";
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
    gridDataLab.widthHint = 130;
    GridData gridDataTex = new GridData();
    gridDataTex.widthHint = 430;
    
    Label IncludeF = new Label(Composite,0);
    IncludeF.setLayoutData(gridDataLab);
    IncludeF.setText("Include folder:");
    this.includeTitle = new Text(Composite,128);
    this.includeTitle.setLayoutData(gridDataTex);
    this.includeTitle.setText("inc");
    
    Label SourceF = new Label(Composite,0);
    SourceF.setLayoutData(gridDataLab);
    SourceF.setText("Source Folder:");
    this.sourceTitle = new Text(Composite,128);
    this.sourceTitle.setLayoutData(gridDataTex);
    this.sourceTitle.setText("src");
    
    Label FirmwareF = new Label(Composite,0);
    FirmwareF.setLayoutData(gridDataLab);
    FirmwareF.setText("Firmware folder:");
    this.firmwareTitle = new Text(Composite,128);
    this.firmwareTitle.setLayoutData(gridDataTex);
    this.firmwareTitle.setText("firmware");
    
    Label ldF = new Label(Composite,0);
    ldF.setLayoutData(gridDataLab);
    ldF.setText("Linker scripts folders:");
    this.ldTitle = new Text(Composite,128);
    this.ldTitle.setLayoutData(gridDataTex);
    this.ldTitle.setText("ldscripts");

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
  public String getIncludeFolderName() {
	  return  this.includeTitle.getText();
  }
  
  public String getSourceFolderName() {
	  return  this.sourceTitle.getText();
  }
  
  public String getFirmwareFolderName() {
	  return this.firmwareTitle.getText();
  }
  
  public String getldFolderName() {
	  return this.ldTitle.getText();
  }
  

  public Map<String, String> getPageData()
  {
	HashMap<String, String> valMap = new HashMap<String, String>();
    valMap.put("includeTitle", getIncludeFolderName());
    valMap.put("sourceTitle", getSourceFolderName());
    valMap.put("firmwareTitle", getFirmwareFolderName());
    valMap.put("ldTitle", getldFolderName());
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

  public IWizardPage getPreviousPage()
  {
	  return this.prev;
  }
}