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
import etomica.ide.actions.RunSimulationAction;
import etomica.ide.actions.SuspendSimulationAction;
import etomica.ide.actions.TerminateSimulationAction;

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
		System.out.println("SimulationView constructor");
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
		run = new RunSimulationAction(this);
		resume = new ResumeSimulationAction(this);
		suspend = new SuspendSimulationAction(this);
		terminate = new TerminateSimulationAction(this);
		toolBarManager.add(run);
		toolBarManager.add(resume);
		toolBarManager.add(suspend);
		toolBarManager.add(terminate);
		setSimulation(null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
		getSite().setSelectionProvider(viewer);
	}

	/**
	 * @return the ListViewer used to display data for this view.
	 */
	public ListViewer getViewer() {
		return viewer;
	}
	
	/**
	 * Sets the given list viewer as the control for this view,
	 * and registers this as a listener for selection changes.
	 * @param viewer
	 */
	public void setViewer(ListViewer viewer) {
		if(this.viewer != null) this.viewer.removeSelectionChangedListener(this);
		this.viewer = viewer;
		viewer.addSelectionChangedListener(this);
	}
	
	/**
	 * Action performed when user changes selected simulation 
	 * in viewer's list.  Implementation of ISelectionChangedListener
	 * interface.
	 */
	public void selectionChanged(SelectionChangedEvent event) {
//		System.out.println("SimulationView selection changed");
//		if(event == null) return;
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		if(selection == null || !(selection.getFirstElement() instanceof Simulation)) {
			setSimulation(null);
			return;
		}
		Simulation sim = (Simulation)selection.getFirstElement();
		setSimulation(sim);
//		System.out.println("SimulationView simulation:"+sim);
	}
	
	/**
	 * Identifies the given simulation as the one currently
	 * selected in this view.  Updates enablement of simulation 
	 * control buttons for the selection.
	 * @param sim
	 */
	public void setSimulation(Simulation sim) {
		run.setSimulation(sim);
		resume.setSimulation(sim);
		suspend.setSimulation(sim);	
		terminate.setSimulation(sim);
	}
	
	public void dispose() {
		run.dispose();
		resume.dispose();
		suspend.dispose();
		terminate.dispose();
		super.dispose();
	}

	private ListViewer viewer;
	private RunSimulationAction run;
	private ResumeSimulationAction resume;
	private SuspendSimulationAction suspend;
	private TerminateSimulationAction terminate;

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
