/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import etomica.Controller;
import etomica.Simulation;
import etomica.ide.ui.simulationview.SimulationView;

/**
 * Action that causes simulation to resume execution.  
 */
public class TerminateSimulationAction extends Action {

	/**
	 * Constructs action and associates "resume" icon.
	 */
	public TerminateSimulationAction(SimulationView view) {
		super("Terminate simulation");
		ImageDescriptor eImage = ImageDescriptor.createFromFile(TerminateSimulationAction.class, "enabled/terminate_co.gif");
		ImageDescriptor dImage = ImageDescriptor.createFromFile(TerminateSimulationAction.class, "disabled/terminate_co.gif");
		setImageDescriptor(eImage);
		setDisabledImageDescriptor(dImage);
		this.view = view;
	}

	/**
	 * Sets the simulation on which the action will be performed.
	 * If simulation is already running, disables resume button;
	 * if not running, enables button.
	 * @param simulation
	 */
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
		boolean enabled = (simulation != null)
		&& simulation.getController().isActive();
		setEnabled(enabled);
	}
	
	/**
	 * Causes most recently set simulation to resume execution.
	 * Performs no action if simulation has not be set, or is null.
	 */
	public void run() {
		if(simulation == null) return;
		Controller controller = simulation.getController();
		if(controller == null) return;
		controller.halt();	
		view.setSimulation(simulation);//update all buttons
	}
	
	//may not need this
	public void dispose() {
//		image.dispose();
	}
	
	private Simulation simulation;
	private ImageDescriptor image;
	private SimulationView view;
	
}
