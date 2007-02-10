package etomica.plugin.editors;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.internal.EditorActionBars;

import etomica.plugin.editors.listeners.EditorSelectionChangedListener;
import etomica.plugin.editors.listeners.EtomicaStatusBarUpdater;
import etomica.plugin.editors.listeners.OpenActionListener;
import etomica.plugin.views.ActionsViewContentProvider;
import etomica.plugin.views.SimulationViewContentProvider;
import etomica.plugin.wrappers.SimulationWrapper;
import etomica.simulation.Simulation;


/**
 * Creates the Editor panel displaying the Simulation view and Actions
 */
public class EtomicaEditorInnerPanel extends EtomicaEditorInnerPanel_visualonly {

	/**
	 * @return the ListViewer used to display data for this view.
	 */
	public EtomicaTreeViewer getViewer() {
		return viewer;
	}
	

	/**
	 * @param parent
	 * @param style
	 */
	public EtomicaEditorInnerPanel(Composite parent, EtomicaEditor editor, int style) {
		super(parent, style);
		
        etomicaEditor = editor;
//        ((EditorActionBars)editor.getEditorSite().getActionBars()).getStatusLineManager().setErrorMessage("blah blah");
        
		viewer = new EtomicaTreeViewer(objectTree);
		viewer.setContentProvider(new SimulationViewContentProvider());
        EtomicaProblemsLabelDecorator pld = new EtomicaProblemsLabelDecorator();
        viewer.setLabelProvider(new DecoratingLabelProvider(new EtomicaLabelProvider(),pld));

        Menu viewMenu = new Menu(viewer.getTree());

        viewer.addSelectionChangedListener(new EditorSelectionChangedListener(viewMenu, editor));
        viewer.getTree().setMenu(viewMenu);

        viewer.addSelectionChangedListener(new EtomicaStatusBarUpdater(
                ((EditorActionBars)editor.getEditorSite().getActionBars()).getStatusLineManager()));
        
        actionsViewer = new TreeViewer(actionsTree);
        actionsViewer.setContentProvider(new ActionsViewContentProvider());
        actionsViewer.setLabelProvider(new DecoratingLabelProvider(new LabelProvider(),null));

        viewMenu = new Menu(actionsViewer.getTree());
        new RefreshItemWrapper().addItemToMenu(viewMenu, actionsViewer, null);
//        refreshItem = new MenuItem(viewMenu,SWT.NONE);
//        refreshItem.setText("Refresh");
//        // stash the viewer in the MenuItem so the listeners can get it
//        refreshItem.setData(actionsViewer);
//        refreshItem.addSelectionListener(new RefreshItemSelectionListener());
        actionsViewer.getTree().setMenu(viewMenu);
        OpenActionListener openActionListener = new OpenActionListener();
        actionsViewer.addDoubleClickListener(openActionListener);
        
	}

	public void setSimulation( Simulation simulation ) {
        SimulationWrapper simWrapper = new SimulationWrapper(simulation);
        simWrapper.setEditor(etomicaEditor);
		viewer.setInput(simWrapper);
        actionsViewer.setInput(simulation.getController());
        ((DecoratingLabelProvider)actionsViewer.getLabelProvider()).setLabelDecorator(new ActionColorDecorator(simulation.getController()));
	}
	
	static {
		// Add root to the search path so we can find our files :) 
		try
		{
			// Get the plugin object
//			EtomicaPlugin plugin = EtomicaPlugin.getDefault();
			
			// Resolve the root URL to a local representation
//			URL url = Platform.resolve( plugin.find( new Path("") ) );
			
			// Extract the path (take out the file:// prefix)
//			String urlstr = url.getPath();
			
			// Fix this silly bug that places a slash at the beginning of the file name (windows only?)
//			if ( urlstr.startsWith( "/") )
//				urlstr = urlstr.substring( 1 );
			
//			String FILESEP	= System.getProperty("file.separator");
//			urlstr = urlstr.replace( '/', FILESEP.charAt(0) );
//			System.out.println( "Etomica plugin is located at " + urlstr );
			
			// Add to search path
//			OrientedObject.appendToSearchPath( urlstr );
//			OrientedObject.appendToSearchPath( urlstr + FILESEP + "3dmodels" );
			

			// Add runtime workspace too
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			IPath rootpath = workspace.getRoot().getLocation();
//			String rootstr = rootpath.toOSString();
//			OrientedObject.appendToSearchPath( rootstr );
		}
		catch ( Exception e )
		{
			System.err.println( e.getMessage() );
			e.printStackTrace();
		}
	}
    
    private final EtomicaEditor etomicaEditor;
    private EtomicaTreeViewer viewer;
    private TreeViewer actionsViewer;
}
