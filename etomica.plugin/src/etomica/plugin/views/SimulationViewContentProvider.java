package etomica.plugin.views;

import java.util.LinkedList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * This class determines the contents of the Simulation view (editor).
 */
public class SimulationViewContentProvider implements ITreeContentProvider {

    public SimulationViewContentProvider() {
    }
    
    /**
     * Returns child elements of the given elements to be displayed in the 
     * Simulation view
     */
    public Object[] getChildren(Object wrappedElement) {
        PropertySourceWrapper parentWrapper = (PropertySourceWrapper)wrappedElement;
        return parentWrapper.getChildren(new LinkedList());
    }
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object wrappedElement) {
        return getChildren(wrappedElement).length > 0;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
