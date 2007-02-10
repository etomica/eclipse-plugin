package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.EtomicaTreeViewer;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.RefreshItemWrapper;
import etomica.plugin.wrappers.ArrayWrapper;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * Listener that fires when an item is selected.  The listener populates
 * the add and remove MenuItems based on information from the 
 * PropertySourceWrapper of the selected item.
 */
public class EditorSelectionChangedListener implements ISelectionChangedListener {
    
    public EditorSelectionChangedListener(Menu menu, EtomicaEditor editor) {
        this.menu = menu;
        etomicaEditor = editor;
    }
    
    public void selectionChanged(SelectionChangedEvent e) {
        clearSubMenu(menu);

        if (e.getSelection().isEmpty()) {
            return;
        }
        EtomicaTreeViewer simViewer = (EtomicaTreeViewer)e.getSource();
        //retrieve the selected tree item from the tree so we can get its parent
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        Object selectedObj = selectedItem.getData();
        
        //retrieve the selected item's parent
        TreeItem parentItem = selectedItem.getParentItem();
        PropertySourceWrapper parentObj = null;
        while (parentItem != null) {
            parentObj = (PropertySourceWrapper)parentItem.getData();
            if (parentObj instanceof ArrayWrapper) {
                //if the parent was an array wrapper, then we really want the array's parent
                parentItem = parentItem.getParentItem();
                continue;
            }
            break;
        }
        if (parentItem == null) {
            // selected item's parent must be the simulation
            parentObj = (PropertySourceWrapper)simViewer.getInput();
        }

        new RefreshItemWrapper().addItemToMenu(menu, simViewer, etomicaEditor);
        
        if (selectedObj instanceof PropertySourceWrapper) {
            MenuItemWrapper[] itemWrappers = ((PropertySourceWrapper)selectedObj).getMenuItemWrappers(parentObj);
            for (int i=0; i<itemWrappers.length; i++) {
                itemWrappers[i].addItemToMenu(menu, simViewer, etomicaEditor);
            }
        }
        
    }

    protected void clearSubMenu(Menu subMenu) {
        while (subMenu.getItemCount() > 0) {
            MenuItem item = subMenu.getItem(0);
            item.dispose();
        }
    }
        
    protected final Menu menu;
    protected final EtomicaEditor etomicaEditor;
}