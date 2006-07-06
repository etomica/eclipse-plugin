package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.action.ActionGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.integrator.Integrator;
import etomica.integrator.IntegratorMC;
import etomica.integrator.IntegratorMD;
import etomica.integrator.IntegratorManagerMC;
import etomica.nbr.list.PotentialMasterList;
import etomica.nbr.site.PotentialMasterSite;
import etomica.plugin.wizards.NewObjectSimplePage.SimpleClassWizard;
import etomica.simulation.Simulation;

/**
 * This wizard allows the user to create a new Integrator.  The user can choose
 * the Integrator class and it is added in an ActivityIntegrate to the 
 * ActionGroup if given.
 */
public class NewIntegratorWizard extends Wizard implements SimpleClassWizard {

    public NewIntegratorWizard(ActionGroup parent, Simulation sim) {
        super();
        actionGroup = parent;
        simulation = sim;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        integratorPage = new NewObjectSimplePage(this,simulation,"Integrator");
        addPage(integratorPage);
    }
    
    public void fixupSelector(SimpleClassSelector selector) {
        selector.setBaseClass(Integrator.class);
        selector.addCategory("Integrator",Integrator.class);
        selector.addCategory("Integrator Manager",IntegratorManagerMC.class);
        if (!(simulation.potentialMaster instanceof PotentialMasterSite)) {
            selector.addCategory("IntegratorMD",IntegratorMD.class);
        }
        else {
            selector.addExcludedClass(IntegratorMD.class);
        }
        if (!(simulation.potentialMaster instanceof PotentialMasterList)) {
            selector.addCategory("IntegratorMC",IntegratorMC.class);
        }
        else {
            selector.addExcludedClass(IntegratorMC.class);
        }
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
	  	
        if (integrator.getPotential() instanceof PotentialMasterList) {
            integrator.addListener(((PotentialMasterList)integrator.getPotential()).getNeighborManager());
        }
        if (actionGroup != null) {
            actionGroup.addAction(new ActivityIntegrate(simulation,integrator));
        }
        simulation.register(integrator);
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final ActionGroup actionGroup;
    private final Simulation simulation;
    private NewObjectSimplePage integratorPage;
    private Integrator integrator;
    private boolean success = false;
}