package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.atom.AtomType;
import etomica.potential.Potential;
import etomica.simulation.Simulation;

/**
 * The wizard page allows setting the potential name, 
 * class and Species to which it applies.
 */
public class NewInterPotentialPage extends WizardPage {
    private final Simulation simulation;
    private final AtomType[] parentAtomTypes;
    private PotentialInterSelector potentialInterSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean potentialNameModified = false;
    private boolean potentialTypeModified = false;
    private boolean potentialBodyModified = false;
    private boolean potentialHardSoftModified = false;
    private boolean iteratorModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewInterPotentialPage(Simulation sim, AtomType[] parentTypes) {
        super("wizardPage");
        simulation = sim;
        parentAtomTypes = parentTypes;
        setTitle("Etomica New Species Wizard");
        setDescription("This wizard creates a new Species.");
    }

    public class ClassLabelProvider extends LabelProvider {
        public String getText(Object element) {
            return ( (Class) element ).getName();
        }
    }

    /**
     * Creates simulation based on user's settings 
     * @return new Simulation based on user's choices 
     */
    public Potential createPotential() {
        Potential newPotential = potentialInterSelector.createPotential();
        newPotential.setName(getPotentialName());
        return newPotential;
    }
    
    public AtomType[] getAtomTypes() {
        return potentialInterSelector.getAtomTypes();
    }
	
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        System.out.println("and now here");

	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    potentialInterSelector = new PotentialInterSelector(simulation, parentAtomTypes, 
                root_container, org.eclipse.swt.SWT.NONE );
        System.out.println("and here");
	    
	    potentialInterSelector.potentialName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            potentialNameModified = true;
	            dialogChanged();
	        }
		});

        potentialInterSelector.potentialHardSoftCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                potentialHardSoftModified = true;
                dialogChanged();
            }
        });

        potentialInterSelector.potentialCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                potentialTypeModified = true;
                dialogChanged();
            }
        });

        potentialInterSelector.type1Combo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        potentialInterSelector.type2Combo.addModifyListener(new ModifyListener() {
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
        if (potentialHardSoftModified) {
            potentialHardSoftModified = false;
            potentialInterSelector.rebuildPotentialList();
        }
		if (!checkPotentialName()) {
            return;
        }
        if (!checkPotentialType()) {
            return;
        }
        if (!checkAtomTypes()) {
            return;
        }
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkPotentialName() {
		String potentialName = getPotentialName();
		if (potentialName.length() == 0) {
			updateStatus("Potential name is empty");
			return false;
		}
		return true;
	}
    
    private boolean checkPotentialType()
    {
        if (potentialInterSelector.getPotentialClass() == null) {
            updateStatus("You must select a Potential type");
            return false;
        }
        return true;
    }
    
    private boolean checkAtomTypes() {
        if (potentialInterSelector.getAtomTypes() == null) {
            updateStatus("You must select both Atom types");
            return false;
        }
        return true;
    }

    private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getPotentialName() {
		return potentialInterSelector.potentialName.getText();
	}

}
