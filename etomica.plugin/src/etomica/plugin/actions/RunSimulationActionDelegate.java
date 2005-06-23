/*
 * Created on Apr 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

import etomica.Controller;
import etomica.ControllerEvent;
import etomica.ControllerListener;
import etomica.Simulation;
import etomica.SimulationEvent;
import etomica.plugin.editors.EtomicaEditor;

/**
 * @author Henrique
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RunSimulationActionDelegate implements IEditorActionDelegate, ControllerListener {

	/**
	 * 
	 */
	public RunSimulationActionDelegate() {
		super();
		// TODO Auto-generated constructor stub
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
		controller.start();	
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

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
		else if ( event.getType()==ControllerEvent.HALTED )
		{
			current_action.setEnabled( true );
		}
	}
	
	/* (non-Javadoc)
	 * @see etomica.SimulationListener#actionPerformed(etomica.SimulationEvent)
	 */
	public void actionPerformed(SimulationEvent event) {
	}
	
	
	protected EtomicaEditor current_editor;
	protected IAction current_action;
	protected Simulation simulation;
	protected Controller controller;
}
