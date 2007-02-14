package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.nbr.cell.PotentialMasterCell;
import etomica.nbr.list.PotentialMasterList;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wizards.NewSpeciesPotential;
import etomica.plugin.wrappers.ActionListItemWrapper.ActionItemWrapper;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.potential.Potential;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;

public class PotentialMasterWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public PotentialMasterWrapper(PotentialMaster object, Simulation sim) {
        super(object,sim);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Potential) {
            ((PotentialMaster)object).removePotential((Potential)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof Potential) {
            Potential[] potentials = ((PotentialMaster)object).getPotentials();
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

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Potential.class, this));

        MenuItemWrapper[] itemWrappers = null;
        if (object instanceof PotentialMasterCell || object instanceof PotentialMasterList) {
            PotentialMasterReset potentialMasterReset = new PotentialMasterReset((PotentialMaster)object);
            ActionListItemWrapper actionListItemWrapper = new ActionListItemWrapper();
            actionListItemWrapper.addSubmenuItem(new ActionItemWrapper(potentialMasterReset));
            itemWrappers = new MenuItemWrapper[]{addItemWrapper, actionListItemWrapper};
        }
        else {
            itemWrappers = new MenuItemWrapper[]{addItemWrapper};
        }

        return PropertySourceWrapper.combineMenuItemWrappers(itemWrappers, 
                super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == Potential.class) {
            NewSpeciesPotential wizard = new NewSpeciesPotential((PotentialMaster)object, sim);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,500);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }

    public EtomicaStatus getStatus() {
        if (object instanceof PotentialMasterList) {
            double range = ((PotentialMasterList)object).getRange();
            if (range == 0) {
                return new EtomicaStatus("Range must be positive", EtomicaStatus.ERROR);
            }
            if (range < ((PotentialMasterList)object).getMaxPotentialRange()) {
                return new EtomicaStatus("Range must be greater than longest-range potential", EtomicaStatus.ERROR);
            }
        }
        else if (object instanceof PotentialMasterCell) {
            double range = ((PotentialMasterCell)object).getRange();
            if (range == 0) {
                return new EtomicaStatus("Range must be positive", EtomicaStatus.ERROR);
            }
            if (range < ((PotentialMasterCell)object).getMaxPotentialRange()) {
                return new EtomicaStatus("Range must be greater than longest-range potential", EtomicaStatus.ERROR);
            }
        }
        return EtomicaStatus.PEACHY;
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
        
        public String getLabel() {
            return "Reset";
        }
        
        private final PotentialMaster potentialMaster;
    }
}
