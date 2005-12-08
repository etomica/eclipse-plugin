/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import etomica.plugin.views.DataSourceView;

/**
 * Action that toggles the refresher thread to be enabled or not.
 */
public class ToggleUpdateAction extends Action {

	public ToggleUpdateAction(DataSourceView.Refresher updateThread) {
		super("Run simulation",IAction.AS_CHECK_BOX);
        thread = updateThread;
        setChecked(thread.isEnabled());
		setText("Auto update");
	}

	/**
	 * Toggle the thread's status
	 */
	public void run() {
        //thread.setEnabled(isChecked());
        if (thread.isEnabled()) {
            thread.setEnabled(false);
        }
        else {
            thread.setEnabled(true);
        }
	}
	
	//may not need this
	public void dispose() {
		thread = null;
	}
	
	private DataSourceView.Refresher thread;
}
