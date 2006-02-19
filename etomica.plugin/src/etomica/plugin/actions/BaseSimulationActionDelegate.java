/*
 * Created on Apr 22, 2005
 *
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import etomica.action.activity.Controller;
import etomica.action.activity.ControllerListener;
import etomica.plugin.editors.EtomicaEditor;
import etomica.simulation.Simulation;

/**
 * @author Henrique
 *
 */
public abstract class BaseSimulationActionDelegate implements IEditorActionDelegate, ControllerListener {

	/**
	 * 
	 */
	public BaseSimulationActionDelegate() {
		super();
	}

	void dispose()
	{
		if ( controller!=null )
		{
			controller.getEventManager().removeListener(this);
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
				controller.getEventManager().addListener(this, false);
			}
		}
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
		
	protected EtomicaEditor current_editor;
	protected IAction current_action;
	protected Simulation simulation;
	protected Controller controller;
}
