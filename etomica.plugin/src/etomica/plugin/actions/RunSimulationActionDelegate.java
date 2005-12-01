/*
 * Created on Apr 22, 2005
 *
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import etomica.action.activity.ControllerEvent;
import etomica.plugin.editors.EtomicaEditor;
import etomica.simulation.SimulationEvent;

/**
 * @author Henrique
 *
 */
public class RunSimulationActionDelegate extends BaseSimulationActionDelegate {

	/**
	 * 
	 */
	public RunSimulationActionDelegate() {
		super();
	}

	void dispose()
	{
		if ( controller!=null )
		{
			controller.removeListener( this );
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		current_editor = (EtomicaEditor) targetEditor;
		current_action = action;
		
		// Set the initial value based on the current simulation state
		if ( current_editor!=null) 
		{
			simulation = current_editor.getSimulation();
			if ( simulation!=null && simulation.getController()!=null )
			{
				controller = simulation.getController();
				action.setEnabled( !controller.isActive() );
				controller.addListener( this );
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		current_action = action;
		if(controller == null || controller.isActive()) return;
		
		if ( controller.isActive() )
			controller.unPause();
		else
			controller.start();	
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	/* (non-Javadoc)
	 * @see etomica.ControllerListener#actionPerformed(etomica.ControllerEvent)
	 */
	public void actionPerformed(ControllerEvent event) {
		if ( event.getType()==ControllerEvent.START )
		{
			current_action.setEnabled( false );
		}
		else if ( event.getType()==ControllerEvent.NO_MORE_ACTIONS )
		{
			current_action.setEnabled( true );
		}
	}
	
	/* (non-Javadoc)
	 * @see etomica.SimulationListener#actionPerformed(etomica.SimulationEvent)
	 */
	public void actionPerformed(SimulationEvent event) {
	}
}
