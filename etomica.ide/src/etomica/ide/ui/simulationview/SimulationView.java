/*
 * History
 * Created on Sep 26, 2004 by kofke
 */
package etomica.ide.ui.simulationview;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
import etomica.ide.ui.propertiesview.PropertySourceWrapper;

/**
 * View that presents instantiated simulations in a list, and
 * provides buttons to start/suspend/resume/terminate them.
 */
public class SimulationView extends ViewPart implements ISelectionChangedListener, IPropertyChangeListener {

	public SimulationView() {
		super();
//		System.out.println("SimulationView constructor");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new SimulationViewContentProvider());
        viewer.setLabelProvider(new LabelProvider());
        java.util.LinkedList instances = Simulation.getInstances(); 
		viewer.setInput(Simulation.getInstances());
		setViewer(viewer);
		createToolbarButtons();
		getSite().getPage().getWorkbenchWindow().getWorkbench().getPreferenceStore().addPropertyChangeListener(this);
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
	public TreeViewer getViewer() {
		return viewer;
	}
	
	/**
	 * Sets the given list viewer as the control for this view,
	 * and registers this as a listener for selection changes.
	 * @param viewer
	 */
	public void setViewer(TreeViewer viewer) {
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
		if(selection == null) {
			setSimulation(null);
			return;
		}
		PropertySourceWrapper propertySource = (PropertySourceWrapper)selection.getFirstElement();
		if(propertySource != null && propertySource.getObject() instanceof Simulation) {
    		Simulation sim = (Simulation)propertySource.getObject();
    		setSimulation(sim);
        }
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
	
	public void propertyChange(PropertyChangeEvent event) {
		viewer.refresh();
	}
	private TreeViewer viewer;
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
			currentSelection = newInput;
		}
		
		private Viewer viewer;
		Object currentSelection;
	}

}
