package etomica.plugin.editors;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.part.EditorPart;

import etomica.simulation.Simulation;
import etomica.simulation.prototypes.HSMD3D;
import etomica.util.EtomicaObjectInputStream;


public class EtomicaEditor extends EditorPart {

	public EtomicaEditor() {
		super();
	}
	public void dispose() {
		//scene.dispose();
		
		if ( this.pageSelectionListener!=null )
			getSite().getPage().removePostSelectionListener( pageSelectionListener );
		super.dispose();
	}
	
	/**
     * Save the contents of this simulation into the file pointed by the IPath object "path". 
	 * doSaveAs() will ask for a file name and set the "path" variable before calling doSave().
	 */
	public void doSave(IProgressMonitor progressMonitor) {
		// Use XML to stream simulation
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		if (progressMonitor != null)
			progressMonitor.setCanceled( false );

        IPath dir = path.removeLastSegments(1);
        IResource resource = workspace.getRoot().findMember(dir);
		IPath absPath = resource.getLocation();
		String filename = absPath.append(path.lastSegment()).toOSString();
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream( filename);
		  	out = new ObjectOutputStream(fos);
		  	out.writeObject( simulation );
		  	out.close();
            dirty_flag = false;
            firePropertyChange(PROP_DIRTY);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

	}
	

	/**
     * Asks for a file name (sets file name in "path" variable) and then makes a runnable thread to call doSave(). 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		Shell shell = getSite().getShell();

		SaveAsDialog dialog = new SaveAsDialog(shell);
		dialog.create();

		IEditorInput input = getEditorInput();

		IFile original = (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;
		if (original != null)
			dialog.setOriginalFile(original);

		if (dialog.open() == Dialog.CANCEL) 
			return;

		IPath filePath = dialog.getResult();
		if (filePath == null) 
			return;

		// Save the old path - it is subject to non-cancellation - next
		IPath old_path = path;
		path = filePath;
		
		// Create a progress dialog
		new ProgressMonitorDialog( shell );
			
		// Fire a thread to load our stuff
		try {
		    IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					doSave( monitor );
				}
		    };
		    // go go go 
		    new ProgressMonitorDialog( shell ).run(true, true, op);
		 } catch (InvocationTargetException e) {
		 	// Handle exception
		    path=old_path;
		 } catch (InterruptedException e) {
		 	// Handle cancellation
		    path=old_path;
		 }
	}
	
    protected Exception readFromFile( String filename ) {
        FileInputStream fis = null;
        EtomicaObjectInputStream in = null;
        try
        {
            fis = new FileInputStream(filename);
            in = new EtomicaObjectInputStream(fis);
            simulation = (etomica.simulation.Simulation) in.readObject();
            in.finalizeRead();
            in.close();
        }
		catch( Exception ex ) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Could not read simulation from file " + filename, ex));
			simulation = null;
            return ex;
		}
        if (simulation != null) {
            simulation.clearDataStreams();
            new DataStreamRegister(simulation).registerDataStreams(simulation);
        }
        return null;
	}
    
	public boolean isDirty() {
		return dirty_flag;
	}
    
    public void markDirty() {
        if (!dirty_flag) {
            dirty_flag = true;
            firePropertyChange(PROP_DIRTY);
        }
    }

	public boolean isSaveAsAllowed() {
		return !simulation.getController().isActive();
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite( site );
		this.setInput( input );
		
		// Create a new simulation
		//simulation = new Simulation();

		IFile original = (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;
		if (original == null)
			return;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		path = original.getFullPath();
		IResource resource = root.findMember(path);
		if (resource == null) 
			return;
		IPath location = resource.getLocation();
		Exception ex = readFromFile(location.toOSString());
        if (ex != null) {
            throw new PartInitException("Could not read file "+location.toOSString(),ex);
        }
        
		// Update inner panel 
		if ( inner_panel != null )
		{
			inner_panel.setSimulation( simulation );
            getSite().setSelectionProvider(inner_panel.getViewer());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
	    if (simulation != null) {
    		inner_panel = new EtomicaEditorInnerPanel(parent, this, SWT.NONE);
    		inner_panel.setSimulation( simulation );
            getSite().setSelectionProvider(inner_panel.getViewer());
        }
	}
	
	
	public Simulation getSimulation()
	{
		return simulation;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	private EtomicaEditorInnerPanel inner_panel;
	private IPath path = null;
	private Simulation simulation = null;
	private ISelectionListener pageSelectionListener;
	private boolean dirty_flag = false;
	//private java.util.HashMap property_bag_list = new HashMap(8);
	
	
	 public static final void main( String[] args )
	    {
	    	String filename = "test.bin";
			
			try
			{
				FileOutputStream fos = null;
				ObjectOutputStream out = null;
				HSMD3D simulation = new HSMD3D();
				fos = new FileOutputStream( filename);
			  	out = new ObjectOutputStream(fos);
			  	out.writeObject( simulation );
			  	out.close();
			  	fos.close();
			  	System.out.println( "Serialization of class HSMD3D succeeded.");
			}
			catch(IOException ex)
			{
				System.err.println( "Exception:" + ex.getMessage() );
				ex.printStackTrace();
			}
			
			// Serialize back
			try
			{
				FileInputStream fis = null;
				ObjectInputStream in = null;
			  fis = new FileInputStream(filename);
			  in = new ObjectInputStream(fis);
			  Simulation simulation = (etomica.simulation.Simulation) in.readObject();
			  in.close();
			  fis.close();
			}
			catch( Exception ex ) {
				System.err.println( "Could not read simulation from file " + filename + ". Cause: " + ex.getLocalizedMessage() );
			}
	    }
}
