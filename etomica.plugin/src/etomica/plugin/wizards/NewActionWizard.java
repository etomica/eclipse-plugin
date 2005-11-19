package etomica.plugin.wizards;

import org.eclipse.jface.wizard.Wizard;

import etomica.action.Action;
import etomica.action.ActionGroup;
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
public class NewActionWizard extends Wizard {
    /**
     * Constructor for NewEtomicaDocument.
     */
    public NewActionWizard(ActionGroup parent) {
        super();
        actionGroup = parent;
        setNeedsProgressMonitor(false);
    }
    
    /**
     * Adding the page to the wizard.
     */
    public void addPages() {
        actionPage = new NewActionPage();
        addPage(actionPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in
     * the wizard. We will create an operation and run it
     * using wizard as execution context.
     */
    public boolean performFinish() {
        // Create simulation based on user's choices
        Action action = actionPage.createAction();
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
    private NewActionPage actionPage;
    private boolean success = false;
}