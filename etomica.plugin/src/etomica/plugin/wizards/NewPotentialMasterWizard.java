package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.potential.PotentialMaster;

/**
 * This wizard allows the user to create a new Species.  The user can choose
 * the Species class and it is added to the Simulation.
 */
public class NewPotentialMasterWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewPotentialMasterWizard(SimulationObjects simObjects) {
        super();
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewObjectSimplePage(this,simObjects,"PotentialMaster");
        addPage(page);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(PotentialMaster.class);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        PotentialMaster potentialMaster = (PotentialMaster)page.createObject();
        if (potentialMaster == null)
            return false;
        simObjects.potentialMasters.add(potentialMaster);
	  	
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