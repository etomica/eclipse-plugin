package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.atom.AtomType;
import etomica.plugin.wizards.NewInterPotential;
import etomica.plugin.wizards.NewIntraPotential;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.simulation.Simulation;

public class PotentialGroupWrapper extends PropertySourceWrapper {

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
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
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

    public Class[] getAdders() {
        if (((PotentialGroup)object).nBody() == 1 || ((PotentialGroup)object).nBody() == 2) {
            return new Class[]{Potential.class};
        }
        return new Class[0];
    }

    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        System.out.println("made it here");
        if (newObjectClass == Potential.class) {
            System.out.println("and here");
            if (((PotentialGroup)object).nBody() == 1) {
                NewIntraPotential wizard = new NewIntraPotential((PotentialGroup)object, sim);

                WizardDialog dialog = new WizardDialog(shell, wizard);
                dialog.create();
                dialog.getShell().setSize(500,500);
                dialog.open();
                return wizard.getSuccess();
            }
            else if (((PotentialGroup)object).nBody() == 2) {
                AtomType[] types = sim.potentialMaster.getAtomTypes((Potential)object);
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
