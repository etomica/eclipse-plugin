package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.wrappers.InterfaceWrapper;
import etomica.plugin.wrappers.RemoverWrapper;
import etomica.plugin.wrappers.SimulationWrapper;

/**
 * Listener that fires when the remove MenuItem is selected.  The 
 * listener attempts to remove the selected item from its parent (in the 
 * Simulation view tree) and then refreshes the tree if the removal was
 * successful.
 */
public class RemoveItemSelectionListener implements SelectionListener {
    public RemoveItemSelectionListener(EtomicaEditor editor, TreeViewer viewer, RemoverWrapper parentWrapper) {
        etomicaEditor = editor;
        this.viewer = viewer;
        this.parentWrapper = parentWrapper;
    }
    
    public void widgetSelected(SelectionEvent e){
        TreeItem selectedItem = viewer.getTree().getSelection()[0];
        Object selectedObj = selectedItem.getData();
        parentWrapper.removeChild(selectedObj);
        if (parentWrapper instanceof SimulationWrapper || parentWrapper instanceof InterfaceWrapper) {
            // refresh the whole thing.
            viewer.refresh(null);
        }
        else {
            viewer.refresh(parentWrapper);
        }
        etomicaEditor.markDirty();
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    private final EtomicaEditor etomicaEditor;
    protected final RemoverWrapper parentWrapper;
    protected final TreeViewer viewer;
}