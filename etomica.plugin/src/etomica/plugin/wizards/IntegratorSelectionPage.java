package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.integrator.Integrator;
import etomica.plugin.editors.SimulationObjects;

/**
 * This wizard page allows the use to select an integrator that a DataStream 
 * would be hooked up to.
 */
public class IntegratorSelectionPage extends WizardPage implements IPageChangedListener {
    private final SimulationObjects simObjects;
    public DataSourceHookupSelector hookupSelector;
	
    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public IntegratorSelectionPage(SimulationObjects simObjects, String name) {
        super(name);
        this.simObjects = simObjects;
        setTitle("Etomica "+name+" Wizard");
        setDescription("This wizard creates a new "+name+".");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ((Class)element).getName();
        }
    }

    /** 
     * Returns the integrator selected by the user 
     */
    public Integrator getIntegrator() {
        if (hookupSelector == null) {
            return null;
        }
        return hookupSelector.getIntegrator();
    }
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Class dataSourceClass = (Class)((NewObjectSimplePage)getPreviousPage()).classSelector.getSelection();
        if (dataSourceClass == null) {
            return;
        }
        ((WizardDialog)getWizard().getContainer()).addPageChangedListener(this);
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    hookupSelector = new DataSourceHookupSelector(root_container, simObjects, org.eclipse.swt.SWT.NONE);
        
        setPageComplete(true);
		setControl(root_container);
	}

    public void pageChanged(PageChangedEvent event) {
        if (event.getSelectedPage() == getPreviousPage()) {
            // if the user goes back, nuke ourselves so we get recreated with
            // appropriate controls
            setControl(null);
        }
    }
}
