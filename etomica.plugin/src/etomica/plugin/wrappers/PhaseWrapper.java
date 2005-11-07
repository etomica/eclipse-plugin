package etomica.plugin.wrappers;

import etomica.phase.Phase;

public class PhaseWrapper extends PropertySourceWrapper {

    public PhaseWrapper(Phase phase) {
        super(phase);
    }

    public PropertySourceWrapper[] getChildren() {
        return new PropertySourceWrapper[0];
    }
    
}
