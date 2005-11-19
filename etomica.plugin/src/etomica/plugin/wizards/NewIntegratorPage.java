package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.integrator.Integrator;
import etomica.simulation.Simulation;

/**
 * The wizard page selecting a new Action.
 */
public class NewIntegratorPage extends WizardPage {
    private IntegratorSelector integratorSelector;
    private final Simulation simulation;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean integratorNameModified = false;
    private boolean integratorTypeModified = false;
    private boolean integratorClassModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewIntegratorPage(Simulation sim) {
        super("wizardPage");
        simulation = sim;
        setTitle("Etomica New Integrator Wizard");
        setDescription("This wizard creates a new Integrator.");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ( (Class) element ).getName();
        }
    }

    /**
     * Creates action based on user's settings 
     */
    public Integrator createIntegrator() {
        Integrator newIntegrator = integratorSelector.createIntegrator(simulation);
        newIntegrator.setName(getIntegratorName());
        return newIntegrator;
    }
    
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    integratorSelector = new IntegratorSelector(root_container, org.eclipse.swt.SWT.NONE );
	    
	    integratorSelector.integratorName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            integratorNameModified = true;
	            dialogChanged();
	        }
		});

        integratorSelector.integratorTypeCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                integratorTypeModified = true;
                dialogChanged();
            }
        });

        integratorSelector.integratorClassCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
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
        if (integratorTypeModified) {
            integratorTypeModified = false;
            integratorSelector.rebuildIntegratorList();
        }
		if (!checkIntegratorName()) {
            return;
        }
        if (!checkIntegratorType()) {
            return;
        }
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkIntegratorName()
	{
		String integratorName = getIntegratorName();
		if (integratorName.length() == 0) {
			updateStatus("Integrator name is empty");
			return false;
		}
		return true;
	}
    
    private boolean checkIntegratorType()
    {
        if (integratorSelector.getIntegratorClass() == null) {
            updateStatus("You must select a Integrator type");
            return false;
        }
        return true;
    }
    
    private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getIntegratorName() {
		return integratorSelector.integratorName.getText();
	}

}
