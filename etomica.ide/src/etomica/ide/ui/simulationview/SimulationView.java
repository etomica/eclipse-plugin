/*
 * History
 * Created on Sep 26, 2004 by kofke
 */
package etomica.ide.ui.simulationview;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import etomica.Simulation;
import etomica.SimulationEvent;
import etomica.SimulationListener;
import etomica.ide.actions.ResumeSimulationAction;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimulationView extends ViewPart implements ISelectionChangedListener {

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
		createToolbarButtons();
	}
	
	private void createToolbarButtons() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		resume = new ResumeSimulationAction();
		toolBarManager.add(resume);
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
		if(this.viewer != null) this.viewer.removeSelectionChangedListener(this);
		this.viewer = viewer;
		viewer.addSelectionChangedListener(this);
	}
	
	/**
	 * Action performed when user changes selected simulation in viewer's list
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		System.out.println("SimulationView selection changed");
//		if(event == null) return;
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection == null || !(selection.getFirstElement() instanceof Simulation)) {
			resume.setSimulation(null);
			return;
		}
		Simulation sim = (Simulation)selection.getFirstElement();
		resume.setSimulation(sim);
		if(sim == null) return;
		System.out.println("SimulationView simulation:"+sim);
	}
	
	public void dispose() {
		resume.dispose();
		super.dispose();
	}

	private ListViewer viewer;
	private ResumeSimulationAction resume;

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
