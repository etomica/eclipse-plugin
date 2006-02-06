package etomica.plugin.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import etomica.data.DataPump;
import etomica.data.DataSink;
import etomica.data.DataSource;
import etomica.data.meter.Meter;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorPhase;
import etomica.integrator.IntervalActionAdapter;
import etomica.phase.Phase;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.Simulation;

/**
 * This wizard allows the user to create a new DataStream.  The user can choose
 * the DataSource class and the DataStream is added to the Simulation.  The user 
 * can then select to hook up the DataStream to an Integrator.
 */
public class NewDataStreamWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewDataStreamWizard(Simulation sim) {
        super();
        simulation = sim;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewObjectSimplePage(this,simulation,"Data Source");
        addPage(page);
        page2 = new IntegratorSelectionPage(simulation,"Data Source");
        addPage(page2);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(DataSource.class);
        selector.setExcludedClasses(new Class[]{DataSink.class});
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        DataSource dataSource = (DataSource)page.createObject();
        if (dataSource == null)
            return false;
	  	
        DataPump pump = new DataPump(dataSource,null);
        simulation.register(dataSource,pump);
        if (page2.getControl() != null && page2.isPageComplete()) {
            Integrator integrator = page2.getIntegrator();
            if (integrator != null) {
                integrator.addListener(new IntervalActionAdapter(pump));
                if (integrator instanceof IntegratorPhase && dataSource instanceof Meter) {
                    Phase phase = ((IntegratorPhase)integrator).getPhase();
                    if (phase != null) {
                        ((Meter)dataSource).setPhase(phase);
                    }
                }
            }
        }
        
        success = true;
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    public void createPageControls(Composite pageContainer) {
        // the default behavior is to create all the pages controls
        IWizardPage[] pages = getPages();
        for (int i = 0; i < pages.length; i++) {
            pages[i].createControl(pageContainer);
        }
    }
    
    private final Simulation simulation;
    private NewObjectSimplePage page;
    private IntegratorSelectionPage page2;
    private boolean success = false;
}