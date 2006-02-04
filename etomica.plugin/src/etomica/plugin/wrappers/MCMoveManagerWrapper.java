package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.integrator.MCMove;
import etomica.integrator.mcmove.MCMoveManager;
import etomica.plugin.wizards.NewMCMoveWizard;
import etomica.simulation.Simulation;

public class MCMoveManagerWrapper extends PropertySourceWrapper {

    public MCMoveManagerWrapper(MCMoveManager object, Simulation sim) {
        super(object,sim);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof MCMove) {
            ((MCMoveManager)object).removeMCMove((MCMove)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof MCMove) {
            MCMove[] moves = ((MCMoveManager)object).getMCMoves();
            for (int i=0; i<moves.length; i++) {
                if (moves[i] == obj) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Class[] getAdders() {
        return new Class[]{MCMove.class};
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == MCMove.class) {
            NewMCMoveWizard wizard = new NewMCMoveWizard((MCMoveManager)object,simulation);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
}