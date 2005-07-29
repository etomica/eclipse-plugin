package etomica.plugin.editors;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
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

import osg.OrientedObject;
import etomica.Atom;
import etomica.Controller;
import etomica.Phase;
import etomica.Simulation;
import etomica.atom.AtomList;
import etomica.graphics2.SceneManager;
import etomica.plugin.EtomicaPlugin;
import etomica.plugin.realtimegraphics.OSGWidget;
import etomica.plugin.views.PropertySourceWrapper;
import etomica.plugin.views.SimulationViewContentProvider;
import etomica.simulations.HSMD3D;
import etomica.utility.EtomicaObjectInputStream;


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
		final IEditorInput newInput = new FileEditorInput(file);

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
		ProgressMonitorDialog progress = new ProgressMonitorDialog( shell );
			
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
		  simulation = (etomica.Simulation) in.readObject();
		  AtomList.rebuildAllLists( in );
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
		if (original != null)
		{
			String filename = original.getFullPath().toOSString();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IResource resource = root.findMember( original.getFullPath() );
			if ( resource!=null )
			{
				IPath location = resource.getLocation();
				readFromFile( location.toOSString() );
			}
		}
		
		showPhase( 0 );
	}

	
	/**
	 * Changes displayed configuration with change of simulation selected in another view.
	 */
	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if(part == this) return;
//		System.out.println("ConfigurationView Selection "+selection.toString());
		if(!(selection instanceof IStructuredSelection)) return;
		IStructuredSelection sel = (IStructuredSelection)selection;
		if(sel.getFirstElement() == null) {
			if(selectionSource == part) {
				scene.setSelectedAtoms(new Atom[0]);
				selectionSource = null;
			}
			return;
		}
		Object firstsel = sel.getFirstElement();
		if ( !(firstsel instanceof PropertySourceWrapper) ) return;
		PropertySourceWrapper property = (PropertySourceWrapper) firstsel;
		Object obj = property.getObject();
		if(obj instanceof Phase) {
			Phase phase = (Phase)obj;
//			System.out.println("ConfigurationView phase "+phase.toString());
			scene.setPhase(phase);
		} else if(obj instanceof Simulation) {
			Simulation sim = (Simulation)obj;
			Phase phase = (Phase)lastPhase.get(sim);//get phase last viewed with selected simulation
			if(phase == null) {
				phase = (Phase)sim.getPhaseList().get(0);
				if(phase != null) lastPhase.put(sim, phase);
			}
			scene.setPhase(phase);	
		} else if(obj instanceof Atom) {
			//selection of one or more atoms
			int nAtom = sel.size();
			Atom[] selectedAtoms = new Atom[nAtom];
			selectionSource = part;
			Object[] objects = sel.toArray();
			for(int i=0; i<nAtom; i++) {
				selectedAtoms[i] = (Atom)((etomica.plugin.views.PropertySourceWrapper)objects[i]).getObject();
			}
			scene.setSelectedAtoms(selectedAtoms);
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
		if ( scene!=null ) 
		{
			scene.setPhase(newphase);
		}
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

	static {
		// Add root to the search path so we can find our files :) 
		try
		{
			// Get the plugin object
			EtomicaPlugin plugin = EtomicaPlugin.getDefault();
			
			// Resolve the root URL to a local representation
			URL url = Platform.resolve( plugin.find( new Path("") ) );
			
			// Extract the path (take out the file:// prefix)
			String urlstr = url.getPath();
			
			// Fix this silly bug that places a slash at the beginning of the file name (windows only?)
			if ( urlstr.startsWith( "/") )
				urlstr = urlstr.substring( 1 );
			
			String FILESEP	= System.getProperty("file.separator");
			urlstr = urlstr.replace( '/', FILESEP.charAt(0) );
			System.out.println( "Etomica plugin is located at " + urlstr );
			
			// Add to search path
			OrientedObject.appendToSearchPath( urlstr );
			OrientedObject.appendToSearchPath( urlstr + FILESEP + "3dmodels" );
			

			// Add runtime workspace too
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath rootpath = workspace.getRoot().getLocation();
			String rootstr = rootpath.toOSString();
			OrientedObject.appendToSearchPath( rootstr );
		}
		catch ( Exception e )
		{
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
	}
	
	private static String	PATHSEP	= System.getProperty("path.separator");

	public class SceneUpdater implements Runnable {
	    private int DELAY = 100;
	    private boolean first_time = true;
	    
	    public SceneUpdater() {
	    }
	    
	    public void setFPS( double fps )
	    {
	    	DELAY = (int)( 1000.0/fps );
	    }
	    public void run() {
	        if (osgwidget != null && inner_panel.isVisible() ) {
	        	scene.updateAtomPositions();
	            osgwidget.render();
	        	if ( first_time )
	        	{
	        		osgwidget.getRenderer().zoomAll();
	        		first_time = false;
	        	}
        		osgwidget.getRenderer().zoomAll();
	        }
            inner_panel.getDisplay().timerExec(DELAY, this);
	    }
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

		inner_panel = new EtomicaEditorInnerPanel(parent, 0 );
		Composite control = inner_panel.getPhasePanel();
		
		osgwidget = new OSGWidget( control );

		scene.setRenderer( osgwidget.getRenderer() );
		
		updater = new SceneUpdater();
		updater.setFPS( 10 );
		updater.run();
		
		viewer = new TreeViewer( inner_panel.objectList  );
		viewer.setContentProvider(new SimulationViewContentProvider());
        viewer.setLabelProvider(new LabelProvider());
		viewer.setInput( simulation );
		
	}
	
	/**
	 * @return the ListViewer used to display data for this view.
	 */
	public TreeViewer getViewer() {
		return viewer;
	}
	

	public void propertyChange(PropertyChangeEvent event) {
		viewer.refresh();
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

	private TreeViewer viewer;
	private EtomicaEditorInnerPanel inner_panel;
	private IPath path = null;
	private Simulation simulation = null;
	private Phase phase = null; // current phase being showed
    private ISelectionListener pageSelectionListener;
	private SceneManager scene = new SceneManager();
	private SceneUpdater updater;
	private OSGWidget osgwidget;
	private boolean render_initialized = false;
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
			  Simulation simulation = (etomica.Simulation) in.readObject();
			  in.close();
			  fis.close();
			}
			catch( Exception ex ) {
				System.err.println( "Could not read simulation from file " + filename + ". Cause: " + ex.getLocalizedMessage() );
			}
	    }
}

