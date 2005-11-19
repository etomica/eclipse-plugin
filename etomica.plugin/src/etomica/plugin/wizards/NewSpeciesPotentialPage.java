package etomica.plugin.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import etomica.potential.Potential;
import etomica.simulation.Simulation;
import etomica.species.Species;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (etom).
 */

public class NewSpeciesPotentialPage extends WizardPage {
    private Simulation simulation;
    private PotentialSpeciesSelector potentialSpeciesSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean potentialNameModified = false;
    private boolean potentialTypeModified = false;
    private boolean potentialBodyModified = false;
    private boolean potentialHardSoftModified = false;
    private boolean speciesModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewSpeciesPotentialPage(Simulation sim) {
        super("wizardPage");
        simulation = sim;
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
	            potentialNameModified = true;
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
                potentialTypeModified = true;
                dialogChanged();
            }
        });

        potentialSpeciesSelector.speciesCombo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                speciesModified = true;
                dialogChanged();
            }
        });

        potentialSpeciesSelector.speciesCombo2.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                speciesModified = true;
                dialogChanged();
            }
        });

        initialize();
		updateSpecies();
		setControl(root_container);
	}

	/**
	 * Tests if the current workbench selection is a suitable
	 * container to use.
	 */
	
	private void initialize() {
        setPageComplete(false);
	}
	
	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
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
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkPotentialName()
	{
		String container = getPotentialName();
		if ( ( container.length() == 0 )) {
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

    private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getPotentialName() {
		return potentialSpeciesSelector.potentialName.getText();
	}

}


/*
/////// FILE ASSIGN
Composite container = new Composite(root_container, SWT.NULL);
GridLayout layout = new GridLayout();
container.setLayout(layout);
layout.numColumns = 3;
layout.verticalSpacing = 9;
Label label = new Label(container, SWT.NULL);
label.setText("&Container:");

containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
GridData gd = new GridData(GridData.FILL_HORIZONTAL);
containerText.setLayoutData(gd);
containerText.addModifyListener(new ModifyListener() {
	public void modifyText(ModifyEvent e) {
		dialogChanged();
	}
});

Button button = new Button(container, SWT.PUSH);
button.setText("Browse...");
button.addSelectionListener(new SelectionAdapter() {
	public void widgetSelected(SelectionEvent e) {
		handleBrowse();
	}
});
label = new Label(container, SWT.NULL);
label.setText("&File name:");

fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
gd = new GridData(GridData.FILL_HORIZONTAL);
fileText.setLayoutData(gd);
fileText.addModifyListener(new ModifyListener() {
	public void modifyText(ModifyEvent e) {
		dialogChanged();
	}
});

////////// SPACE SELECTION
container = new Composite(root_container, SWT.NULL);
layout = new GridLayout();
container.setLayout(layout);
layout.numColumns = 1;
layout.verticalSpacing = 9;

// List of spaces
label = new Label(container, SWT.NULL);
label.setText("&Space:");
ListViewer spacelist = new ListViewer( container );
Collection spaces_from_registry = Registry.queryWhoExtends( etomica.Space.class );

spacelist.setContentProvider( 
		new IStructuredContentProvider() 
		{
			public Object[] getElements( Object input ) {
				return ((Collection)input).toArray();
			}

			public void dispose() {	}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		}
	);
spacelist.setLabelProvider( new ClassLabelProvider() );
spacelist.setInput( spaces_from_registry );


Collection pmaster_from_registry = Registry.queryWhoExtends( etomica.PotentialMaster.class );
*/
