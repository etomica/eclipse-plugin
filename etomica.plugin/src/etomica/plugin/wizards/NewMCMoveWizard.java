package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.integrator.mcmove.MCMove;
import etomica.integrator.mcmove.MCMoveManager;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;

/**
 * This wizard allows the user to create a new Species.  The user can choose
 * the Species class and it is added to the Simulation.
 */
public class NewMCMoveWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewMCMoveWizard(MCMoveManager manager, SimulationObjects simObjects) {
        super();
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
        moveManager = manager;
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        page = new NewObjectSimplePage(this, simObjects, "MC Move");
        addPage(page);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(MCMove.class);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        MCMove move = (MCMove)page.createObject();
        if (move==null)
            return false;
	  	
        if (moveManager != null) {
            moveManager.addMCMove(move);
        }
        success = true;
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final SimulationObjects simObjects;
    private final MCMoveManager moveManager;
    private NewObjectSimplePage page;
    private boolean success = false;
}