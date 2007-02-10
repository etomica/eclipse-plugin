package etomica.plugin.wrappers;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.plugin.editors.EtomicaEditor;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.listeners.OpenSelectionListener;

/**
 * MenuItem wrapper for the "Open as" cascading menu.
 * @author Andrew Schultz
 */
public class OpenItemWrapper extends MenuItemCascadeWrapper {

    public OpenItemWrapper() {
        super("Open as");
    }
    
    public int getPositionPriority() {
        return 4;
    }
    
    public static class OpenViewItemWrapper extends MenuItemWrapper {
        public OpenViewItemWrapper(String viewName, OpenerWrapper openerWrapper) {
            this.viewName = viewName;
            this.openerWrapper = openerWrapper;
        }
        
        public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
            MenuItem openViewItem = new MenuItem(menu, SWT.NONE);
            openViewItem.setText(viewName);
            openViewItem.addSelectionListener(new OpenSelectionListener(editor.getSite().getPage(), viewer, viewName, openerWrapper));
        }
        
        public String getViewName() {
            return viewName;
        }
        
        public boolean equals(Object otherAddClassItemWrapper) {
            return super.equals(otherAddClassItemWrapper) && 
                   (((OpenViewItemWrapper)otherAddClassItemWrapper).getViewName().equals(viewName));
        }
        
        public int compareTo(Object otherOpenViewItemWrapper) {
            return viewName.compareTo((((OpenViewItemWrapper)otherOpenViewItemWrapper).getViewName()));
        }
        
        protected final String viewName;
        protected final OpenerWrapper openerWrapper;
    }
}
