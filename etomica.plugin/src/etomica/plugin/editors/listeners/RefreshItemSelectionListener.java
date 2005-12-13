package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.wrappers.ArrayWrapper;

/**
 * Listener that fires when the refresh MenuItem is selected.
 * Refreshes the Simulation view tree from the selected element.
 */
public class RefreshItemSelectionListener implements SelectionListener {
    public void widgetSelected(SelectionEvent e){
        TreeViewer simViewer = (TreeViewer)e.widget.getData();
        //retrieve the object from the tree viewer directly
        TreeItem[] selection = simViewer.getTree().getSelection();
        if (selection.length == 0) {
            simViewer.refresh();
            return;
        }
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        TreeItem parentItem = selectedItem.getParentItem();
        if (parentItem == null) {
            simViewer.refresh();
            return;
        }
        Object parentObj = parentItem.getData();
        while (parentObj instanceof ArrayWrapper) {
            parentItem = parentItem.getParentItem();
            if (parentItem == null) {
                simViewer.refresh();
                return;
            }
            parentObj = selectedItem.getData();
        }
        simViewer.refresh(parentObj);
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
}