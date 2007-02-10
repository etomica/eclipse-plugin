package etomica.plugin.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;

/**
 * A "wrapper" for a menu item.  Creating this object does not actually create
 * the menu item.  The wrapper acts like a factory for the menu item.
 * @author Andrew Schultz
 */
public abstract class MenuItemWrapper implements Comparable {

    /**
     * Adds the menu item associated with this wrapper to the given menu.  The
     * TreeViewer and EtomicaEditor are included primarily to be passed on to
     * the selection listener, which might want them 
     * @param menu
     * @param viewer
     * @param editor
     */
    public abstract void addItemToMenu(Menu menu, TreeViewer viewer, EtomicaEditor editor);
    
    /**
     * Returns true if the "other" wrapper accomplishes the same task as this
     * one -- meaning they should not both be included.
     */
    public boolean equals(Object otherItemWrapper) {
        // by default, assume wrappers of the same class accomplish the same
        // task
        return this.getClass() == otherItemWrapper.getClass();
    }
    
    public int compareTo(Object otherItemWrapper) {
        // use "priority" first
        int otherPriority = ((MenuItemWrapper)otherItemWrapper).getPositionPriority();
        if (getPositionPriority() < otherPriority) return -1;
        if (getPositionPriority() > otherPriority) return 1;
        // if equal priority, sort by class name alphabetically
        return getClass().getName().compareTo(otherItemWrapper.getClass().getName());
    }
    
    /**
     * Returns sorting priority of this item.  Lower values are placed nearer
     * the top of the menu.
     */
    public int getPositionPriority() {return 100;}
}
