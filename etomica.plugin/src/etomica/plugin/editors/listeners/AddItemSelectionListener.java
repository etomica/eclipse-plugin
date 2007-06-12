package etomica.plugin.editors.listeners;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.wrappers.AdderWrapper;

/**
 * Listener that fires when an "add" MenuItem is selected.  It finds the
 * PropertySourceWrapper of the parent object and invokes addObjectClass on that
 * wrapper.
 */
public class AddItemSelectionListener implements SelectionListener {
    public AddItemSelectionListener(EtomicaEditor editor, AdderWrapper parentWrapper, 
                                    Class addClass, TreeViewer simViewer) {
        etomicaEditor = editor;
        this.parentWrapper = parentWrapper;
        this.addClass = addClass;
        this.simViewer = simViewer;
    }
    
    public void widgetSelected(SelectionEvent e){
        if (parentWrapper.addObjectClass(addClass,simViewer.getControl().getShell())) {
            simViewer.refresh(null);
            etomicaEditor.markDirty();
        }
    }

    public void widgetDefaultSelected(SelectionEvent e){
        widgetSelected(e);
    }
    
    protected final EtomicaEditor etomicaEditor;
    protected final AdderWrapper parentWrapper;
    protected final Class addClass;
    protected final TreeViewer simViewer;
}