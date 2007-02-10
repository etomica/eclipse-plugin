package etomica.plugin.wrappers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.listeners.RemoveItemSelectionListener;

/**
 * Wrapper for a MenuItem to remove the selected item
 * @author Andrew Schultz.
 */
public class RemoveItemWrapper extends MenuItemWrapper {

    public RemoveItemWrapper(RemoverWrapper parentWrapper) {
        this.parentWrapper = parentWrapper;
    }
    
    public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
        MenuItem menuItem = new MenuItem(menu,SWT.NONE);
        menuItem.setText("Remove");
        menuItem.addSelectionListener(new RemoveItemSelectionListener(editor, viewer, parentWrapper));
    }
    
    public int getPositionPriority() {
        return 2;
    }
    
    protected final RemoverWrapper parentWrapper;
}
