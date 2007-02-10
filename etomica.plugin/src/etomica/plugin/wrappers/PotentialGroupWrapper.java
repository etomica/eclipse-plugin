package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomType;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.wizards.NewInterPotential;
import etomica.plugin.wizards.NewIntraPotential;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.simulation.Simulation;

public class PotentialGroupWrapper extends PotentialWrapper implements RemoverWrapper, AdderWrapper {

    public PotentialGroupWrapper(PotentialGroup object, Simulation sim) {
        super(object,sim);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((PotentialGroup)object).getPotentials(),simulation,etomicaEditor);
    }
    
    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Potential) {
            ((PotentialGroup)object).removePotential((Potential)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof Potential) {
            Potential[] potentials = ((PotentialGroup)object).getPotentials();
            for (int i=0; i<potentials.length; i++) {
                if (potentials[i] == obj) {
                    return true;
                }
            }
        }
        return false;
    }

    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        
        MenuItemWrapper[] itemWrappers = new MenuItemWrapper[0];
        
        if (((PotentialGroup)object).nBody() == 1 || ((PotentialGroup)object).nBody() == 2) {
            AddItemWrapper addItemWrapper = new AddItemWrapper();

            addItemWrapper.addSubmenuItem(new AddClassItemWrapper(Potential.class, this));
            itemWrappers = new MenuItemWrapper[]{addItemWrapper};
        }
        
        return PropertySourceWrapper.combineMenuItemWrappers(
                itemWrappers, super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == Potential.class) {
            if (((PotentialGroup)object).nBody() == 1) {
                NewIntraPotential wizard = new NewIntraPotential((PotentialGroup)object, sim);

                WizardDialog dialog = new WizardDialog(shell, wizard);
                dialog.create();
                dialog.getShell().setSize(500,500);
                dialog.open();
                return wizard.getSuccess();
            }
            else if (((PotentialGroup)object).nBody() == 2) {
                AtomType[] types = sim.getPotentialMaster().getAtomTypes((Potential)object);
                if (types == null) {
                    return false;
                }
                NewInterPotential wizard = new NewInterPotential((PotentialGroup)object, sim, types);

                WizardDialog dialog = new WizardDialog(shell, wizard);
                dialog.create();
                dialog.getShell().setSize(500,500);
                dialog.open();
                return wizard.getSuccess();
            }
        }
        return false;
    }
}
