package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.action.Activity;
import etomica.action.AtomAction;
import etomica.action.IntegratorAction;
import etomica.action.PhaseAction;
import etomica.action.SimulationAction;
import etomica.action.activity.ActivityIntegrate;
import etomica.action.activity.Controller;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;

/**
 * This wizard allows the user to create a new Action.  The user can choose
 * the Action class and it is added to the ActionGroup if given.
 */
public class NewActionWizard extends Wizard implements SimpleClassWizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewActionWizard(ActionGroup parent, SimulationObjects simObjects) {
        super();
        actionGroup = parent;
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        actionPage = new NewObjectSimplePage(this,simObjects,"Action");
        addPage(actionPage);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(Action.class);
        selector.setExcludedClasses(new Class[]{AtomAction.class,Controller.class,ActivityIntegrate.class});
        selector.addCategory("Action",Action.class);
        selector.addCategory("Activity",Activity.class);
        selector.addCategory("Phase Action",PhaseAction.class);
        selector.addCategory("Integrator Action",IntegratorAction.class);
        selector.addCategory("Simulation Action",SimulationAction.class);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Action action = (Action)actionPage.createObject();
        if (action==null)
            return false;
	  	
        if (actionGroup != null) {
            actionGroup.addAction(action);
        }
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final ActionGroup actionGroup;
    private final SimulationObjects simObjects;
    private NewObjectSimplePage actionPage;
    private boolean success = false;
}