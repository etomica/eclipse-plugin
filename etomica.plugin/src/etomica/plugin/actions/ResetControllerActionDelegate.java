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
 * Delegate for resetting the simulation
 */
public class ResetControllerActionDelegate extends BaseSimulationActionDelegate 
{

    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        current_editor = (EtomicaEditor) targetEditor;
        current_action = action;
        
        // Set the initial value based on the current simulation state
        if ( current_editor!=null) {
            simulation = current_editor.getSimulation();
            if (simulation!=null && simulation.getController()!=null) {
                controller = simulation.getController();
                action.setEnabled(controller.getCurrentActions().length == 0);
                controller.addListener( this );
            }
        }
    }
    
    public void actionPerformed(ControllerEvent event) {
        if (event.getType()==ControllerEvent.START_ACTION) {
            current_action.setEnabled(false);
        }
        else if (event.getType()==ControllerEvent.END_ACTION) {
            current_action.setEnabled(true);
        }
    }
    
    public void run(IAction action) {
        if(controller == null) return;
        controller.reset();
    }

}
