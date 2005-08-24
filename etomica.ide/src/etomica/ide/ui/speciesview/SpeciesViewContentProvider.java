/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.ide.ui.speciesview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import etomica.atom.Atom;
import etomica.atom.AtomTreeNodeGroup;
import etomica.atom.SpeciesAgent;
import etomica.ide.ui.propertiesview.PropertySourceWrapper;
import etomica.phase.Phase;
import etomica.simulation.Simulation;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SpeciesViewContentProvider implements ITreeContentProvider {

	/**
	 * Simulation is root.
	 * Phases are children of simulation.
	 * Agents are children of phases, and rest of hierarchy follows from there.
	 */
	public Object[] getChildren(Object wrappedElement) {
		Object parentElement = ((PropertySourceWrapper)wrappedElement).getObject();
		if(parentElement instanceof Atom) {
			return getAtomChildren((Atom)parentElement);
		} else if(parentElement instanceof Simulation) {
			Simulation sim = (Simulation)parentElement;
			return PropertySourceWrapper.wrapArrayElements(sim.getPhaseList().toArray());
		} else if(parentElement instanceof Phase) {
			return getAtomChildren(((Phase)parentElement).speciesMaster());
		} else return new PropertySourceWrapper[0];
	}

	//used by getChildren
	private Object[] getAtomChildren(Atom atom) {
		if(atom.node instanceof AtomTreeNodeGroup) {
			return PropertySourceWrapper.wrapArrayElements(((AtomTreeNodeGroup)atom.node).childList.toArray());
		} else return new PropertySourceWrapper[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		System.out.println("SpeciesViewContentProvide.getParent");
		if(element instanceof Phase) return null;//((Phase)element).simulation();
		else if(element instanceof SpeciesAgent) return ((SpeciesAgent)element).node.parentPhase();
		else if(element instanceof Atom) return ((Atom)element).node.parentGroup();
		else return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object wrappedElement) {
		Object element = ((PropertySourceWrapper)wrappedElement).getObject();
		if(element instanceof Atom) {
			return atomHasChildren((Atom)element);
		} else if(element instanceof Simulation) {
			Simulation sim = (Simulation)element;
			return sim.getPhaseList().size() > 0;
		} else if(element instanceof Phase) {
			return atomHasChildren(((Phase)element).speciesMaster);
		} else return false;
	}
	
	//used by hasChildren
	private boolean atomHasChildren(Atom atom) {
		if(atom.node.isLeaf()) return false;
		return (((AtomTreeNodeGroup)atom.node).childList.size() > 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		viewer = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	private TreeViewer viewer;
}