/*
	public static class ContentProvider implements IStructuredContentProvider, SimulationListener {

		ContentProvider() {
			Simulation.instantiationEventManager.addListener(this);	
		}
		
		 * @param inputElement a linked list containing the simulation instances,
		 * coming from Simulation.getInstances
		 
		//the call to viewer.setInput in createPartControl causes the list of
		//simulation instances to be the input element in this method
		public Object[] getElements(Object inputElement) {
			Object[] elements = ((java.util.LinkedList)inputElement).toArray();
			PropertySourceWrapper[] wrappedElements = PropertySourceWrapper.wrapArrayElements(elements);
			return wrappedElements;
		}

		 (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 
		public void dispose() {
			Simulation.instantiationEventManager.removeListener(this);
		}
		
		public void actionPerformed(SimulationEvent evt) {
			viewer.refresh();
		}

		 (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.viewer = viewer;
			currentSelection = newInput;
		}
		
		private Viewer viewer;
		Object currentSelection;
	}

	
}

*/

/*
public void doSave(IProgressMonitor progressMonitor) {
	// Use XML to stream simulation
	
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	IFile file = workspace.getRoot().getFile(path);
	final IEditorInput newInput = new FileEditorInput(file);

	if (progressMonitor != null)
		progressMonitor.setCanceled( false );
	// Initialize
	Element e = null;
	Node n = null;
	
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = null;
	try {
		builder = factory.newDocumentBuilder();
	} catch ( ParserConfigurationException ep )	{
		System.out.println( ep );
		return;
	}
	Document xmldoc = builder.newDocument();
	
	// Root element.
	Element root = xmldoc.createElement("USERS");
	String[] id = {"PWD122","MX787","A4Q45"};
	String[] type = {"customer","manager","employee"};
	String[] desc = {"Tim@Home","Jack&Moud","John D'oé"};
	for (int i=0;i<id.length;i++)
	{
	   // Child i.
	   e = xmldoc.createElementNS(null, "USER");
	   e.setAttributeNS(null, "ID", id[i]);
	   e.setAttributeNS(null, "TYPE", type[i]);
	   n = xmldoc.createTextNode(desc[i]);
	   e.appendChild(n);
	   root.appendChild(e);
	 }
	 xmldoc.appendChild(root);
	 
	 
	 
	 FileOutputStream fos = null;
	 try {
	 	fos = new FileOutputStream( path.toOSString() );
	 }
	 catch ( FileNotFoundException ef ) {
	 	System.out.println( ef );
	 	return;
	 }
	 OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
	 of.setIndent(1);
	 of.setIndenting(true);
	 of.setDoctype(null,"users.dtd");
	 XMLSerializer serializer = new XMLSerializer(fos,of);
//	  As a DOM Serializer
	 try {
	 	serializer.asDOMSerializer();
	 	serializer.serialize( xmldoc.getDocumentElement() );
	 	fos.close();
	 }
	 catch ( IOException ie ) {
	 	System.out.println( ie );
	 	return;
	 }
	 
}*/


