package etomica.plugin.editors;

import java.io.IOException;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.properties.PropertySheetEntry;

import etomica.plugin.editors.eclipse.EtomicaPropertyViewer;
import etomica.plugin.wrappers.ParamWrapper;
import etomica.util.ParamBase;
import etomica.util.ReadParams;
import etomica.util.WriteParams;


public class EtomicaInputEditor extends EditorPart {

    public EtomicaInputEditor() {
        super();
    }
    
    /**
     * Save the contents of this simulation into the file pointed by the IPath object "path". 
     * doSaveAs() will ask for a file name and set the "path" variable before calling doSave().
     */
    public void doSave(IProgressMonitor progressMonitor) {
        
        doSave(path, progressMonitor);

        dirty_flag = false;
        firePropertyChange(PROP_DIRTY);
    }
    
    protected void doSave(IPath savePath, IProgressMonitor progressMonitor) {
        if (progressMonitor != null)
            progressMonitor.setCanceled( false );
        
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        IPath dir = savePath.removeLastSegments(1);
        IResource resource = workspace.getRoot().findMember(dir);
        IPath absPath = resource.getLocation();
        String filename = absPath.append(savePath.lastSegment()).toOSString();
        WriteParams paramWriter = new WriteParams(filename,paramObject);
        
        try {
            paramWriter.writeParameters(); 
            // actually write the file
        }
        catch (IOException ex) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Unable to write input file " + filename, ex));
            return;
        }
        catch (RuntimeException ex) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Unable to write input file " + filename, ex));
            return;
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

		if (dialog.open() == Window.CANCEL) 
			return;

        final IPath filePath = dialog.getResult();
        if (filePath == null) 
            return;

        // Save the old path - it is subject to non-cancellation - next
        
        // Create a progress dialog
        new ProgressMonitorDialog( shell );
            
        // Fire a thread to load our stuff
        try {
            IRunnableWithProgress op = new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    doSave(filePath, monitor);
                }
            };
            // go go go 
            new ProgressMonitorDialog( shell ).run(true, true, op);
         } catch (InvocationTargetException e) {
             // Handle exception
         } catch (InterruptedException e) {
             // Handle cancellation
         }
         
    }
    
    protected void readFromFile( String filename ) {
        ReadParams paramReader = new ReadParams(filename);
        // paramReader reads the class name, instantiates the object and reads the values from the file!
        boolean success = false;
        try {
            success = paramReader.readParameters();
        }
        catch (RuntimeException ex) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Unable to read input file " + filename, ex));
        }
        if (!success) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.WARNING, PlatformUI.PLUGIN_ID, 0, "Problems reading input file " + filename, paramReader.getFirstException()));
        }
        paramObject = paramReader.getParameterWrapper();
        if (paramObject == null) {
            WorkbenchPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error read input file " + filename, new ClassNotFoundException()));
        }
    }
    
    public boolean isDirty() {
        return dirty_flag && !isBusy;
    }
    
    /**
     * Marks the editor's simulation as "dirty", meaning it's somewhat 
     * different than it used to be
     */
    public void markDirty() {
        if (!dirty_flag) {
            dirty_flag = true;
            firePropertyChange(PROP_DIRTY);
        }
    }
    
    /**
     * Marks the editor's simulation as busy or not busy, meaning it can't be 
     * saved (probably because it's running).
     */
    public void markBusy(boolean busyNow) {
        if (isBusy != busyNow) {
            isBusy = busyNow;
            firePropertyChange(PROP_DIRTY);
        }
    }

    public boolean isSaveAsAllowed() {
        return !isBusy;
    }

    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {
        this.setSite( site );
        this.setInput( input );
        
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
        readFromFile(location.toOSString());
        if (paramObject == null) {
            throw new PartInitException("Could not read file "+location.toOSString(),new ClassNotFoundException());
        }
        
        // Update viewer 
        if (viewer != null) {
            viewer.setInput(new ParamWrapper[]{new ParamWrapper(paramObject)});
        }
    }

    public void createPartControl(Composite parent) {
        viewer = new EtomicaPropertyViewer(parent);
        viewer.setRootEntry(new PropertySheetEntry());
        if (paramObject != null) {
            viewer.setInput(new ParamWrapper[]{new ParamWrapper(paramObject)});
        }
    }
	
    public EtomicaPropertyViewer getViewer() {
        return viewer;
    }
	
    public void setFocus() {
    }

    private IPath path = null;
    private ParamBase paramObject;
    private EtomicaPropertyViewer viewer;
    private boolean dirty_flag = false;
    private boolean isBusy = false;
}
