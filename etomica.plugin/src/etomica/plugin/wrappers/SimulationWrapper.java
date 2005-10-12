package etomica.plugin.wrappers;

import etomica.phase.Phase;
import etomica.simulation.DataStreamHeader;
import etomica.simulation.Simulation;
import etomica.species.Species;

public class SimulationWrapper extends PropertySourceWrapper {

    public SimulationWrapper(Simulation sim) {
        super(sim);
    }

    public PropertySourceWrapper[] getChildren() {
        Simulation sim = (Simulation)object;
        Phase[] phases = sim.getPhases();
        Species[] species = sim.getSpecies();
        DataStreamHeader[] streams = sim.getDataStreams();
        PropertySourceWrapper[] elements = new PropertySourceWrapper[2+phases.length+species.length+streams.length];
        int i=0;
        elements[i++] = PropertySourceWrapper.makeWrapper(sim.getController());
        elements[i++] = PropertySourceWrapper.makeWrapper(sim.potentialMaster);
        System.arraycopy(PropertySourceWrapper.wrapArrayElements(phases),0,elements,i,phases.length);
        i+=phases.length;
        System.arraycopy(PropertySourceWrapper.wrapArrayElements(species),0,elements,i,species.length);
        i+=species.length;
        System.arraycopy(PropertySourceWrapper.wrapArrayElements(streams),0,elements,i,streams.length);
        return elements;
    }

}
