package etomica.plugin.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchPlugin;

import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;
import etomica.space.Space;
import etomica.util.ParameterBase;

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

public class NewSimulationWizard extends Wizard implements INewWizard {
    private NewSimulationPage page;
	private SimulationParametersPage page2;
	private ISelection selection;

	/**
	 * Constructor for NewEtomicaDocument.
	 */
	public NewSimulationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new NewSimulationPage(selection);
		addPage(page);
        page2 = new SimulationParametersPage();
        addPage(page2);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
	  	// Create simulation based on user's choices
	  	Simulation sim = createSimulation();
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
			    doFinish( containerName, fileName, monitor );
				monitor.done();
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

	protected void doFinish( String containerName, String fileName,
		IProgressMonitor monitor) {
		try
		{

			// Get the workspace root
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.findMember(new Path(containerName));
			if ( resource==null || !resource.exists() || !(resource instanceof IContainer)) {
                IStatus status =
                    new Status(IStatus.ERROR, "etomica.plugin", IStatus.OK, 
                            "Container \"" + containerName + "\" does not exist.", null);
                throw new CoreException(status);
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
					IWorkbenchPage activePage =
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					try {
						IDE.openEditor(activePage, file, true);
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

	private InputStream openContentStream() {
		
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

    public Simulation createSimulation() {
        Class simclass = page.getSimulationClass();
        if ( simclass!=null )
        {
            ParameterBase simParams = page2.getSimulationParameters();
            try {
                if (simParams != null) {
                    Constructor simConstructor = null;
                    try {
                        simConstructor = simclass.getConstructor(new Class[]{simParams.getClass()});
                    }
                    catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        return (Simulation)simConstructor.newInstance(new Object[]{simParams});
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    return (Simulation) simclass.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            catch (RuntimeException e) {
                WorkbenchPlugin.getDefault().getLog().log(
                        new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
            }                
            return null;//new Simulation();
        }
        // It's not a stock one
        Class spaceClass = page.getSpaceClass();
        Space space = null;
        try {
            space = (Space)spaceClass.getDeclaredMethod("getInstance",new Class[]{}).invoke(null,new Object[]{});
        }
        catch (IllegalAccessException e) {
            System.err.println( "Illegal access while creating Space class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        catch (NoSuchMethodException e) {
            System.err.println( "No such method exception while creating Space class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        catch (InvocationTargetException e) {
            System.err.println( "Invocation exception while creating Space class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        Class potClass = page.getPotentialMasterClass();
        PotentialMaster pot = null;
        try {
            pot = (PotentialMaster)potClass.getConstructor(new Class[]{Space.class}).newInstance(new Space[]{space});
        }
        catch (IllegalAccessException e) {
            System.err.println( "Illegal access while creating PotentialMaster class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        catch (NoSuchMethodException e) {
            System.err.println( "No such method exception while creating PotentialMaster class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        catch (InstantiationException e) {
            System.err.println( "Instantiation exception while creating PotentialMaster class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        catch (InvocationTargetException e) {
            System.err.println( "Invocation exception while creating space class: " + e.getMessage() );
            e.printStackTrace();
            return null;
        }
        return new Simulation(space, true, pot);
    }

    /**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection newSelection) {
		selection = newSelection;
	}

    public void createPageControls(Composite pageContainer) {
        // the default behavior is to create all the pages controls
        IWizardPage[] pages = getPages();
        for (int i = 0; i < pages.length; i++) {
            pages[i].createControl(pageContainer);
        }
    }
    
	private Simulation newsim = null;
}