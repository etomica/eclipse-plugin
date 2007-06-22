package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomType;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewInterPotential;
import etomica.plugin.wizards.NewIntraPotential;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.potential.IPotential;
import etomica.potential.PotentialGroup;

public class PotentialGroupWrapper extends PotentialWrapper implements RemoverWrapper, AdderWrapper {

    public PotentialGroupWrapper(PotentialGroup object, SimulationObjects simObjects) {
        super(object,simObjects);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof IPotential) {
            ((PotentialGroup)object).removePotential((IPotential)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof IPotential) {
            IPotential[] potentials = ((PotentialGroup)object).getPotentials();
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

            addItemWrapper.addSubmenuItem(new AddClassItemWrapper(IPotential.class, this));
            itemWrappers = new MenuItemWrapper[]{addItemWrapper};
        }
        
        return PropertySourceWrapper.combineMenuItemWrappers(
                itemWrappers, super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Class newObjectClass, Shell shell) {
        if (newObjectClass == IPotential.class) {
            if (((PotentialGroup)object).nBody() == 1) {
                NewIntraPotential wizard = new NewIntraPotential((PotentialGroup)object, simObjects);

                WizardDialog dialog = new WizardDialog(shell, wizard);
                dialog.create();
                dialog.getShell().setSize(500,500);
                dialog.open();
                return wizard.getSuccess();
            }
            else if (((PotentialGroup)object).nBody() == 2 && myPotentialMaster != null) {
                AtomType[] types = myPotentialMaster.getAtomTypes((IPotential)object);
                if (types == null) {
                    return false;
                }
                NewInterPotential wizard = new NewInterPotential((PotentialGroup)object, simObjects, types);

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
