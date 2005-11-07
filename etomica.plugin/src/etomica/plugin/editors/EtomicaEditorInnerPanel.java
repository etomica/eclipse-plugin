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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import etomica.etomica3D.OrientedObject;
import etomica.plugin.EtomicaPlugin;
import etomica.plugin.views.SimulationViewContentProvider;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.plugin.wrappers.SimulationWrapper;
import etomica.simulation.Simulation;


/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtomicaEditorInnerPanel extends EtomicaEditorInnerPanel_visualonly {

	private TreeViewer viewer;
    private TreeViewer actionsViewer;

	/**
	 * @return the ListViewer used to display data for this view.
	 */
	public TreeViewer getViewer() {
		return viewer;
	}
	

	public void propertyChange(PropertyChangeEvent event) {
		viewer.refresh();
	}

	/**
	 * @param parent
	 * @param style
	 */
	public EtomicaEditorInnerPanel(Composite parent, int style) {
		super(parent, style);
		
		viewer = new TreeViewer( objectTree  );
		viewer.setContentProvider(new SimulationViewContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        Menu viewMenu = new Menu(viewer.getTree());
        MenuItem removeItem = new MenuItem(viewMenu,SWT.NONE);
        removeItem.setText("Remove");
        // stash the viewer in the MenuItem so the listeners can get it
        removeItem.setData(viewer);
        removeItem.addSelectionListener(new RemoveItemSelectionListener());
        MenuItem refreshItem = new MenuItem(viewMenu,SWT.NONE);
        refreshItem.setText("Refresh");
        // stash the viewer in the MenuItem so the listeners can get it
        refreshItem.setData(viewer);
        refreshItem.addSelectionListener(new RefreshItemSelectionListener());
        viewer.addSelectionChangedListener(new MySelectionChangedListener(removeItem));
        viewer.getTree().setMenu(viewMenu);
	
        actionsViewer = new TreeViewer( actionsTree );
        actionsViewer.setContentProvider(new ActionsViewContentProvider());
        actionsViewer.setLabelProvider(new LabelProvider());
	}

	public void setSimulation( Simulation simulation )
	{
		viewer.setInput( new SimulationWrapper(simulation) );
        actionsViewer.setInput(simulation.getController());
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
    
    private class RefreshItemSelectionListener implements SelectionListener {
        public void widgetSelected(SelectionEvent e){
            TreeViewer simViewer = (TreeViewer)e.widget.getData();
            //retrieve the object from the tree viewer directly
            Object selectedObj = simViewer.getTree().getSelection()[0].getData();
            simViewer.refresh(selectedObj);
        }

        public void widgetDefaultSelected(SelectionEvent e){
            widgetSelected(e);
        }
    }
	
	private class RemoveItemSelectionListener implements SelectionListener {
        public void widgetSelected(SelectionEvent e){
            TreeViewer simViewer = (TreeViewer)e.widget.getData();
            //retrieve the selected tree item from the tree so we can get its parent
            TreeItem selectedItem = simViewer.getTree().getSelection()[0];
            Object selectedObj = selectedItem.getData();
            //retrieve the selected item's parent
            TreeItem parentItem = selectedItem.getParentItem();
            while (parentItem != null) {
                Object parentObj = parentItem.getData();
                if (parentObj instanceof ArrayWrapper) {
                    //if the parent was an array wrapper, then we really want the array's parent
                    parentItem = parentItem.getParentItem();
                    continue;
                }
                if (parentObj instanceof PropertySourceWrapper) {
                    //found it.  now try to remove the selected object from its parent
                    if (((PropertySourceWrapper)parentObj).removeChild(selectedObj)) {
                        // refresh the tree if it worked
                        simViewer.refresh(parentObj);
                    }
                }
                break;
            }
            if (parentItem == null) {
                // selected item's parent must be the simulation.  retrieve it from
                // the tree viewer's root.
                SimulationWrapper simWrapper = (SimulationWrapper)simViewer.getInput();
                simWrapper.removeChild(selectedObj);
                //refresh everything
                simViewer.refresh();
            }
        }

        public void widgetDefaultSelected(SelectionEvent e){
            widgetSelected(e);
        }
    }

    private static class MySelectionChangedListener implements ISelectionChangedListener {
        public MySelectionChangedListener(MenuItem remove) {
            removeItem = remove;
        }
        
        public void selectionChanged(SelectionChangedEvent e) {
            if (e.getSelection().isEmpty()) {
                return;
            }
            TreeViewer simViewer = (TreeViewer)e.getSource();
            //retrieve the selected tree item from the tree so we can get its parent
            TreeItem selectedItem = simViewer.getTree().getSelection()[0];
            Object selectedObj = selectedItem.getData();
            //retrieve the selected item's parent
            TreeItem parentItem = selectedItem.getParentItem();
            Object parentObj = null;
            while (parentItem != null) {
                parentObj = parentItem.getData();
                if (parentObj instanceof ArrayWrapper) {
                    //if the parent was an array wrapper, then we really want the array's parent
                    parentItem = parentItem.getParentItem();
                    continue;
                }
                break;
            }
            if (parentItem == null) {
                // selected item's parent must be the simulation
                parentObj = (SimulationWrapper)simViewer.getInput();
            }
            // query the parent's wrapper to see if the selected child can be removed 
            if (parentObj instanceof PropertySourceWrapper && 
                    ((PropertySourceWrapper)parentObj).canRemoveChild(selectedObj)) {
                removeItem.setEnabled(true);
            }
            else {
                removeItem.setEnabled(false);
            }
        }
        
        private final MenuItem removeItem;
    }
}
