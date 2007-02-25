package etomica.plugin.editors.listeners;

import java.util.LinkedList;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.plugin.wrappers.EtomicaStatus;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * Listener that fires when an item is selected.  The listener populates
 * the add and remove MenuItems based on information from the 
 * PropertySourceWrapper of the selected item.
 */
public class EtomicaStatusBarUpdater implements ISelectionChangedListener {
    
    public EtomicaStatusBarUpdater(IStatusLineManager statusLineManager) {
        this.statusLineManager = statusLineManager;
    }
    
    public void selectionChanged(SelectionChangedEvent e) {
        if (e.getSelection().isEmpty() || !(e.getSelection() instanceof StructuredSelection)) {
            return;
        }
        
        Object obj = ((StructuredSelection)e.getSelection()).getFirstElement();
        
        if (obj instanceof PropertySourceWrapper) {
            EtomicaStatus status = ((PropertySourceWrapper)obj).getStatus(new LinkedList());
            if (status.type != EtomicaStatus.OK) {
                statusLineManager.setErrorMessage(status.message);
            }
            else {
                // setting to null reverts the message to the non-error message
                statusLineManager.setErrorMessage(null);
            }
        }
    }

    protected void clearSubMenu(Menu subMenu) {
        while (subMenu.getItemCount() > 0) {
            MenuItem item = subMenu.getItem(0);
            item.dispose();
        }
    }
        
    private final IStatusLineManager statusLineManager;
}