package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.integrator.Integrator;
import etomica.integrator.IntegratorHard;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntegratorMC;
import etomica.integrator.IntegratorMD;
import etomica.integrator.IntegratorNonintervalListener;
import etomica.integrator.IntegratorPhase;
import etomica.nbr.list.PotentialMasterList;
import etomica.nbr.site.PotentialMasterSite;
import etomica.phase.Phase;
import etomica.plugin.wizards.NewIntervalListenerWizard;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialHard;
import etomica.potential.PotentialMaster;
import etomica.potential.PotentialSoft;
import etomica.simulation.Simulation;

public class IntegratorWrapper extends PropertySourceWrapper {

    public IntegratorWrapper(Integrator object, Simulation sim) {
        super(object,sim);
    }

    protected IPropertyDescriptor makeDescriptor(java.beans.PropertyDescriptor property) {
        String name = property.getDisplayName();
        if(name.equals("potential")) return null;//skip the PotentialMaster
        
        return super.makeDescriptor(property);
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof IntegratorIntervalListener) {
            ((Integrator)object).removeListener((IntegratorIntervalListener)obj);
            return true;
        }
        if (obj instanceof IntegratorNonintervalListener) {
            ((Integrator)object).removeListener((IntegratorNonintervalListener)obj);
            return true;
        }
        if (obj instanceof Phase && object instanceof IntegratorPhase) {
            if (((IntegratorPhase)object).getPhase() == obj) {
                ((IntegratorPhase)object).setPhase(null);
                return true;
            }
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof IntegratorIntervalListener) {
            IntegratorIntervalListener[] listeners = ((Integrator)object).getIntervalListeners();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == obj) {
                    return true;
                }
            }
        }
        if (obj instanceof IntegratorNonintervalListener) {
            IntegratorNonintervalListener[] listeners = ((Integrator)object).getNonintervalListeners();
            for (int i=0; i<listeners.length; i++) {
                if (listeners[i] == obj) {
                    return true;
                }
            }
        }
        else if (obj instanceof Phase && object instanceof IntegratorPhase) {
            return obj == ((IntegratorPhase)object).getPhase();
        }
        return false;
    }
    
    public Class[] getAdders() {
        return new Class[]{IntegratorIntervalListener.class};
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == IntegratorIntervalListener.class) {
            NewIntervalListenerWizard wizard = new NewIntervalListenerWizard((Integrator)object,simulation);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
    
    public EtomicaStatus getStatus() {
        EtomicaStatus superStatus = super.getStatus();
        if (superStatus.type == EtomicaStatus.ERROR) {
            return superStatus;
        }
        if (object instanceof IntegratorPhase) {
            if (((IntegratorPhase)object).getPhase() == null) {
                return new EtomicaStatus("Integrator must have a Phase", EtomicaStatus.ERROR);
            }
        }
        if (superStatus.type == EtomicaStatus.WARNING) {
            // the rest of the checking here is for warnings
            return superStatus;
        }
        if (superStatus == EtomicaStatus.PEACHY && object instanceof IntegratorMC) {
            PotentialMaster potentialMaster = ((Integrator)object).getPotential();
            if (potentialMaster instanceof PotentialMasterList) {
                return new EtomicaStatus("MC Integrators don't work well with neighbor-listing "+potentialMaster.getClass(), EtomicaStatus.WARNING);
            }
        }
        if (object instanceof IntegratorMD) {
            PotentialMaster potentialMaster = ((Integrator)object).getPotential();
            if (superStatus == EtomicaStatus.PEACHY && potentialMaster instanceof PotentialMasterSite) {
                return new EtomicaStatus("MC Integrators don't work well with cell-listing "+potentialMaster.getClass(), EtomicaStatus.WARNING);
            }
            Potential[] potentials = potentialMaster.getPotentials();
            // must all hard integrators extend IntegratorHard?  Are the not-soft-not-hard Integrators?
            // need to use etomica.compatibility
            boolean hardIntegrator = object instanceof IntegratorHard;
            EtomicaStatus potentialStatus = checkPotentialStatus(potentials, hardIntegrator);
            if (potentialStatus.type == EtomicaStatus.ERROR) {
                return potentialStatus;
            }
        }
        return superStatus;
    }
    
    protected EtomicaStatus checkPotentialStatus(Potential[] potentials, boolean hardIntegrator) {
        EtomicaStatus status = EtomicaStatus.PEACHY;
        for (int i=0; i<potentials.length; i++) {
            if (potentials[i] instanceof PotentialGroup) {
                status = checkPotentialStatus(((PotentialGroup)potentials[i]).getPotentials(), hardIntegrator);
            }
            else if (hardIntegrator && !(potentials[i] instanceof PotentialHard)) {
                status = new EtomicaStatus("A hard Integrator can only use hard potentials.  "+
                                           potentials[i]+" is not a hard potential.",EtomicaStatus.ERROR);
            }
            else if (!hardIntegrator && !(potentials[i] instanceof PotentialSoft)) {
                status = new EtomicaStatus("A soft Integrator can only use soft potentials.  "+
                                           potentials[i]+" is a not a soft potential.",EtomicaStatus.ERROR);
            }
            if (status != EtomicaStatus.PEACHY) {
                return status;
            }
        }
        return status;
    }
}
