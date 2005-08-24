/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.ide.ui.simulationview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import etomica.action.activity.ActivityGroupParallel;
import etomica.action.activity.ActivityGroupSeries;
import etomica.ide.ui.propertiesview.PropertySourceWrapper;
import etomica.simulation.Simulation;
import etomica.simulation.SimulationEvent;
import etomica.simulation.SimulationListener;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationViewContentProvider implements ITreeContentProvider, SimulationListener {

    public SimulationViewContentProvider() {
        etomica.simulation.instantiationEventManager.addListener(this); 
        
    }
	/**
	 * Simulation is root.
	 * Controller is child of simulation.
	 * ActivityGroups are parents of actions/activities
	 */
	public Object[] getChildren(Object wrappedElement) {
		Object parentElement = ((PropertySourceWrapper)wrappedElement).getObject();
        if(parentElement instanceof ActivityGroupParallel) {
            return PropertySourceWrapper.wrapArrayElements(((ActivityGroupParallel)parentElement).getActions());
        } else if(parentElement instanceof ActivityGroupSeries) {//temporary
                return PropertySourceWrapper.wrapArrayElements(((ActivityGroupSeries)parentElement).pendingActions());
		} else if(parentElement instanceof Simulation) {
			Simulation sim = (Simulation)parentElement;
			return PropertySourceWrapper.wrapArrayElements(new Object[] {sim.getController()});
		} else return new PropertySourceWrapper[0];
	}
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    //the call to viewer.setInput in createPartControl causes the list of
    //simulation instances to be the input element in this method
    public Object[] getElements(Object inputElement) {
        Object[] elements = ((java.util.LinkedList)inputElement).toArray();
        PropertySourceWrapper[] wrappedElements = PropertySourceWrapper.wrapArrayElements(elements);
        return wrappedElements;
    }


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		System.out.println("SimulationViewContentProvide.getParent");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object wrappedElement) {
		Object element = ((PropertySourceWrapper)wrappedElement).getObject();
        if(element instanceof ActivityGroupParallel) {
            return ((ActivityGroupParallel)element).getActions().length > 0;
        } else if(element instanceof ActivityGroupSeries) {
            return ((ActivityGroupSeries)element).pendingActions().length > 0;
		} else if(element instanceof Simulation) {
			return true;
		} else return false;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
        etomica.simulation.instantiationEventManager.removeListener(this);
		viewer = null;
	}

    
    public void actionPerformed(SimulationEvent evt) {
        viewer.refresh();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer)viewer;
        currentSelection = newInput;
    }
    
    Object currentSelection;

	private TreeViewer viewer;
}
