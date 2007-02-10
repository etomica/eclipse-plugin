package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IWorkbenchPage;

import etomica.plugin.wrappers.OpenerWrapper;

/**
 * Listener that fires when an "action" MenuItem is selected.  It retrieves
 * the action from the menu item and invokes it.
 */
public class OpenSelectionListener implements SelectionListener {
    public OpenSelectionListener(IWorkbenchPage page, TreeViewer viewer, String openView, OpenerWrapper wrapper) {
        workbenchPage = page;
        this.viewer = viewer;
        this.openView = openView;
        this.wrapper = wrapper;
    }
    
    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    public void widgetSelected(SelectionEvent e){
        wrapper.open(openView, workbenchPage, viewer.getControl().getShell());
    }
    
    private final IWorkbenchPage workbenchPage;
    private final TreeViewer viewer;
    private final String openView;
    private final OpenerWrapper wrapper;
}