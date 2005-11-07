package etomica.plugin.wrappers;

import etomica.potential.Potential;
import etomica.potential.PotentialGroup;
import etomica.potential.PotentialMaster;

public class PotentialGroupWrapper extends PropertySourceWrapper {

    public PotentialGroupWrapper(PotentialGroup object) {
        super(object);
    }

    public PropertySourceWrapper[] getChildren() {
        return PropertySourceWrapper.wrapArrayElements(((PotentialGroup)object).getPotentials());
    }
    
    public boolean removeChild(Object obj) {
        if (obj instanceof PropertySourceWrapper) {
            obj = ((PropertySourceWrapper)obj).getObject();
        }
        if (obj instanceof Potential) {
            ((PotentialGroup)object).removePotential((Potential)obj);
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
