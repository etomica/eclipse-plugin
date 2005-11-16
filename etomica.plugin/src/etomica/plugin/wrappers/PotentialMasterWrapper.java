package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.plugin.wizards.NewSpeciesPotential;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;

public class PotentialMasterWrapper extends PropertySourceWrapper {

    public PotentialMasterWrapper(PotentialMaster object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((PotentialMaster)object).getPotentials());
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
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
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
    
    public Class[] getAdders() {
        return new Class[]{Potential.class};
    }

    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == Potential.class) {
            NewSpeciesPotential wizard = new NewSpeciesPotential((PotentialMaster)object, sim);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
}
