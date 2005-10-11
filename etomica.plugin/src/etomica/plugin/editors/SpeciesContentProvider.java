/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;

import etomica.action.ActionGroup;
import etomica.action.activity.ActivityIntegrate;
import etomica.atom.Atom;
import etomica.atom.AtomTreeNode;
import etomica.atom.AtomTreeNodeGroup;
import etomica.atom.SpeciesRoot;
import etomica.integrator.Integrator;
import etomica.phase.Phase;
import etomica.plugin.wrappers.PropertySourceWrapper;
import etomica.simulation.Simulation;
import etomica.simulation.SimulationEvent;
import etomica.simulation.SimulationListener;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpeciesContentProvider implements ITreeContentProvider, SimulationListener {

    public SpeciesContentProvider() {
    }
	/**
	 * Simulation is root.
	 * Controller is child of simulation.
	 * ActivityGroups are parents of actions/activities
	 */
	public Object[] getChildren(Object speciesRoot) {
        return ((SpeciesRoot)speciesRoot).getSpecies();
	}
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    //the call to viewer.setInput in createPartControl causes the list of
    //simulation instances to be the input element in this method
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
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
        if(element instanceof ActionGroup) {
            return ((ActionGroup)element).getAllActions().length > 0;
		} else if(element instanceof Simulation) {
			return true;
		} else if (element instanceof ActivityIntegrate) {
            return true;
        } else if (element instanceof Integrator) {
            return ((Integrator)element).getPhase().length > 0;
        } else if (element instanceof Phase) {
            return true;
        } else if (element instanceof Atom) {
            AtomTreeNode node = ((Atom)element).node;
            if (node instanceof AtomTreeNodeGroup) {
                return ((AtomTreeNodeGroup)node).childList.size() > 0;
            }
            return false;
        }
        return false;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
        //Simulation.instantiationEventManager.removeListener(this);
		viewer = null;
	}

    
    public void actionPerformed(SimulationEvent evt) {
        viewer.refresh();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (ListViewer)viewer;
        currentSelection = newInput;
    }
    
    Object currentSelection;

	private ListViewer viewer;
}
