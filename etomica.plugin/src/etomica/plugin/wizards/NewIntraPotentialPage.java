package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.atom.iterator.AtomsetIteratorBasisDependent;
import etomica.plugin.editors.SimulationObjects;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;

/**
 * The wizard page allows setting the potential name, 
 * class and Species to which it applies.
 */
public class NewIntraPotentialPage extends WizardPage {
    private SimulationObjects simObjects;
    private PotentialGroup parentPotential;
    private PotentialIntraSelector potentialIntraSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    protected boolean potentialBodyModified = false;
    protected boolean potentialHardSoftModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewIntraPotentialPage(SimulationObjects simObjects, PotentialGroup parentPotential) {
        super("wizardPage");
        this.simObjects = simObjects;
        this.parentPotential = parentPotential;
        setTitle("Etomica New Species Wizard");
        setDescription("This wizard creates a new Species.");
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
        Potential newPotential = potentialIntraSelector.createPotential();
        return newPotential;
    }
    
    public AtomsetIteratorBasisDependent createIterator() {
        return potentialIntraSelector.createIterator();
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

	    potentialIntraSelector = new PotentialIntraSelector(simObjects, parentPotential, root_container, org.eclipse.swt.SWT.NONE );
        System.out.println("and here");
	    
	    potentialIntraSelector.potentialName.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            dialogChanged();
	        }
		});

        potentialIntraSelector.potentialBodyCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                potentialBodyModified = true;
                dialogChanged();
            }
        });

        potentialIntraSelector.potentialHardSoftCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                potentialHardSoftModified = true;
                dialogChanged();
            }
        });

        potentialIntraSelector.potentialCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        potentialIntraSelector.iteratorCombo.addModifyListener(new ModifyListener() {
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
        if (potentialBodyModified || potentialHardSoftModified) {
            //FIXME the selector should really do this itself
            if (potentialBodyModified) {
                potentialIntraSelector.rebuildIteratorList();
            }
            potentialBodyModified = false;
            potentialHardSoftModified = false;
            potentialIntraSelector.rebuildPotentialList();
        }
		if (!checkPotentialName()) {
            return;
        }
        if (!checkPotentialType()) {
            return;
        }
        if (!checkIterator()) {
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
        if (potentialIntraSelector.getPotentialClass() == null) {
            updateStatus("You must select a Potential type");
            return false;
        }
        return true;
    }
    
    private boolean checkIterator() {
        if (potentialIntraSelector.getIteratorSelection() == null) {
            updateStatus("You must select an Iterator type");
            return false;
        }
        return true;
    }

    private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getPotentialName() {
		return potentialIntraSelector.potentialName.getText();
	}

}
