package etomica.plugin.wrappers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.action.Action;
import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.listeners.ActionSelectionListener;

/**
 * MenuItemWrapper class for the Actions cascading menu
 * @author Andrew Schultz
 */
public class ActionListItemWrapper extends MenuItemCascadeWrapper {

    public ActionListItemWrapper() {
        super("Actions");
    }
    
    public int getPositionPriority() {
        return 10;
    }
    
    /**
     * MenuItemWrapper class for individual Action items.  Clicking on the item
     * invokes the Action
     */
    public static class ActionItemWrapper extends MenuItemWrapper {
        public ActionItemWrapper(Action action) {
            this.action = action;
        }
        
        public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
            MenuItem addClassItem = new MenuItem(menu, SWT.NONE);
            addClassItem.setText(action.getLabel());
            addClassItem.addSelectionListener(new ActionSelectionListener(editor, viewer, action));
        }
        
        public Action getAction() {
            return action;
        }
        
        public boolean equals(Object otherActionItemWrapper) {
            return super.equals(otherActionItemWrapper) && 
                   (((ActionItemWrapper)otherActionItemWrapper).getAction().getLabel().equals(action.getLabel()));
        }
        
        public int compareTo(Object otherActionItemWrapper) {
            return action.getLabel().compareTo((((ActionItemWrapper)otherActionItemWrapper).getAction().getLabel()));
        }
        
        protected final Action action;
    }
}
