package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.data.DataPump;
import etomica.data.DataSink;
import etomica.data.DataSource;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.Simulation;

/**
 * This wizard allows the user to create a new DataStream.  The user can choose
 * the DataSource class and the DataStream is added to the Simulation.
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
	  	
        simulation.register(dataSource,new DataPump(dataSource,null));
        
        success = true;
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Simulation simulation;
    private NewObjectSimplePage page;
    private boolean success = false;
}