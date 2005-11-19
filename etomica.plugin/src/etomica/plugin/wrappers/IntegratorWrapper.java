package etomica.plugin.wrappers;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntegratorNonintervalListener;
import etomica.integrator.IntegratorPhase;
import etomica.phase.Phase;
import etomica.plugin.wizards.NewIntervalListenerWizard;
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
        return new Class[]{IntegratorIntervalListener.class,IntegratorNonintervalListener.class};
    }
    
    public boolean addObjectClass(Simulation sim, Class newObjectClass, Shell shell) {
        if (newObjectClass == IntegratorIntervalListener.class) {
            NewIntervalListenerWizard wizard = new NewIntervalListenerWizard((Integrator)object);

            WizardDialog dialog = new WizardDialog(shell, wizard);
            dialog.create();
            dialog.getShell().setSize(500,400);
            dialog.open();
            return wizard.getSuccess();
        }
        return false;
    }
}
