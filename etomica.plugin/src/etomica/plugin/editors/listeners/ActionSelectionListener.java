package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

import etomica.action.Action;
import etomica.plugin.editors.EtomicaEditor;

/**
 * Listener that fires when an "action" MenuItem is selected.  It retrieves
 * the action from the menu item and invokes it.
 */
public class ActionSelectionListener implements SelectionListener {
    
    public ActionSelectionListener(EtomicaEditor editor, TreeViewer viewer, Action action) {
        etomicaEditor = editor;
        this.viewer = viewer;
        this.action = action;
    }
    
    public void widgetSelected(SelectionEvent e){
        //retrieve the selected tree item from the tree so we can get its parent
        TreeItem selectedItem = viewer.getTree().getSelection()[0];
        action.actionPerformed();
        viewer.refresh(selectedItem);
        etomicaEditor.markDirty();
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    private final EtomicaEditor etomicaEditor;
    private final TreeViewer viewer;
    private final Action action;
}