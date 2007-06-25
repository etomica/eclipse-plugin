package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.action.ActionGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.integrator.IIntegrator;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorMC;
import etomica.integrator.IntegratorMD;
import etomica.integrator.IntegratorManagerMC;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;

/**
 * This wizard allows the user to create a new Integrator.  The user can choose
 * the Integrator class and it is added in an ActivityIntegrate to the 
 * ActionGroup if given.
 */
public class NewIntegratorWizard extends Wizard implements SimpleClassWizard {

    public NewIntegratorWizard(ActionGroup parent, SimulationObjects simObjects) {
        super();
        actionGroup = parent;
        this.simObjects = simObjects;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        integratorPage = new NewObjectSimplePage(this,simObjects,"Integrator");
        addPage(integratorPage);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(IIntegrator.class);
        selector.addCategory("Integrator",Integrator.class);
        selector.addCategory("Integrator Manager",IntegratorManagerMC.class);
        selector.addCategory("IntegratorMD",IntegratorMD.class);
        selector.addCategory("IntegratorMC",IntegratorMC.class);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        integrator = (Integrator)integratorPage.createObject();
        if (integrator==null)
            return false;
	  	
        if (actionGroup != null) {
            actionGroup.addAction(new ActivityIntegrate(integrator));
        }
        simObjects.integrators.add(integrator);
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final ActionGroup actionGroup;
    private final SimulationObjects simObjects;
    private NewObjectSimplePage integratorPage;
    private Integrator integrator;
    private boolean success = false;
}