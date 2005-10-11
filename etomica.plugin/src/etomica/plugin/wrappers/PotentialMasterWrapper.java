package etomica.plugin.wrappers;

import etomica.potential.PotentialMaster;

public class PotentialMasterWrapper extends PropertySourceWrapper {

    public PotentialMasterWrapper(PotentialMaster object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((PotentialMaster)object).getPotentials());
    }
}
