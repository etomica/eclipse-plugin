/*
 * Created on May 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IEditorPart;

import etomica.action.activity.ControllerEvent;
import etomica.plugin.editors.EtomicaEditor;

/**
 * @author Henrique
 */
public class StopSimulationActionDelegate extends BaseSimulationActionDelegate 
{

	/**
	 * 
	 */
	public StopSimulationActionDelegate() {
		super();
		// TODO Auto-generated constructor stub
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
				action.setEnabled( controller.isActive() );
				controller.addListener( this );
			}
		}
		
	}
	
	/* (non-Javadoc)
	 * @see etomica.ControllerListener#actionPerformed(etomica.ControllerEvent)
	 */
	public void actionPerformed(ControllerEvent event) {
		if ( event.getType()==ControllerEvent.START )
		{
			current_action.setEnabled( true );
		}
		else if ( event.getType()==ControllerEvent.NO_MORE_ACTIONS )
		{
			current_action.setEnabled( false );
		}
		else if ( event.getType()==ControllerEvent.HALTED )
		{
			current_action.setEnabled( false );
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if(controller == null) return;
		controller.halt();
	}

}
