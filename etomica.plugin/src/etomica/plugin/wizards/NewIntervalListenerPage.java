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
import etomica.action.Activity;
import etomica.action.AtomAction;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntervalActionAdapter;

/**
 * The wizard page selecting a new Action.
 */
public class NewIntervalListenerPage extends WizardPage {
    private ActionSelector actionSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean actionNameModified = false;
    private boolean actionClassModified = false;
    private boolean actionTypeModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewIntervalListenerPage() {
        super("wizardPage");
        setTitle("Etomica New Interval Listener Wizard");
        setDescription("This wizard creates a new Interval Listener.");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ( (Class) element ).getName();
        }
    }

    /**
     * Creates action based on user's settings 
     */
    public IntegratorIntervalListener createListener() {
        Action newAction = actionSelector.createAction();
        if (newAction == null) {
            return null;
        }
        IntegratorIntervalListener listener = new IntervalActionAdapter(newAction);
        return listener;
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
        actionSelector.setExcludedClasses(new Class[]{Activity.class,AtomAction.class});
	    
	    actionSelector.actionName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            actionNameModified = true;
	            dialogChanged();
	        }
		});

        actionSelector.actionTypeCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                actionTypeModified = true;
                dialogChanged();
            }
        });

        actionSelector.actionClassCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                actionClassModified = true;
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
        if (actionTypeModified) {
            actionTypeModified = false;
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
