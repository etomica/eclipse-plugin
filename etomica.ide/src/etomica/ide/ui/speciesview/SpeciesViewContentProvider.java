/*
 * History
 * Created on Sep 20, 2004 by kofke
 */
package etomica.ide.ui.speciesview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import etomica.*;

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
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof Atom) {
			Atom atom = (Atom)parentElement;
			if(atom.node instanceof AtomTreeNodeGroup) {
				return ((AtomTreeNodeGroup)atom.node).childList.toArray();
			} else return new Object[0];
		} else if(parentElement instanceof Simulation) {
			Simulation sim = (Simulation)parentElement;
			return sim.phaseList().toArray();
		} else if(parentElement instanceof Phase) {
			return getChildren(((Phase)parentElement).speciesMaster());
		} else return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if(element instanceof Phase) return ((Phase)element).simulation();
		else if(element instanceof SpeciesAgent) return ((SpeciesAgent)element).node.parentPhase();
		else if(element instanceof Atom) return ((Atom)element).node.parentGroup();
		else return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if(element instanceof Atom) {
			Atom atom = (Atom)element;
			if(atom.node.isLeaf()) return false;
			return (((AtomTreeNodeGroup)atom.node).childList.size() > 0);
		} else if(element instanceof Simulation) {
			Simulation sim = (Simulation)element;
			return sim.phaseList().size() > 0;
		} else if(element instanceof Phase) {
			return hasChildren(((Phase)element).speciesMaster);
		} else return false;
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
