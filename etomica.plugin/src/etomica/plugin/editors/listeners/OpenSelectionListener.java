package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ExceptionHandler;

import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * Listener that fires when an "action" MenuItem is selected.  It retrieves
 * the action from the menu item and invokes it.
 */
public class OpenSelectionListener implements SelectionListener {
    public void widgetSelected(SelectionEvent event){
        TreeViewer simViewer = (TreeViewer)event.widget.getData("viewer");
        //retrieve the selected tree item from the tree so we can get its parent
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        Object obj = selectedItem.getData();
        if (obj instanceof PropertySourceWrapper) {
            IWorkbenchPage page = (IWorkbenchPage)event.widget.getData("page");
            ((PropertySourceWrapper)obj).open(page);
        }
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
}