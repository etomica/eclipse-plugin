package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import etomica.action.Action;
import etomica.nbr.PotentialMasterNbr;
import etomica.nbr.cell.PotentialMasterCell;
import etomica.phase.Phase;
import etomica.plugin.wizards.NewSpeciesPotential;
import etomica.potential.Potential;
import etomica.potential.PotentialMaster;
import etomica.simulation.Simulation;

public class PotentialMasterWrapper extends PropertySourceWrapper {

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
            dialog.getShell().setSize(500,500);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }

    public Action[] getActions() {
        if (object instanceof PotentialMasterNbr) {
            UpdateTypeList updateTypeList = new UpdateTypeList((PotentialMasterNbr)object,simulation);
            return new Action[]{updateTypeList};
        }
        return new Action[0];
    }
    
    private static class UpdateTypeList implements Action {
        public UpdateTypeList(PotentialMasterNbr potentialMasterNbr, Simulation simulation) {
            potentialMaster = potentialMasterNbr;
            sim = simulation;
        }

        public String getLabel() {
            return "Update Type List";
        }
        
        public void actionPerformed() {
            Phase[] phases = sim.getPhases();
            for (int i=0; i<phases.length; i++) {
                potentialMaster.updateTypeList(phases[i]);
            }
        }
        
        private final PotentialMasterNbr potentialMaster;
        private final Simulation sim;
    }

}
