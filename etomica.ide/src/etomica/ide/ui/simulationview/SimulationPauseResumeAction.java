/*
 * History
 * Created on Oct 3, 2004 by kofke
 */
package etomica.ide.ui.simulationview;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import etomica.Simulation;

/**
 * Simulation view action that pauses/resumes the selected simulation.
 */
public class SimulationPauseResumeAction implements IViewActionDelegate {

	/**
	 * 
	 */
	public SimulationPauseResumeAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		simulationView = (SimulationView)view;
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
		// TODO Auto-generated method stub

	}

	SimulationView simulationView;

}
