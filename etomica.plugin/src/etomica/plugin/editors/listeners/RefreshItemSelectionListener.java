package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.wrappers.ArrayWrapper;

/**
 * Listener that fires when the refresh MenuItem is selected.
 * Refreshes the Simulation view tree from the selected element.  The actual 
 * implementation refreshes from the selected element's parent so that changes
 * to the element itself are picked up.  The expanded status of each expanded 
 * item (that still exists after the refresh) is restored.
 */
public class RefreshItemSelectionListener implements SelectionListener {
    
    public void widgetSelected(SelectionEvent e){
        TreeViewer simViewer = (TreeViewer)e.widget.getData();

        //retrieve the object from the tree viewer directly
        TreeItem[] selection = simViewer.getTree().getSelection();
        
        if (selection.length == 0) {
            //nothing selected, refresh everything
            simViewer.refresh();
            return;
        }
        
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        TreeItem parentItem = selectedItem.getParentItem();
        if (parentItem == null) {
            //top-level item selected, refresh everything
            simViewer.refresh();
            return;
        }
        
        //we want to refresh the parent to pick up changes to the element itself
        Object parentObj = parentItem.getData();
        while (parentObj instanceof ArrayWrapper) {
            //array element selected; we want the array's parent
            parentItem = parentItem.getParentItem();
            if (parentItem == null) {
                //top-level array selected, refresh everything
                simViewer.refresh();
                return;
            }
            parentObj = selectedItem.getData();
        }
        
        //refresh the parent
        simViewer.refresh(parentObj);
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
}
