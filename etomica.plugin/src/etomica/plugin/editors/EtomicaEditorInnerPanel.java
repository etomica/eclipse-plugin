/*
 * Created on Aug 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.editors;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

import osg.OrientedObject;

import etomica.Atom;
import etomica.Phase;
import etomica.Simulation;
import etomica.graphics2.SceneManager;
import etomica.plugin.EtomicaPlugin;
import etomica.plugin.realtimegraphics.OSGWidget;
import etomica.plugin.views.SimulationViewContentProvider;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtomicaEditorInnerPanel extends EtomicaEditorInnerPanel_visualonly {

	private SceneManager scene = new SceneManager();
	private SceneUpdater updater;
	private OSGWidget osgwidget;
	private boolean render_initialized = false;
	private TreeViewer viewer;


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
	    	if ( osgwidget!=null && isDisposed()  ) 
	    		return;
	        if ( osgwidget!=null &&  isVisible() ) {
	        	scene.updateAtomPositions();
	            osgwidget.render();
	        	if ( first_time )
	        	{
	        		osgwidget.getRenderer().zoomAll();
	        		first_time = false;
	        	}
	        }
            getDisplay().timerExec(DELAY, this);
	    }
	}

	public void setPhase( Phase ph )
	{
		if ( scene!=null ) 
		{
			scene.setPhase( ph );
		}
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

	public void setSelectedAtoms( Atom[] atoms )
	{
		scene.setSelectedAtoms( atoms );
	}
	/**
	 * @param parent
	 * @param style
	 */
	public EtomicaEditorInnerPanel(Composite parent, int style) {
		super(parent, style);
		Composite control = getPhasePanel();
		
		osgwidget = new OSGWidget( control );

		scene.setRenderer( osgwidget.getRenderer() );
		
		updater = new SceneUpdater();
		updater.setFPS( 20 );
		updater.run();
		
		viewer = new TreeViewer( objectList  );
		viewer.setContentProvider(new SimulationViewContentProvider());
        viewer.setLabelProvider(new LabelProvider());
	
	}

	public void setSimulation( Simulation simulation )
	{
		viewer.setInput( simulation );
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


}
