/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SummaryViewContentProvider implements ITreeContentProvider {

    public SummaryViewContentProvider() {
    }
    
    /**
     * Simulation is root.
     * Controller is child of simulation.
     * ActivityGroups are parents of actions/activities
     */
    public Object[] getChildren(Object wrappedElement) {
        return ((PropertySourceWrapper)wrappedElement).getChildren();
    }
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    //the call to viewer.setInput in createPartControl causes the list of
    //simulation instances to be the input element in this method
    public Object[] getElements(Object inputElement) {
        return ((PropertySourceWrapper)inputElement).getChildren();
    }


    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object wrappedElement) {
        return ((PropertySourceWrapper)wrappedElement).getChildren().length > 0;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
