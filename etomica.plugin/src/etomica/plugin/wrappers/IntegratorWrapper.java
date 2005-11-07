package etomica.plugin.wrappers;

import etomica.integrator.Integrator;
import etomica.integrator.IntegratorIntervalListener;
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
        else if (obj instanceof Phase) {
            ((Integrator)object).removePhase((Phase)obj);
            return true;
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof IntegratorIntervalListener || obj instanceof Phase) {
            return true;
        }
        return false;
    }
}
