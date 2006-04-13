package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.atom.AtomType;
import etomica.nbr.CriterionMolecular;
import etomica.nbr.CriterionSimple;
import etomica.nbr.list.PotentialMasterList;
import etomica.potential.Potential;
import etomica.potential.Potential2;
import etomica.potential.PotentialGroup;
import etomica.simulation.Simulation;

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
public class NewInterPotential extends Wizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewInterPotential(PotentialGroup parent, Simulation sim, AtomType[] parentTypes) {
        super();
        simulation = sim;
        potentialGroup = parent;
        parentAtomTypes = parentTypes;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        interPotentialPage = new NewInterPotentialPage(simulation, parentAtomTypes);
        addPage(interPotentialPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Potential potential = interPotentialPage.createPotential();
        if ( potential==null )
            return false;
        
        AtomType[] types = interPotentialPage.getAtomTypes();
	  	
        if (simulation.potentialMaster instanceof PotentialMasterList) {
            if (potential instanceof Potential2) {
                CriterionSimple nbrCriterion = new CriterionSimple(simulation,potential.getRange(),
                        ((PotentialMasterList)simulation.potentialMaster).getRange());
                CriterionMolecular criterion = new CriterionMolecular(nbrCriterion);
                criterion.setIntraMolecular(false);
                ((Potential2)potential).setCriterion(criterion);
            }
        }
        
        potentialGroup.addPotential(potential,types);
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Simulation simulation;
    private final PotentialGroup potentialGroup;
    private NewInterPotentialPage interPotentialPage;
    private boolean success = false;
    private final AtomType[] parentAtomTypes;
}