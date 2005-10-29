package etomica.plugin.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import etomica.simulation.Simulation;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "etom". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewEtomicaDocument extends Wizard implements INewWizard {
	private NewEtomicaDocumentPage page;
	private ISelection selection;

	/**
	 * Constructor for NewEtomicaDocument.
	 */
	public NewEtomicaDocument() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new NewEtomicaDocumentPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
	  	// Create simulation based on user's choices
	  	Simulation sim = page.createSimulation();
	  	if ( sim==null )
			return false;
	  	
	  	// copy simulation to local variable
  		newsim = sim;

		// Get container options
		final String containerName = "/"+page.getContainerName();
		String tmpFileName = page.getFileName();
        int dotLoc = tmpFileName.lastIndexOf('.');
        if (dotLoc != -1) {
            String ext = tmpFileName.substring(dotLoc + 1);
            if (ext.equalsIgnoreCase("etom") == false) {
                tmpFileName = tmpFileName + ".etom";
            }
        }
        else {
            tmpFileName = tmpFileName + ".etom";
        }
        final String fileName = tmpFileName;

		// Run the creation
	  	IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish( containerName, fileName, monitor );
				} catch (CoreException e) {
					System.err.println( e.getMessage() );
					e.printStackTrace();
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			System.err.println( e.getMessage() );
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish( String containerName, String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		try
		{

			// Get the workspace root
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(containerName));
			if ( resource==null || !resource.exists() || !(resource instanceof IContainer)) {
				throwCoreException("Container \"" + containerName + "\" does not exist.");
			}

			// Create a new file in the container
			//IContainer container = (IContainer) resource;
			//IPath filepath = new Path(fileName );

			IProject project = root.getProject( containerName );
			// open if necessary
			if (project.exists() && !project.isOpen())
			      project.open(monitor);

			final IFile file = project.getFile( fileName );
			
			//final IFile file = container.getFileForLocation(filepath);
			try {
				InputStream stream = openContentStream();
				if (file.exists()) {
					file.setContents(stream, true, true, monitor);
				} else {
					file.create(stream, true, monitor);
				}
				stream.close();
				
			} catch (IOException e) {
				System.err.println( "Error creating document: " + e.getMessage());
				e.printStackTrace();
			}
			monitor.worked(1);
			monitor.setTaskName("Opening file for editing...");
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage page =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(page, file, true);
					} catch (PartInitException e) {
						System.err.println( "Error opening editor in NewEtomicaDocument.doFinish(): " + e.getMessage() );
						e.printStackTrace();
					}
				} 
			} );
		}
		catch ( Exception e )
		{
			System.err.println( "Error in NewEtomicaDocument.doFinish(): " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() 
	{
		
		ByteArrayOutputStream fos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		try
		{
		  	out = new ObjectOutputStream(fos);
		  	out.writeObject( newsim );
		  	out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

		return new ByteArrayInputStream( fos.toByteArray() );
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "etomica.plugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
	/*
	protected void createProject(IProgressMonitor monitor)
	{
	   monitor.beginTask( "Creating project...",50);
	   try
	   {
	      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	      monitor.subTask("Creating directories...");
	      IProject project = root.getProject( page.getProjectName());
	      IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
	      if(!Platform.getLocation().equals(page.getLocationPath()))
	         description.setLocation(page.getLocationPath());
	      description.setNatureIds(new String[] { PluginConstants.NATURE_ID });
	      ICommand command = description.newCommand();
	      command.setBuilderName(PluginConstants.BUILDER_ID);
	      description.setBuildSpec(new ICommand[] { command });
	      project.create(description,monitor);
	      monitor.worked(10);
	      
	      project.open(monitor);
	      project.setPersistentProperty(PluginConstants.SOURCE_PROPERTY_NAME,"src");
	      project.setPersistentProperty(PluginConstants.RULES_PROPERTY_NAME,"rules");
	      project.setPersistentProperty(PluginConstants.PUBLISH_PROPERTY_NAME,"publish");
	      project.setPersistentProperty(PluginConstants.BUILD_PROPERTY_NAME,"false");
	      monitor.worked(10);
	      IPath projectPath = project.getFullPath(),
	            srcPath = projectPath.append("src"),
	            rulesPath = projectPath.append("rules"),
	            publishPath = projectPath.append("publish");
	      IFolder srcFolder = root.getFolder(srcPath),
	              rulesFolder = root.getFolder(rulesPath),
	              publishFolder = root.getFolder(publishPath);
	      createFolderHelper(srcFolder,monitor);
	      createFolderHelper(rulesFolder,monitor);
	      createFolderHelper(publishFolder,monitor);
	      monitor.worked(10);
	      monitor.subTask(Resources.getString("eclipse.creatingfiles"));
	      IPath indexPath = srcPath.append("index.xml"),
	            defaultPath = rulesPath.append("default.xsl");
	      IFile indexFile = root.getFile(indexPath),
	            defaultFile = root.getFile(defaultPath);
	      Class clasz = getClass();
	      InputStream indexIS = clasz.getResourceAsStream("/org/ananas/xm/eclipse/resources/index.xml"),
	          defaultIS = clasz.getResourceAsStream("/org/ananas/xm/eclipse/resources/default.xsl");
	      indexFile.create(indexIS,false,new SubProgressMonitor(monitor,10));
	      defaultFile.create(defaultIS,false,new SubProgressMonitor(monitor,10));
	   }
	   catch(CoreException x)
	   {
	      reportError(x);
	   }
	   finally
	   {
	      monitor.done();
	   }
	}
	*/
	private Simulation newsim = null;
}