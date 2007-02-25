package etomica.plugin.editors;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import etomica.plugin.editors.listeners.ExpandedState;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * Provides Etomica with a TreeViewer that is aware that the TreeItem's objects
 * (PropertySourceWrappers) are subject to change even when then underlying 
 * object stays the same.  So, when refresh is called, the expanded state of 
 * all elements are remembered and restored after the refresh.
 * 
 * @author Andrew Schultz
 */
public class EtomicaTreeViewer extends TreeViewer {

    public EtomicaTreeViewer(Composite parent) {
        super(parent);
    }

    public EtomicaTreeViewer(Composite parent, int style) {
        super(parent, style);
    }

    public EtomicaTreeViewer(Tree tree) {
        super(tree);
    }

    public void refresh(final Object object) {
        // the wrappers cache their status and children.
        if (object != null) {
            ((PropertySourceWrapper)object).refresh();
        }
        else {
            // input will be the SimulationWrapper, but might be null during
            // initialization
            Object root = getInput();
            if (root != null) {
                ((PropertySourceWrapper)root).refresh();
            }
        }

        //Construct tree of expanded items.  Root isn't actually a tree item, so 
        //The top-level structure is for the children of the root.
        ExpandedState[] linkTrees = new ExpandedState(null, getTree().getItems()).getChildren();

        super.refresh(object);

        resetExpansion(getTree().getItems(), linkTrees);
    }

    /**
     * Resets the expanded state of tree items from the ExpandedLink tree
     */
    protected void resetExpansion(TreeItem[] childItems, ExpandedState[] children) {
        for (int i=0; i<children.length; i++) {
            Object child = children[i].getObject();
            if (child instanceof PropertySourceWrapper) {
                child = ((PropertySourceWrapper)child).getObject();
            }

            for (int j=0; j<childItems.length; j++) {
                Object childItem = childItems[j].getData();
                if (childItem instanceof PropertySourceWrapper) {
                    childItem = ((PropertySourceWrapper)childItem).getObject();
                }

                // Compare the underlying objects rather than the TreeItems or even their 
                // "data" (which are generally PropertySourceWrappers generated on the fly).
                // For arrays, require only that they arrays of the same type of object 
                // since the arrays will sometimes be generated on the fly as well.
                if (childItem == child || (childItem instanceof Object[] && 
                        childItem.getClass().getComponentType() == child.getClass().getComponentType())) {
                    setExpandedState(childItems[j].getData(),true);
                    resetExpansion(childItems[j].getItems(), children[i].getChildren());
                    break;
                }
            }
        }
    }

}
