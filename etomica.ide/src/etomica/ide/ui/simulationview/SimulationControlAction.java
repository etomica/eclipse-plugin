/*
 * History
 * Created on Oct 3, 2004 by kofke
 */
package etomica.ide.ui.simulationview;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import etomica.Simulation;
import etomica.ide.actions.ResumeSimulationAction;

/**
 * Simulation view action that pauses/resumes the selected simulation.
 */
public class SimulationControlAction implements IViewActionDelegate, ISelectionChangedListener {

	/**
	 * 
	 */
	public SimulationControlAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		System.out.println("SimulationControlAction init");
		simulationView = (SimulationView)view;
		simulationList = simulationView.getViewer();
		resume = new ResumeSimulationAction();
//		simulationView.getViewer().addSelectionChangedListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		IStructuredSelection selection = (IStructuredSelection)simulationView.getViewer().getSelection();
		if(selection == null) return;
//		if(!(selection.getFirstElement() instanceof Simulation)) return;
		Simulation sim = (Simulation)selection.getFirstElement();
		if(sim == null) return;
		System.out.println("in pause/resume, simulation: "+sim.toString());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("Selection changed");
		if(selection == null) return;
//		if(!(selection.getFirstElement() instanceof Simulation)) return;
		Simulation sim = (Simulation)((IStructuredSelection)selection).getFirstElement();
		resume.setSimulation(sim);
		if(sim == null) return;
		System.out.println("in selectionchanged, simulation: "+sim.toString());
	}
	
	/**
	 * Action performed when user changes selected simulation in viewer's list
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		System.out.println("SimulationControlAction selection changed");
		Simulation sim = (Simulation)event.getSelection();
		if(sim == null) return;
		System.out.println("SimulationControlAction simulation:"+sim.toString());
	}


	SimulationView simulationView;
	ListViewer simulationList;
	ResumeSimulationAction resume;

}
