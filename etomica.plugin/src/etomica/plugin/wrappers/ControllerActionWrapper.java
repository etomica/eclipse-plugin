package etomica.plugin.wrappers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import etomica.action.Action;
import etomica.plugin.editors.SimulationObjects;

/**
 * This doesn't work as a PropertySourceWrapper.  Might just need to go away
 */
public class ControllerActionWrapper { //extends PropertySourceWrapper {

    public ControllerActionWrapper(Action object, SimulationObjects simObjects) {
//        super(object,sim);
    }

    public void setException(Exception e) {
        exception = e;
    }
    
    public boolean open(String type, IWorkbenchPage page, Shell shell) {
        if (exception != null) {
            ErrorDialog dialog = new ErrorDialog(shell, "Action Failed", "",
                    new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, exception.getMessage(), exception),
                    IStatus.ERROR);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return true;
        }
        return false;
    }
    
    private Exception exception;
}
