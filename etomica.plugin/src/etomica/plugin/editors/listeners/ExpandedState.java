/**
 * 
 */
package etomica.plugin.editors.listeners;

import java.util.LinkedList;

import org.eclipse.swt.widgets.TreeItem;

/**
 * This object remembers the expanded state of the children of a TreeItem (or
 * the tree root itself).  Each ExpandedState holds a list of ExpandedStates that
 * correspond to its own child items that are expanded.
 *
 * @author Andrew Schultz
 */
public class ExpandedState {
    
    /**
     * Constructs an instance for the given TreeItem as the root.
     */
    public ExpandedState(TreeItem parentItem) {
        this(parentItem.getData(), parentItem.getItems());
    }
    
    /**
     * Constructs an instance for the given object (the return value of
     * treeItem.getData()) and the children.  This constructor can be
     * used for the tree root which is not actually a TreeItem.
     */
    public ExpandedState(Object obj, TreeItem[] childItems) {
        childList = new LinkedList();
        this.obj = obj;
        addChildren(childItems);
    }

    /**
     * Returns the object for this link.
     */
    public Object getObject() {
        return obj;
    }
    
    /**
     * Adds the items from the array of TreeItems that are expanded to
     * this objects list of expanded children.
     */
    protected void addChildren(TreeItem[] children) {
        for (int i=0; i<children.length; i++) {
            if (children[i].getExpanded()) {
                ExpandedState childLink = new ExpandedState(children[i]);
                childList.add(childLink);
            }
        }
    }

    /**
     * Returns an array of ExpandedLinks corresponding to this element's 
     * expanded children.
     */
    public ExpandedState[] getChildren() {
        return (ExpandedState[])childList.toArray(new ExpandedState[childList.size()]);
    }
    
    protected final Object obj;
    protected final LinkedList childList;
}