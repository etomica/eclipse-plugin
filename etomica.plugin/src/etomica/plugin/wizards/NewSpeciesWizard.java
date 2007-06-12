package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.species.Species;

/**
 * This wizard allows the user to create a new Species.  The user can choose
 * the Species class and it is added to the Simulation.
 */
public class NewSpeciesWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewSpeciesWizard(SimulationObjects simObjects) {
        super();
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewObjectSimplePage(this,simObjects,"Species");
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
        simObjects.simulation.getSpeciesManager().addSpecies(species);
	  	
        success = true;
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final SimulationObjects simObjects;
    private NewObjectSimplePage page;
    private boolean success = false;
}