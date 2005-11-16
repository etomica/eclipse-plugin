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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import etomica.util.Arrays;


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
        
        MenuItem addItem = new MenuItem(viewMenu,SWT.CASCADE);
        addItem.setText("Add");
        Menu addSubMenu = new Menu(addItem);
        addItem.setMenu(addSubMenu);
        // stash the viewer in the MenuItem so the listeners can get it
        addItem.setData(viewer);
        viewer.addSelectionChangedListener(new MySelectionChangedListener(removeItem,addItem));
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
    
    private static class RefreshItemSelectionListener implements SelectionListener {
        public void widgetSelected(SelectionEvent e){
            TreeViewer simViewer = (TreeViewer)e.widget.getData();
            //retrieve the object from the tree viewer directly
            TreeItem selectedItem = simViewer.getTree().getSelection()[0];
            Object selectedObj = selectedItem.getData();
            while (selectedObj instanceof ArrayWrapper) {
                selectedItem = selectedItem.getParentItem();
                if (selectedItem == null) {
                    simViewer.refresh();
                    return;
                }
                selectedObj = selectedItem.getData();
            }
            simViewer.refresh(selectedObj);
        }

        public void widgetDefaultSelected(SelectionEvent e){
            widgetSelected(e);
        }
    }
	
	private static class RemoveItemSelectionListener implements SelectionListener {
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
                if (simWrapper.removeChild(selectedObj)) {
                    simViewer.refresh(null);
                }
            }
        }

        public void widgetDefaultSelected(SelectionEvent e){
            widgetSelected(e);
        }
    }
    
    private static class AddItemSelectionListener implements SelectionListener {
        public void widgetSelected(SelectionEvent e){
            TreeViewer simViewer = (TreeViewer)e.widget.getData("viewer");
            SimulationWrapper simWrapper = (SimulationWrapper)simViewer.getInput();
            //retrieve the selected tree item from the tree so we can get its parent
            TreeItem selectedItem = simViewer.getTree().getSelection()[0];
            Object selectedObj = selectedItem.getData();
            TreeItem parentItem = selectedItem;
            Object parentObj = selectedObj;
            if (parentObj instanceof ArrayWrapper) {
                //retrieve the selected item's parent
                parentItem = selectedItem.getParentItem();
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
                    // selected item's parent must be the simulation.  retrieve it from
                    // the tree viewer's root.
                    parentObj = simWrapper;
                }
            }
            if (((PropertySourceWrapper)parentObj).addObjectClass((Simulation)simWrapper.getObject(),
                    (Class)e.widget.getData("newClass"),simViewer.getControl().getShell())) {
                simViewer.refresh(parentItem);
            }
        }

        public void widgetDefaultSelected(SelectionEvent e){
            widgetSelected(e);
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

    private static class MySelectionChangedListener implements ISelectionChangedListener {
        public MySelectionChangedListener(MenuItem remove, MenuItem add) {
            removeItem = remove;
            addItem = add;
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
            if (parentObj instanceof PropertySourceWrapper
                  && ((PropertySourceWrapper)parentObj).canRemoveChild(selectedObj)) {
                removeItem.setEnabled(true);
            }
            else {
                removeItem.setEnabled(false);
            }
            if (selectedObj instanceof PropertySourceWrapper) {
                Class[] adders;
                if (selectedObj instanceof ArrayWrapper) {
                    // if we have an array, we really want to add something to the 
                    // array's parent.
                    adders = ((PropertySourceWrapper)parentObj).getAdders();
                    Object obj = ((PropertySourceWrapper)selectedObj).getObject();
                    Class arrayClass = obj.getClass();
                    Class componentClass = arrayClass.getComponentType();

                    for (int i=0; i<adders.length; ) {
                        if (!adders[i].isAssignableFrom(componentClass)) {
                            adders = (Class[])Arrays.removeObject(adders,adders[i]);
                        }
                        else {
                            i++;
                        }
                    }
                }
                else {
                    adders = ((PropertySourceWrapper)selectedObj).getAdders();
                }
                if (adders.length == 0) {
                    addItem.setEnabled(false);
                }
                else {
                    addItem.setEnabled(true);
                    Menu addSubMenu = addItem.getMenu();
                    while (addSubMenu.getItemCount() > 0) {
                        MenuItem item = addSubMenu.getItem(0);
                        item.dispose();
                    }
//                    MenuItem setSubItemNone = new MenuItem(setSubMenu,SWT.NONE);
//                    setSubItemNone.setText("(empty)");
//                    setSubItemNone.setEnabled(false);
//                    setItem.addSelectionListener(new RemoveItemSelectionListener());
                    for (int i=0; i<adders.length; i++) {
                        MenuItem addSubItem = new MenuItem(addSubMenu,SWT.NONE);
                        addSubItem.setText(adders[i].getName());
                        addSubItem.setData("viewer",simViewer);
                        addSubItem.setData("newClass",adders[i]);
                        addSubItem.addSelectionListener(new AddItemSelectionListener());
                    }
                }
            }
        }
        
        private final MenuItem removeItem, addItem;
    }
}
