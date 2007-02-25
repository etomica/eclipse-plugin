package etomica.plugin.wrappers;

import java.util.LinkedList;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.integrator.mcmove.MCMove;
import etomica.integrator.mcmove.MCMoveManager;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wizards.NewMCMoveWizard;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.simulation.Simulation;

public class MCMoveManagerWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

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
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        AddItemWrapper addItemWrapper = new AddItemWrapper();
        
        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(MCMove.class, this));
        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{addItemWrapper}, super.getMenuItemWrappers(parentWrapper));
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
    
    public EtomicaStatus getStatus(LinkedList parentList) {
        EtomicaStatus superStatus = super.getStatus(parentList);
        if (superStatus.type == EtomicaStatus.OK && ((MCMoveManager)object).getMCMoves().length == 0) {
            return new EtomicaStatus("MCMoveManager needs MCMoves", EtomicaStatus.WARNING);
        }
        return superStatus;
    }
}
