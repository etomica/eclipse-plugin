package etomica.plugin.wrappers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.listeners.AddItemSelectionListener;

/**
 * MenuItemWrapper class for the Actions cascading menu
 * @author Andrew Schultz
 */
public class AddItemWrapper extends MenuItemCascadeWrapper {

    public AddItemWrapper() {
        super("Add");
    }
    
    public int getPositionPriority() {
        return 6;
    }
    
    /**
     * MenuItemWrapper class for individual items that can be added.  Clicking
     * on the item adds an object to the selected Object, perhaps by invoking
     * a wizard.
     */
    public static class AddClassItemWrapper extends MenuItemWrapper {
        public AddClassItemWrapper(Class addClass, AdderWrapper parentWrapper) {
            this.addClass = addClass;
            this.parentWrapper = parentWrapper;
            displayText = addClass.getName();
        }
        
        public void setDisplayText(String newText) {
            displayText = newText;
        }
        
        public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
            MenuItem addClassItem = new MenuItem(menu, SWT.NONE);
            addClassItem.setText(addClass.getName());
            addClassItem.addSelectionListener(new AddItemSelectionListener(editor, parentWrapper, addClass, viewer));
        }
        
        public Class getAddClass() {
            return addClass;
        }
        
        public boolean equals(Object otherAddClassItemWrapper) {
            return super.equals(otherAddClassItemWrapper) && 
                   (((AddClassItemWrapper)otherAddClassItemWrapper).getAddClass() == addClass);
        }
        
        public int compareTo(Object otherAddClassItemWrapper) {
            return addClass.getName().compareTo((((AddClassItemWrapper)otherAddClassItemWrapper).getAddClass().getName()));
        }
        
        protected final Class addClass;
        protected final AdderWrapper parentWrapper;
        protected String displayText;
    }
}
