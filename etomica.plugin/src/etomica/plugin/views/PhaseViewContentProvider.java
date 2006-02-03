/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import etomica.atom.Atom;
import etomica.atom.AtomArrayList;
import etomica.atom.AtomList;
import etomica.atom.AtomTreeNode;
import etomica.atom.AtomTreeNodeGroup;
import etomica.atom.iterator.AtomIteratorListSimple;
import etomica.phase.Phase;
import etomica.plugin.wrappers.PropertySourceWrapper;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PhaseViewContentProvider implements ITreeContentProvider {

    public PhaseViewContentProvider() {}
	/**
	 * Simulation is root.
	 * Controller is child of simulation.
	 * ActivityGroups are parents of actions/activities
	 */
    public Object[] getChildren(Object wrappedElement) {
        AtomTreeNode node = ((Atom)((PropertySourceWrapper)wrappedElement).getObject()).node;
        if (!(node instanceof AtomTreeNodeGroup)) {
            return new Object[0];
        }
        return wrapAtomList(((AtomTreeNodeGroup)node).childList);
    }
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    //the call to viewer.setInput in createPartControl causes the list of
    //simulation instances to be the input element in this method
    public Object[] getElements(Object inputElement) {
        Phase phase = (Phase)((PropertySourceWrapper)inputElement).getObject();
        AtomArrayList agentList = ((AtomTreeNodeGroup)phase.getSpeciesMaster().node).childList;
        return wrapAtomList(agentList);
    }
    
    private PropertySourceWrapper[] wrapAtomList(AtomArrayList list) {
        PropertySourceWrapper[] wrappers = new PropertySourceWrapper[list.size()];
        for (int i=0; i<list.size(); i++) {
            wrappers[i++] = PropertySourceWrapper.makeWrapper(list.get(i));
        }
        return wrappers;
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
        return getChildren(wrappedElement).length > 0;
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
        //Simulation.instantiationEventManager.removeListener(this);
		viewer = null;
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
