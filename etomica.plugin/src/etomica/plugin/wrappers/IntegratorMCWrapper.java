package etomica.plugin.wrappers;

import etomica.integrator.IntegratorMC;
import etomica.integrator.MCMove;

public class IntegratorMCWrapper extends IntegratorWrapper {

    public IntegratorMCWrapper(IntegratorMC object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((IntegratorMC)object).getMCMoves());
    }

    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (super.removeChild(obj)) {
            return true;
        }
        if (obj instanceof MCMove) {
            return ((IntegratorMC)object).removeMCMove((MCMove)obj);
        }
        return false;
    }
    
    public boolean canRemoveChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof MCMove) {
            return true;
        }
        return false;
    }
}
