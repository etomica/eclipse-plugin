package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
public class OpenSelectionListener implements SelectionListener, IDoubleClickListener {
    public OpenSelectionListener(IWorkbenchPage page) {
        workbenchPage = page;
    }
    
    public void widgetSelected(SelectionEvent event){
        openSelectedItem((TreeViewer)event.widget.getData("viewer"));
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    public void doubleClick(DoubleClickEvent event) {
        openSelectedItem((TreeViewer)event.getViewer());
    }
    
    private void openSelectedItem(TreeViewer viewer) {
        TreeItem selectedItem = viewer.getTree().getSelection()[0];
        Object obj = selectedItem.getData();
        if (obj instanceof PropertySourceWrapper && ((PropertySourceWrapper)obj).canBeOpened()) {
            ((PropertySourceWrapper)obj).open(workbenchPage,viewer.getControl().getShell());
        }
    }
    
    private final IWorkbenchPage workbenchPage;
}