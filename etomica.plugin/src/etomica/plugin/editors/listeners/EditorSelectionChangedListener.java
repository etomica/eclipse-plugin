package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import etomica.action.Action;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.plugin.wrappers.SimulationWrapper;
import etomica.util.Arrays;

/**
 * Listener that fires when an item is selected.  The listener populates
 * the add and remove MenuItems based on information from the 
 * PropertySourceWrapper of the selected item.
 */
public class EditorSelectionChangedListener implements ISelectionChangedListener {
    public EditorSelectionChangedListener(MenuItem open, MenuItem remove, 
            MenuItem add, MenuItem action) {
        openItem = open;
        removeItem = remove;
        addItem = add;
        actionItem = action;
    }
    
    public void selectionChanged(SelectionChangedEvent e) {
        if (e.getSelection().isEmpty()) {
            return;
        }
        TreeViewer simViewer = (TreeViewer)e.getSource();
        //retrieve the selected tree item from the tree so we can get its parent
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        Object selectedObj = selectedItem.getData();
        if (selectedObj instanceof PropertySourceWrapper) {
            openItem.setEnabled(((PropertySourceWrapper)selectedObj).canBeOpened());
        }
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
            // fix up add menu item
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
                for (int i=0; i<adders.length; i++) {
                    MenuItem addSubItem = new MenuItem(addSubMenu,SWT.NONE);
                    addSubItem.setText(adders[i].getName());
                    addSubItem.setData("viewer",simViewer);
                    addSubItem.setData("newClass",adders[i]);
                    addSubItem.addSelectionListener(new AddItemSelectionListener());
                }
            }

            // fix up action item
            Action[] actions = ((PropertySourceWrapper)selectedObj).getActions();
            if (actions.length == 0) {
                actionItem.setEnabled(false);
            }
            else {
                actionItem.setEnabled(true);
                Menu actionSubMenu = actionItem.getMenu();
                while (actionSubMenu.getItemCount() > 0) {
                    MenuItem item = actionSubMenu.getItem(0);
                    item.dispose();
                }
                for (int i=0; i<actions.length; i++) {
                    MenuItem actionSubItem = new MenuItem(actionSubMenu,SWT.NONE);
                    actionSubItem.setText(actions[i].getLabel());
                    actionSubItem.setData("viewer",simViewer);
                    actionSubItem.setData("action",actions[i]);
                    actionSubItem.addSelectionListener(new ActionSelectionListener());
                }
            }
        }
    }
    
    private final MenuItem openItem, removeItem, addItem, actionItem;
}