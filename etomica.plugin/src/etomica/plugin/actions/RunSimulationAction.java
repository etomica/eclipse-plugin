/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import etomica.action.activity.Controller;
import etomica.action.activity.ControllerEvent;
import etomica.action.activity.ControllerListener;
import etomica.simulation.Simulation;
import etomica.simulation.SimulationEvent;

/**
 * Action that causes simulation to resume execution.  
 */
public class RunSimulationAction extends Action implements ControllerListener {

	/**
	 * Constructs action and associates "resume" icon.
	 */
	public RunSimulationAction() {
		super("Run simulation");
		ImageDescriptor eImage = ImageDescriptor.createFromFile(RunSimulationAction.class, "enabled/run_exc.gif");
		ImageDescriptor dImage = ImageDescriptor.createFromFile(RunSimulationAction.class, "disabled/run_exc.gif");
		setImageDescriptor(eImage);
		setDisabledImageDescriptor(dImage);
		setToolTipText("Start Simulation");
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
		&& !simulation.getController().isActive();  //remove not
		setEnabled(enabled);
	}
	
	/**
	 * Causes most recently set simulation to resume execution.
	 * Performs no action if simulation has not be set, or is null.
	 */
	public void run() {
		if(simulation == null) return;
		Controller controller = simulation.getController();
		if(controller == null || controller.isActive()) return;
		controller.start();	
		//view.setSimulation(simulation);//update all buttons
	}
	
	//may not need this
	public void dispose() {
//		image.dispose();
	}
	
	private Simulation simulation;
	private ImageDescriptor image;
	/* (non-Javadoc)
	 * @see etomica.ControllerListener#actionPerformed(etomica.ControllerEvent)
	 */
	public void actionPerformed(ControllerEvent event) {
		if ( event.getType()==ControllerEvent.START )
		{
			this.setEnabled( false );
		}
		else if ( event.getType()==ControllerEvent.NO_MORE_ACTIONS )
		{
			this.setEnabled( true );
		}
		else if ( event.getType()==ControllerEvent.HALTED )
		{
			this.setEnabled( true );
		}
	}

	/* (non-Javadoc)
	 * @see etomica.SimulationListener#actionPerformed(etomica.SimulationEvent)
	 */
	public void actionPerformed(SimulationEvent event) {
	}
}
