/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import etomica.Controller;
import etomica.Simulation;

/**
 * @author kofke
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResumeSimulationAction extends Action {

	/**
	 * @param text
	 * @param image
	 */
	public ResumeSimulationAction() {
		super("Resume simulation");
		ImageDescriptor eImage = ImageDescriptor.createFromFile(ResumeSimulationAction.class, "enabled/resume_co.gif");
		ImageDescriptor dImage = ImageDescriptor.createFromFile(ResumeSimulationAction.class, "disabled/resume_co.gif");
		setImageDescriptor(eImage);
		setDisabledImageDescriptor(dImage);
	}

	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
		System.out.println("ResumeSimulationAction setsimulation "+simulation);
	}
	
	public void run() {
//		System.out.println("ResumeSimulationAction run");
		if(simulation == null) return;
		Controller controller = simulation.controller(0);
		if(controller == null) return;
		controller.start();	
	}
	
	public void dispose() {
//		image.dispose();
	}
	
	Simulation simulation;
	ImageDescriptor image;
	
}
