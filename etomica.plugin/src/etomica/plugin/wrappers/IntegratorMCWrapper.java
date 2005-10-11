package etomica.plugin.wrappers;

import etomica.integrator.IntegratorMC;

public class IntegratorMCWrapper extends PropertySourceWrapper {

    public IntegratorMCWrapper(IntegratorMC object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((IntegratorMC)object).getMCMoves());
    }
}
