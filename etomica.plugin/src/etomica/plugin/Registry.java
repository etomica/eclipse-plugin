/*
 * Created on Apr 26, 2005
 */
package etomica.plugin;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import etomica.Space;
import etomica.Species;


/**
 * @author Henrique
 *
 * The etomica.plugin.Registry class maintains a list of important class information to be used by the etomica.plugin UI. 
 * The bulk of the work is done by the ClassDiscovery class, which does the traversal of both CLASSPATH and the plugin itself.
 * Use of this class must be done through the Registry::getInstance() call since this follows the Singleton pattern with private constructors. 
 * Additionally this class registers itself with the plugin workspace to receive POST changes so to dinamically receive updates on changes.
 */
public class Registry {

	/** Singleton pattern - define a private constructor so no one - but this class - can create an object of this class */
	private Registry() {
		super();
		// Register a listener to track changesin the workspace
		registerResourceChangeListener();
		// Discovery methods
		class_discovery.addClass( Space.class );
		class_discovery.addClass( Species.class );
		class_discovery.addClass( etomica.potential.Potential1.class );
		class_discovery.addClass( etomica.potential.Potential2.class );
		class_discovery.addClass( etomica.Integrator.class );
		class_discovery.addClass( etomica.Phase.class );
		class_discovery.addClass( etomica.Controller.class );
		class_discovery.addClass( etomica.data.DataSource.class );
		class_discovery.addClass( etomica.graphics.Display.class );
		class_discovery.addClass( etomica.Action.class );
		class_discovery.addClass( etomica.Activity.class );
		class_discovery.addClass( etomica.Simulation.class );
		class_discovery.addClass( etomica.PotentialMaster.class );
		class_discovery.addClass( etomica.graphics.Device.class );
	}

	private ClassDiscovery class_discovery = new ClassDiscovery();
	
	/** Singleton pattern - define static variable to hold the unique instance of this class */
	static public final Registry instance = new Registry(); 
	/** Singleton pattern - define a static public method to return the instance name */
	static public Registry getInstance() {	return instance; }

	/** A helper function to return a meaningfull string from a delta type */
	static protected String getDeltaName( int type )
	{
		String typename = "";
		switch ( type )
		{
			case IResourceDelta.ADDED:
				typename = "Added"; 
			break;
			case IResourceDelta.REMOVED:
				typename = "Deleted";
			break;
			case IResourceDelta.CHANGED: 
				typename = "Changed";
			break;
		}
		return typename;
	}

	/** Thanslate the IResourceDelta object - in fact a tree of subdirectory changes - into a more meaningfull list with leaves only. 
	 * Example: if the fiole /home/john/eclipse/workspace/my.file was altered delta would contain 5 different objects, one for each subdirectory. 
	 * This routine just filter what matters - my.file. */
	protected LinkedList getDeltaLeaves( IResourceDelta delta )
	{
		LinkedList listofchanges = new LinkedList();
		
		IResourceDelta[] deltas = delta.getAffectedChildren();
		if ( deltas==null || deltas.length==0 )
		{
			// Push this delta
			listofchanges.add( delta );
		}
		else
		for ( int j=0; j<deltas.length; j++ )
		{
			// add all leaves
			listofchanges.addAll( getDeltaLeaves( deltas[j] ) );
		}
		return listofchanges;
	}
	/** The routine that handles notifications from the etomica.plugin workspace */
	private void handleNotification( IResourceChangeEvent event ){
		if ( event==null )
			return; // nothing to notify
		LinkedList changes = getDeltaLeaves( event.getDelta() );
		java.util.ListIterator ich = changes.listIterator();
		while( ich.hasNext() )
		{
			IResourceDelta delta = (IResourceDelta) ich.next();

			// it's a leaf - add
			String fullpath = delta.getFullPath().toString();
			String comment = getDeltaName( delta.getKind() ) + ":" + fullpath;

			System.out.println( comment );
		}
	}
	
	/** A listener that will track all changes in the resource while this plugin is running */
	static private IResourceChangeListener resource_change_listener = null;
	
