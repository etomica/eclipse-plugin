/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.plugin.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import etomica.atom.Atom;
import etomica.atom.AtomArrayList;
import etomica.atom.AtomGroup;
import etomica.phase.Phase;
import etomica.plugin.wrappers.PropertySourceWrapper;


public class PhaseViewContentProvider implements ITreeContentProvider {

    public PhaseViewContentProvider() {}

    public Object[] getChildren(Object wrappedElement) {
        // wrapped element is an atom.  If it's a parent, return the children
        Atom atom = (Atom)((PropertySourceWrapper)wrappedElement).getObject();
        if (!(atom instanceof AtomGroup)) {
            return new Object[0];
        }
        return wrapAtomList(((AtomGroup)atom).getChildList());
    }
    
    /**
     * @param inputElement a linked list containing the simulation instances,
     * coming from Simulation.getInstances
     */
    //the call to viewer.setInput in createPartControl causes the list of
    //simulation instances to be the input element in this method
    public Object[] getElements(Object inputElement) {
        Phase phase = (Phase)((PropertySourceWrapper)inputElement).getObject();
        AtomArrayList agentList = ((AtomGroup)phase.getSpeciesMaster()).getChildList();
        return wrapAtomList(agentList);
    }
    
    private PropertySourceWrapper[] wrapAtomList(AtomArrayList list) {
        PropertySourceWrapper[] wrappers = new PropertySourceWrapper[list.size()];
        for (int i=0; i<list.size(); i++) {
            wrappers[i] = PropertySourceWrapper.makeWrapper(list.get(i));
        }
        return wrappers;
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
