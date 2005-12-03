package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.plugin.wrappers.SimulationWrapper;

/**
 * Listener that fires when the remove MenuItem is selected.  The 
 * listener attempts to remove the selected item from its parent (in the 
 * Simulation view tree) and then refreshes the tree if the removal was
 * successful.
 */
public class RemoveItemSelectionListener implements SelectionListener {
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