/*
void readFromFile( String original )
{
	DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
    //factory.setValidating(true);   
    //factory.setNamespaceAware(true);
	Document document = null;
    try {
       DocumentBuilder builder = factory.newDocumentBuilder();
       document = builder.parse( new File( original ) );

    } catch (SAXException sxe) {
       // Error generated during parsing)
       Exception  x = sxe;
       if (sxe.getException() != null)
           x = sxe.getException();
       x.printStackTrace();

    } catch (ParserConfigurationException pce) {
        // Parser with specified options can't be built
        pce.printStackTrace();

    } catch (IOException ioe) {
       // I/O error
       ioe.printStackTrace();
    }
    
    
}
*/

/*
 protected java.util.LinkedList parseList( String string_to_parse )
	{
		string_to_parse = string_to_parse.trim();
		String[] string_list = string_to_parse.split(",");
		java.util.LinkedList list = new java.util.LinkedList();
		for ( int j=0; j<string_list.length; j++ )
		{
			// Check if value corresponds to an object in the current namespace
			String value = string_list[j];
			Object obj = property_bag_list.get( value );
			
			// If it does, add the object, otherwise store the string itself
			if ( obj==null )
			{
				// Try reflection
				try {
					Class objclass = Class.forName( value );
					obj = objclass.newInstance();
				} catch ( Exception e ) {} // do nothing, just fail
				if ( obj!=null )
					list.add( obj );
				else
					list.add( value );
			}
			else
				list.add( obj );
		}
		return list;
	}
	
void readFromFile( String original )
{
	try {
		BufferedReader bf = new BufferedReader( new FileReader(original) );
		if ( bf==null )
			return;
		int linecnt = 0;
		Object last_object = null;
		while ( bf.ready() )
		{
			// Read next line
			linecnt++;
			String line = bf.readLine().trim();

			// Skip comments and empty lines
			if ( line.length()==0 || line.startsWith( "#") )
				continue;
			
			// Check if this is a property (has a = sign somewhere)
			String[] options = line.split("=");
			if ( options.length<=1 ) 
			{
				// This is a new property bag object
				
				// Reset our tracking variable
				last_object = null;
				
				// Split for alias
				options = line.split(" ");
				if ( options.length==0 )
				{
					System.out.println( "(Etomica) Unknown command at line " + String.valueOf(linecnt) + ": " + line );
					continue;
				}
				
				// The first item is the bag name
				String bagname = options[0].trim();
				
				// Instantiate this object from its name
				Object bagobj = null;
				try
				{
					Class simclass = Class.forName( bagname );
					bagobj = simclass.newInstance();

					// Set our tracking variable
					last_object = bagobj;
					
					// Add alias to our mapping
					if ( options.length>=2 )
					{
						String alias = options[1].trim();
						property_bag_list.put( alias, bagobj );
					}
					
					// set simulation if this is derived from etomica.Simualtion
					if ( bagobj instanceof Simulation )	
						simulation = (Simulation) bagobj;
					else {
						System.out.println( "(Etomica) Class " + options[1] + " is not an instance of etomica.Simulation");
					}
				} catch ( ClassNotFoundException e ) {
					System.out.println( "(Etomica) Simulation class not found: " + bagname + ": " + e.getLocalizedMessage() );
				}
				catch ( InstantiationException e ) {
					System.out.println( "(Etomica) Instantiation error of class " + bagname + ": " + e.getLocalizedMessage() );
				}
				catch ( IllegalAccessException e ) {
					System.out.println( "(Etomica) Illegal Access of class " + bagname + ": " + e.getLocalizedMessage() );
				}
			}
			else
			{
				// This is a property, get
				if ( last_object==null )
					continue;
				
				// Parse value - if it is a list, create a linked list
				String key = options[0].trim();
				String value = options[1].trim();
				LinkedList arglist = parseList( value );
				Object objvalue = arglist;
				if ( arglist.size()==1 )
					objvalue = arglist.get(0);

				// Try to use the property bag interface first to set this field
				boolean property_set = false;
				if ( last_object instanceof IPropertyBag )
				{
					((IPropertyBag)last_object).setProperty( key, objvalue );
				}
				else
				{
					try {
						// IPropertyBag did not succeed. Try reflection
						Class  objclass = last_object.getClass();
						java.lang.reflect.Field field = objclass.getField( key );
						if ( field != null ) {
							field.set( last_object, objvalue );
						}
					} catch ( NoSuchFieldException e ) 	{
						System.out.println( "(Etomica) Field " + last_object.getClass().getName()+ "." + key + " does not exist: " + e.getLocalizedMessage() );
					}
					catch ( IllegalArgumentException e ) {
						System.out.println( "(Etomica) Field " + last_object.getClass().getName()+ "." + key + " was assigned an illegal argument: " + e.getLocalizedMessage() );
					}
					catch ( IllegalAccessException e )  {
						System.out.println( "(Etomica) Field " + last_object.getClass().getName()+ "." + key + " does not allow access: " + e.getLocalizedMessage() );
					}
				}
	
			}
		}
		bf.close();
	}
	catch( FileNotFoundException e ){
		e.printStackTrace();
	}
	catch( IOException e ) {
		e.printStackTrace();
	}
	
}
*/