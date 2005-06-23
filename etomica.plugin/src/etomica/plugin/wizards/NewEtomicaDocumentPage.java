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
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

import etomica.Simulation;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (etom).
 */

public class NewEtomicaDocumentPage extends WizardPage {
	private ISelection selection;
	private SpaceDimensionSelector sds;
	/**
	 * Constructor for SampleNewWizardPage.
	 * @param pageName
	 */
	public NewEtomicaDocumentPage(ISelection selection) {
		super("wizardPage");
		setTitle("Etomica New File Wizard");
		setDescription("This wizard creates a new Etomica document.");
		this.selection = selection;
		
		/*
		 * 
		URL url = null;
		try {
		url = new URL(MyPlugin.getInstance().getDescriptor().getInstallURL(),
		              "icons/sample.gif");
		    } catch (MalformedURLException e) {
		    }
		this.setImageDescriptor( ImageDescriptor.createFromURL(url) );
		   */ 
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

		sds = new SpaceDimensionSelector( root_container, org.eclipse.swt.SWT.EMBEDDED );
		
		sds.container_name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		sds.browse_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		sds.file_name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		
		sds.sim_types.addSelectionListener( new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				int index = sds.sim_types.getSelectionIndex();
				boolean custom = (index==0);
				sds.space_list.setEnabled( custom );
				sds.master_potential_list.setEnabled( custom );
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}} );
		
		
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
		sds.file_name.setText("newfile.etom");
	}
	
	public String getProjectName()
	{
		return sds.file_name.getText();
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
		 
		
		ResourceSelectionDialog dialog =
			new ResourceSelectionDialog(
				shell,
				myroot,
				"Select new file container");
		if (dialog.open() == ResourceSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				sds.container_name.setText(((Path)result[0]).toOSString());
			}
		}
		
		
		/*ContainerSelectionDialog dialog = new ContainerSelectionDialog
			( shell, myroot, true, "Select new file container");
		if ( dialog.open()==ContainerSelectionDialog.OK )
		{
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				sds.container_name.setText(((Path)result[0]).toOSString());
		}
		*/
	}
			
	
	
	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		String container = getContainerName();
		String fileName = getFileName();

		if (container.length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("etom") == false) {
				updateStatus("File extension must be \"etom\"");
				return;
			}
		}
		updateStatus(null);
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

