package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.potential.Potential;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.species.Species;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "etom". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */
public class NewSpeciesPotential extends Wizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewSpeciesPotential(PotentialMaster parent, Simulation sim) {
        super();
        simulation = sim;
        potentialMaster = parent;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewSpeciesPotentialPage(simulation);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Potential potential = page.createPotential();
        if ( potential==null )
            return false;
	  	
        Species[] speciesArray = page.getSpecies();
        potentialMaster.setSpecies(potential,speciesArray);
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Simulation simulation;
    private final PotentialMaster potentialMaster;
    private NewSpeciesPotentialPage page;
    private boolean success = false;
}