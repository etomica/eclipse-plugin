package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;

import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * Listener that fires when an "action" MenuItem is selected.  It retrieves
 * the action from the menu item and invokes it.
 */
public class OpenSelectionListener implements SelectionListener {
    public OpenSelectionListener(IWorkbenchPage page) {
        workbenchPage = page;
    }
    
    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    public void widgetSelected(SelectionEvent e){
        TreeViewer simViewer = (TreeViewer)e.widget.getData("viewer");
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        Object selectedObj = selectedItem.getData();

        if (selectedObj instanceof PropertySourceWrapper) {
            String openView = (String)e.widget.getData("openView");
            ((PropertySourceWrapper)selectedObj).open(openView,workbenchPage,simViewer.getControl().getShell());
        }
    }
    
    private final IWorkbenchPage workbenchPage;
}