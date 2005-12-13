package etomica.plugin.editors.listeners;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import etomica.action.Action;
import etomica.action.activity.Controller;

/**
 * Listener that fires when an "action" MenuItem is selected.  It retrieves
 * the action from the menu item and invokes it.
 */
public class OpenActionListener implements IDoubleClickListener {

    public void doubleClick(DoubleClickEvent event) {
        openSelectedItem((TreeViewer)event.getViewer());
    }
    
    private void openSelectedItem(TreeViewer viewer) {
        TreeItem selectedItem = viewer.getTree().getSelection()[0];
        Object obj = selectedItem.getData();
        if (obj instanceof Action) {
            Exception exception = ((Controller)viewer.getInput()).getException((Action)obj);
            if (exception != null) {
                ErrorDialog.openError(viewer.getControl().getShell(), "Action Failed", exception.getMessage(), 
                        new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, exception.getMessage(), null));
                //PDE Error log uses EventDetailsDialog
                //http://landfill.mozilla.org/mxr-test/eclipse/source/eclipse/org.eclipse.pde.runtime/src/org/eclipse/pde/internal/runtime/logview/EventDetailsDialog.java
            }
        }
    }
    
}