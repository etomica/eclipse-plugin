/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import etomica.action.activity.Controller;
import etomica.ide.ui.simulationview.SimulationView;
import etomica.simulation.Simulation;

/**
 * Action that causes simulation to resume execution.  
 */
public class ResumeSimulationAction extends Action {

	/**
	 * Constructs action and associates "resume" icon.
	 */
	public ResumeSimulationAction(SimulationView view) {
		super("Resume simulation");
		ImageDescriptor eImage = ImageDescriptor.createFromFile(ResumeSimulationAction.class, "enabled/resume_co.gif");
		ImageDescriptor dImage = ImageDescriptor.createFromFile(ResumeSimulationAction.class, "disabled/resume_co.gif");
		setImageDescriptor(eImage);
		setDisabledImageDescriptor(dImage);
		setToolTipText("Resume Simulation");
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
		&& simulation.getController().isActive()
		&& simulation.getController().isPaused();
		setEnabled(enabled);
//		System.out.println("ResumeSimulationAction setsimulation "+simulation);
	}
	
	/**
	 * Causes most recently set simulation to resume execution.
	 * Performs no action if simulation has not be set, or is null.
	 */
	public void run() {
//		System.out.println("ResumeSimulationAction run");
		if(simulation == null) return;
		Controller controller = simulation.getController();
		if(controller == null) return;
		if(controller.isActive()) controller.unPause();
//		else controller.start();	
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
