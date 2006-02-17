package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.plugin.wrappers.SimulationWrapper;
import etomica.simulation.Simulation;

/**
 * Listener that fires when an "add" MenuItem is selected.  It finds the
 * PropertySourceWrapper of the parent object and invokes addObjectClass on that
 * wrapper.
 */
public class AddItemSelectionListener implements SelectionListener {
    public AddItemSelectionListener(EtomicaEditor editor) {
        etomicaEditor = editor;
    }
    
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
            etomicaEditor.markDirty();
        }
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    private final EtomicaEditor etomicaEditor;
}