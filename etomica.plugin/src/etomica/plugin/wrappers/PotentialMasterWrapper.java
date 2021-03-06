package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.nbr.cell.PotentialMasterCell;
import etomica.nbr.list.PotentialMasterList;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewSpeciesPotential;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.potential.IPotential;
import etomica.potential.PotentialMaster;

public class PotentialMasterWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public PotentialMasterWrapper(PotentialMaster object, SimulationObjects simObjects) {
        super(object,simObjects);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof IPotential) {
            ((PotentialMaster)object).removePotential((IPotential)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof IPotential) {
            IPotential[] potentials = ((PotentialMaster)object).getPotentials();
            for (int i=0; i<potentials.length; i++) {
                if (potentials[i] == obj) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        AddItemWrapper addItemWrapper = new AddItemWrapper();

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(IPotential.class, this));

        MenuItemWrapper[] itemWrappers = null;
        if (object instanceof PotentialMasterCell || object instanceof PotentialMasterList) {
            PotentialMasterReset potentialMasterReset = new PotentialMasterReset((PotentialMaster)object);
            ActionListItemWrapper actionListItemWrapper = new ActionListItemWrapper();
            actionListItemWrapper.addSubmenuItem(new ActionItemWrapper(potentialMasterReset, "Reset"));
            itemWrappers = new MenuItemWrapper[]{addItemWrapper, actionListItemWrapper};
        }
        else {
            itemWrappers = new MenuItemWrapper[]{addItemWrapper};
        }

        return PropertySourceWrapper.combineMenuItemWrappers(itemWrappers, 
                super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Class newObjectClass, Shell shell) {
        if (newObjectClass == IPotential.class) {
            NewSpeciesPotential wizard = new NewSpeciesPotential((PotentialMaster)object, simObjects);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,500);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }

    private static class PotentialMasterReset implements Action {
        public PotentialMasterReset(PotentialMaster potentialMaster) {
            super();
            this.potentialMaster = potentialMaster;
        }

        public void actionPerformed() {
            if (potentialMaster instanceof PotentialMasterCell) {
                ((PotentialMasterCell)potentialMaster).reset();
            }
            else if (potentialMaster instanceof PotentialMasterList) {
                ((PotentialMasterList)potentialMaster).reset();
            }
        }
        
        private final PotentialMaster potentialMaster;
    }
}
