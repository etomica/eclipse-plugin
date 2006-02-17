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
    
    public ActionSelectionListener(EtomicaEditor editor) {
        etomicaEditor = editor;
    }
    
    public void widgetSelected(SelectionEvent e){
        TreeViewer simViewer = (TreeViewer)e.widget.getData("viewer");
        //retrieve the selected tree item from the tree so we can get its parent
        TreeItem selectedItem = simViewer.getTree().getSelection()[0];
        Action action = (Action)e.widget.getData("action");
        action.actionPerformed();
        simViewer.refresh(selectedItem);
        etomicaEditor.markDirty();
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    private final EtomicaEditor etomicaEditor;
}