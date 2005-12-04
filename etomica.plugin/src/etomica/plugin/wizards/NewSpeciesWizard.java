package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.Simulation;
import etomica.species.Species;

/**
 * This wizard allows the user to create a new Species.  The user can choose
 * the Species class and it is added to the Simulation.
 */
public class NewSpeciesWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewSpeciesWizard(Simulation sim) {
        super();
        simulation = sim;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewObjectSimplePage(this,simulation,"Species");
        addPage(page);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(Species.class);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Species species = (Species)page.createObject();
        if ( species==null )
            return false;
	  	
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