package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.atom.iterator.AtomsetIteratorBasisDependent;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
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
public class NewIntraPotential extends Wizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewIntraPotential(PotentialGroup parent, Simulation sim) {
        super();
        simulation = sim;
        potentialGroup = parent;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        intraPotentialPage = new NewIntraPotentialPage(simulation);
        System.out.println("and even here");
        addPage(intraPotentialPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Potential potential = intraPotentialPage.createPotential();
        if ( potential==null )
            return false;
        
        AtomsetIteratorBasisDependent iterator = intraPotentialPage.createIterator();
	  	
        potentialGroup.addPotential(potential,iterator);
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Simulation simulation;
    private final PotentialGroup potentialGroup;
    private NewIntraPotentialPage intraPotentialPage;
    private boolean success = false;
}