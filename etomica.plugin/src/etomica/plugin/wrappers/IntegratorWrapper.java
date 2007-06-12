package etomica.plugin.wrappers;

import java.util.LinkedList;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.integrator.Integrator;
import etomica.integrator.IntegratorHard;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntegratorListener;
import etomica.integrator.IntegratorMC;
import etomica.integrator.IntegratorMD;
import etomica.integrator.IntegratorNonintervalListener;
import etomica.integrator.IntegratorPhase;
import etomica.nbr.list.PotentialMasterList;
import etomica.nbr.site.PotentialMasterSite;
import etomica.phase.Phase;
import etomica.plugin.editors.MenuItemCascadeWrapper;
import etomica.plugin.editors.MenuItemWrapper;
import etomica.plugin.editors.SimulationObjects;
import etomica.plugin.wizards.NewIntegratorListenerWizard;
import etomica.plugin.wrappers.AddItemWrapper.AddClassItemWrapper;
import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialHard;
import etomica.potential.PotentialMaster;
import etomica.potential.PotentialSoft;

public class IntegratorWrapper extends PropertySourceWrapper implements RemoverWrapper, AdderWrapper {

    public IntegratorWrapper(Integrator object, SimulationObjects simObjects) {
        super(object,simObjects);
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
        if (obj instanceof IntegratorListener) {
            ((Integrator)object).removeListener((IntegratorListener)obj);
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

    public MenuItemWrapper[] getMenuItemWrappers(PropertySourceWrapper parentWrapper) {
        MenuItemCascadeWrapper addItemWrapper = new AddItemWrapper();

        addItemWrapper.addSubmenuItem(new AddClassItemWrapper(IntegratorListener.class, this));

        return PropertySourceWrapper.combineMenuItemWrappers(
                new MenuItemWrapper[]{addItemWrapper}, super.getMenuItemWrappers(parentWrapper));
    }

    public boolean addObjectClass(Class newObjectClass, Shell shell) {
        if (newObjectClass == IntegratorListener.class) {
            NewIntegratorListenerWizard wizard = new NewIntegratorListenerWizard((Integrator)object,simObjects);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
    
    public EtomicaStatus getStatus(LinkedList parentList) {
        // super.getStatus assigns the status to our |status| field
        super.getStatus(parentList);
        if (object instanceof IntegratorPhase) {
            if (((IntegratorPhase)object).getPhase() == null && status.type.severity < EtomicaStatus.ERROR.severity) {
                status = new EtomicaStatus("Integrator must have a Phase", EtomicaStatus.ERROR);
            }
        }
        if (status.type.severity < EtomicaStatus.WARNING.severity && object instanceof IntegratorMC) {
            PotentialMaster potentialMaster = ((IntegratorMC)object).getPotential();
            if (potentialMaster instanceof PotentialMasterList) {
                status = new EtomicaStatus("MC Integrators don't work well with neighbor-listing "+potentialMaster.getClass(), EtomicaStatus.WARNING);
            }
        }
        if (object instanceof IntegratorMD) {
            PotentialMaster potentialMaster = ((IntegratorMD)object).getPotential();
            if (status.type.severity < EtomicaStatus.WARNING.severity && potentialMaster instanceof PotentialMasterSite) {
                status = new EtomicaStatus("MD Integrators don't work well with cell-listing "+potentialMaster.getClass(), EtomicaStatus.WARNING);
            }
            Potential[] potentials = potentialMaster.getPotentials();
            // must all hard integrators extend IntegratorHard?  Are the not-soft-not-hard Integrators?
            // need to use etomica.compatibility
            boolean hardIntegrator = object instanceof IntegratorHard;
            EtomicaStatus potentialStatus = checkPotentialStatus(potentials, hardIntegrator);
            if (status.type.severity < potentialStatus.type.severity) {
                status = potentialStatus;
            }
        }
        return status;
    }
    
    protected EtomicaStatus checkPotentialStatus(Potential[] potentials, boolean hardIntegrator) {
        EtomicaStatus potentialStatus = EtomicaStatus.PEACHY;
        for (int i=0; i<potentials.length; i++) {
            if (potentials[i] instanceof PotentialGroup) {
                potentialStatus = checkPotentialStatus(((PotentialGroup)potentials[i]).getPotentials(), hardIntegrator);
            }
            else if (hardIntegrator && !(potentials[i] instanceof PotentialHard)) {
                potentialStatus = new EtomicaStatus("A hard Integrator can only use hard potentials.  "+
                                           potentials[i]+" is not a hard potential.",EtomicaStatus.ERROR);
            }
            else if (!hardIntegrator && !(potentials[i] instanceof PotentialSoft)) {
                potentialStatus = new EtomicaStatus("A soft Integrator can only use soft potentials.  "+
                                           potentials[i]+" is a not a soft potential.",EtomicaStatus.ERROR);
            }
            if (potentialStatus != EtomicaStatus.PEACHY) {
                return potentialStatus;
            }
        }
        return potentialStatus;
    }
}
