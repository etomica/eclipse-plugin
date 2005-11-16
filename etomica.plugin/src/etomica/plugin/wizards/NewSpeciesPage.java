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

public class NewSpeciesPage extends WizardPage {
    private Simulation simulation;
    private SpeciesSelector speciesSelector;
	
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean speciesNameModified = false;
    private boolean speciesTypeModified = false;

    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewSpeciesPage(Simulation sim) {
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
    public Species createSpecies() {
        Species newSpecies = speciesSelector.createSpecies(simulation);
        newSpecies.setName(getSpeciesName());
        return newSpecies;
    }
	
    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
	    Composite root_container = new Composite(parent, SWT.NULL);
	    FillLayout master_layout = new FillLayout();
	    master_layout.type = SWT.VERTICAL;
	    root_container.setLayout( master_layout );

	    speciesSelector = new SpeciesSelector(root_container, org.eclipse.swt.SWT.NONE );
	    
	    speciesSelector.species_name.addModifyListener(new ModifyListener() {
	        public void modifyText(ModifyEvent e) {
	            speciesNameModified = true;
	            dialogChanged();
	        }
		});

        initialize();
		dialogChanged();
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
		if ( speciesNameModified && !checkSpeciesName() ) return;
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkSpeciesName()
	{
		String container = getSpeciesName();
		if ( ( container.length() == 0 )) {
			updateStatus("Species name is empty");
			return false;
		}
		
		return true;
	}
	

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getSpeciesName() {
		return speciesSelector.species_name.getText();
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

