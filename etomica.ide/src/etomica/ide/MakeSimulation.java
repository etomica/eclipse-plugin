/*
 * History
 * Created on Sep 16, 2004 by kofke
 */
package etomica.ide;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import etomica.simulation.Simulation;
import etomica.simulation.prototypes.HSMD2D;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MakeSimulation implements IWorkbenchWindowActionDelegate {

	/**
	 * 
	 */
	public MakeSimulation() {
		super();
        System.err.println("MakeSimulation constructor");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
        System.out.println("MakeSimulation.run");
		simulation = new HSMD2D();
        System.out.println("MakeSimulation.run out");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

	public void dispose() {
	}
	
	public void init(IWorkbenchWindow window) {
	
	}

	Simulation simulation;
	Object obj;
}
