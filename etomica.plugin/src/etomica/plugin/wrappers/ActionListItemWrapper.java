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
        public ActionItemWrapper(Action action, String label) {
            this.action = action;
            this.label = label;
        }
        
        public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
            MenuItem addClassItem = new MenuItem(menu, SWT.NONE);
            addClassItem.setText(label);
            addClassItem.addSelectionListener(new ActionSelectionListener(editor, viewer, action));
        }
        
        public Action getAction() {
            return action;
        }
        
        public String getLabel() {
            return label;
        }
        
        public boolean equals(Object otherActionItemWrapper) {
            return super.equals(otherActionItemWrapper) && 
                   (((ActionItemWrapper)otherActionItemWrapper).getLabel().equals(label));
        }
        
        public int compareTo(Object otherActionItemWrapper) {
            return label.compareTo((((ActionItemWrapper)otherActionItemWrapper).getLabel()));
        }
        
        protected final Action action;
        protected final String label;
    }
}
