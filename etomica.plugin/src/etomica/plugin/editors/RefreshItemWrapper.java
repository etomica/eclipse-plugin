package etomica.plugin.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.plugin.editors.listeners.RefreshItemSelectionListener;

/**
 * Wrapper for a refresh menu item.
 * @author Andrew Schultz
 */
public class RefreshItemWrapper extends MenuItemWrapper {

    public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
        MenuItem menuItem = new MenuItem(menu,SWT.NONE);
        menuItem.setText("Refresh");
        menuItem.addSelectionListener(new RefreshItemSelectionListener(viewer));
    }

    public int getPositionPriority() {
        return 0;
    }
    
}
