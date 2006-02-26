package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.potential.Potential;
import etomica.simulation.Simulation;
import etomica.species.Species;

/**
 * The wizard page allows setting the potential name, 
 * class and Species to which it applies.
 */
public class NewSpeciesPotentialPage extends WizardPage {
    private Simulation simulation;
    private PotentialSpeciesSelector potentialSpeciesSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    protected boolean potentialBodyModified = false;
    protected boolean potentialHardSoftModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewSpeciesPotentialPage(Simulation sim) {
        super("wizardPage");
        simulation = sim;
        setTitle("Etomica New Potential Wizard");
        setDescription("This wizard creates a new molecular potential.");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ( (Class) element ).getName();
        }
    }

    /** Creates simulation based on user's settings 
     * 
     * @return new Simulation based on user's choices 
     */
    public Potential createPotential() {
        Potential newPotential = potentialSpeciesSelector.createPotential();
        newPotential.setName(getPotentialName());
        return newPotential;
    }
    
    public Species[] getSpecies() {
        return potentialSpeciesSelector.getSpecies();
    }
	
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    potentialSpeciesSelector = new PotentialSpeciesSelector(simulation, root_container, org.eclipse.swt.SWT.NONE );
	    
	    potentialSpeciesSelector.potentialName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            dialogChanged();
	        }
		});

        potentialSpeciesSelector.potentialBodyCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                potentialBodyModified = true;
                dialogChanged();
            }
        });

        potentialSpeciesSelector.potentialHardSoftCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                potentialHardSoftModified = true;
                dialogChanged();
            }
        });

        potentialSpeciesSelector.potentialCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        potentialSpeciesSelector.speciesCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        potentialSpeciesSelector.speciesCombo2.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        potentialSpeciesSelector.truncatedCheckbox.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            public void widgetSelected(SelectionEvent e) {
                dialogChanged();
            }
        });

        setPageComplete(false);
		updateSpecies();
		setControl(root_container);
	}

	/**
	 * Ensures that the potential class is selected and has a name,
     * and that the species to which it applies are selected.
	 */
	protected void dialogChanged() {
        if (potentialBodyModified || potentialHardSoftModified) {
            if (potentialBodyModified) {
                updateSpecies();
            }
            potentialBodyModified = false;
            potentialHardSoftModified = false;
            potentialSpeciesSelector.rebuildPotentialList();
        }
		if (!checkPotentialName()) {
            return;
        }
        if (!checkPotentialType()) {
            return;
        }
        if (!checkSpecies()) {
            return;
        }
        if (!checkTruncation()) {
            return;
        }
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkPotentialName()
	{
		String potentialName = getPotentialName();
		if (potentialName.length() == 0) {
			updateStatus("Potential name is empty");
			return false;
		}
		return true;
	}
    
    private boolean checkPotentialType()
    {
        if (potentialSpeciesSelector.getPotentialClass() == null) {
            updateStatus("You must select a Potential type");
            return false;
        }
        return true;
    }
    
    private void updateSpecies() {
        int nBody = potentialSpeciesSelector.getPotenialNumBody();
        if (nBody == 2) {
            potentialSpeciesSelector.speciesCombo2.setEnabled(true);
        }
        else {
            potentialSpeciesSelector.speciesCombo2.setEnabled(false);
        }
    }

    
    private boolean checkSpecies() {
        Species[] species = potentialSpeciesSelector.getSpecies();
        if (species[0] == null && potentialSpeciesSelector.speciesCombo.isEnabled()) {
            updateStatus("You must select the first Species");
            return false;
        }
        if (species.length > 1 && species[1] == null && potentialSpeciesSelector.speciesCombo2.isEnabled()) {
            updateStatus("You must select the second Species");
            return false;
        }
        return true;
    }
    
    // truncation is either on or off
    private boolean checkTruncation() {
        return true;
    }

    private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getPotentialName() {
		return potentialSpeciesSelector.potentialName.getText();
	}

}
