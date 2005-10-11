package etomica.plugin.wrappers;

import etomica.potential.PotentialGroup;

public class PotentialGroupWrapper extends PropertySourceWrapper {

    public PotentialGroupWrapper(PotentialGroup object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((PotentialGroup)object).getPotentials());
    }
}
