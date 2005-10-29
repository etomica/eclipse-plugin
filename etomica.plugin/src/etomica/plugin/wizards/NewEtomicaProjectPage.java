package etomica.plugin.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (etom).
 */

public class NewEtomicaProjectPage extends WizardPage {
    private ISelection selection;
    private ProjectNameSelector sds;
    
    // These are to follow eclipse UI guidelines - not to present an error message while the user 
    //   did not input anything yet
    private boolean containerNameModified = false;
    /**
     * Constructor for SampleNewWizardPage.
     * @param pageName
     */
    public NewEtomicaProjectPage(ISelection selection) {
        super("wizardPage");
        setTitle("Etomica New File Wizard");
        setDescription("This wizard creates a new Etomica project.");
        this.selection = selection;
    }

	public class ClassLabelProvider extends LabelProvider {
		public String getText(Object element) {
			return ( (Class) element ).getName();
		}
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite root_container = new Composite(parent, SWT.NULL);
		FillLayout master_layout = new FillLayout();
		master_layout.type = SWT.VERTICAL;
		root_container.setLayout( master_layout );

		sds = new ProjectNameSelector( root_container, org.eclipse.swt.SWT.NONE );
		
		sds.container_name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				containerNameModified = true;
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
		if (selection!=null && selection.isEmpty()==false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection)selection;
			if (ssel.size()>1) return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer)obj;
				else
					container = ((IResource)obj).getParent();
				sds.container_name.setText(container.getFullPath().toString());
			}
		}
	}
	
	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
		if ( containerNameModified && !checkContainerName() ) return;
		// Everything went ok, just clean up the error bar
		updateStatus(null);
	}
	
	private boolean checkContainerName()
	{
		String container = getContainerName();
		if ( ( container.length() == 0 )) {
			updateStatus("File container is empty");
			return false;
		}
		// Find out if this container is valid
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(container));
		boolean container_exists = true;
		if ( resource==null || !resource.exists() || !(resource instanceof IContainer)) 
			container_exists = false;
		
		if ( container_exists ){
			updateStatus("File container already exists");
			return false;
		}
		return true;
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return sds.container_name.getText();
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

