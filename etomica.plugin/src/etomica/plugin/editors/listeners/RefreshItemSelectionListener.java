package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;

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

        //Construct tree of expanded items.  Root isn't actually a tree item, so 
        //The top-level structure is for the children of the root.
        ExpandedState[] linkTrees = new ExpandedState(null, simViewer.getTree().getItems()).getChildren();

        //retrieve the object from the tree viewer directly
        TreeItem[] selection = simViewer.getTree().getSelection();
        
        if (selection.length == 0) {
            //nothing selected, refresh everything
            simViewer.refresh();
            resetExpansion(simViewer, simViewer.getTree().getItems(), linkTrees);
            return;
        }
        
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        TreeItem parentItem = selectedItem.getParentItem();
        if (parentItem == null) {
            //top-level item selected, refresh everything
            simViewer.refresh();
            resetExpansion(simViewer, simViewer.getTree().getItems(), linkTrees);
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
                resetExpansion(simViewer, simViewer.getTree().getItems(), linkTrees);
                return;
            }
            parentObj = selectedItem.getData();
        }
        
        //refresh the parent
        simViewer.refresh(parentObj);
        
        resetExpansion(simViewer, simViewer.getTree().getItems(), linkTrees);
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    /**
     * Resets the expanded state of tree items from the ExpandedLink tree
     */
    protected void resetExpansion(TreeViewer simViewer, TreeItem[] childItems, ExpandedState[] children) {
        for (int i=0; i<children.length; i++) {
            Object child = children[i].getObject();
            if (child instanceof PropertySourceWrapper) {
                child = ((PropertySourceWrapper)child).getObject();
            }

            for (int j=0; j<childItems.length; j++) {
                Object childItem = childItems[j].getData();
                if (childItem instanceof PropertySourceWrapper) {
                    childItem = ((PropertySourceWrapper)childItem).getObject();
                }

                // Compare the underlying objects rather than the TreeItems or even their 
                // "data" (which are generally PropertySourceWrappers generated on the fly).
                // For arrays, require only that they arrays of the same type of object 
                // since the arrays will sometimes be generated on the fly as well.
                if (childItem == child || (childItem instanceof Object[] && 
                        childItem.getClass().getComponentType() == child.getClass().getComponentType())) {
                    simViewer.setExpandedState(childItems[j].getData(),true);
                    resetExpansion(simViewer, childItems[j].getItems(), children[i].getChildren());
                    break;
                }
            }
        }
    }
    
}