package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;

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
public class NewIntervalListenerWizard extends Wizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewIntervalListenerWizard(Integrator integrator) {
        super();
        this.integrator = integrator;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        intervalListenerPage = new NewIntervalListenerPage();
        addPage(intervalListenerPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        IntegratorIntervalListener listener = intervalListenerPage.createListener();
        if (listener==null)
            return false;
	  	
        if (integrator != null) {
            integrator.addListener(listener);
        }
        success = true;
        
        return true;
    }
    
    public boolean getSuccess() {
        return success;
    }
    
    private final Integrator integrator;
    private NewIntervalListenerPage intervalListenerPage;
    private boolean success = false;
}