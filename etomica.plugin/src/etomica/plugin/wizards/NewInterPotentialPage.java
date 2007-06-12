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
import etomica.plugin.editors.SimulationObjects;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;

/**
 * The wizard page allows setting the potential name, 
 * class and Species to which it applies.
 */
public class NewInterPotentialPage extends WizardPage {
    private final SimulationObjects simObjects;
    private final AtomType[] parentAtomTypes;
    private final PotentialGroup parentPotential;
    private PotentialInterSelector potentialInterSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    protected boolean potentialHardSoftModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewInterPotentialPage(SimulationObjects simObjects, PotentialGroup parentPotential, AtomType[] parentTypes) {
        super("wizardPage");
        this.simObjects = simObjects;
        parentAtomTypes = parentTypes;
        this.parentPotential = parentPotential;
        setTitle("Etomica New Intermolecular Potential Wizard");
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

	    potentialInterSelector = new PotentialInterSelector(simObjects, parentPotential, parentAtomTypes, 
                root_container, org.eclipse.swt.SWT.NONE );
        System.out.println("and here");
	    
	    potentialInterSelector.potentialName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
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
	protected void dialogChanged() {
        if (potentialHardSoftModified) {
            potentialHardSoftModified = false;
            //FIXME the selector should really do this itself!
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
