package ilg.gnumcueclipse.templates.gd.ui;

import ilg.gnumcueclipse.templates.gd.ui.WizardPageTargetSettings;
import org.eclipse.cdt.ui.templateengine.IPagesAfterTemplateSelectionProvider;
import org.eclipse.cdt.ui.templateengine.IWizardDataPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class PagesAfterVF103TemplateSelectionProvider
  implements IPagesAfterTemplateSelectionProvider
{
  private WizardPageTargetSettings ts = null;
  private WizardPageFoldersSettings fs = null;
  private IWizardDataPage[] pages;

  public IWizardDataPage[] createAdditionalPages(IWorkbenchWizard wizard, IWorkbench workbench, IStructuredSelection selection)
  {

    this.ts = new WizardPageTargetSettings(1);
    this.fs = new WizardPageFoldersSettings(ts);

    this.pages = new IWizardDataPage[2];
    this.pages[0] = this.ts;
    this.pages[1] = this.fs;

    return this.pages;
  }

  public IWizardDataPage[] getCreatedPages(IWorkbenchWizard wizard)
  {
    return this.pages;
  }
}