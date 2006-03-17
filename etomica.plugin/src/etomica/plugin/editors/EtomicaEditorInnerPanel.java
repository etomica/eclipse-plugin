package etomica.plugin.editors;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.EditorActionBars;

import etomica.etomica3D.OrientedObject;
import etomica.plugin.EtomicaPlugin;
import etomica.plugin.editors.listeners.EditorSelectionChangedListener;
import etomica.plugin.editors.listeners.EtomicaStatusBarUpdater;
import etomica.plugin.editors.listeners.OpenActionListener;
import etomica.plugin.editors.listeners.RefreshItemSelectionListener;
import etomica.plugin.editors.listeners.RemoveItemSelectionListener;
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
	public TreeViewer getViewer() {
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
        
		viewer = new TreeViewer( objectTree  );
		viewer.setContentProvider(new SimulationViewContentProvider());
        EtomicaProblemsLabelDecorator pld = new EtomicaProblemsLabelDecorator();
        viewer.setLabelProvider(new DecoratingLabelProvider(new EtomicaLabelProvider(),pld));

        Menu viewMenu = new Menu(viewer.getTree());

        MenuItem refreshItem = new MenuItem(viewMenu,SWT.NONE);
        refreshItem.setText("Refresh");
        // stash the viewer in the MenuItem so the listeners can get it
        refreshItem.setData(viewer);
        refreshItem.addSelectionListener(new RefreshItemSelectionListener());
        
        MenuItem removeItem = new MenuItem(viewMenu,SWT.NONE);
        removeItem.setText("Remove");
        // stash the viewer in the MenuItem so the listeners can get it
        removeItem.setData(viewer);
        removeItem.addSelectionListener(new RemoveItemSelectionListener(editor));

        MenuItem addItem = new MenuItem(viewMenu,SWT.CASCADE);
        addItem.setText("Add");
        Menu addSubMenu = new Menu(addItem);
        addItem.setMenu(addSubMenu);
        // stash the viewer in the MenuItem so the listeners can get it
        addItem.setData(viewer);

        MenuItem openItem = new MenuItem(viewMenu,SWT.CASCADE);
        openItem.setText("Open As");
        // stash the viewer in the MenuItem so the listeners can get it
        openItem.setData("viewer",viewer);
        Menu openSubMenu = new Menu(openItem);
        openItem.setMenu(openSubMenu);
        
        MenuItem actionItem = new MenuItem(viewMenu,SWT.CASCADE);
        actionItem.setText("Actions");
        Menu actionSubMenu = new Menu(actionItem);
        actionItem.setMenu(actionSubMenu);
        // stash the viewer in the MenuItem so the listeners can get it
        actionItem.setData(viewer);
        
        viewer.addSelectionChangedListener(new EditorSelectionChangedListener(openItem,removeItem,addItem,actionItem,editor));
        viewer.getTree().setMenu(viewMenu);

        viewer.addSelectionChangedListener(new EtomicaStatusBarUpdater(
                ((EditorActionBars)editor.getEditorSite().getActionBars()).getStatusLineManager()));
        
        actionsViewer = new TreeViewer(actionsTree);
        actionsViewer.setContentProvider(new ActionsViewContentProvider());
        actionsViewer.setLabelProvider(new DecoratingLabelProvider(new LabelProvider(),null));

        viewMenu = new Menu(actionsViewer.getTree());
        refreshItem = new MenuItem(viewMenu,SWT.NONE);
        refreshItem.setText("Refresh");
        // stash the viewer in the MenuItem so the listeners can get it
        refreshItem.setData(actionsViewer);
        refreshItem.addSelectionListener(new RefreshItemSelectionListener());
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
//			System.out.println( "Etomica plugin is located at " + urlstr );
			
			// Add to search path
			OrientedObject.appendToSearchPath( urlstr );
//			OrientedObject.appendToSearchPath( urlstr + FILESEP + "3dmodels" );
			

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
    
    protected void refreshTree(TreeItem item) {
        if (item == null) {
            viewer.refresh();
        }
        else {
            viewer.refresh(item.getData());
        }
    }

    private final EtomicaEditor etomicaEditor;
    private TreeViewer viewer;
    private TreeViewer actionsViewer;
}
