package etomica.plugin.wrappers;

import etomica.potential.Potential;
import etomica.simulation.Simulation;

public class PotentialWrapper extends PropertySourceWrapper {

    public PotentialWrapper(Potential object, Simulation sim) {
        super(object,sim);
    }

    public String toString() {
        return ((Potential)object).getName();
    }
}
