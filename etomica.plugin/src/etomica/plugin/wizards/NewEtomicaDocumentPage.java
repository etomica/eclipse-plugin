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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import etomica.simulation.Simulation;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (etom).
 */

public class NewEtomicaDocumentPage extends WizardPage {
	private ISelection selection;
	private SpaceDimensionSelector sds;
	
	// These are to follow eclipse UI guidelines - not to present an error message while the user 
	//   did not input anything yet
	private boolean containerNameModified = false;
	private boolean fileNameModified = false;
	private boolean simTypeModified = false;
	private boolean spaceTypeModified = false;
	private boolean pMasterTypeModified = false;
	/**
	 * Constructor for SampleNewWizardPage.
	 * @param pageName
	 */
	public NewEtomicaDocumentPage(ISelection selection) {
		super("wizardPage");
		setTitle("Etomica New File Wizard");
		setDescription("This wizard creates a new Etomica document.");
		this.selection = selection;
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
	public Simulation createSimulation()
	{
		return sds.createSimulation();
	}
	

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite root_container = new Composite(parent, SWT.NULL);
		FillLayout master_layout = new FillLayout();
		master_layout.type = SWT.VERTICAL;
		root_container.setLayout( master_layout );

		sds = new SpaceDimensionSelector( root_container, org.eclipse.swt.SWT.NONE );
		
		sds.container_name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				containerNameModified = true;
				dialogChanged();
			}
		});
		sds.browse_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
				fileNameModified = true;
				dialogChanged();
			}
		});
		sds.file_name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				fileNameModified = true;
				dialogChanged();
			}
		});
		
		sds.sim_types.addSelectionListener( new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				simTypeModified = true;
				updateCustomSimulationControls();
				dialogChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				updateCustomSimulationControls();
				
			}} );
		
		sds.space_list.addSelectionListener( new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				spaceTypeModified = true;
				dialogChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				updateCustomSimulationControls();
				
			}} );
		
		sds.master_potential_list.addSelectionListener( new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				pMasterTypeModified = true;
				dialogChanged();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				updateCustomSimulationControls();
				
			}} );
		
		initialize();
		dialogChanged();
		updateCustomSimulationControls();
		setControl(root_container);
	}

	private void updateCustomSimulationControls()
	{
		// TODO Auto-generated method stub
		int index = sds.sim_types.getSelectionIndex();
		boolean custom = false;
		if ( sds.sim_types.getItem(index).compareToIgnoreCase( "Custom...")==0 )
			custom = true;
		sds.space_list.setEnabled( custom );
		sds.master_potential_list.setEnabled( custom );
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
				sds.container_name.setText(container.getFullPath().toString().replaceFirst("/",""));
			}
		}
        if (sds.container_name.getText().equals("")) {
            setPageComplete(false);
        }
		sds.file_name.setText("newsim");
	}
	
	/**
	 * Uses the standard container selection dialog to
	 * choose the new value for the container field.
	 */

	private void handleBrowse() {
		
		IWorkspaceRoot myroot = ResourcesPlugin.getWorkspace().getRoot();
		Shell shell = getShell();
		
		
		/*
		FileDialog dialog = new FileDialog( shell );
		
		String filename = dialog.open();
		if ( filename.length()>0 )
		{
			containerText.setText( filename );
		}
		*/
		 
		/*
		ResourceSelectionDialog dialog =
			new ResourceSelectionDialog(
				shell,
				myroot,
				"Select new file container");
		if (dialog.open() == ResourceSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			int nfiles = result.length;
			if (nfiles >= 1) {
				sds.container_name.setText(((Path)result[0]).toOSString());
			}
			else
			{
				// leave it blank
				sds.container_name.setText( "" ); 
			}
		}
		*/
		
		ContainerSelectionDialog dialog = new ContainerSelectionDialog
			( shell, myroot, true, "Select new file container");
		if ( dialog.open()==ContainerSelectionDialog.OK )
		{
			Object[] result = dialog.getResult();
			if (result.length == 1) 
				sds.container_name.setText(((Path)result[0]).toOSString().replaceFirst("/",""));
		}
	}
	
	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		if ( (containerNameModified || fileNameModified) && (!checkContainerName() || !checkFileName())) return;
		if ( (simTypeModified || spaceTypeModified || pMasterTypeModified) && !checkCustomControls() ) return;
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
		IResource resource = root.findMember(new Path("/"+container));
		if ( resource==null || !resource.exists() || !(resource instanceof IContainer)) { 
			updateStatus("File container does not exist");
			return false;
		}
		
		return true;
	}
	
	private boolean checkCustomControls()
	{
		// TODO Auto-generated method stub
		int index = sds.sim_types.getSelectionIndex();
		boolean custom = false;
		if ( sds.sim_types.getItem(index).compareToIgnoreCase( "Custom...")==0 )
			custom = true;
		
		if ( custom && (simTypeModified || spaceTypeModified || pMasterTypeModified))
		{
			// If there's nothing selected in the space/master potential boxes, signalize with an error
			if ( sds.space_list.getSelectionIndex()==-1 )
			{
				updateStatus( "A space type is required for custom simulations but none selected");
				return false;
			}
			if ( sds.master_potential_list.getSelectionIndex()==-1 )
			{
				updateStatus( "A master potential is required for custom simulations but none selected");
				return false;
			}
		}
		return true;
	}
	private boolean checkFileName()
	{
		String fileName = getFileName();
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
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
	public String getFileName() {
		return sds.file_name.getText();
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

