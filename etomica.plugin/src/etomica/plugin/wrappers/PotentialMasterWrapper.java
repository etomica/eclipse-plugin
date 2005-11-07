package etomica.plugin.wrappers;

import etomica.potential.Potential;
import etomica.potential.PotentialMaster;

public class PotentialMasterWrapper extends PropertySourceWrapper {

    public PotentialMasterWrapper(PotentialMaster object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((PotentialMaster)object).getPotentials());
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
            return true;
        }
        return false;
    }
}
