package etomica.plugin.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import etomica.util.Arrays;

/**
 * Parent wrapper class for cascading MenuItems.  Contains helper classes for
 * dealing with submenu items.
 */
public abstract class MenuItemCascadeWrapper extends MenuItemWrapper {

    public MenuItemCascadeWrapper(String text) {
        submenuWrapperItems = new MenuItemWrapper[0];
        this.text = text;
    }
    
    /**
     * Add unique submenu items (from another MenuItemCascadeWrapper) to this
     * menu.  Submenu items with the same "text" are skipped.
     */
    public void addSubmenuItems(MenuItemWrapper[] moreItems) {
        for (int i=0; i<moreItems.length; i++) {
            addSubmenuItem(moreItems[i]);
        }
    }

    /**
     * Add unique submenu items (from another MenuItemCascadeWrapper) to this
     * menu.  Submenu items with the same "text" are skipped.
     */
    public void addSubmenuItem(MenuItemWrapper newItem) {
        for (int j=0; j<submenuWrapperItems.length; j++) {
            if (newItem.equals(submenuWrapperItems[j])) {
                return;
            }
        }
        submenuWrapperItems = (MenuItemWrapper[])Arrays.addObject(submenuWrapperItems, newItem);
        java.util.Arrays.sort(submenuWrapperItems);
    }
    
    public boolean equals(Object anotherItemWrapper) {
        // even same-class cascade menus aren't the "same".  They should be combined
        return false;
    }
    
    public MenuItemWrapper[] getSubmenuWrapperItems() {
        return submenuWrapperItems;
    }
    
    public void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor) {
        MenuItem addItem = new MenuItem(menu,SWT.CASCADE);
        addItem.setText(text);
        Menu addSubMenu = new Menu(addItem);
        addItem.setMenu(addSubMenu);
        // stash the viewer in the MenuItem so the listeners can get it
        addItem.setData(viewer);
        
        for (int i=0; i<submenuWrapperItems.length; i++) {
            submenuWrapperItems[i].addItemToMenu(addSubMenu, viewer, editor);
        }
    
    }

    protected MenuItemWrapper[] submenuWrapperItems;
    protected final String text;
}
