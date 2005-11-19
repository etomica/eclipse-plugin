package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.action.Action;
import etomica.action.ActionGroup;
import etomica.potential.Potential;
import etomica.species.Species;

/**
 * The wizard page selecting a new Action.
 */
public class NewActionPage extends WizardPage {
    private ActionSelector actionSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean actionNameModified = false;
    private boolean actionTypeModified = false;
    private boolean actionActivityModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewActionPage() {
        super("wizardPage");
        setTitle("Etomica New Action Wizard");
        setDescription("This wizard creates a new Action.");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ( (Class) element ).getName();
        }
    }

    /**
     * Creates action based on user's settings 
     */
    public Action createAction() {
        Action newAction = actionSelector.createAction();
//        newAction.setLabel(getActionName());
        return newAction;
    }
    
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    actionSelector = new ActionSelector(root_container, org.eclipse.swt.SWT.NONE );
	    
	    actionSelector.actionName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            actionNameModified = true;
	            dialogChanged();
	        }
		});

        actionSelector.actionTypeCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                actionActivityModified = true;
                dialogChanged();
            }
        });

        actionSelector.actionClassCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                actionTypeModified = true;
                dialogChanged();
            }
        });

        setPageComplete(false);
		setControl(root_container);
	}

	/**
	 * Ensures that the potential class is selected and has a name,
     * and that the species to which it applies are selected.
	 */
	private void dialogChanged() {
        if (actionActivityModified) {
            actionActivityModified = false;
            actionSelector.rebuildActionList();
        }
		if (!checkActionName()) {
            return;
        }
        if (!checkActionType()) {
            return;
        }
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkActionName()
	{
		String actionName = getActionName();
		if (actionName.length() == 0) {
			updateStatus("Action name is empty");
			return false;
		}
		return true;
	}
    
    private boolean checkActionType()
    {
        if (actionSelector.getActionClass() == null) {
            updateStatus("You must select a Action type");
            return false;
        }
        return true;
    }
    
    private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getActionName() {
		return actionSelector.actionName.getText();
	}

}
