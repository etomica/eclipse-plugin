/*
 * History
 * Created on Sep 26, 2004 by kofke
 */
package etomica.ide.ui.simulationview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import etomica.Simulation;
import etomica.SimulationEvent;
import etomica.SimulationListener;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationView extends ViewPart {

	/**
	 * 
	 */
	public SimulationView() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new ListViewer(parent);
		viewer.setContentProvider(new ContentProvider());
		viewer.setInput(Simulation.getInstances());
		setViewer(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
		getSite().setSelectionProvider(viewer);
//		etomica.utility.java2.LinkedList instances = Simulation.getInstances();
//		java.util.LinkedList list = new java.util.LinkedList();
//		Iterator iterator = instances.iterator();
//		while(iterator.hasNext()) {
//			list.add(iterator.next());
//			System.out.println("iterate");
//		}
//		viewer.setInput(list);
	}

	public ListViewer getViewer() {
		return viewer;
	}
	public void setViewer(ListViewer viewer) {
		this.viewer = viewer;
	}
	
	private ListViewer viewer;

	/**
	 * Content provider for ListViewer.
	 */
	public static class ContentProvider implements IStructuredContentProvider, SimulationListener {

		ContentProvider() {
			Simulation.instantiationEventManager.addListener(this);	
		}
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement) {
			return ((etomica.utility.java2.LinkedList)inputElement).toArray();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose() {
			Simulation.instantiationEventManager.removeListener(this);
		}
		
		public void actionPerformed(SimulationEvent evt) {
			viewer.refresh();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.viewer = viewer;
		}
		
		private Viewer viewer;
	}

}
