package etomica.plugin.wrappers;

import etomica.plugin.editors.SimulationObjects;
import etomica.simulation.DataStreamHeader;

public class DataStreamWrapper extends PropertySourceWrapper {

    public DataStreamWrapper(DataStreamHeader object, SimulationObjects simObjects) {
        super(object,simObjects);
    }

    public String toString() {
        // pretend we're the data source
        return ((DataStreamHeader)object).getDataSource().getDataInfo().getLabel()+" stream";
    }
}
