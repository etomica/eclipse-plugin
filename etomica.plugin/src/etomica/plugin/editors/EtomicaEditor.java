package etomica.plugin.editors;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

import etomica.action.activity.Controller;
import etomica.atom.Atom;
import etomica.phase.Phase;
import etomica.plugin.views.PropertySourceWrapper;
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
	
	/** Save the contents of this simulation into the file pointed by the IPath object "path". 
	 * doSaveAs() will ask for a file name and set the "path" variable before calling doSave().
	 */
	public void doSave(IProgressMonitor progressMonitor) {
		// Use XML to stream simulation
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile file = workspace.getRoot().getFile(path);
		new FileEditorInput(file);

		if (progressMonitor != null)
			progressMonitor.setCanceled( false );

		IFile[] files = workspace.getRoot().findFilesForLocation( path );
		if ( files.length!=1 )
		{
			System.out.println( "(Etomica) Error saving editor to location " + path.toOSString() );
			return;
		}
		String filename = files[0].getFullPath().toOSString();
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream( filename);
		  	out = new ObjectOutputStream(fos);
		  	out.writeObject( simulation );
		  	out.close();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}

	}
	

	/* Asks for a file name (sets file name in "path" variable) and then makes a runnable thread to call doSave(). 
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

	
	
	void readFromFile( String filename )
	{
		FileInputStream fis = null;
		EtomicaObjectInputStream in = null;
		try
		{
		  fis = new FileInputStream(filename);
		  in = new EtomicaObjectInputStream(fis);
		  simulation = (etomica.simulation.Simulation) in.readObject();
          in.finalizeRead();
		  in.close();

		  // While we do not implement serialization for the controller...
		  if ( simulation.getController()==null )
		  	simulation.setController( new Controller() );
		}
		catch( Exception ex ) {
			System.err.println( "Could not read simulation from file " + filename + ". Cause: " + ex.getLocalizedMessage() );
			simulation = new Simulation();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return dirty_flag;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
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
		IResource resource = root.findMember(original.getFullPath());
		if (resource == null) 
			return;
		IPath location = resource.getLocation();
		readFromFile(location.toOSString());
		
		// Update inner panel 
		if ( inner_panel != null )
		{
			inner_panel.setSimulation( simulation );
			showPhase(0);
		}
	}

	
	/**
	 * Changes displayed configuration with change of simulation selected in
	 * another view.
	 */
	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if(part == this) return;
//		System.out.println("ConfigurationView Selection "+selection.toString());
		if(!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
		if(sel.getFirstElement() == null) {
			if(selectionSource == part) {
				inner_panel.setSelectedAtoms(new Atom[0]);
				selectionSource = null;
			}
			return;
		}
		Object firstsel = sel.getFirstElement();
		if ( !(firstsel instanceof PropertySourceWrapper) ) return;
		PropertySourceWrapper property = (PropertySourceWrapper) firstsel;
		Object obj = property.getObject();
		if(obj instanceof Phase) {
			phase = (Phase)obj;
//			System.out.println("ConfigurationView phase "+phase.toString());
			inner_panel.setPhase(phase);
		} else if(obj instanceof Simulation) {
			Simulation sim = (Simulation)obj;
			Phase phase = (Phase)lastPhase.get(sim);//get phase last viewed with selected simulation
			if(phase == null) {
				phase = (Phase)sim.getPhaseList().get(0);
				if(phase != null) lastPhase.put(sim, phase);
			}
			inner_panel.setPhase(phase);	
		} else if(obj instanceof Atom) {
			//selection of one or more atoms
			int nAtom = sel.size();
			Atom[] selectedAtoms = new Atom[nAtom];
			selectionSource = part;
			Object[] objects = sel.toArray();
			for(int i=0; i<nAtom; i++) {
				selectedAtoms[i] = (Atom)((etomica.plugin.views.PropertySourceWrapper)objects[i]).getObject();
			}
			inner_panel.setSelectedAtoms(selectedAtoms);
		}

	}

	public void showPhase( int n )
	{
		if ( simulation==null )
			return;
		Phase newphase = null;
		try {
			if ( n>=0 && n<simulation.getPhaseList().size() )
			{
				LinkedList phaselist = simulation.getPhaseList();
				if ( n<=phaselist.size() )
					newphase = (Phase)phaselist.get(n);
			}	
			
			
		}
		catch ( Exception e ) {
			System.err.println( "(Etomica) Could not retrieve phase #" + n + ", reason: " + e.getLocalizedMessage() );
		}
		if(newphase == null) {
			newphase = (Phase)lastPhase.get(simulation);//get phase last viewed with selected simulation
			if(newphase != null) lastPhase.put(simulation, phase);
		}
		inner_panel.setPhase( newphase );
		phase = newphase;
	}
	/**
	 * 
	 *
	 */
	private void hookPageSelection() {
		pageSelectionListener = new ISelectionListener() {
			public void selectionChanged(
					IWorkbenchPart part,
					ISelection selection) {
				pageSelectionChanged(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(pageSelectionListener);
	}

	
	public void runSimulation() {
		if ( simulation != null )
			simulation.getController().start();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

		inner_panel = new EtomicaEditorInnerPanel(parent, 0 );
		inner_panel.setSimulation( simulation );
		showPhase(0);
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
	private Phase phase = null; // current phase being showed
	private ISelectionListener pageSelectionListener;
	private final HashMap lastPhase = new HashMap(8);//store last phase viewed for each simulation
	private IWorkbenchPart selectionSource;
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
