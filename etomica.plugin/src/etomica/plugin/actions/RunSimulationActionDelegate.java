/*
 * Created on Apr 22, 2005
 *
 */
package etomica.plugin.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;

import etomica.action.activity.ControllerEvent;

/**
 * Action delegate that starts the simulation.  This delegate is used for the run 
 * button in the toolbar.
 * @author Henrique
 */
public class RunSimulationActionDelegate extends BaseSimulationActionDelegate {

	/**
	 * 
	 */
	public RunSimulationActionDelegate() {
		super();
	}

	public void run(IAction action) {
		current_action = action;
		if(controller == null || controller.isActive()) return;
		
        current_editor.markBusy(true);
        current_editor.markDirty();
        Thread runner = new Thread(new Runnable() {
            public void run() {
                try {
                    simulation.getController().actionPerformed();
                }
                catch (RuntimeException e) {
                    WorkbenchPlugin.getDefault().getLog().log(
                            new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, e.getMessage(), e.getCause()));
                }
            }
        });
        runner.start();
	}
	
	public void actionPerformed(ControllerEvent event) {
		if ( event.getType()==ControllerEvent.START )
		{
			current_action.setEnabled( false );
		}
		else if ( event.getType()==ControllerEvent.NO_MORE_ACTIONS )
		{
			current_action.setEnabled( true );
            current_editor.markBusy(false);
		}
	}
	
}