	/** Checks if a listener was already registered with the workspace */
	static private void registerResourceChangeListener()
	{
		if ( resource_change_listener==null )
			resource_change_listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				if ( event!=null ) instance.handleNotification( event );
			}
		};
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if ( workspace!=null )
		{
			workspace.addResourceChangeListener(resource_change_listener, IResourceChangeEvent.POST_CHANGE);
		}
	}
		/*
	    static final public Class[] spaceClasses = introspect("Space", true);
	    static final Class[] speciesClasses = introspect("Species", true);
	    static final Class[] potentialClasses = introspect("P2", true);
	    static final Class[] p1Classes = introspect("P1", true);
	    static final Class[] integratorClasses = introspect("Integrator", true);
	    static final Class[] phaseClasses = introspect("Phase", true);
	    static final Class[] controllerClasses = introspect("Controller", true);
	    static final Class[] displayClasses = introspect("Display", true);
	    static final Class[] meterClasses = introspect("Meter", true);
	    static final Class[] deviceClasses = introspect("Device", true);
	    static final Class[] actionClasses = introspect(etomica.Default.CLASS_DIRECTORY+"/action","",false);
	    static Class[] workingClass = null;
	    static Class[] validClasses = null;
	    static int validCount = 0;
	    */
		public static Collection queryWhoExtends( Class class_to_search )
		{
			return instance.class_discovery.queryWhoExtends( class_to_search );
		}
		
	    /*public static Class[] introspect(String t, boolean b){
	        return introspect(etomica.Default.CLASS_DIRECTORY, t, b);
	    }
	    
	    public static Class[] introspect(String path, String t, boolean b){
	        final String title = t;
	        final boolean checkInterfaces = b;
			
	        String newpath = System.getProperty("java.class.path");
	        //newpath.replace('/', '.');
		    java.io.File dir = new java.io.File( newpath );
		    String[] files = dir.list(new java.io.FilenameFilter() {
		        public boolean accept(java.io.File d, String name) {
		        	System.out.println( "---> " + d.getName() + "-" + name );
		                return name.startsWith(title)
		                && ( name.endsWith("class") || name.endsWith("jar") )
		                && name.indexOf("$") == -1;}
		        });
		    
		    LinkedList class_collection = new LinkedList();
		    if ( files!=null )
		    {
		    	for(int i=0; i<files.length; i++) {
		    		int idx = files[i].lastIndexOf(".");
		    		files[i] = files[i].substring(0,idx);
		    		String packageName = "";
		    		try{
		    			packageName = path.substring(etomica.Default.WORKING_DIRECTORY.length());
		    			packageName = packageName.replace('/', '.');
		    			Class classobj = Class.forName(packageName + "." + files[i]);
		    			if ( classobj!=null )
		    				class_collection.add( classobj );
		    		} catch(ClassNotFoundException e) {System.out.println("Failed for "+files[i]);}
		    		catch(java.lang.SecurityException se) {System.out.println("security exc");}
		    		catch(java.lang.NoClassDefFoundError e) {
		    			System.out.println(packageName);
		    			System.out.println(files[i]);
		    			System.out.println("no class def found error");
		    			e.printStackTrace();
		    		}
		    	}// End of initialization of Classes array
		    }
		    // Add classes available in the classpath
		    
		    
		    // Copy to a new array
	        Class[] validClasses = null;
	        int nclasses = class_collection.size();
		    if (nclasses!=0) {
	    		validClasses = new Class[nclasses];
	    		for (int i = 0; i < nclasses; i++){
	    			validClasses[i] = (Class)class_collection.get(i);
	    		}	
	    	}
		    return validClasses;
	    }// end of introspect method
	    
	    private static boolean checkForEtomicaInfo(Class wClass){
	        try {
	            java.lang.reflect.Method method = wClass.getMethod("getEtomicaInfo",null);
	            etomica.EtomicaInfo info = (etomica.EtomicaInfo)method.invoke(wClass,null);
	  //          java.util.Vector compatElements = info.getCompatibility();
	            return true;
	        }
	        catch(java.lang.SecurityException se){return false;}
	        catch(java.lang.IllegalAccessException iae){System.out.println("illegal access exception");return false;}
	        catch(java.lang.reflect.InvocationTargetException ite){System.out.println("invocation target exception");return false;}
	        catch(java.lang.NoSuchMethodException nsme){return false;}
	    }
	    */
}
