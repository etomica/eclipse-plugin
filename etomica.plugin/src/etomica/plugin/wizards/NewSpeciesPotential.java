package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.plugin.editors.SimulationObjects;
import etomica.potential.Potential;
import etomica.potential.PotentialMaster;
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
    public NewSpeciesPotential(PotentialMaster parent, SimulationObjects simObjects) {
        super();
        this.simObjects = simObjects;
        potentialMaster = parent;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        speciesPotentialPage = new NewSpeciesPotentialPage(simObjects, potentialMaster);
        addPage(speciesPotentialPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Potential potential = speciesPotentialPage.createPotential();
        if ( potential==null )
            return false;
	  	
        Species[] speciesArray = speciesPotentialPage.getSpecies();
        potentialMaster.addPotential(potential,speciesArray);
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final SimulationObjects simObjects;
    private final PotentialMaster potentialMaster;
    private NewSpeciesPotentialPage speciesPotentialPage;
    private boolean success = false;
}