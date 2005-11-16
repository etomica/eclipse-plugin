package etomica.plugin.wrappers;

import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
import etomica.integrator.IntegratorNonintervalListener;
import etomica.integrator.IntegratorPhase;
import etomica.phase.Phase;

public class IntegratorWrapper extends PropertySourceWrapper {

    public IntegratorWrapper(Integrator object) {
        super(object);
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
}
