/*
 * History
 * Created on Oct 5, 2004 by kofke
 */
package etomica.plugin.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import etomica.plugin.views.DataTableView;

/**
 * Action that toggles the refresher thread to be enabled or not.
 */
public class ToggleUpdateAction extends Action {

	public ToggleUpdateAction(DataTableView.Refresher updateThread) {
		super("Run simulation",IAction.AS_CHECK_BOX);
        thread = updateThread;
        if (thread != null) {
            setChecked(thread.isEnabled());
        }
        else {
            setChecked(false);
        }
		setText("Auto update");
	}

	/**
	 * Toggle the thread's status
	 */
	public void run() {
        if (thread == null) {
            if (isChecked()) {
                setChecked(false);
            }
            return;
        }
        thread.setEnabled(isChecked());
	}
	
	//may not need this
	public void dispose() {
		thread = null;
	}
    
    public void setRefresher(DataTableView.Refresher updateThread) {
        thread = updateThread;
    }
	
	private DataTableView.Refresher thread;
}
